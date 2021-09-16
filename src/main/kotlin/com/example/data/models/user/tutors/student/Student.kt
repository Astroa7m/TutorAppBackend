package com.example.data.models.user.tutors.student

data class Student(
    val studentName: String?=null,
    val studentYear: Int?=null,
    val studentSubject: String?=null,
    var studentsTutorId: String?=null,
    val studentPicUrl: String?=null,
    val _id: String?=null
)
