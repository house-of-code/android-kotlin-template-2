package io.houseofcode.template2.presentation.feature.main.imageselect

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import io.houseofcode.template2.R
import io.houseofcode.template2.TemplateApp
import io.houseofcode.template2.domain.model.Resource
import io.houseofcode.template2.presentation.viewmodel.ImageSelectViewModel
import timber.log.Timber
import java.io.File

class ImageSelectPresenter(private val view: ImageSelectContract.View): ImageSelectContract.Presenter {

    private lateinit var context: Context
    private lateinit var lifecycleOwner: LifecycleOwner

    private lateinit var imageSelectViewModel: ImageSelectViewModel

    override fun attach(fragment: Fragment, params: Void?) {
        this.context = fragment.requireContext()
        this.lifecycleOwner = fragment

        // View model for getting image from device.
        imageSelectViewModel = ViewModelProvider(fragment, ImageSelectViewModel.SavedStateFactory(TemplateApp.instance, fragment, fragment)).get(ImageSelectViewModel::class.java)

        imageSelectViewModel.pickImageLiveData.observe(lifecycleOwner) { resource ->
            Timber.d("pickImageLiveData { data: ${resource.data}, error: ${resource.errorMessage} }")

            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    val filePath: String? = resource.data
                    if (filePath != null) {
                        val cachedImageFile: File = File(filePath)
                        if (cachedImageFile.exists()) {
                            view.onImageSelected(cachedImageFile)
                        } else {
                            view.onError(resource.errorMessage ?: context.getString(R.string.error_result_launcher_pick_image))
                        }
                    }
                }
                Resource.Status.ERROR -> {
                    view.onError(resource.errorMessage ?: context.getString(R.string.error_result_launcher_pick_image))
                }
            }
        }

        imageSelectViewModel.captureImageLiveData.observe(lifecycleOwner) { resource ->
            Timber.d("captureImageLiveData { data: ${resource.data}, error: ${resource.errorMessage} }")

            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    val filePath: String? = resource.data
                    if (filePath != null) {
                        val cachedImageFile: File = File(filePath)
                        if (cachedImageFile.exists()) {
                            view.onImageSelected(cachedImageFile)
                        } else {
                            view.onError(resource.errorMessage ?: context.getString(R.string.error_result_launcher_capture_image))
                        }
                    }
                }
                Resource.Status.ERROR -> {
                    view.onError(resource.errorMessage ?: context.getString(R.string.error_result_launcher_capture_image))
                }
            }
        }
    }

    override fun pickImage() {
        imageSelectViewModel.pickImage()
    }

    override fun captureImage() {
        imageSelectViewModel.captureImage()
    }
}