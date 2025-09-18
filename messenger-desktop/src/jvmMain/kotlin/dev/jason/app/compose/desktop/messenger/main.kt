@file:OptIn(ExperimentalMaterial3Api::class)

package dev.jason.app.compose.desktop.messenger

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.jason.app.compose.desktop.messenger.data.di.MessengerApp
import dev.jason.app.compose.desktop.messenger.ui.model.MessageUi
import dev.jason.app.compose.desktop.messenger.ui.model.toUi
import dev.jason.app.compose.desktop.messenger.ui.nav.Routes
import dev.jason.app.compose.desktop.messenger.ui.screen.EnterRoomIdScreen
import dev.jason.app.compose.desktop.messenger.ui.screen.InfoScreen
import dev.jason.app.compose.desktop.messenger.ui.screen.LoadingScreen
import dev.jason.app.compose.desktop.messenger.ui.screen.LoginScreen
import dev.jason.app.compose.desktop.messenger.ui.screen.MessagingScreen
import dev.jason.app.compose.desktop.messenger.ui.screen.SigninScreen
import dev.jason.app.compose.desktop.messenger.ui.theme.Theme
import dev.jason.app.compose.desktop.messenger.ui.util.SnackbarController
import dev.jason.app.compose.desktop.messenger.ui.viewmodel.MainViewModel
import dev.jason.lib.dynamic_material_theme.DynamicColorTheme
import org.koin.compose.koinInject
import org.koin.core.qualifier.named
import java.net.URI

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

    DynamicColorTheme(
        defaultLightScheme = Theme.lightScheme,
        defaultDarkScheme = Theme.darkScheme
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHost) },
            modifier = Modifier.fillMaxSize()
        ) {

            LaunchedEffect(true) {
                SnackbarController.events.collect { event ->
                    event?.let {
                        snackbarHost.showSnackbar(it)
                    }
                }
            }

            LaunchedEffect(currentDestination) {
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
                        onInfoClick = { navController.navigate(Routes.InfoScreen) },
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

                composable<Routes.InfoScreen> {
                    InfoScreen(
                        uiState = chatroomUiState,
                        onBackClick = { navController.navigateUp() },
                        onDeleteAccountClick = mainViewModel::deleteAccount,
                        onDeleteChatroomClick = mainViewModel::deleteRoom
                    )
                }

                composable<Routes.UpdateScreen> {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            TopAppBar(
                                title = { Text("Not Latest Version!") }
                            )
                        }
                    ) { innerPadding ->
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            Text(
                                text = "The current app you are using is not the latest version.\nPlease download the latest version by clicking the button below.",
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    mainViewModel.openInBrowser(URI("https://github.com/Jason3859/jason-messenger/releases/download/v1.1.0/messenger-desktop-release.msi"))
                                }
                            ) {
                                Text("Get latest version")
                            }
                        }
                    }
                }
            }
        }
    }

}