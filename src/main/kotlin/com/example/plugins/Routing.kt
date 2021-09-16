package com.example.plugins

import com.example.routes.studentsRoute
import com.example.routes.tutorRoute
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*

fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        tutorRoute()
        studentsRoute()
    }
}
