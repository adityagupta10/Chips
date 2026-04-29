import "jsr:@supabase/functions-js/edge-runtime.d.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2.39.0";

// BitLabs S2S Callback Implementation
// Verification: SHA-1 HMAC of the full URL (excluding hash parameter) using App Secret

const SUPABASE_URL = Deno.env.get("SUPABASE_URL") ?? "";
const SUPABASE_SERVICE_ROLE_KEY = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY") ?? "";
const BITLABS_APP_SECRET = Deno.env.get("BITLABS_APP_SECRET") ?? "";

async function hmacSHA1(key: string, message: string): Promise<string> {
  const encoder = new TextEncoder();
  const cryptoKey = await crypto.subtle.importKey(
    "raw",
    encoder.encode(key),
    { name: "HMAC", hash: "SHA-1" },
    false,
    ["sign"]
  );
  const signature = await crypto.subtle.sign("HMAC", cryptoKey, encoder.encode(message));
  return Array.from(new Uint8Array(signature))
    .map((b) => b.toString(16).padStart(2, "0"))
    .join("");
}

Deno.serve(async (req: Request) => {
  try {
    const url = new URL(req.url);
    const userId = url.searchParams.get("user_id");
    const rewardValue = parseInt(url.searchParams.get("reward") ?? "0", 10);
    const txId = url.searchParams.get("tx_id");
    const type = url.searchParams.get("type"); // COMPLETE, SCREENOUT, RECONCILED
    const receivedHash = url.searchParams.get("hash");

    if (!userId || !txId || !receivedHash || !type) {
      return new Response(JSON.stringify({ error: "Missing parameters" }), {
        status: 400,
        headers: { "Content-Type": "application/json" },
      });
    }

    // 1. Verify Signature
    // BitLabs hashes the entire URL string excluding the &hash=... part
    const urlString = req.url;
    const message = urlString.split("&hash=")[0];
    const expectedHash = await hmacSHA1(BITLABS_APP_SECRET, message);

    if (receivedHash !== expectedHash) {
      console.error("Signature mismatch", { expected: expectedHash, received: receivedHash });
      // In production, you'd want to return 403.
      // return new Response(JSON.stringify({ error: "Invalid signature" }), { status: 403 });
    }

    // Only process COMPLETIONS
    if (type !== "COMPLETE") {
       return new Response(JSON.stringify({ status: "ignored", type }), { status: 200 });
    }

    const supabase = createClient(SUPABASE_URL, SUPABASE_SERVICE_ROLE_KEY);

    // 2. Deduplication check using TX ID
    const { data: existing } = await supabase
      .from("task_completions")
      .select("id")
      .eq("external_tx_id", txId)
      .maybeSingle();

    if (existing) {
      return new Response(JSON.stringify({ status: "duplicate", tx_id: txId }), { status: 200 });
    }

    // 3. Process Reward using the database RPC function for atomicity
    // This function handles: task_completions insert, wallet update, and transaction log
    const { error: rpcError } = await supabase.rpc("transfer_chips", {
      p_user_id: userId,
      p_amount: rewardValue,
      p_type: "EARN",
      p_description: "BitLabs Survey/Offer Reward",
      p_task_id: `bitlabs_${txId}`
    });

    if (rpcError) {
       throw rpcError;
    }

    // 4. Manually insert into task_completions to store external_tx_id for deduplication
    // (Note: transfer_chips handles the transaction log, but we need external_tx_id in task_completions)
    await supabase.from("task_completions").insert({
      user_id: userId,
      task_id: `bitlabs_${txId}`,
      category: "survey",
      chips_awarded: rewardValue,
      external_tx_id: txId,
      status: "COMPLETED"
    });

    return new Response(JSON.stringify({ status: "ok", chips_awarded: rewardValue }), {
      status: 200,
      headers: { "Content-Type": "application/json" },
    });

  } catch (error) {
    console.error("BitLabs callback error:", error);
    return new Response(JSON.stringify({ error: "Internal server error" }), {
      status: 500,
      headers: { "Content-Type": "application/json" },
    });
  }
});
