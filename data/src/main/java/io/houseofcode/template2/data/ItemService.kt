package io.houseofcode.template2.data

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import io.houseofcode.template2.data.interceptor.AuthenticationInterceptor
import io.houseofcode.template2.data.interceptor.ItemMockInterceptor
import io.houseofcode.template2.domain.model.LoginCredentials
import io.houseofcode.template2.domain.model.LoginToken
import io.houseofcode.template2.domain.model.Item
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.File
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

/**
 * Remote service example.
 */
interface ItemService {

    companion object {
        // Base URL of service.
        private const val BASE_URL = "https://houseofcode.io/api/v1/"
        // Date format used in service.
        private const val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ"

        // HTTP client set when creating service.
        var client: OkHttpClient? = null

        /**
         * Create service for example API.
         * This service provides basic logging of requests, parsing of dates and conversion of field names.
         * @param isDebug True if debug variant, to enable logging of requests.
         * @param cacheFile Optional cache file if requests should be cached by OkHttp.
         * @param isNetworkAvailable Higher-order function for determining if network is available.
         * @return Test service.
         */
        fun create(isDebug: Boolean, cacheFile: File?, getToken: () -> LoginToken?, isNetworkAvailable: () -> Boolean): ItemService {
            val cacheMaxSize: Long = 15 * 1024 * 1024 // 15 mb

            client = OkHttpClient.Builder()
                .addInterceptor(AuthenticationInterceptor { getToken() })
                .addInterceptor { chain ->
                    if (!isNetworkAvailable()) {
                        throw UnknownHostException("Network not available")
                    }
                    chain.proceed(chain.request())
                }
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = if (isDebug) {
                        HttpLoggingInterceptor.Level.HEADERS
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                })
                .addInterceptor(ItemMockInterceptor())
                .cache(cacheFile?.let { Cache(it, cacheMaxSize) })
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client!!)
                .addConverterFactory(
                    GsonConverterFactory.create(
                        GsonBuilder()
                            // Automatic parsing of date strings into date objects.
                            .setDateFormat(DATE_FORMAT)
                            // Automatic parsing of field naming for lowercase with underscores.
                            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                            .create()
                    )
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