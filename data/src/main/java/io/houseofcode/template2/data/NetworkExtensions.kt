package io.houseofcode.template2.data

import io.houseofcode.template2.domain.model.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Response

/**
 * Execute request safely while catching any thrown exception or error.
 * @param request Request that can throw exceptions when executed.
 * @param onSuccess Callback for successful response.
 * @param onFailed Callback for failed/error response.
 * @param onException Callback for caught exceptions.
 */
suspend fun <T, R> executeSafely(
    request: suspend () -> Response<T>,
    onSuccess: (T?) -> Resource<R>,
    onFailed: (errorMessage: String?, T?) -> Resource<R>,
    onException: (error: Exception) -> Resource<R>
): Resource<R> {
    return try {
        // Perform request.
        val response = request()
        // Check if request was successful.
        if (response.isSuccessful) {
            // Return response body.
            onSuccess(response.body())
        } else {
            // Get message from error response without blocking main thread.
            @Suppress("BlockingMethodInNonBlockingContext")
            val errorMessage = withContext(Dispatchers.IO) {
                // Get JSON error response.
                response.errorBody()?.string()?.let { rawJsonError ->
                    // Extract error message from error response.
                    // This needs to be adjusted to how each API handles error messages, if any.
                    JSONObject(rawJsonError).getString("message")
                }
            }
            // Return error message.
            onFailed(errorMessage ?: "Unsuccessful network request", response.body())
        }
    } catch (error: Exception) {
        // Return caught exception, e.g. from interceptors.
        onException(error)
    }
}
