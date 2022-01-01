package com.example.data.models

import kotlinx.serialization.Serializable


@Serializable
data class NotificationMessage(
    val senderName: String,
    val message: String
)
