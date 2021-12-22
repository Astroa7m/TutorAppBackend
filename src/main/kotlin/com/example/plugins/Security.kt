package com.example.plugins

import com.example.authentication.JWTService
import com.example.data.DatabaseConnection
import com.example.data.models.user.Tutor
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import org.litote.kmongo.eq

fun Application.configureSecurity() {

    authentication {
        jwt("jwt"){
            verifier(JWTService.verifier)
            realm = "accessing tutor operations"
            validate { credentials ->
                val payload = credentials.payload
                val id = payload.getClaim("id").asString()
                val tutor = DatabaseConnection.tutorCollection.findOne(Tutor::_id eq id)
                tutor
            }
        }
    }
}
