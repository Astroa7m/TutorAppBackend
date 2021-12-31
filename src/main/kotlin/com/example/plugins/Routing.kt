package com.example.plugins

import com.example.routes.*
import io.ktor.application.*
import io.ktor.routing.*

fun Application.configureRouting() {
    routing {
        registerTutor()
        loginTutor()
        updateTutor()
        chatWithTutors()
        getAllMessages()
        sendNotification()

        addStudent()
        getStudents()
        updateStudent()
        deleteStudent()
    }
}
