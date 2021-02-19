package io.houseofcode.template2.data.model

import kotlinx.serialization.Serializable

/**
 * Token received when logging in.
 */
@Serializable
class LoginToken(val token: String?)