package io.houseofcode.template2.presentation.util

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import timber.log.Timber
import java.io.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

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
 * Get rotation of image from stream based on image orientation.
 */
private fun InputStream.getImageRotation(): Float {
    // Get image rotation from original image.
    val exif = ExifInterface(this)

    // Get orientation.
    val orientation = exif.getAttributeInt(
        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
    )

    // Return degrees of rotation from orientation.
    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90f
        ExifInterface.ORIENTATION_ROTATE_180 -> 180f
        ExifInterface.ORIENTATION_ROTATE_270 -> 270f
        else -> 0f
    }
}

/**
 * Get rotation of image from file based on image orientation.
 */
private fun File.getImageRotation(): Float {
    // Get image rotation from original image.
    val exif = ExifInterface(this.absolutePath)

    // Get orientation.
    val orientation = exif.getAttributeInt(
        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
    )

    // Return degrees of rotation from orientation.
    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90f
        ExifInterface.ORIENTATION_ROTATE_180 -> 180f
        ExifInterface.ORIENTATION_ROTATE_270 -> 270f
        else -> 0f
    }
}

/**
 * Rotate and scale bitmap.
 */
private fun Bitmap.scaleAndRotate(maxDimension: Float, rotation: Float): Bitmap {
    // Get ratio of smallest dimension.
    val ratio: Float = (maxDimension / this.width)
        .coerceAtMost(maxDimension / this.height)

    // Create image matrix with applied scale and rotation.
    val matrix = Matrix().apply {
        postScale(ratio, ratio)
        postRotate(rotation)
    }

    // Create rotated bitmap.
    val rotatedBitmap = Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
    // Clean up original bitmap.
    this.recycle()

    return rotatedBitmap
}

/**
 * Get image from Uri and scale/rotate image to adjust size and orientation.
 */
fun Uri.createScaledAndRotatedBitmap(context: Context, maxDimension: Float = 1024f): Bitmap? {
    var inputStream: InputStream? = null

    try {
        // Get image as stream.
        inputStream = context.contentResolver?.openInputStream(this)
        if (inputStream != null) {
            // Get original bitmap picked from gallery.
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            // Get rotation of original bitmap.
            val bitmapRotation = inputStream.getImageRotation()

            // Scale and rotate bitmap.
            val scaledAndRotatedBitmap = originalBitmap.scaleAndRotate(maxDimension, bitmapRotation)
            // Clean up.
            originalBitmap.recycle()

            return scaledAndRotatedBitmap
        }
    } catch (e: IOException) {
        Timber.e("Not able to create bitmap: $this")
    } finally {
        // Close stream if set.
        inputStream?.close()
    }

    return null
}

/**
 * Get image from file and scale/rotate image to adjust size and orientation.
 */
fun File.createScaledAndRotatedBitmap(maxDimension: Float = 500f): Bitmap {
    // Get original bitmap captured by camera.
    val originalBitmap = BitmapFactory.decodeFile(this.absolutePath)
    // Get rotation of original bitmap.
    val bitmapRotation = this.getImageRotation()

    // Scale and rotate bitmap.
    val scaledAndRotatedBitmap = originalBitmap.scaleAndRotate(maxDimension, bitmapRotation)
    // Clean up.
    originalBitmap.recycle()

    return scaledAndRotatedBitmap
}

/**
 * Get empty file for caching with specified file extension.
 */
fun Context.getEmptyCacheFile(extension: String): File? {
    // Create new file name.
    val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS", Locale.US)
    val cacheFile = File(this.cacheDir, "${dateFormat.format(Date())}.$extension")

    // Create file and return it, or return null.
    return try {
        cacheFile.createNewFile()
        cacheFile
    } catch (e: IOException) {
        Timber.e("Not able to create cache file")
        null
    }
}

/**
 * Get file uri through file provider.
 * The file provider is used to access files securely.
 */
fun File.getFileUri(context: Context): Uri {
    // Get content uri through file provider defined in manifest.
    return FileProvider.getUriForFile(
        context,
        context.packageName + ".fileprovider",
        this
    )
}

/**
 * Store bitmap into cache folder.
 */
fun Bitmap.writeToCache(context: Context, fileExtension: String = "png"): File? {
    val cacheFile: File? = context.getEmptyCacheFile(fileExtension)

    if (cacheFile != null) {
        try {
            // Convert bitmap to byte array.
            val bos = ByteArrayOutputStream()
            this.compress(Bitmap.CompressFormat.PNG, 100, bos)
            val bitmapData = bos.toByteArray()

            // Write bytes into file.
            val out = FileOutputStream(cacheFile)
            out.write(bitmapData)
            out.flush()
            out.close()

            return cacheFile
        } catch (e: IOException) {
            Timber.e(e, "Cannot write bitmap to cache file")
            return null
        }
    } else {
        Timber.e("Cannot save bitmap to cache")
        return null
    }
}
