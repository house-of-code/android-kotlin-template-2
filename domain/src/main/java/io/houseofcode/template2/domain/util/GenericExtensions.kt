package io.houseofcode.template2.domain.util

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Make deep copy of object.
 * A shallow copy will keep references to the original object.
 */
@Throws(RuntimeException::class)
inline fun <reified T> T.deepCopy(): T {
    val json: String = Json.encodeToString(this)
    return Json.decodeFromString<T>(json)
}
