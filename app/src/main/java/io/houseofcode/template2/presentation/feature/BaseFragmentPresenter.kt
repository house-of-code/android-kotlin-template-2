package io.houseofcode.template2.presentation.feature

import androidx.fragment.app.Fragment

/**
 * Base presenter for fragments.
 */
interface BaseFragmentPresenter {

    fun attach(fragment: Fragment)
}