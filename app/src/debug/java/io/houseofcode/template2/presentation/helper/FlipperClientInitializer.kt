package io.houseofcode.template2.presentation.helper

import android.content.Context
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.core.FlipperClient
import com.facebook.flipper.plugins.databases.DatabasesFlipperPlugin
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.facebook.flipper.plugins.sharedpreferences.SharedPreferencesFlipperPlugin
import com.facebook.soloader.SoLoader
import io.houseofcode.template2.presentation.repository.SharedPreferencesRepository
import okhttp3.Interceptor

/**
 * Implementation of initializer for Flipper in debug build variant.
 */
class FlipperClientInitializer: FlipperInitializer {

    private lateinit var networkPlugin: NetworkFlipperPlugin

    override fun start(context: Context) {
        // Native library unpacker, used by Flipper.
        SoLoader.init(context, false)

        // Setup FlipperClient.
        val flipperClient: FlipperClient = AndroidFlipperClient.getInstance(context)

        // Create network plugin.
        networkPlugin = NetworkFlipperPlugin()

        // Setup plugins and start FlipperClient.
        if (FlipperUtils.shouldEnableFlipper(context)) {
            flipperClient.apply {
                // Layout inspector (https://fbflipper.com/docs/setup/layout-plugin.html).
                addPlugin(InspectorFlipperPlugin(context, DescriptorMapping.withDefaults()))

                // Network (https://fbflipper.com/docs/setup/network-plugin.html).
                addPlugin(networkPlugin)

                // Database (https://fbflipper.com/docs/setup/databases-plugin.html).
                addPlugin(DatabasesFlipperPlugin(context))

                // SharedPreferences (https://fbflipper.com/docs/setup/shared-preferences-plugin.html).
                addPlugin(
                    SharedPreferencesFlipperPlugin(context, listOf(
                        SharedPreferencesFlipperPlugin.SharedPreferencesDescriptor(
                            SharedPreferencesRepository.PREF_PACKAGE_NAME, Context.MODE_PRIVATE)
                ))
                )
            }.start()
        }
    }

    override fun getDebugNetworkInterceptor(): Interceptor? = FlipperOkhttpInterceptor(networkPlugin)
}
