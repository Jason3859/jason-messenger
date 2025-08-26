package dev.jason.app.compose.messenger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.jason.app.compose.messenger.ui.nav.Routes
import dev.jason.app.compose.messenger.ui.screen.LoginScreen
import dev.jason.app.compose.messenger.ui.screen.SigninScreen
import dev.jason.app.compose.messenger.ui.theme.MessengerTheme
import dev.jason.app.compose.messenger.ui.viewmodel.MainViewModel
import java.io.File

class MessengerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MessengerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val viewModel = viewModel<MainViewModel>(factory = MessengerApplication.viewModelFactory)
                    val navController = rememberNavController()
                    val loginUiState by viewModel.loginUiState.collectAsStateWithLifecycle()

                    val file = File(getExternalFilesDir(null), "saved_prefs.json")

                    val startDestination: Routes = if (file.exists() && file.readText().isNotBlank()) Routes.MessageScreen else Routes.LoginScreen

                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable<Routes.LoginScreen> {
                            LoginScreen(
                                uiState = loginUiState,
                                onUsernameChange = viewModel::updateUsername,
                                onPasswordChange = viewModel::updatePassword,
                                onLoginClick = viewModel::login,
                                onSigninClick = { navController.navigate(Routes.SigninScreen) },
                            )
                        }

                        composable<Routes.SigninScreen> {
                            SigninScreen(
                                uiState = loginUiState,
                                onUsernameChange = viewModel::updateUsername,
                                onPasswordChange = viewModel::updatePassword,
                                onSigninClick = viewModel::signin,
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