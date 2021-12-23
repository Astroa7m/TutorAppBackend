package com.example.data.models

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class Message(
    val messageText: String,
    val senderName: String,
    val senderId: String,
    val timeStamp: Long,
    @BsonId
    val messageId: String = ObjectId().toString()
)
