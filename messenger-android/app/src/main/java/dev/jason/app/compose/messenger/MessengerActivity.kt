package dev.jason.app.compose.messenger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.jason.app.compose.messenger.ui.theme.MessengerTheme
import dev.jason.app.compose.messenger_app.auth_ui.AuthNavGraph

class MessengerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MessengerTheme {
                AuthNavGraph ()
            }
        }
    }
}