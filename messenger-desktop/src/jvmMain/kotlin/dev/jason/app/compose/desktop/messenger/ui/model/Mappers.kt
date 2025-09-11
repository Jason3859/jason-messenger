package dev.jason.app.compose.desktop.messenger.ui.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun LocalDateTime.toUi(): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")
    return this.format(formatter)
}