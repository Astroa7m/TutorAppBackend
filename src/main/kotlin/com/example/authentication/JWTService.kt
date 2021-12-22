package com.example.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.example.data.models.user.Tutor

object JWTService {
    private const val issuer = "MyServer"
    private val jwtSecret = System.getenv("JWT_SECRET")
    private val algorithm = Algorithm.HMAC512(jwtSecret)

    val verifier : JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()

    fun generateToken(tutor: Tutor): String = JWT.create()
        .withSubject("notesSubject")
        .withIssuer(issuer)
        .withClaim("id", tutor._id)
        .sign(algorithm)
}