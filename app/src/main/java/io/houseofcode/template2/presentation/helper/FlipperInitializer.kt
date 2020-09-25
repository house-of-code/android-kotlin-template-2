package io.houseofcode.template2.presentation.helper

import android.content.Context
import okhttp3.Interceptor

/**
 * Interface for initializing Flipper.
 * This interface is implemented in both release and debug build variants, but
 * [@link FlipperClient] is only initialized and used in debug.
 */
interface FlipperInitializer {

    /**
     * Start FlipperClient with plugins.
     */
    fun start(context: Context)

    /**
     * Get network debug interceptor for OkHttp.
     * This method should return an interceptor when running a debug build variant,
     * null should be returned otherwise.
     */
    fun getDebugNetworkInterceptor(): Interceptor?
}
