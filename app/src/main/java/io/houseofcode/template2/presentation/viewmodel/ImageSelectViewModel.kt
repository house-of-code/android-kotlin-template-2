package io.houseofcode.template2.presentation.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import io.houseofcode.template2.R
import io.houseofcode.template2.domain.model.Resource
import io.houseofcode.template2.presentation.util.createScaledAndRotatedBitmap
import io.houseofcode.template2.presentation.util.getEmptyCacheFile
import io.houseofcode.template2.presentation.util.getFileUri
import io.houseofcode.template2.presentation.util.writeToCache
import timber.log.Timber
import java.io.File

class ImageSelectViewModel(val app: Application, fragment: Fragment, savedStateHandle: SavedStateHandle): AndroidViewModel(app) {

    companion object {
        private const val STATE_LIVE_DATA_PICK_IMAGE = "pick_image"
        private const val STATE_LIVE_DATA_CAPTURE_IMAGE = "capture_image"
    }

    class SavedStateFactory(
        private val app: Application,
        private val fragment: Fragment,
        owner: SavedStateRegistryOwner,
        defaultArgs: Bundle? = null
    ): AbstractSavedStateViewModelFactory(owner, defaultArgs) {
        override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
            if (modelClass.isAssignableFrom(ImageSelectViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ImageSelectViewModel(app, fragment, handle) as T
            }
            throw java.lang.IllegalArgumentException("Unknown ViewModel class")
        }

    }

    // Live data for image picked from device.
    val pickImageLiveData: MutableLiveData<Resource<String>> =
        Transformations.map(savedStateHandle.getLiveData<String>(STATE_LIVE_DATA_PICK_IMAGE)) {
            Resource.success(it)
        } as MutableLiveData<Resource<String>>
    // Activity result launcher for getting image from device.
    // https://developer.android.com/reference/kotlin/androidx/activity/result/contract/ActivityResultContracts
    // https://developer.android.com/reference/kotlin/androidx/activity/result/contract/ActivityResultContracts.GetContent
    private val pickImageResultLauncher: ActivityResultLauncher<String> =
        fragment.registerForActivityResult(
            // The GetContent() contract expects a file type as string and returns a content uri.
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            if (uri != null) {
                val bitmap: Bitmap? = uri.createScaledAndRotatedBitmap(app.applicationContext)

                if (bitmap != null) {
                    val cachedScaledBitmap: File? = bitmap.writeToCache(app.applicationContext)
                    this.pickImageLiveData.value = Resource.success(cachedScaledBitmap?.absolutePath)
                    savedStateHandle.set(STATE_LIVE_DATA_PICK_IMAGE, cachedScaledBitmap?.absolutePath)
                } else {
                    Timber.w("Unable to pick image")
                    this.pickImageLiveData.value = Resource.error(app.getString(R.string.error_result_launcher_pick_image))
                }
            } else {
                Timber.i("No image was picked")
            }
        }

    // Live data for image captured by camera.
    val captureImageLiveData: MutableLiveData<Resource<String>> =
        savedStateHandle.getLiveData<String>(STATE_LIVE_DATA_CAPTURE_IMAGE).map {
            Resource.success(it)
        } as MutableLiveData<Resource<String>>
    // Temporary file for storing captured image.
    private var cacheFileImageCapture: File? = null
    // Activity result launcher for capturing image with camera.
    // https://developer.android.com/reference/kotlin/androidx/activity/result/contract/ActivityResultContracts.TakePicture
    private val captureImageResultLauncher: ActivityResultLauncher<Uri> =
        fragment.registerForActivityResult(
            // The TakePicture() contract expects output uri and returns true if file was saved.
            ActivityResultContracts.TakePicture()
        ) { imageSaved ->
            if (imageSaved) {
                val cacheFile: File? = cacheFileImageCapture
                if (cacheFile != null && cacheFile.exists()) {
                    val bitmap = cacheFile.createScaledAndRotatedBitmap()
                    val cachedScaledBitmap: File? = bitmap.writeToCache(fragment.requireContext())
                    if (cachedScaledBitmap != null && cachedScaledBitmap.exists()) {
                        this.captureImageLiveData.value =
                            Resource.success(cachedScaledBitmap.absolutePath)
                        savedStateHandle.set(
                            STATE_LIVE_DATA_CAPTURE_IMAGE,
                            cachedScaledBitmap.absolutePath
                        )
                    } else {
                        Timber.w("Unable to save scaled bitmap to cache")
                        this.captureImageLiveData.value =
                            Resource.error(app.getString(R.string.error_result_launcher_capture_image))
                    }
                } else {
                    Timber.i("Image not captured")
                }
            } else {
                Timber.i("No image was captured")
            }
        }

    fun pickImage(): LiveData<Resource<String>> {
        pickImageResultLauncher.launch("image/*")

        // Return live data which is used in ActivityResultCallback.
        return this.pickImageLiveData
    }

    fun captureImage(): LiveData<Resource<String>> {
        val cacheFile: File? = app.getEmptyCacheFile("jpg")

        if (cacheFile != null) {
            // Save cache file for captured image.
            this.cacheFileImageCapture = cacheFile

            // Get secure file uri.
            val photoUri = cacheFile.getFileUri(app)

            captureImageResultLauncher.launch(photoUri)
        } else {
            Timber.w("Not able to create file for capturing photo")
        }

        // Return live data which is used in ActivityResultCallback.
        return this.captureImageLiveData
    }
}
