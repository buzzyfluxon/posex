/*
 * Copyright (c) Fluxon. All rights reserved.
 */

package com.example

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.CameraScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.data.AppSettings

// Duration/easing chosen to match the push/pop feel of a UINavigationController.
private const val IOS_TRANSITION_DURATION_MS = 320

@Composable
fun AppNavigation(appSettings: AppSettings, navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = "home",
        // iOS-style push: incoming screen slides in from the right while the
        // current one slides slightly left and dims, mirroring UINavigationController.
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(IOS_TRANSITION_DURATION_MS)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(IOS_TRANSITION_DURATION_MS)
            )
        },
        // iOS-style pop: reverse of the push, the previous screen slides back in from the left.
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(IOS_TRANSITION_DURATION_MS)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(IOS_TRANSITION_DURATION_MS)
            )
        }
    ) {
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
