package com.example.data.models.request

data class Update(
    val email: String?=null,
    val password: String?=null,
    val name: String?=null,
    val profilePicUrl: String?=null,
    val _id: String?=null
)
