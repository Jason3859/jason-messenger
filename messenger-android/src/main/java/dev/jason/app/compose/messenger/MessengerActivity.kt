package dev.jason.app.compose.messenger

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import dev.jason.app.compose.messenger.ui.nav.Routes
import dev.jason.app.compose.messenger.ui.screen.EnterChatroomScreen
import dev.jason.app.compose.messenger.ui.screen.LoadingScreen
import dev.jason.app.compose.messenger.ui.screen.LoginScreen
import dev.jason.app.compose.messenger.ui.screen.SigninScreen
import dev.jason.app.compose.messenger.ui.theme.MessengerTheme
import dev.jason.app.compose.messenger.ui.viewmodel.MainViewModel

class MessengerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MessengerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val viewModel = viewModel<MainViewModel>(factory = MessengerApplication.viewModelFactory)
                    val navController = rememberNavController()
                    val loginUiState by viewModel.loginUiState.collectAsStateWithLifecycle()
                    val chatroomUiState by viewModel.chatroomUiState.collectAsStateWithLifecycle()

                    val savedPrefs = viewModel.savedPrefs
                    var startDestination: Routes = Routes.LoginNavigation

                    if (savedPrefs.user?.username != null) {
                        startDestination = Routes.LoginLoadingScreen
                    }

                    NavHost(
                        navController = navController,
                        startDestination = startDestination.also { println(it) },
                    ) {
                        navigation<Routes.LoginNavigation>(
                            startDestination = Routes.LoginScreen
                        ) {
                            composable<Routes.LoginScreen> {
                                LoginScreen(
                                    uiState = loginUiState,
                                    onUsernameChange = viewModel::updateUsername,
                                    onPasswordChange = viewModel::updatePassword,
                                    onLoginClick = {
                                        Toast.makeText(
                                            this@MessengerActivity,
                                            "Logging in",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        viewModel.login()
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
                                    onUsernameChange = viewModel::updateUsername,
                                    onPasswordChange = viewModel::updatePassword,
                                    onSigninClick = viewModel::signin,
                                    onSignedIn = {
                                        viewModel.login()
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
                                onLoaded = {
                                    navController.navigate(Routes.EnterChatroomScreen)
                                },
                            )
                        }

                        composable<Routes.LoadingScreen> {
                            LoadingScreen(
                                loaded = loginUiState.isSuccessful,
                                onLoaded = { navController.navigate(Routes.MessageScreen) }
                            )
                        }

                        composable<Routes.EnterChatroomScreen> {
                            EnterChatroomScreen(
                                username = loginUiState.username,
                                uiState = chatroomUiState,
                                onChatroomIdChange = viewModel::updateChatroomId,
                                onConnectClick = viewModel::connect,
                                onLogoutClick = {
                                    viewModel.logout()
                                    navController.navigateUp()
                                },
                                onConnect = {
                                    navController.navigate(Routes.MessageScreen) {
                                        popUpTo<Routes.EnterChatroomScreen> {
                                            inclusive = true
                                        }
                                    }
                                }
                            )
                        }

                        composable<Routes.MessageScreen> {
                            Text("message")
                        }
                    }
                }
            }
        }
    }
}