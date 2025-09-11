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
import dev.jason.app.compose.desktop.messenger.ui.nav.Routes
import dev.jason.app.compose.desktop.messenger.ui.screen.EnterRoomIdScreen
import dev.jason.app.compose.desktop.messenger.ui.screen.LoadingScreen
import dev.jason.app.compose.desktop.messenger.ui.screen.LoginScreen
import dev.jason.app.compose.desktop.messenger.ui.screen.SigninScreen
import dev.jason.app.compose.desktop.messenger.ui.util.SnackbarController
import dev.jason.app.compose.desktop.messenger.ui.viewmodel.ChatViewModel
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
    val chatViewModel = koinInject<ChatViewModel>(named(MessengerApp.Qualifier.CHAT_VIEW_MODEL))
    val loginUiState by mainViewModel.loginUiState.collectAsState()
    val chatroomUiState by chatViewModel.chatroomUiState.collectAsState()
    val startDestination by mainViewModel.startDestination.collectAsState()
    val navController = rememberNavController()
    val snackbarHost = remember { SnackbarHostState() }

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
                    text = "Logging in",
                    loaded = loginUiState.isSuccessful,
                    onLoaded = { println("logged in") },
                    error = loginUiState.isError,
                    onError = { navController.navigateUp() }
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
                    text = "Signing in",
                    loaded = loginUiState.isSuccessful,
                    onLoaded = { println("signed in") },
                    error = loginUiState.isError,
                    onError = { navController.navigateUp() }
                )
            }

            composable<Routes.EnterRoomIdScreen> {
                EnterRoomIdScreen(
                    username = loginUiState.username,
                    uiState = chatroomUiState,
                    onChatroomIdChange = chatViewModel::updateRoomId,
                    onConnectClick = chatViewModel::connect,
                    onLogoutClick = {  }
                )
            }
        }
    }

}