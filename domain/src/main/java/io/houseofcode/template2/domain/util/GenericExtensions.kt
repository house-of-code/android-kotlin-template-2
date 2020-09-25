package io.houseofcode.template2.domain.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Make deep copy of object.
 * A shallow copy can contain references to the original object, and in some cases we need to make
 * sure to create a completely new object with cloned values.
 * Using Gson we create a deep copy of all values, and avoid keeping references to the original
 * object.
 */
inline fun <reified T> T.deepCopy(): T {
    val gson = Gson()
    return Gson().fromJson(gson.toJson(this), object : TypeToken<T>() {}.type)
}
