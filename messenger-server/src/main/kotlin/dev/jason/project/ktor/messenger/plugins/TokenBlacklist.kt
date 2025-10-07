
package dev.jason.project.ktor.messenger.plugins

import java.util.concurrent.ConcurrentHashMap

object TokenBlacklist {
    val invalidatedTokens = ConcurrentHashMap.newKeySet<String>()!!
}
