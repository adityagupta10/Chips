package com.ean.chips.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ean.chips.ui.theme.PrimaryNeon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(100.dp).clip(CircleShape).background(PrimaryNeon),
                    contentAlignment = Alignment.Center
                ) {
                    Text("J", style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.onPrimary)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("John Doe", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("+91 9999999999", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            
            ListItem(
                headlineContent = { Text("Edit Profile") },
                leadingContent = { Icon(Icons.Filled.Person, contentDescription = null) },
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.background)
            )
            ListItem(
                headlineContent = { Text("Settings") },
                leadingContent = { Icon(Icons.Filled.Settings, contentDescription = null) },
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.background)
            )
            ListItem(
                headlineContent = { Text("Logout", color = MaterialTheme.colorScheme.error) },
                leadingContent = { Icon(Icons.Filled.ExitToApp, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    }
}
