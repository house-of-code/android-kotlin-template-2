package io.houseofcode.template2.domain.model

import kotlinx.serialization.Serializable

/**
 * Credentials used when logging in.
 */
@Serializable
data class LoginCredentials(
    val login: String,
    val password: String
)