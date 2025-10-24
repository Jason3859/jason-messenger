package dev.jason.app.compose.messenger_app.auth_ui

import dev.jason.app.compose.messenger_app.auth_ui.viewmodel.AuthViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authUiModule = module {
    viewModelOf(::AuthViewModel)
}