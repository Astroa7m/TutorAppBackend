package com.example.routes

import com.example.authentication.JWTService.generateToken
import com.example.data.DatabaseConnection
import com.example.data.WebSocketService
import com.example.data.models.Message
import com.example.data.models.TutorSession
import com.example.data.models.TutorSocket
import com.example.data.models.request.Login
import com.example.data.models.request.Register
import com.example.data.models.request.Update
import com.example.data.models.response.UserResponse
import com.example.data.models.user.Tutor
import com.example.util.getUserExistenceResult
import com.example.util.hashPassword
import com.example.util.validateUserInfo
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import org.litote.kmongo.eq

// TODO: 9/15/2021 ADD TUTORS SUBJECT TO THE MODEL
const val API_VERSION = "/v1"
private const val USERS = "$API_VERSION/users"
private const val REGISTER_REQUEST = "$USERS/register"
private const val LOGIN_REQUEST = "$USERS/login"
private const val UPDATE_REQUEST = "$USERS/update"
private const val ALL_MESSAGES = "$USERS/all-messages"
private const val CHAT = "$USERS/chat"

val webSocketService = WebSocketService()

fun Route.tutorRoute() {
    post(REGISTER_REQUEST) {
        //taking tutor registration info from server
        val registeredTutor = try {
            call.receive<Register>()
        } catch (e: Exception) {
            call.respond(UserResponse(false, message = "Badly written fields or connection error"))
            return@post
        }
        try {
            val validationResult = validateUserInfo(registeredTutor)
            /* if the user info entered has any problem a response will will stop the process
            and will notify the user with the problem */
            if (validationResult != null) {
                call.respond(UserResponse(false, message = validationResult))
                return@post
            }
            //if info are valid then create a model and put it into database
            //hashing the password before putting it into the database
            val tutor = Tutor(
                registeredTutor.email, registeredTutor.password.hashPassword(), registeredTutor.name,
                registeredTutor.modules!!,
                registeredTutor.profilePic
            )
            val insertionResult = DatabaseConnection.tutorCollection.insertOne(tutor)
            if (insertionResult.wasAcknowledged()) {
                call.respond(UserResponse(true, tutor, generateToken(tutor)))
            }

        } catch (e: Exception) {
            call.respond(
                UserResponse(
                    success = false,
                    message = e.message ?: "error occurred while inserting information"
                )
            )
        }
    }
    post(LOGIN_REQUEST) {
        val loggedInTutor = try {
            call.receive<Login>()
        } catch (e: Exception) {
            call.respond(UserResponse(false, message = e.message ?: "Badly written fields or connection error"))
            return@post
        }

        try {
            val userExistenceResult = loggedInTutor.email.getUserExistenceResult()
            if (userExistenceResult == null
                || loggedInTutor.password.hashPassword() != userExistenceResult.hashedPassword
            ) {
                call.respond(UserResponse(false, message = "Email or password is incorrect"))
                return@post
            }
            call.respond(UserResponse(true, userExistenceResult, generateToken(userExistenceResult)))
        } catch (e: Exception) {
            call.respond(UserResponse(false, message = e.message ?: "error occurred while logging in"))
        }
    }
    authenticate("jwt") {
        put(UPDATE_REQUEST) {
            val updatedInfo = try {
                call.receive<Update>()
            } catch (e: Exception) {
                call.respond(
                    UserResponse(
                        false,
                        message = e.message ?: "Badly written fields or connection error"
                    )
                )
                return@put
            }
            try {
                val validationResult = validateUserInfo(updatedInfo)
                if (validationResult != null) {
                    call.respond(UserResponse(false, message = "$validationResult - On updating"))
                    return@put
                }
                val tutorEmail = updatedInfo.email ?: call.principal<Tutor>()!!.email
                val tutorHashedPw = updatedInfo.password?.hashPassword() ?: call.principal<Tutor>()!!.hashedPassword
                val tutorName = updatedInfo.name ?: call.principal<Tutor>()!!.name
                val tutorProfilePic = updatedInfo.profilePic ?: call.principal<Tutor>()!!.profilePic
                val tutorId = call.principal<Tutor>()!!._id
                val tutorModules = updatedInfo.modules ?: call.principal<Tutor>()!!.modules

                val updatedTutor = Tutor(tutorEmail, tutorHashedPw, tutorName, tutorModules, tutorProfilePic, tutorId)

                val updateResult = DatabaseConnection.tutorCollection.updateOne(Tutor::_id eq tutorId, updatedTutor)

                if (updateResult.wasAcknowledged()) {
                    if (call.sessions.get<TutorSession>() == null)
                        call.sessions.set(TutorSession(updatedTutor.name, generateSessionId()))
                    call.respond(UserResponse(true, updatedTutor, generateToken(updatedTutor)))
                } else {
                    call.respond(UserResponse(false, message = "Could not update user"))
                }

            } catch (e: Exception) {
                call.respond(UserResponse(false, message = e.message ?: "Could not update user - from catch"))
            }
        }
        webSocket(CHAT) {
            val tutorName = call.principal<Tutor>()!!.name
            val tutorId = call.principal<Tutor>()!!._id!!
            val tutorSocket = TutorSocket(tutorName, tutorId, this)
            try {
                webSocketService.onChatJoined(tutorSocket)
                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        webSocketService.sendMessage(
                            frame.readText(),
                            tutorName,
                            tutorId
                        )
                    }
                }
            } catch (e: Exception) {
                call.respond(UserResponse(false, message = e.message ?: "Could not send message"))
            } finally {
                webSocketService.disconnect(tutorSocket)
            }
        }
        get(ALL_MESSAGES) {
            try {
                val allMessages =
                    DatabaseConnection.messagesCollection.find().ascendingSort(Message::timeStamp).toList()
                call.respond(
                    allMessages
                )
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Could not retrieve messages")
            }
        }
    }
}