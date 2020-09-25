package io.houseofcode.template2.presentation.helper

import android.content.Context
import okhttp3.Interceptor

/**
 * Implementation of initializer for Flipper in release build variant.
 */
class FlipperClientInitializer: FlipperInitializer {

    override fun start(context: Context) {
        // No-op.
    }

    override fun getDebugNetworkInterceptor(): Interceptor? = null
}
