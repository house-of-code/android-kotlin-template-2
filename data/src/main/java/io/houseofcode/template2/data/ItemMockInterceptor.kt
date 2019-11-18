package io.houseofcode.template2.data

import okhttp3.*
import okio.Buffer
import java.io.InputStream
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull

/**
 * Interceptor for mocking item responses.
 */
class ItemMockInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url

        return when (url.encodedPath) {
            "/api/v1/items/1" -> {
                return when (request.method) {
                    "GET" -> buildJsonResponse(200, readFile("200-items-1.json"), chain)
                    else -> buildJsonResponse(401,"{\"error\":\"error\"}", chain)
                }
            }
            "/api/v1/items" -> {
                return when (request.method) {
                    "GET" -> buildJsonResponse(200, readFile("200-items.json"), chain)
                    "POST" -> {
                        val buffer = Buffer()
                        request.body?.writeTo(buffer)
                        buildJsonResponse(201, buffer.readUtf8(), chain)
                    }
                    else -> buildJsonResponse(401,"{\"error\":\"error\"}", chain)
                }
            }
            else -> chain.proceed(request)
        }
    }

    /**
     * Build response as JSON.
     */
    private fun buildJsonResponse(code: Int, response: String, chain: Interceptor.Chain): Response {
        return Response.Builder()
            .code(code)
            .message(response)
            .request(chain.request())
            .protocol(Protocol.HTTP_1_1)
            .body(response.toResponseBody("application/json".toMediaTypeOrNull()))
            .addHeader("Content-Type", "application/json")
            .build()
    }

    /**
     * Read and return content of file.
     */
    private fun readFile(path: String): String {
        val inputStream: InputStream? = this.javaClass.classLoader?.getResourceAsStream(path)
        return inputStream?.bufferedReader()?.use { it.readText() } ?: ""
    }
}