package dev.jason.app.compose.messenger_app.auth_ui.route

import kotlinx.serialization.Serializable

internal sealed interface AuthRoute {

    @Serializable
    data object LoginScreen : AuthRoute
    @Serializable
    data object SigninScreen : AuthRoute
    @Serializable
    data class LoadingScreen(val text: String) : AuthRoute
}