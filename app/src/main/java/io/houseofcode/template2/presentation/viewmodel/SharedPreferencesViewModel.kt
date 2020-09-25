package io.houseofcode.template2.presentation.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import io.houseofcode.template2.domain.usecase.*
import io.houseofcode.template2.presentation.repository.SharedPreferencesRepository
import kotlinx.coroutines.Dispatchers

/**
 * Persistent storage for saving and retrieving values.
 */
class SharedPreferencesViewModel(application: Application): AndroidViewModel(application) {

    // Factory for providing application to view model.
    class SharedPreferencesViewModelFactory(private val application: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SharedPreferencesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SharedPreferencesViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    companion object {
        const val PREF_PACKAGE_NAME = "io.houseofcode.template2.preferences"
    }

    private val sharedPreferences = application.getSharedPreferences(PREF_PACKAGE_NAME, Context.MODE_PRIVATE)
    private val sharedPreferencesRepository = SharedPreferencesRepository(sharedPreferences)

    fun getLoginToken(): String? {
        return GetLoginTokenUseCase(sharedPreferencesRepository)
            .execute()
    }

    fun setLoginToken(loginToken: String) {
        SetLoginTokenUseCase(sharedPreferencesRepository)
            .execute(SetLoginTokenUseCase.Params(loginToken))
    }

    fun getFirstRunFlag(): LiveData<Boolean> {
        return GetFirstRunFlagUseCase(sharedPreferencesRepository)
            .execute()
            .asLiveData(Dispatchers.Main)
    }

    fun setFirstRunFlag(isFirstRun: Boolean) {
        SetFirstRunFlagUseCase(sharedPreferencesRepository)
            .execute(SetFirstRunFlagUseCase.Params(isFirstRun))
    }

    fun logout() {
        ClearUserDataUseCase(sharedPreferencesRepository)
            .execute()
    }
}