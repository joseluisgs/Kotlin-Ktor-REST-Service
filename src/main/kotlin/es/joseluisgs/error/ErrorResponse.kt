package es.joseluisgs.error

import kotlinx.serialization.Serializable

/**
 * Class representing an error response
 */
@Serializable
data class ErrorResponse(val code: Int, val message: String)