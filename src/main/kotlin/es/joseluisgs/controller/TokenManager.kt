package es.joseluisgs.controller

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import es.joseluisgs.models.User
import java.util.*

/**
 * JWT verification and generation
 * Lo primero que es configurar parámetros dle token que me ayudarńa a saber si es correcto
 * audience, issuer y realm
 */

object TokenManager {
    private lateinit var audience: String
    private lateinit var secret: String
    private lateinit var issuer: String
    private var expirationDate: Long = 0

    fun init(audience: String, secret: String, issuer: String, expirationDate: Long) {
        TokenManager.audience = audience
        TokenManager.secret = secret
        TokenManager.issuer = issuer
        TokenManager.expirationDate = System.currentTimeMillis() + 1000 * expirationDate
    }

    fun generateJWTToken(user: User): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withSubject("Authentication")
            .withClaim("username", user.username)
            .withClaim("userId", user.id)
            .withExpiresAt(Date(expirationDate))
            .sign(Algorithm.HMAC512(secret))
    }

    fun verifyJWTToken(): JWTVerifier {
        return JWT.require(Algorithm.HMAC512(secret))
            .withAudience(audience)
            .withIssuer(issuer)
            .build()
    }
}
