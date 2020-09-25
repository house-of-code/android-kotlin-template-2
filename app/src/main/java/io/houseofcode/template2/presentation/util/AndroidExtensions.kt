package io.houseofcode.template2.presentation.util

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresPermission

/**
 * Check is network is available.
 */
@Suppress("DEPRECATION")
@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
fun Context.isNetworkAvailable(): Boolean {
    var result = false
    val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        connectivityManager?.run {
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.run {
                result = when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            }
        }
    } else {
        connectivityManager?.run {
            connectivityManager.activeNetworkInfo?.run {
                result = when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    else -> false
                }
            }
        }
    }
    return result
}

/**
 * Apply rotation on image and scale it to a max dimension (width or height).
 */
fun Bitmap.rotateAndScale(degrees: Float, maxDimension: Float = 1600f): Bitmap {
    val matrix = Matrix().apply {
        postRotate(degrees)
        if (this@rotateAndScale.width > this@rotateAndScale.height) {
            if (width > maxDimension) {
                postScale((maxDimension / width), (maxDimension / width))
            }
        } else {
            if (height > maxDimension) {
                postScale((maxDimension / height), (maxDimension / height))
            }
        }
    }
    return Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
}