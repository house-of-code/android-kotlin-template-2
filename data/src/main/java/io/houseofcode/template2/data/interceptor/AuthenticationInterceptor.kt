package io.houseofcode.template2.data.interceptor

import okhttp3.Interceptor
import okhttp3.Response

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

        return if (placeholderAuthHeader != null && loginToken != null ) {
            // Replace placeholder authentication header.
            chain.proceed(originalRequest.newBuilder()
                .removeHeader(AUTH_HEADER_KEY)
                .addHeader(AUTH_HEADER_KEY, "Bearer $loginToken")
                .build())
        } else {
            // Request does not require authentication, proceed with original request.
            chain.proceed(originalRequest)
        }
    }

}