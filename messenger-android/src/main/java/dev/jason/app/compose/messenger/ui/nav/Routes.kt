package dev.jason.app.compose.messenger.ui.nav

import kotlinx.serialization.Serializable

sealed interface Routes {
    @Serializable data object LoginNavigation : Routes

    @Serializable data object LoginScreen : Routes
    @Serializable data object SigninScreen : Routes
    @Serializable data object LoginLoadingScreen : Routes
    @Serializable data object InfoScreen : Routes
    @Serializable data object EnterChatroomScreen : Routes
    @Serializable data object ConnectLoadingScreen : Routes
    @Serializable data object MessageScreen : Routes
    @Serializable data object ErrorScreen : Routes
}