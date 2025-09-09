package dev.jason.app.compose.messenger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import dev.jason.app.compose.messenger.ui.model.MessageUi
import dev.jason.app.compose.messenger.ui.model.toUi
import dev.jason.app.compose.messenger.ui.nav.Routes
import dev.jason.app.compose.messenger.ui.screen.*
import dev.jason.app.compose.messenger.ui.theme.MessengerTheme
import dev.jason.app.compose.messenger.ui.viewmodel.ChatViewModel
import dev.jason.app.compose.messenger.ui.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.qualifier.named

class MessengerActivity : ComponentActivity() {

    private val mainViewModel by lazy {
        getViewModel<MainViewModel>(named(MessengerApplication.Qualifier.MAIN_VIEW_MODEL))
    }

    private val chatViewModel by lazy {
        getViewModel<ChatViewModel>(named(MessengerApplication.Qualifier.CHAT_VIEW_MODEL))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MessengerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    val loginUiState by mainViewModel.loginUiState.collectAsStateWithLifecycle()
                    val chatroomUiState by chatViewModel.chatroomUiState.collectAsStateWithLifecycle()
                    val messages by chatViewModel.messages.collectAsStateWithLifecycle()
                    val message by chatViewModel.message.collectAsStateWithLifecycle()
                    val messagesUi = messages.map {
                        MessageUi(
                            id = it.id,
                            chatRoomId = it.chatRoomId,
                            sender = it.sender,
                            text = it.message,
                            timestamp = it.timestamp.toUi()
                        )
                    }

                    val savedPrefs = mainViewModel.savedPrefs
                    var startDestination: Routes = Routes.LoginNavigation

                    if (savedPrefs.user?.username != null) {
                        startDestination = Routes.LoginLoadingScreen
                    }

                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                    ) {
                        navigation<Routes.LoginNavigation>(
                            startDestination = Routes.LoginScreen
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
                                    onLoggedIn = {
                                        navController.navigate(Routes.EnterChatroomScreen) {
                                            popUpTo<Routes.LoginScreen> {
                                                inclusive = true
                                            }
                                        }
                                    },
                                )
                            }

                            composable<Routes.SigninScreen> {
                                SigninScreen(
                                    uiState = loginUiState,
                                    onUsernameChange = mainViewModel::updateUsername,
                                    onPasswordChange = mainViewModel::updatePassword,
                                    onSigninClick = {
                                        mainViewModel.signin()
                                        navController.navigate(Routes.LoginLoadingScreen) {
                                            popUpTo<Routes.SigninScreen> {
                                                inclusive = true
                                            }
                                        }
                                    }
                                )
                            }
                        }

                        composable<Routes.LoginLoadingScreen> {
                            LoadingScreen(
                                text = "Logging in",
                                loaded = loginUiState.isSuccessful,
                                error = loginUiState.isError,
                                onLoaded = {
                                    navController.navigate(Routes.EnterChatroomScreen)
                                },
                                onError = {
                                    navController.navigate(Routes.ErrorScreen)
                                }
                            )
                        }

                        composable<Routes.EnterChatroomScreen> {
                            EnterChatroomScreen(
                                username = loginUiState.username,
                                uiState = chatroomUiState,
                                onChatroomIdChange = chatViewModel::updateChatroomId,
                                onConnectClick = {
                                    chatViewModel.connect()
                                    navController.navigate(Routes.ConnectLoadingScreen)
                                },
                                onLogoutClick = {
                                    mainViewModel.logout()
                                    navController.navigate(Routes.LoginScreen) {
                                        popUpTo<Routes.EnterChatroomScreen> {
                                            inclusive = true
                                        }
                                    }
                                },
                                onBack = {
                                    finish()
                                }
                            )
                        }

                        composable<Routes.ConnectLoadingScreen> {
                            LoadingScreen(
                                text = "Connecting to ${chatroomUiState.chatroomId}",
                                loaded = chatroomUiState.isSuccessful,
                                onLoaded = {
                                    navController.navigate(Routes.MessageScreen)
                                },
                                error = false,
                                onError = { }
                            )
                        }

                        composable<Routes.MessageScreen> {
                            MessagingScreen(
                                messages = messagesUi,
                                chatroomId = chatroomUiState.chatroomId,
                                message = message,
                                onMessageValueChange = chatViewModel::updateMessage,
                                onSend = chatViewModel::sendMessage,
                                onInfoClick = { navController.navigate(Routes.InfoScreen) },
                                onBack = {
                                    chatViewModel.disconnect()
                                    navController.navigate(Routes.EnterChatroomScreen) {
                                        popUpTo<Routes.MessageScreen> {
                                            inclusive = true
                                        }
                                    }
                                }
                            )
                        }

                        composable<Routes.ErrorScreen> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                val msg =
                                    if (loginUiState.error?.error?.localizedMessage?.contains("resolve host") == true) {
                                        "No Internet!"
                                    } else loginUiState.error?.error?.localizedMessage

                                if (msg == null) {
                                    navController.navigateUp()
                                }

                                Text(msg ?: "Unknown error", fontSize = 20.sp)
                            }
                        }

                        composable<Routes.InfoScreen> {
                            InfoScreen(
                                uiState = chatroomUiState,
                                onBackClick = { navController.navigateUp() },
                                onDeleteChatroomClick = {
                                    chatViewModel.deleteChatroom()

                                    navController.navigate(Routes.EnterChatroomScreen) {
                                        popUpTo<Routes.InfoScreen> {
                                            inclusive = true
                                        }
                                    }
                                },
                                onDeleteAccountClick = {
                                    chatViewModel.deleteAccount()

                                    navController.navigate(Routes.LoginNavigation) {
                                        popUpTo<Routes.InfoScreen> {
                                            inclusive = true
                                        }
                                    }
                                },
                                onBack = {
                                    chatViewModel.disconnect()
                                    navController.navigate(Routes.EnterChatroomScreen) {
                                        popUpTo<Routes.InfoScreen> {
                                            inclusive = true
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}