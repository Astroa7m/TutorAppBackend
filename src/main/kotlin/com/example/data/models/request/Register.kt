package com.example.data.models.request

data class Register(
    val email: String,
    val password: String,
    val name: String,
    val profilePic: ByteArray?=null,
    val _id: String?=null
)
