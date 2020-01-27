package io.houseofcode.template2.data.interceptor

import io.houseofcode.template2.domain.model.LoginToken
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor for authentication.
 * - Check for expired JWT.
 * - Add authentication header to request.
 *
 */
class AuthenticationInterceptor(val getToken: () -> LoginToken?): Interceptor {

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

        return if (placeholderAuthHeader != null) {
            // Request requires authentication, get token.
            val loginToken = getToken()

            // Replace placeholder authentication header.
            chain.proceed(originalRequest.newBuilder()
                .removeHeader(AUTH_HEADER_KEY)
                .addHeader(AUTH_HEADER_KEY, "Bearer ${loginToken?.token}")
                .build())
        } else {
            // Request does not require authentication, proceed with original request.
            chain.proceed(originalRequest)
        }
    }

}