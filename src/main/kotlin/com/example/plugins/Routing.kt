package com.example.plugins

import com.example.routes.*
import io.ktor.application.*
import io.ktor.routing.*

fun Application.configureRouting() {
    routing {
        sendNotification()
        registerTutor()
        loginTutor()
        updateTutor()
        chatWithTutors()
        getAllMessages()

        addStudent()
        getStudents()
        updateStudent()
        deleteStudent()
    }
}
