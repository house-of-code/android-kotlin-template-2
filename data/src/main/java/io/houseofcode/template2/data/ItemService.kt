package io.houseofcode.template2.data

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import io.houseofcode.template2.domain.model.Item
import kotlinx.coroutines.Deferred
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
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
        private const val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"

        /**
         * Create service for example API.
         * This service provides basic logging of requests, parsing of dates and conversion of field names.
         * @param isDebug True if debug variant, to enable logging of requests.
         * @param cacheFile Optional cache file if requests should be cached by OkHttp.
         * @param isNetworkAvailable Higher-order function for determining if network is available.
         * @return Test service.
         */
        fun create(isDebug: Boolean, cacheFile: File?, isNetworkAvailable: () -> Boolean): ItemService {
            val cacheMaxSize: Long = 15 * 1024 * 1024 // 15 mb

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    if (!isNetworkAvailable()) {
                        throw UnknownHostException("Network not available")
                    }
                    chain.proceed(chain.request())
                }
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = if (isDebug) {
                        HttpLoggingInterceptor.Level.BASIC
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
                .client(okHttpClient)
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
     * Example of a simple POST request for adding item.
     */
    @POST("items")
    suspend fun addItem(@Body body: Item): Response<Item>
}