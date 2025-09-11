package dev.jason.app.compose.desktop.messenger.ui.nav

import kotlinx.serialization.Serializable

sealed interface Routes {
    @Serializable data object LoginScreen : Routes
    @Serializable data object LoginLoadingScreen : Routes
    @Serializable data object SigninScreen : Routes
    @Serializable data object SigninLoadingScreen : Routes
    @Serializable data object EnterRoomIdScreen : Routes
}