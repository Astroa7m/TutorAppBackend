package com.example.data

import com.example.data.models.user.Tutor
import com.example.data.models.user.tutors.student.Student
import com.mongodb.ConnectionString
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

object DatabaseConnection {
    private val connectionString = System.getenv("MONGO_DB_URI")
    private val client = KMongo.createClient(ConnectionString(connectionString)).coroutine
    private val db = client.getDatabase("tutors")
    val tutorCollection = db.getCollection<Tutor>("tutorInfo")
    val studentCollection = db.getCollection<Student>("studentInfo")
}