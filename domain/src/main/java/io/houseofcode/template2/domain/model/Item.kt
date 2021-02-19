package io.houseofcode.template2.domain.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

/**
 * Example model class.
 */
@Serializable
data class Item(
    val id: String,
    val title: String,
    @SerialName("created_at")
    @Contextual
    val createdAt: Date
)