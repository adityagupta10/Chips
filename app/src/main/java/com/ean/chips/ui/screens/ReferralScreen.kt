package com.ean.chips.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ean.chips.ui.theme.AccentGold
import com.ean.chips.ui.theme.PrimaryNeon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferralScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Refer & Earn", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(24.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(60.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Share, contentDescription = null, tint = AccentGold, modifier = Modifier.size(48.dp))
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text("Invite Friends, Earn Chips!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Share your code and get 500 Chips when your friend logs in.", color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            
            Spacer(modifier = Modifier.height(48.dp))
            
            OutlinedTextField(
                value = "CHIPS99",
                onValueChange = {},
                readOnly = true,
                label = { Text("Your Code") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { /* Share Intent */ },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryNeon)
            ) {
                Text("Share Now", fontWeight = FontWeight.Bold)
            }
        }
    }
}
