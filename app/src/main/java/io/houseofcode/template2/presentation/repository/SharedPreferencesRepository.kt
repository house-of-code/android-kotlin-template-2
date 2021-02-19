package io.houseofcode.template2.presentation.repository

import android.content.SharedPreferences
import io.houseofcode.template2.domain.repository.PersistentStorageRepository
import io.houseofcode.template2.presentation.util.get
import io.houseofcode.template2.presentation.util.observe
import io.houseofcode.template2.presentation.util.set
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

class SharedPreferencesRepository(private val sharedPreferences: SharedPreferences): PersistentStorageRepository {

    companion object {
        /**
         * Name of SharedPreferences storage.
         * Reflected in backup_rules.xml.
         */
        const val PREF_PACKAGE_NAME = "io.houseofcode.template2.preferences"
    }

    /**
     * Set value in SharedPreferences.
     */
    override fun setValue(key: String, value: Any) {
        sharedPreferences.set(key, value)
    }

    /**
     * Get value from SharedPreferences.
     */
    override fun <T> getValue(key: String, defaultValue: T): T {
        @Suppress("UNCHECKED_CAST")
        return when (defaultValue) {
            is String? -> sharedPreferences.getString(key, defaultValue) as T
            is Int -> sharedPreferences.getInt(key, defaultValue) as T
            is Long -> sharedPreferences.getLong(key, defaultValue) as T
            is Boolean -> sharedPreferences.getBoolean(key, defaultValue) as T
            is Float -> sharedPreferences.getFloat(key, defaultValue) as T
            is Set<*> -> sharedPreferences.getStringSet(key, defaultValue as Set<String>) as T
            is MutableSet<*> -> sharedPreferences.getStringSet(key, defaultValue as MutableSet<String>) as T
            else -> sharedPreferences.get(key, defaultValue as Any) as T
        }
    }

    /**
     * Observe value from SharedPreferences.
     */
    @ExperimentalCoroutinesApi
    override fun <T> observeValue(key: String, defaultValue: T): Flow<T> {
        @Suppress("UNCHECKED_CAST")
        return when (defaultValue) {
            is String? -> sharedPreferences.observe(key, defaultValue as String) as Flow<T>
            is Int -> sharedPreferences.observe(key, defaultValue as Int) as Flow<T>
            is Long -> sharedPreferences.observe(key, defaultValue as Long) as Flow<T>
            is Boolean -> sharedPreferences.observe(key, defaultValue as Boolean) as Flow<T>
            is Float -> sharedPreferences.observe(key, defaultValue as Float) as Flow<T>
            is Set<*> -> sharedPreferences.observe(key, defaultValue as Set<*>) as Flow<T>
            is MutableSet<*> -> sharedPreferences.observe(key, defaultValue as MutableSet<*>) as Flow<T>
            else -> sharedPreferences.observe(key, defaultValue as Any) as Flow<T>
        }
    }

    /**
     * Clear set of volatile data.
     */
    override fun clearVolatileData(keys: List<String>) {
        return sharedPreferences.edit()
            .also { editor ->
                for (key in keys) {
                    editor.remove(key)
                }
            }
            .apply()
    }
}