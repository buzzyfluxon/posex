/*
 * Copyright (c) Fluxon. All rights reserved.
 */

package com.example

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.CameraScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.data.AppSettings

@Composable
fun AppNavigation(appSettings: AppSettings, navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onNavigateToCamera = { imageUri ->
                    val route = if (imageUri != null) "camera?imageUri=$imageUri" else "camera"
                    navController.navigate(route)
                },
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }
        composable("camera?imageUri={imageUri}") { backStackEntry ->
            val imageUri = backStackEntry.arguments?.getString("imageUri")
            CameraScreen(
                initialImageUri = imageUri,
                appSettings = appSettings,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToGallery = {
                    navController.popBackStack()
                }
            )
        }
        composable("camera") {
            CameraScreen(
                initialImageUri = null,
                appSettings = appSettings,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToGallery = {
                    navController.popBackStack()
                }
            )
        }
        composable("settings") {
            SettingsScreen(
                appSettings = appSettings,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
