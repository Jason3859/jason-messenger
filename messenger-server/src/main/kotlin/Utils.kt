package dev.jason

import java.util.Collections
import java.util.LinkedHashSet

val clients: MutableSet<Connection?> = Collections.synchronizedSet(LinkedHashSet())
val chatRooms: MutableMap<String, MutableSet<Connection>> = mutableMapOf()

fun getChatId(user1: String, user2: String): String {
    return listOf(user1, user2).sorted().joinToString("_")
}
