package dev.jason.app.compose.messenger_app.auth_ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dev.jason.app.compose.messenger_app.auth_ui.controller.NavigationController
import dev.jason.app.compose.messenger_app.auth_ui.route.AuthRoute
import dev.jason.app.compose.messenger_app.auth_ui.screen.AuthLoadingScreen
import dev.jason.app.compose.messenger_app.auth_ui.screen.LoginScreen
import dev.jason.app.compose.messenger_app.auth_ui.screen.SigninScreen
import dev.jason.app.compose.messenger_app.auth_ui.viewmodel.AuthViewModel
import org.koin.compose.viewmodel.koinViewModel

@SuppressLint("RestrictedApi")
@Composable
fun AuthNavGraph() {
    val viewModel: AuthViewModel = koinViewModel()
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(true) {
        NavigationController.destination.collect { (route, inclusive) ->
            Log.d("AuthNavGraph", "----------------------------------------------------------------------------------------------------------------")
            navController.navigate(route) {
                if (inclusive) {
                    popUpTo(route) {
                        this.inclusive = true
                    }
                }
            }

            Log.d("AuthNavGraph", "current back stack:")
            navController.currentBackStack.value.forEach { backStackEntry ->
                Log.d("AuthNavGraph", "\t$backStackEntry")
            }
            Log.d("AuthNavGraph", "----------------------------------------------------------------------------------------------------------------")
        }
    }

    Surface {
        NavHost(
            navController = navController,
            startDestination = AuthRoute.LoginScreen
        ) {
            composable<AuthRoute.LoginScreen> {
                LoginScreen(
                    uiState = uiState,
                    updateState = viewModel::updateState,
                    onAction = viewModel::onAction
                )
            }

            composable<AuthRoute.SigninScreen>(
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start,
                        tween(
                            500, easing = LinearOutSlowInEasing
                        )
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.End,
                        tween(
                            500, easing = LinearOutSlowInEasing
                        )
                    )
                }
            ) {
                SigninScreen(
                    uiState = uiState,
                    updateState = viewModel::updateState,
                    onAction = viewModel::onAction
                )
            }

            composable<AuthRoute.LoadingScreen>(
                enterTransition = {
                    expandVertically()
                }
            ) {
                AuthLoadingScreen(
                    text = it.toRoute<AuthRoute.LoadingScreen>().text
                )
            }
        }
    }
}