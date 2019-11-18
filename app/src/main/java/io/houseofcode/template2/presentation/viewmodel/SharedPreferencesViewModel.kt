package io.houseofcode.template2.presentation.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import io.houseofcode.template2.domain.live.SharedPreferenceBooleanLiveData
import io.houseofcode.template2.domain.usecase.GetFirstRunFlagUseCase
import io.houseofcode.template2.domain.usecase.SetFirstRunFlagUseCase

/**
 * Persistent storage for saving and retrieving values.
 */
class SharedPreferencesViewModel(application: Application): AndroidViewModel(application) {

    companion object {
        private const val PREF_PACKAGE_NAME = "io.houseofcode.template2.preferences"
    }

    private val sharedPreferences = application.getSharedPreferences(PREF_PACKAGE_NAME, Context.MODE_PRIVATE)

    fun getFirstRunFlag(): SharedPreferenceBooleanLiveData {
        return GetFirstRunFlagUseCase(sharedPreferences)
            .execute()
    }

    fun setFirstRunFlag(isFirstRun: Boolean) {
        SetFirstRunFlagUseCase(sharedPreferences)
            .execute(SetFirstRunFlagUseCase.Params(isFirstRun))
    }
}