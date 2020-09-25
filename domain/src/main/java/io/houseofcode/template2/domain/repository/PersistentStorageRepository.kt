package io.houseofcode.template2.domain.repository

import kotlinx.coroutines.flow.Flow

interface PersistentStorageRepository {

    companion object {
        const val PREF_LOGIN_TOKEN = "login_token"
        const val PREF_FIRST_LAUNCH = "first_launch"
    }

    /**
     * Set value on persistent storage by key.
     */
    fun setValue(key: String, value: Any)

    /**
     * Get single value from persistent storage by key.
     */
    fun <T> getValue(key: String, defaultValue: T): T

    /**
     * Observe value from persistent storage by key.
     */
    fun <T> observeValue(key: String, defaultValue: T): Flow<T>

    /**
     * Clear volatile data, such as user data.
     */
    fun clearVolatileData(keys: List<String>)
}