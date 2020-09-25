package io.houseofcode.template2.data.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Interceptor for authentication.
 * Login token will be added to request as it is executed.
 */
class AuthenticationInterceptor(val getToken: () -> String?): Interceptor {

    companion object {
        // Placeholder for authentication header.
        private const val AUTH_HEADER_KEY = "Authorization"
        private const val AUTH_HEADER_VALUE = "token_bearer"
        const val AUTH_HEADER = "$AUTH_HEADER_KEY: $AUTH_HEADER_VALUE"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        // Look for placeholder authentication header to replace with correct token.
        val placeholderAuthHeader = originalRequest.header(AUTH_HEADER_KEY)
        // Get token.
        val loginToken = getToken()

        // Builder for request we might want to modify.
        val requestBuilder = originalRequest.newBuilder()

        if (placeholderAuthHeader != null) {
            // Remove placeholder authentication header.
            requestBuilder.removeHeader(AUTH_HEADER_KEY)

            if (loginToken != null) {
                // Add authentication token as header.
                requestBuilder.addHeader(AUTH_HEADER_KEY, "Bearer $loginToken")
            } else {
                // Throw error, intercept method can only handle IOException.
                throw IOException("Login token not set on request with required authentication: [${originalRequest.method}] ${originalRequest.url.encodedPath}")
            }
        }

        // Return request.
        return chain.proceed(
            requestBuilder.build()
        )
    }

}