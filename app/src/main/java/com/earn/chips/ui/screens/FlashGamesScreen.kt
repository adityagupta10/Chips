package com.earn.chips.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.earn.chips.ui.navigation.Screen
import com.earn.chips.ui.theme.PrimaryNeon

data class FlashGame(
    val title: String,
    val url: String
)

data class GameCategory(
    val name: String,
    val games: List<FlashGame>
)

val gameZipperCategories = listOf(
    GameCategory("🎯 Arcade & Classic", listOf(
        FlashGame("Snake", "https://gamezipper.com/snake/"),
        FlashGame("2048", "https://gamezipper.com/2048/"),
        FlashGame("Slope", "https://gamezipper.com/slope/"),
        FlashGame("Brick Breaker", "https://gamezipper.com/brick-breaker/"),
        FlashGame("Pong", "https://gamezipper.com/pong/"),
        FlashGame("Flappy Wings", "https://gamezipper.com/flappy-wings/"),
        FlashGame("T-Rex", "https://gamezipper.com/t-rex/"),
        FlashGame("Stacker", "https://gamezipper.com/stacker/"),
        FlashGame("Alien Whack", "https://gamezipper.com/alien-whack/"),
        FlashGame("Ball Catch", "https://gamezipper.com/ball-catch/"),
        FlashGame("Bounce Bot", "https://gamezipper.com/bounce-bot/"),
        FlashGame("Neon Run", "https://gamezipper.com/neon-run/")
    )),
    GameCategory("🧩 Puzzle & Logic", listOf(
        FlashGame("Sudoku", "https://gamezipper.com/sudoku/"),
        FlashGame("Minesweeper", "https://gamezipper.com/minesweeper/"),
        FlashGame("Memory Match", "https://gamezipper.com/memory-match/"),
        FlashGame("Color Sort", "https://gamezipper.com/color-sort/"),
        FlashGame("Word Puzzle", "https://gamezipper.com/word-puzzle/"),
        FlashGame("Wood Block Puzzle", "https://gamezipper.com/wood-block-puzzle/"),
        FlashGame("Crossword", "https://gamezipper.com/crossword/"),
        FlashGame("Glyph Quest", "https://gamezipper.com/glyph-quest/")
    )),
    GameCategory("♟️ Strategy & Board", listOf(
        FlashGame("Chess", "https://gamezipper.com/chess/"),
        FlashGame("Sushi Stack", "https://gamezipper.com/sushi-stack/"),
        FlashGame("Tetris", "https://gamezipper.com/tetris/"),
        FlashGame("Bolt Jam 3D", "https://gamezipper.com/bolt-jam-3d/")
    )),
    GameCategory("🚀 Action & Reflex", listOf(
        FlashGame("Basketball Shoot", "https://gamezipper.com/basketball-shoot/"),
        FlashGame("Whack A Mole", "https://gamezipper.com/whack-a-mole/"),
        FlashGame("Reaction Time", "https://gamezipper.com/reaction-time/"),
        FlashGame("Catch Turkey", "https://gamezipper.com/catch-turkey/"),
        FlashGame("Phantom Blade", "https://gamezipper.com/phantom-blade/")
    )),
    GameCategory("🎨 Creative & Casual", listOf(
        FlashGame("Kitty Cafe", "https://gamezipper.com/kitty-cafe/"),
        FlashGame("Paint Splash", "https://gamezipper.com/paint-splash/"),
        FlashGame("Dessert Blast", "https://gamezipper.com/dessert-blast/"),
        FlashGame("Ocean Gem Pop", "https://gamezipper.com/ocean-gem-pop/"),
        FlashGame("Abyss Chef", "https://gamezipper.com/abyss-chef/"),
        FlashGame("Cloud Sheep", "https://gamezipper.com/cloud-sheep/"),
        FlashGame("Idle Clicker", "https://gamezipper.com/idle-clicker/"),
        FlashGame("Typing Speed", "https://gamezipper.com/typing-speed/")
    ))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashGamesScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Flash Games", fontWeight = FontWeight.Black) },
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
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            gameZipperCategories.forEach { category ->
                item {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = PrimaryNeon,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                
                items(category.games.chunked(2)) { rowGames ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowGames.forEach { game ->
                            FlashGameButton(
                                title = game.title,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    navController.navigate(Screen.GamePlayer.createRoute(game.url, game.title))
                                }
                            )
                        }
                        if (rowGames.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FlashGameButton(title: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(60.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF1E1E1E),
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            maxLines = 2
        )
    }
}
