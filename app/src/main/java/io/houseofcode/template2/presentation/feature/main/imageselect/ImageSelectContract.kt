package io.houseofcode.template2.presentation.feature.main.imageselect


import io.houseofcode.template2.presentation.feature.BaseFragmentPresenter

import java.io.File


interface ImageSelectContract {

    interface Presenter: BaseFragmentPresenter<Void> {

        /**
         * Get image from device.
         */
        fun pickImage()

        /**
         * Capture image with camera.
         */
        fun captureImage()
    }

    interface View {

        /**
         * Callback on image selected, either by picking it from device or capturing it with camera.
         */
        fun onImageSelected(file: File)

        /**
         * Callback on generic error.
         */
        fun onError(message: String)
    }
}