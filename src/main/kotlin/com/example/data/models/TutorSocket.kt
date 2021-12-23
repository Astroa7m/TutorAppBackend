package com.example.data.models

import io.ktor.http.cio.websocket.*

data class TutorSocket(
    val tutorName: String,
    val tutorId: String,
    val socket: WebSocketSession
)
