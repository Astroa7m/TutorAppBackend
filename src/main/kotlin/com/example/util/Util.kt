package com.example.util

import com.example.data.DatabaseConnection
import com.example.data.models.request.Register
import com.example.data.models.request.Update
import com.example.data.models.user.Tutor
import io.ktor.util.*
import org.litote.kmongo.eq
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
// algorithm to be used for creating a hash messaging authentication code (HMAC)
private const val algorithm = "HmacSHA1"
// getting the custom hash key from environment variables
private val hashKey = System.getenv("HASH_SECRET_KEY").toByteArray()
// creating the HMAC to use it in hashing the password
private val hmacKey = SecretKeySpec(hashKey, algorithm)

// hashing the given pw
fun String.hashPassword(): String {
    // creating an instance of MAC
    val hmac = Mac.getInstance(algorithm)
    // init MAC
    hmac.init(hmacKey)
    // creating the hashed string from the byte array provided in a specified charset
    return hex(hmac.doFinal(this.toByteArray(Charsets.UTF_8)))
}

// pattern to check the email address format
private val VALID_EMAIL_ADDRESS_REGEX: Pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)

// checking the email address format
private fun String.validateEmail(): Boolean {
    val matcher: Matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(this)
    return matcher.find()
}

// checking if the pw length
private fun String.isPasswordValid() = this.length < 8

// checking if the email given to register a new tutor already exists
suspend fun String.getUserExistenceResult() = DatabaseConnection.tutorCollection.findOne(Tutor::email eq this)

// validating user info given at register request
suspend fun validateUserInfo(tutorRequest: Register) : String? = when {
    !tutorRequest.email.validateEmail()-> {
        "Email is badly formatted"
    }
    tutorRequest.password.isPasswordValid()->{
        "Password must be 8 or more characters"
    }
    tutorRequest.email.getUserExistenceResult()!=null -> {
        "Email is already in use"
    }
    else -> null
}

fun validateUserInfo(tutorUpdateRequest: Update) : String? {
        if(tutorUpdateRequest.email!=null&&!tutorUpdateRequest.email.validateEmail())
            return "Email is badly formatted"
        if(tutorUpdateRequest.password!=null&&tutorUpdateRequest.password.isPasswordValid())
            return  "Password must be 8 or more characters"
    return null
}

