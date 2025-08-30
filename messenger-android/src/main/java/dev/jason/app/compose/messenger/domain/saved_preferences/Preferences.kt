package dev.jason.app.compose.messenger.domain.saved_preferences

import dev.jason.app.compose.messenger.domain.api.User

data class Preferences(
    val user: User?,
)