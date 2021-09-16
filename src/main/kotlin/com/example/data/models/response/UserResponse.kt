package com.example.data.models.response

import com.example.data.models.user.Tutor
import com.example.data.models.user.tutors.student.Student

data class UserResponse(
    val success: Boolean,
    val tutorInfo: Tutor?=null,
    val token: String?=null,
    val message: String?=null,
    val studentsList: List<Student>?=null
)
