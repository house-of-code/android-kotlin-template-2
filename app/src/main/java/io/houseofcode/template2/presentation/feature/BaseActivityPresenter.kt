package io.houseofcode.template2.presentation.feature

import androidx.fragment.app.FragmentActivity

/**
 * Base presenter for activities.
 */
interface BaseActivityPresenter<in Params: Any?> {

    fun attach(activity: FragmentActivity, params: Params? = null)
}