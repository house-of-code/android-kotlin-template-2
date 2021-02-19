package io.houseofcode.template2.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import io.houseofcode.template2.databinding.FragmentImageSelectBinding
import io.houseofcode.template2.presentation.feature.main.imageselect.ImageSelectContract
import io.houseofcode.template2.presentation.feature.main.imageselect.ImageSelectPresenter
import timber.log.Timber
import java.io.File

class ImageSelectFragment: Fragment(), ImageSelectContract.View {

    companion object {
        fun newInstance(): ImageSelectFragment {
            return ImageSelectFragment()
        }
    }

    // Bind of layout (R.layout.fragment_image_select).
    private var binding: FragmentImageSelectBinding? = null
    private val layout get() = binding!!

    // Presenter for main view actions.
    private lateinit var presenter: ImageSelectContract.Presenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentImageSelectBinding.inflate(inflater, container, false)

        presenter = ImageSelectPresenter(this)
        presenter.attach(this)

        layout.imagePickerButton.setOnClickListener {
            Timber.d("pickImage")
            presenter.pickImage()
        }

        layout.imageCaptureButton.setOnClickListener {
            Timber.d("captureImage")
            presenter.captureImage()
        }

        return layout.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Cleanup reference to binding class.
        binding = null
    }

    override fun onImageSelected(file: File) {
        Timber.i("onImageSelected { file: ${file.absolutePath} }")

        // TODO: GlideApp
        Glide.with(this)
            .load(file)
            .centerCrop()
            .into(layout.imagePickerPreview)
    }

    override fun onError(message: String) {
        Timber.e(message)
    }
}