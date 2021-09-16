package com.example

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.example.plugins.*

fun main() {
    embeddedServer(Netty, port = System.getenv("PORT").toInt(), host = "localhost") {
        configureSecurity()
        configureRouting()
        configureSerialization()
    }.start(wait = true)
}
