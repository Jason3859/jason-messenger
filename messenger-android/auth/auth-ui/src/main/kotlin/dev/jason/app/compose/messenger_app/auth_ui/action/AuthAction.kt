package dev.jason.app.compose.messenger_app.auth_ui.action

sealed interface AuthAction {

    data object LoginAction : AuthAction
    data object SigninAction : AuthAction
}