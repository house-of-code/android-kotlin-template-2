package io.houseofcode.template2.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import io.houseofcode.template2.data.interceptor.AuthenticationInterceptor
import io.houseofcode.template2.data.interceptor.ItemMockInterceptor
import io.houseofcode.template2.data.model.LoginToken
import io.houseofcode.template2.domain.model.Item
import io.houseofcode.template2.domain.model.LoginCredentials
import io.houseofcode.template2.domain.serializer.DateAsStringSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*
import java.io.File
import java.net.UnknownHostException
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Remote service example.
 */
interface ItemService {

    companion object {
        // Base URL of service.
        private const val BASE_URL = "https://houseofcode.io/api/v1/"

        /**
         * Create service for example API with optional default caching.
         * All responses are mocked in ItemMockInterceptor.
         * @param cacheFile Optional cache file if requests should be cached by OkHttp.
         * @param debugNetworkInterceptor
         * @param getToken Function for retrieving login token.
         * @param isNetworkAvailable Higher-order function for determining if network is available.
         * @param noNetworkErrorMessage Optional message for missing network connection.
         * @param cacheMaxSize Optional max custom size of cache in bytes.
         * @return Configured client with mocked responses.
         */
        fun createOkHttpClient(cacheFile: File?,
                               debugNetworkInterceptor: Interceptor?,
                               getToken: () -> String?,
                               isNetworkAvailable: () -> Boolean,
                               noNetworkErrorMessage: String? = "Network connection not available",
                               cacheMaxSize: Long = 15 * 1024 * 1024): OkHttpClient.Builder {
            return OkHttpClient.Builder().apply {

                // Add authentication header to authenticated requests.
                addInterceptor(AuthenticationInterceptor { getToken() })

                // Interceptor for checking if network connection is available.
                addInterceptor { chain ->
                    if (!isNetworkAvailable()) {
                        throw UnknownHostException(noNetworkErrorMessage)
                    }
                    chain.proceed(chain.request())
                }

                // Add interceptor for Flipper debugging tool.
                if (debugNetworkInterceptor != null) {
                    addInterceptor(debugNetworkInterceptor)
                }

                // Mocking responses.
                addInterceptor(ItemMockInterceptor())

                // Optional default caching.
                cache(cacheFile?.let { Cache(it, cacheMaxSize) })

                // Timeouts for long running requests.
                connectTimeout(60, TimeUnit.SECONDS)
                readTimeout(60, TimeUnit.SECONDS)
            }
        }

        /**
         * Create service for example API.
         * This service provides basic logging of requests, parsing of dates and conversion of field names.
         * @param okHttpClient Client on which to perform requests on base URL.
         * @return Mocked test service.
         */
        @ExperimentalSerializationApi
        fun createService(okHttpClient: OkHttpClient): ItemService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(
                    Json {
                        prettyPrint = true
                        ignoreUnknownKeys = true
                        isLenient = true
                        encodeDefaults = false
                        serializersModule = SerializersModule {
                            contextual(Date::class, DateAsStringSerializer)
                        }
                    }.asConverterFactory("application/json".toMediaType())
                )
                .build()

            return retrofit.create(ItemService::class.java)
        }
    }

    /**
     * Example of authentication/login request, providing credentials for token.
     */
    @POST("login")
    suspend fun login(@Body loginCredentials: LoginCredentials): Response<LoginToken>

    /**
     * Example of simple GET request for retrieving single item.
     */
    @GET("items/{id}")
    suspend fun getItem(@Path("id") id: String): Response<Item>

    /**
     * Example of simple GET request for retrieving all items.
     */
    @GET("items")
    suspend fun getItems(): Response<List<Item>>

    /**
     * Example of a authenticated POST request for adding item.
     */
    @Headers(AuthenticationInterceptor.AUTH_HEADER)
    @POST("items")
    suspend fun addItem(@Body body: Item): Response<Item>
}
