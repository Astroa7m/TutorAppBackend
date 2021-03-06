package com.example.data.models.user.tutors.student

import kotlinx.serialization.Serializable

@Serializable
data class Student(
    val studentName: String?=null,
    val studentYear: Int?=null,
    val studentSubject: String?=null,
    var studentsTutorId: String?=null,
    val studentPic: ByteArray?=null,
    val _id: String?=null
)
