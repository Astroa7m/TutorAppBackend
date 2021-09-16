package com.example.routes

import com.example.data.DatabaseConnection
import com.example.data.models.response.UserResponse
import com.example.data.models.user.Tutor
import com.example.data.models.user.tutors.student.Student
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.litote.kmongo.eq

private const val STUDENTS = "$API_VERSION/students"
private const val CREATE_STUDENTS_REQUEST = "$STUDENTS/create"
private const val READ_STUDENT_REQUEST = "$STUDENTS/read"
private const val UPDATE_STUDENT_REQUEST = "$STUDENTS/update"
private const val DELETE_STUDENT_REQUEST = "$STUDENTS/delete"

fun Route.studentsRoute(){
    authenticate("jwt"){
        post(CREATE_STUDENTS_REQUEST){
            val student = try{
                call.receive<Student>()
            }catch (e: Exception){
                call.respond(UserResponse(false, message = e.message ?: "Badly written fields or connection error"))
                return@post
            }
            try{
                val tutorId = call.principal<Tutor>()!!._id
                student.studentsTutorId = tutorId!!
                val insertionResult = DatabaseConnection.studentCollection.insertOne(student)
                if(insertionResult.wasAcknowledged())
                    call.respond(UserResponse(true, message = "Student inserted successfully"))
            }catch (e : Exception){
                call.respond(UserResponse(false, message = e.message ?: "Could not insert user"))
            }
        }
        get(READ_STUDENT_REQUEST){
            try{
                val tutorId = call.principal<Tutor>()!!._id
                val tutorsStudent = DatabaseConnection.studentCollection.find(Student::studentsTutorId eq tutorId).toList()
                call.respond(UserResponse(true, studentsList = tutorsStudent))
            }
            catch (e: Exception){
                call.respond(UserResponse(true, message = e.message ?: "Error occurred while getting students"))
                return@get
            }
        }
        put("$UPDATE_STUDENT_REQUEST/{id}"){
            val studentId: String?
            val student = try{
                studentId = call.parameters["id"]
                call.receive<Student>()
            }catch (e: Exception){
                call.respond(UserResponse(false, message = "Badly written fields or connection error"))
                return@put
            }
            try {
                val tutorId = call.principal<Tutor>()!!._id
                student.studentsTutorId = tutorId
                val updateResult = DatabaseConnection.studentCollection.updateOne(Student::_id eq studentId, student)
                if(updateResult.wasAcknowledged())
                    call.respond(UserResponse(true, message = "Update student successfully"))
            }catch (e: Exception){
                call.respond(UserResponse(false, message = e.message ?: "Could not update student due to an error"))
            }
        }
        delete(DELETE_STUDENT_REQUEST){
            val studentId = try{
                call.request.queryParameters["id"]
            }catch (e: Exception){
                call.respond(UserResponse(false, message = e.message ?: "No student id found"))
            }
            try{
                val deletionResult = DatabaseConnection.studentCollection.deleteOne(Student::_id eq studentId)
                if(deletionResult.wasAcknowledged())
                    call.respond(UserResponse(true, message = "Student deleted successfully"))
            }catch (e: Exception){
                call.respond(UserResponse(false, message = e.message ?: "Could not delete student"))

            }
        }
    }
}