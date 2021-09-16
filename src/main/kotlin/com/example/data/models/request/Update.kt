package com.example.data.models.request

data class Update(
    val email: String?=null,
    val password: String?=null,
    val name: String?=null,
    val modules: List<String>?=null,
    val profilePic: ByteArray?=null,
    val _id: String?=null
)
