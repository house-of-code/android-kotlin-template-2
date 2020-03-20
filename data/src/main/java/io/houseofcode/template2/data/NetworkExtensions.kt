package io.houseofcode.template2.data

import io.houseofcode.template2.domain.model.Resource
import org.json.JSONObject
import retrofit2.Response

/**
 * Transform response wrapper to resource.
 * 3 higher-order functions are provided for transforming response:
 * - Successful request: Transform original response
 * - Failed request: Transform response with returned error message
 * - Thrown exception: Transform response with caught exception
 */
fun <T, R> Response<T>.getResponseResource(
        onSuccess: (T?) -> Resource<R>,
        onFailed: (errorMessage: String?, T?) -> Resource<R>,
        onException: (error: Exception, T?) -> Resource<R>
): Resource<R> {
    // Make sure to catch any exception thrown during request.
    return try {
        // Check if request was successful.
        if (this.isSuccessful) {
            // Return response body.
            onSuccess(this.body())
        } else {
            // Get message from error response.
            var errorMessage: String? = null
            this.errorBody()?.string()?.let { rawErrorBody ->
                errorMessage = JSONObject(rawErrorBody).getString("message")
            }
            onFailed(errorMessage ?: "Fejl", null)
        }
    } catch (error: Exception) {
        onException(error, this.body())
    }
}