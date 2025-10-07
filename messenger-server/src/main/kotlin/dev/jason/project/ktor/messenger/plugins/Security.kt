package dev.jason.project.ktor.messenger.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.request.header
import io.ktor.server.response.respond
import kotlinx.serialization.Serializable

@Serializable
data class UnauthorizedResponse(val message: String)

fun Application.configureSecurity() {
    val secret = System.getenv("JWT_SECRET")
    val issuer = System.getenv("JWT_ISSUER")
    val audience = System.getenv("JWT_AUDIENCE")
    val myRealm = System.getenv("JWT_REALM")

    install(Authentication) {
        jwt("auth-jwt") {
            realm = myRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(secret))
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .build()
            )
            validate { credential ->
                val token = this.request.header("Authorization")?.removePrefix("Bearer ")
                if (token != null && TokenBlacklist.invalidatedTokens.contains(token)) {
                    return@validate null
                }

                if (credential.payload.getClaim("username").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, UnauthorizedResponse("Token is not valid or has expired"))
            }
        }
    }
}
