package com.earn.chips.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.earn.chips.ui.navigation.Screen
import com.earn.chips.ui.theme.AccentGold
import com.earn.chips.ui.theme.PrimaryNeon

data class MiniGame(
    val id: String,
    val title: String,
    val imageUrl: String,
    val gameUrl: String,
    val category: String
)

val featuredGames = listOf(
    MiniGame("1", "Knife Rain", "https://img.gamedistribution.com/71239c046e7b419889814421689255ec-512x512.jpeg", "https://html5.gamedistribution.com/71239c046e7b419889814421689255ec/", "Action"),
    MiniGame("2", "Bubble Shooter", "https://img.gamedistribution.com/58c3a9d0944047a08b335f606a2542a1-512x512.jpeg", "https://html5.gamedistribution.com/58c3a9d0944047a08b335f606a2542a1/", "Puzzle"),
    MiniGame("3", "Traffic Tom", "https://img.gamedistribution.com/3932822d05f3408f909166f228d447d2-512x512.jpeg", "https://html5.gamedistribution.com/3932822d05f3408f909166f228d447d2/", "Racing"),
    MiniGame("4", "Tower Crash", "https://img.gamedistribution.com/0046522c069b4e1a8e974e6f497a61d6-512x512.jpeg", "https://html5.gamedistribution.com/0046522c069b4e1a8e974e6f497a61d6/", "Casual"),
    MiniGame("5", "Penalty Challenge", "https://img.gamedistribution.com/350c3848b030430095874254425a8128-512x512.jpeg", "https://html5.gamedistribution.com/350c3848b030430095874254425a8128/", "Sports"),
    MiniGame("6", "Merge Cakes", "https://img.gamedistribution.com/73c4f74d47d4469796e625a66699a7c3-512x512.jpeg", "https://html5.gamedistribution.com/73c4f74d47d4469796e625a66699a7c3/", "Strategy")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Instant Games", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Text(
                "Play & Have Fun",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(featuredGames) { game ->
                    GameCard(game) {
                        navController.navigate(Screen.GamePlayer.createRoute(game.gameUrl, game.title))
                    }
                }
            }
        }
    }
}

@Composable
fun GameCard(game: MiniGame, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column {
            AsyncImage(
                model = game.imageUrl,
                contentDescription = game.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    game.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    color = Color.White
                )
                Text(
                    game.category,
                    fontSize = 12.sp,
                    color = PrimaryNeon,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
