package com.example.data.models.user

import io.ktor.auth.*

data class Tutor(
    val email: String,
    val hashedPassword: String,
    val name: String,
    val profilePic: ByteArray?=null,
    val _id: String?=null
) : Principal
