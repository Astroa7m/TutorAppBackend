package com.example.data

import com.example.data.models.Notification
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

class CMService(
    private val client: HttpClient,
    private val apiKey: String
    ) {

    suspend fun sendNotification(notification: Notification) : Boolean{
        return try {
            client.post<String> {
                url(BASE_NOTIFICATION_URL)
                contentType(ContentType.Application.Json)
                header("Authorization", "Basic $apiKey")
                body = notification
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    companion object {
        const val ONE_SIGNAL_APP_ID = "e9524b1f-1446-4dab-9e48-a3175219bd8c"
        const val BASE_NOTIFICATION_URL = "https://onesignal.com/api/v1/notifications"
    }

}