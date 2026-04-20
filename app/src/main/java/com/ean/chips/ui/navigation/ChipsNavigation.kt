package com.ean.chips.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ean.chips.ui.screens.*

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    
    // Bottom Tabs
    object Home : Screen("home")
    object TaskFeed : Screen("task_feed")
    object Wallet : Screen("wallet")
    object Referral : Screen("referral")
    object Profile : Screen("profile")
    
    // Inner Roots
    object TaskDetail : Screen("task_detail/{taskId}") {
        fun createRoute(taskId: String) = "task_detail/$taskId"
    }
    object Redemption : Screen("redemption")
}

data class BottomNavItem(val screen: Screen, val label: String, val icon: ImageVector)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, "Home", Icons.Filled.Home),
    BottomNavItem(Screen.TaskFeed, "Tasks", Icons.Filled.List),
    BottomNavItem(Screen.Wallet, "Wallet", Icons.Filled.Warning), // Will replace with proper icon later if needed
    BottomNavItem(Screen.Referral, "Referral", Icons.Filled.Share),
    BottomNavItem(Screen.Profile, "Profile", Icons.Filled.AccountCircle)
)

@Composable
fun ChipsNavigation(navController: NavHostController = rememberNavController()) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val isBottomNavRoute = bottomNavItems.any { it.screen.route == currentRoute }

    if (isBottomNavRoute) {
        NavigationSuiteScaffold(
            navigationSuiteItems = {
                bottomNavItems.forEach { item ->
                    item(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentRoute == item.screen.route,
                        onClick = {
                            navController.navigate(item.screen.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        ) {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                NavHostGroup(navController = navController, modifier = Modifier.padding(innerPadding))
            }
        }
    } else {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            NavHostGroup(navController = navController, modifier = Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun NavHostGroup(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(onSplashComplete = {
                navController.navigate(Screen.Onboarding.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(onComplete = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Login.route) {
            AuthScreen(onLoginSuccess = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            })
        }
        
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.TaskFeed.route) {
            TaskFeedScreen(navController = navController)
        }
        composable(Screen.Wallet.route) {
            WalletScreen(navController = navController)
        }
        composable(Screen.Referral.route) {
            ReferralScreen(navController = navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
        
        composable(Screen.TaskDetail.route) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")
            TaskDetailScreen(navController = navController, taskId = taskId ?: "")
        }
        composable(Screen.Redemption.route) {
            RedemptionScreen(navController = navController)
        }
    }
}
