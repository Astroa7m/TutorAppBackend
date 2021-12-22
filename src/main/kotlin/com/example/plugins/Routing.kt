package com.example.plugins

import com.example.routes.studentsRoute
import com.example.routes.tutorRoute
import io.ktor.application.*
import io.ktor.routing.*

fun Application.configureRouting() {
    routing {
        tutorRoute()
        studentsRoute()
    }
}
