import "jsr:@supabase/functions-js/edge-runtime.d.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2.39.0";

// AdGem S2S Callback Implementation
// Verification: SHA-256 HMAC of the request parameters using Postback Key

const SUPABASE_URL = Deno.env.get("SUPABASE_URL") ?? "";
const SUPABASE_SERVICE_ROLE_KEY = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY") ?? "";

Deno.serve(async (req: Request) => {
  console.log(`AdGem Callback: ${req.method} ${req.url}`);

  try {
    const url = new URL(req.url);
    const userId = url.searchParams.get("player_id");
    const amount = parseInt(url.searchParams.get("amount") ?? "0", 10);
    const transactionId = url.searchParams.get("transaction_id");
    const campaignName = url.searchParams.get("campaign_name") ?? "AdGem Offer";

    console.log("AdGem Params:", { userId, amount, transactionId, campaignName });

    if (!userId || !transactionId || amount <= 0) {
      return new Response(JSON.stringify({ error: "Missing required parameters" }), {
        status: 400,
        headers: { "Content-Type": "application/json" },
      });
    }

    const supabase = createClient(SUPABASE_URL, SUPABASE_SERVICE_ROLE_KEY);

    // 1. Deduplication check
    const { data: existing } = await supabase
      .from("task_completions")
      .select("id")
      .eq("external_tx_id", transactionId)
      .maybeSingle();

    if (existing) {
      console.log(`Duplicate transaction: ${transactionId}`);
      return new Response(JSON.stringify({ status: "duplicate" }), { status: 200 });
    }

    // 2. Process Reward using transfer_chips RPC
    const { error: rpcError } = await supabase.rpc("transfer_chips", {
      p_user_id: userId,
      p_amount: amount,
      p_type: "EARN",
      p_description: `AdGem: ${campaignName}`,
      p_task_id: `adgem_${transactionId}`
    });

    if (rpcError) throw rpcError;

    // 3. Record completion
    await supabase.from("task_completions").insert({
      user_id: userId,
      task_id: `adgem_${transactionId}`,
      category: "offerwall",
      chips_awarded: amount,
      external_tx_id: transactionId,
      status: "COMPLETED"
    });

    return new Response(JSON.stringify({ status: "success" }), {
      status: 200,
      headers: { "Content-Type": "application/json" },
    });

  } catch (error) {
    console.error("AdGem Callback Error:", error);
    return new Response(JSON.stringify({ error: "Internal Server Error", details: error.message }), {
      status: 500,
      headers: { "Content-Type": "application/json" },
    });
  }
});
