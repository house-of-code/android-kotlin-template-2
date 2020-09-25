package io.houseofcode.template2.presentation.util

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlin.coroutines.CoroutineContext

/**
 * Observe changes of key in SharedPreferences as Flow.
 * @param key Name of preference.
 * @param default Value to return if preference does not exist.
 * @param emitInitialValue Allow initial value of preference to be send.
 * @param dispatcher Context which operation should be executed with.
 */
@ExperimentalCoroutinesApi
inline fun <reified T: Any> SharedPreferences.observe(key: String,
                                                 default: T,
                                                 emitInitialValue: Boolean = true,
                                                 dispatcher: CoroutineContext = Dispatchers.Default): Flow<T> {
    val flow: Flow<T> = channelFlow {
        if (emitInitialValue) {
            // Get value initially.
            offer(get(key, default))
        }

        // Listener for changed SharedPreferences.
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, k ->
            if (key == k) {
                offer(get(key, default))
            }
        }

        // Register listener.
        registerOnSharedPreferenceChangeListener(listener)
        // Unregister listener when channel is closed or canceled.
        awaitClose { unregisterOnSharedPreferenceChangeListener(listener) }
    }
    return flow.flowOn(dispatcher)
}

/**
 * Get single value of key in SharedPreferences.
 * @param key Name of preference.
 * @param default Value to return if preference does not exist.
 */
inline fun <reified T> SharedPreferences.get(key: String, default: T): T {
    @Suppress("UNCHECKED_CAST")
    return when (default) {
        is String? -> getString(key, default) as T
        is Int -> getInt(key, default) as T
        is Long -> getLong(key, default) as T
        is Boolean -> getBoolean(key, default) as T
        is Float -> getFloat(key, default) as T
        is Set<*> -> getStringSet(key, default as Set<String>) as T
        is MutableSet<*> -> getStringSet(key, default as MutableSet<String>) as T
        else -> throw IllegalArgumentException("Generic type cannot be handled: ${T::class.java.name}")
    }
}

/**
 * Set value for key in SharedPreferences.
 * @param key Name of preference.
 * @param value New value for preference.
 * @param commit If true, commit will be used to persist changes, otherwise apply will be used.
 */
inline fun <reified T> SharedPreferences.set(key: String, value: T, commit: Boolean = false) {
    @Suppress("UNCHECKED_CAST")
    when (value) {
        is String -> edit(commit) { putString(key, value) }
        is Int -> edit(commit) { putInt(key, value) }
        is Long -> edit(commit) { putLong(key, value) }
        is Boolean -> edit(commit) { putBoolean(key, value) }
        is Float -> edit(commit) { putFloat(key, value) }
        is Set<*> -> edit(commit) { putStringSet(key, value as? Set<String>) }
        is MutableSet<*> -> edit(commit) { putStringSet(key, value as? MutableSet<String>) }
        else -> throw IllegalArgumentException("Generic type cannot be handled: ${T::class.java.name}")
    }
}
