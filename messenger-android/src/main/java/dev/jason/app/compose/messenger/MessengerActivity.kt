package dev.jason.app.compose.messenger

import android.os.Bundle
import android.widget.Toast
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
import dev.jason.app.compose.messenger.ui.MessageUi
import dev.jason.app.compose.messenger.ui.nav.Routes
import dev.jason.app.compose.messenger.ui.screen.*
import dev.jason.app.compose.messenger.ui.theme.MessengerTheme
import dev.jason.app.compose.messenger.ui.viewmodel.ChatViewModel
import dev.jason.app.compose.messenger.ui.viewmodel.MainViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.qualifier.named
import java.net.UnknownHostException
import java.time.ZoneId

class MessengerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MessengerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val mainViewModel = koinViewModel<MainViewModel>(named(MessengerApplication.Qualifier.MAIN_VIEW_MODEL))
                    val chatViewModel = koinViewModel<ChatViewModel>(named(MessengerApplication.Qualifier.CHAT_VIEW_MODEL))
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
                            timestamp = "${it.timestamp.atZone(ZoneId.systemDefault()).hour}:${it.timestamp.atZone(ZoneId.systemDefault()).minute}"
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
                                        Toast.makeText(
                                            this@MessengerActivity,
                                            "Logging in",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        mainViewModel.login()
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
                                    onSigninClick = mainViewModel::signin,
                                    onSignedIn = {
                                        mainViewModel.login()
                                        navController.navigate(Routes.EnterChatroomScreen) {
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
                                onConnectClick = chatViewModel::connect,
                                onLogoutClick = {
                                    mainViewModel.logout()
                                    navController.navigateUp()
                                },
                                onConnect = {
                                    navController.navigate(Routes.MessageScreen) {
                                        popUpTo<Routes.LoginLoadingScreen> {
                                            inclusive = true
                                        }
                                    }
                                },
                                onBack = {
                                    finish()
                                }
                            )
                        }

                        composable<Routes.MessageScreen> {
                            MessagingScreen(
                                messages = messagesUi,
                                chatroomId = chatroomUiState.chatroomId,
                                message = message,
                                onMessageValueChange = chatViewModel::updateMessage,
                                onSend = chatViewModel::sendMessage,
                                onInfoClick = { navController.navigate(Routes.InfoScreen) }
                            )
                        }

                        composable<Routes.ErrorScreen> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                val msg = if (loginUiState.error?.error?.cause is UnknownHostException) {
                                    "No Internet!"
                                } else loginUiState.error?.error?.localizedMessage

                                Text(msg ?: "Unknown error", fontSize = 20.sp)
                            }
                        }

                        composable<Routes.InfoScreen> {
                            Text("Todo")
                        }
                    }
                }
            }
        }
    }
}