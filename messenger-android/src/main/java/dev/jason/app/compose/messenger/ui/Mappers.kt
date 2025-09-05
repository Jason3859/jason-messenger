package dev.jason.app.compose.messenger.ui

import java.time.LocalDateTime
import java.time.ZoneId

fun LocalDateTime.toUi() = "${this.atZone(ZoneId.systemDefault()).hour}:${this.atZone(ZoneId.systemDefault()).minute}"