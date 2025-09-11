package dev.jason.app.compose.desktop.messenger

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.jason.app.compose.desktop.messenger.data.di.MessengerApp
import dev.jason.app.compose.desktop.messenger.ui.model.MessageUi
import dev.jason.app.compose.desktop.messenger.ui.model.toUi
import dev.jason.app.compose.desktop.messenger.ui.nav.Routes
import dev.jason.app.compose.desktop.messenger.ui.screen.*
import dev.jason.app.compose.desktop.messenger.ui.util.SnackbarController
import dev.jason.app.compose.desktop.messenger.ui.viewmodel.MainViewModel
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

fun main() = application {
    MessengerApp.initKoin()
    Window(
        onCloseRequest = ::exitApplication,
        title = "Messenger",
    ) {
        App()
    }
}

@Composable
fun App() {
    val mainViewModel = koinInject<MainViewModel>(named(MessengerApp.Qualifier.MAIN_VIEW_MODEL))
    val loginUiState by mainViewModel.loginUiState.collectAsState()
    val chatroomUiState by mainViewModel.chatroomUiState.collectAsState()
    val startDestination by mainViewModel.startDestination.collectAsState()
    val currentDestination by mainViewModel.currentDestination.collectAsState()
    val message by mainViewModel.message.collectAsState()
    val messages by mainViewModel.messages.collectAsState()
    val navController = rememberNavController()
    val snackbarHost = remember { SnackbarHostState() }
    val messagesUi = messages.map { (id, chatRoomId, sender, message, timestamp) ->
        MessageUi(
            id = id,
            chatRoomId = chatRoomId,
            sender = sender,
            text = message,
            timestamp = timestamp.toUi()
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHost) }
    ) {

        LaunchedEffect(true) {
            SnackbarController.events.collect { event ->
                event?.let {
                    snackbarHost.showSnackbar(it)
                }
            }
        }

        LaunchedEffect(currentDestination) {
            println("navigating to $currentDestination")
            navController.navigate(currentDestination)
        }

        NavHost(
            startDestination = startDestination,
            navController = navController
        ) {
            composable<Routes.LoginScreen> {
                LoginScreen(
                    uiState = loginUiState,
                    onUsernameChange = mainViewModel::updateUsername,
                    onPasswordChange = mainViewModel::updatePassword,
                    onLoginClick = {
                        mainViewModel.login()
                        navController.navigate(Routes.LoginLoadingScreen)
                    },
                    onSigninClick = { navController.navigate(Routes.SigninScreen) },
                    onLoggedIn = { }
                )
            }

            composable<Routes.LoginLoadingScreen> {
                LoadingScreen(
                    text = "Logging in"
                )
            }

            composable<Routes.SigninScreen> {
                SigninScreen(
                    uiState = loginUiState,
                    onUsernameChange = mainViewModel::updateUsername,
                    onPasswordChange = mainViewModel::updatePassword,
                    onSigninClick = {
                        mainViewModel.signin()
                        navController.navigate(Routes.SigninLoadingScreen)
                    },
                    onBack = {
                        navController.navigateUp()
                    }
                )
            }

            composable<Routes.SigninLoadingScreen> {
                LoadingScreen(
                    text = "Signing in"
                )
            }

            composable<Routes.EnterRoomIdScreen> {
                EnterRoomIdScreen(
                    username = loginUiState.username,
                    uiState = chatroomUiState,
                    onChatroomIdChange = mainViewModel::updateRoomId,
                    onConnectClick = {
                        mainViewModel.connect()
                        navController.navigate(Routes.ConnectLoadingScreen)
                    },
                    onLogoutClick = mainViewModel::logout
                )
            }
            
            composable<Routes.ConnectLoadingScreen> {
                LoadingScreen(
                    text = "Connecting"
                )
            }

            composable<Routes.MessagingScreen> {
                MessagingScreen(
                    messages = messagesUi,
                    chatroomId = chatroomUiState.roomId,
                    message = message,
                    onMessageValueChange = mainViewModel::updateMessage,
                    onSend = mainViewModel::sendMessage,
                    onInfoClick = {  },
                    onDisconnectClick = {
                        mainViewModel.disconnect()
                        navController.navigate(Routes.EnterRoomIdScreen) {
                            popUpTo<Routes.MessagingScreen> {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }
    }

}