package com.example.data.models.request

import kotlinx.serialization.Serializable

@Serializable
data class Login(
    val email: String,
    val password: String
)
