package io.houseofcode.template2.presentation.feature.main

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import io.houseofcode.template2.R
import io.houseofcode.template2.domain.model.Item
import io.houseofcode.template2.domain.model.Resource
import io.houseofcode.template2.domain.usecase.GenerateItemUseCase
import io.houseofcode.template2.presentation.util.rotateAndScale
import io.houseofcode.template2.presentation.viewmodel.ItemViewModel
import io.houseofcode.template2.presentation.viewmodel.SharedPreferencesViewModel
import timber.log.Timber
import java.io.InputStream

class MainPresenter(private val view: MainContract.View): MainContract.Presenter {

    private lateinit var context: Context
    private lateinit var lifecycleOwner: LifecycleOwner
    private lateinit var itemViewModel: ItemViewModel
    private lateinit var sharedPreferencesViewModel: SharedPreferencesViewModel

    // Launchers from activity result API, to get image from device or with camera.
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    private lateinit var imageCaptureLauncher: ActivityResultLauncher<Void>

    override fun attach(activity: FragmentActivity, params: Void?) {
        // Variables.
        context = activity
        lifecycleOwner = activity

        // View models.
        itemViewModel = ViewModelProvider(activity).get(ItemViewModel::class.java)
        sharedPreferencesViewModel = ViewModelProvider(activity, SharedPreferencesViewModel.SharedPreferencesViewModelFactory(activity.application))
            .get(SharedPreferencesViewModel::class.java)
        sharedPreferencesViewModel.getFirstRunFlag().observe(activity, Observer { isFirstRun ->
            view.onFirstRunFlagReceived(isFirstRun)
        })

        // Activity result launcher for getting image from device.
        // https://developer.android.com/reference/kotlin/androidx/activity/result/contract/ActivityResultContracts
        // https://developer.android.com/reference/kotlin/androidx/activity/result/contract/ActivityResultContracts.GetContent
        imagePickerLauncher = activity.registerForActivityResult<String, Uri?>(
            ActivityResultContracts.GetContent()
        ) { uri ->
            if (uri != null) {
                var inputStream: InputStream? = null

                try {
                    inputStream = activity.contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    val imagePath = uri.path

                    if (inputStream != null && imagePath != null) {
                        // Get image rotation from original image.
                        val exif = ExifInterface(inputStream)

                        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                        val rotation = when (orientation) {
                            ExifInterface.ORIENTATION_ROTATE_90 -> 90.0f
                            ExifInterface.ORIENTATION_ROTATE_180 -> 180.0f
                            ExifInterface.ORIENTATION_ROTATE_270 -> 270.0f
                            else -> 0.0f
                        }

                        // Create bitmap and apply rotation.
                        val rotatedBitmap = bitmap.rotateAndScale(rotation)

                        view.onImageSelected(rotatedBitmap)
                    } else {
                        Timber.w("Could not open input stream while picking image")
                    }
                } catch (exception: Exception) {
                    Timber.w(exception, "Error while picking image")
                } finally {
                    inputStream?.close()
                }
            } else {
                Timber.w("No image was picked")
            }
        }

        // Activity result launcher for capturing image with camera.
        // https://developer.android.com/reference/kotlin/androidx/activity/result/contract/ActivityResultContracts.TakePicturePreview
        imageCaptureLauncher = activity.registerForActivityResult<Void, Bitmap?>(
            ActivityResultContracts.TakePicturePreview()
        ) { bitmap ->
            if (bitmap != null) {
                view.onImageSelected(bitmap)
            } else {
                Timber.w("No image was captured")
            }
        }
    }

    override fun getItem(id: String) {
        // Get item by id and observe returned data.
        itemViewModel.getItem(id).observe(lifecycleOwner, Observer { resource ->
            // Check if returned resource is successful or has errors.
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    val item: Item? = resource.data
                    if (item != null) {
                        view.onItemReceived(item)
                    } else {
                        view.onError(context.getString(R.string.error_request_get_item))
                    }
                }
                Resource.Status.ERROR -> {
                    view.onError(resource.errorMessage ?: context.getString(R.string.error_request_get_item))
                }
            }
        })
    }

    override fun getItems() {
        itemViewModel.getItems().observe(lifecycleOwner, Observer { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    val items: List<Item>? = resource.data
                    if (!items.isNullOrEmpty()) {
                        view.onItemsReceived(items)
                    } else {
                        view.onError(context.getString(R.string.error_request_get_items))
                    }
                }
                Resource.Status.ERROR -> {
                    view.onError(resource.errorMessage ?: context.getString(R.string.error_request_get_items))
                }
            }
        })
    }

    override fun addItem(id: String) {
        // Generate item from id.
        val item = GenerateItemUseCase().execute(
            GenerateItemUseCase.Params(id)
        )

        // Add item and observe newly created data.
        itemViewModel.addItem(item).observe(lifecycleOwner, Observer { resource ->
            // Check if returned resource is successful or has errors.
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    val newItem: Item? = resource.data
                    if (newItem != null) {
                        view.onNewItemAdded(newItem)
                    } else {
                        view.onError(context.getString(R.string.error_request_add_item))
                    }
                }
                Resource.Status.ERROR -> {
                    view.onError(resource.errorMessage ?: context.getString(R.string.error_request_add_item))
                }
            }
        })
    }

    override fun setFirstRunFlag(isFirstRun: Boolean) {
        sharedPreferencesViewModel.setFirstRunFlag(isFirstRun)
    }

    override fun pickImage() {
        // Get image content with launcher.
        imagePickerLauncher.launch("image/*")
    }

    override fun captureImage() {
        // Start camera with launcher.
        imageCaptureLauncher.launch(null)
    }
}