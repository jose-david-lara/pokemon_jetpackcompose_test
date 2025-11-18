package com.chelo.pokemon.core.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.chelo.pokemon.feature.home.ui.HomeScreen

sealed class ScreenRoute(val route: String){
    object Home : ScreenRoute("home_screen")
}

private fun defaultEnterTransition() =
    slideIn(initialOffset = { IntOffset(it.width, 0) }, animationSpec = tween(500))

private fun defaultExitTransition() =
    slideOut(targetOffset = { IntOffset(-it.width, 0) }, animationSpec = tween(500))

private fun defaultPopEnterTransition() =
    slideIn(initialOffset = { IntOffset(-it.width, 0) }, animationSpec = tween(500))

private fun defaultPopExitTransition() =
    slideOut(targetOffset = { IntOffset(it.width, 0) }, animationSpec = tween(500))

private fun NavGraphBuilder.animatedComposable(
    route: String,
    content: @Composable () -> Unit
) {
    composable(
        route = route,
        enterTransition = { defaultEnterTransition() },
        exitTransition = { defaultExitTransition() },
        popEnterTransition = { defaultPopEnterTransition() },
        popExitTransition = { defaultPopExitTransition() },
        content = { content() }
    )
}

@Composable
fun AppNavigation(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = ScreenRoute.Home.route
    ) {

        animatedComposable(ScreenRoute.Home.route) {
            HomeScreen()
        }



    }
}