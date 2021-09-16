package com.example.plugins

import com.example.authentication.JWTService
import com.example.data.DatabaseConnection
import com.example.data.models.user.Tutor
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import org.litote.kmongo.eq
import kotlin.collections.set

fun Application.configureSecurity() {

    authentication {
        jwt("jwt"){
            verifier(JWTService.verifier)
            realm = "My note server"
            validate { credentials ->
                val payload = credentials.payload
                val id = payload.getClaim("id").asString()
                val tutor = DatabaseConnection.tutorCollection.findOne(Tutor::_id eq id)
                tutor
            }
        }
    }
    data class MySession(val count: Int = 0)
    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }

    routing {
        get("/session/increment") {
            val session = call.sessions.get<MySession>() ?: MySession()
            call.sessions.set(session.copy(count = session.count + 1))
            call.respondText("Counter is ${session.count}. Refresh to increment.")
        }
    }
}
