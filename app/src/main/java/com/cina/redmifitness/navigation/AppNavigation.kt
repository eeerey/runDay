package com.cina.redmifitness.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import com.cina.redmifitness.feature.history.HistoryScreen
import com.cina.redmifitness.feature.olahraga.OlahragaScreen
import com.cina.redmifitness.feature.profile.ProfileScreen
import com.cina.redmifitness.feature.smartwatch.SmartwatchScreen
import com.cina.redmifitness.feature.olahraga.RouteRecommendationScreen

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: @Composable () -> Unit
)

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    val bottomNavItems = listOf(
        BottomNavItem(
            route = "olahraga",
            label = "Olahraga",
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Olahraga"
                )
            }
        ),
        BottomNavItem(
            route = "history",
            label = "History",
            icon = {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "History"
                )
            }
        ),
        BottomNavItem(
            route = "smartwatch",
            label = "Smartwatch",
            icon = {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Smartwatch"
                )
            }
        ),
        BottomNavItem(
            route = "profile",
            label = "Profile",
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile"
                )
            }
        )
    )

    Scaffold(
        bottomBar = {
            NavigationBar {

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { item ->

                    NavigationBarItem(
                        selected = currentDestination
                            ?.hierarchy
                            ?.any { it.route == item.route } == true,

                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo("olahraga") {
                                    saveState = true
                                }

                                launchSingleTop = true
                                restoreState = true
                            }
                        },

                        icon = item.icon,

                        label = {
                            Text(item.label)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "olahraga",
            modifier = Modifier.padding(innerPadding)
        ) {

            composable("olahraga") {
                OlahragaScreen(
                    onCariRute = { activity, level, distance, latitude, longitude ->
                        navController.navigate(
                            "route_recommendation/$activity/$level/$distance/$latitude/$longitude"
                        )
                    }
                )
            }

            composable(
                route = "route_recommendation/{activity}/{level}/{distance}/{latitude}/{longitude}"
            ) { backStackEntry ->

                val activity =
                    backStackEntry.arguments?.getString("activity") ?: "Lari"

                val level =
                    backStackEntry.arguments?.getString("level") ?: "Sedang"

                val distance =
                    backStackEntry.arguments?.getString("distance") ?: "5 km"

                val latitude =
                    backStackEntry.arguments
                        ?.getString("latitude")
                        ?.toDoubleOrNull()

                val longitude =
                    backStackEntry.arguments
                        ?.getString("longitude")
                        ?.toDoubleOrNull()

                RouteRecommendationScreen(
                    activity = activity,
                    level = level,
                    distance = distance,
                    latitude = latitude,
                    longitude = longitude
                )
            }

            composable("history") {
                HistoryScreen()
            }

            composable("smartwatch") {
                SmartwatchScreen()
            }

            composable("profile") {
                ProfileScreen()
            }
        }
    }
}