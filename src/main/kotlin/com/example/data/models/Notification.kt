package com.example.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    @SerialName("included_segments")
    val includedSegments: List<String>,
    val content: NotificationContent,
    val header: NotificationContent,
    @SerialName("app_id")
    val appId: String
)
