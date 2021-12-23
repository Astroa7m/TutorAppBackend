package com.example.data.models.user

import io.ktor.auth.*
import kotlinx.serialization.Serializable

@Serializable
data class Tutor(
    val email: String,
    val hashedPassword: String,
    val name: String,
    val modules: List<String>?,
    val profilePic: ByteArray?=null,
    val _id: String?=null
) : Principal
