package io.houseofcode.template2.presentation

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.request.RequestOptions

/**
 * Module for Glide configuration.
 * @link https://bumptech.github.io/glide/doc/configuration.html
 */
@GlideModule
class TemplateAppGlideModule: AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        // Calculate memory size according to screen size and density.
        val calculator = MemorySizeCalculator.Builder(context)
            .setMemoryCacheScreens(2f)
            .build()
        // Set memory cache (not the same as disk cache) size.
        builder.setMemoryCache(LruResourceCache(calculator.memoryCacheSize.toLong()))
        // Set pool size for reusable bitmaps.
        builder.setBitmapPool(LruBitmapPool(calculator.bitmapPoolSize.toLong()))

        // Cache images after decoding.
        builder.setDefaultRequestOptions(
            RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        )
    }
}