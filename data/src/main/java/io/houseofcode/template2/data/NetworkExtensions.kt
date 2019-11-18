package io.houseofcode.template2.data

import io.houseofcode.template2.domain.model.Resource
import retrofit2.Response

/**
 * Make request and return resource as response.
 * @param T Generic data from request.
 * @return Resource holding a successful response or a failed error.
 */
fun <T> Response<T>.getResponseResource(): Resource<T> {
    // Make sure to catch any exception thrown during request.
    return try {
        // Check if request was successful.
        if (this.isSuccessful) {
            Resource.success(this.body())
        } else {
            // Timber.w("Request was unsuccessful; code: ${response.code()}, url: ${response.raw().request().url()}")
            Resource.error("Request was unsuccessful; code: ${this.code()}, url: ${this.raw().request.url}")
        }
    } catch (error: Exception) {
        // Timber.w("Error during request; message: ${error.localizedMessage}")
        Resource.error("Error during request; message: ${error.localizedMessage}")
    }
}