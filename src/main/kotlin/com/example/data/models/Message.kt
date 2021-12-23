package com.example.data.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Message(
    val messageText: String,
    val senderName: String,
    val senderId: String,
    val timeStamp: Long,
    @BsonId
    val messageId: String = ObjectId().toString()
)
