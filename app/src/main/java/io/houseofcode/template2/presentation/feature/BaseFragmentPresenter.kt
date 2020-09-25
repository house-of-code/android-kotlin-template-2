package io.houseofcode.template2.presentation.feature

import androidx.fragment.app.Fragment

/**
 * Base presenter for fragments.
 */
interface BaseFragmentPresenter<in Params: Any?> {

    fun attach(fragment: Fragment, params: Params? = null)
}