package com.example.data

import com.example.data.models.Message
import com.example.data.models.TutorSocket
import com.example.util.CMInstance
import io.ktor.http.cio.websocket.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

class WebSocketService {

    private val tutors = Collections.synchronizedSet<TutorSocket>(linkedSetOf())

    fun onChatJoined(
        tutorSocket: TutorSocket
    ){
        tutors+=tutorSocket
    }

    @OptIn(ExperimentalSerializationApi::class)
    suspend fun sendMessage(messageText: String, senderName: String, senderId: String){
        val message = Message(
            messageText = messageText,
            senderName = senderName,
            senderId = senderId,
            timeStamp = System.currentTimeMillis()
        )
        DatabaseConnection.messagesCollection.insertOne(message)
        CMInstance.sendNotification(senderName, messageText)
        tutors.forEach { tutor ->
            val parsedMessage = Json.encodeToString(message)
            tutor.socket.send(parsedMessage)
        }
    }

    suspend fun disconnect(tutorSocket: TutorSocket, closeReason: CloseReason){
        tutorSocket.socket.close(closeReason)
        if(tutors.contains(tutorSocket))
            tutors-= tutorSocket
    }

}