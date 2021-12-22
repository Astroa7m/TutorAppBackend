package com.example.data

import com.example.data.models.Message
import com.example.data.models.TutorSocket
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
    suspend fun sendMessage(messageText: String, sender: String){
        val message = Message(
            messageText = messageText,
            sender = sender,
            timeStamp = System.currentTimeMillis()
        )
        DatabaseConnection.messagesCollection.insertOne(message)
        tutors.forEach { tutor ->
            val parsedMessage = Json.encodeToString(message)
            tutor.socket.send(Frame.Text(parsedMessage))
        }
    }

    suspend fun disconnect(tutorSocket: TutorSocket){
        tutorSocket.socket.close()
        if(tutors.contains(tutorSocket))
            tutors-= tutorSocket
    }

}