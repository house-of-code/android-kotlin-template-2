package io.houseofcode.template2.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import io.houseofcode.template2.domain.repository.PersistentStorageRepository
import io.houseofcode.template2.domain.usecase.ClearUserDataUseCase
import io.houseofcode.template2.domain.usecase.GetFirstRunFlagUseCase
import io.houseofcode.template2.domain.usecase.GetLoginTokenUseCase
import io.houseofcode.template2.domain.usecase.SetFirstRunFlagUseCase
import kotlinx.coroutines.Dispatchers

/**
 * Persistent storage for saving and retrieving values.
 */
class SharedPreferencesViewModel(private val repository: PersistentStorageRepository): ViewModel() {

    // Factory for providing repository to view model.
    class Factory(private val repository: PersistentStorageRepository): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SharedPreferencesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SharedPreferencesViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    fun getLoginToken(): String? {
        return GetLoginTokenUseCase(repository)
            .execute()
    }

    fun getFirstRunFlag(): LiveData<Boolean> {
        return GetFirstRunFlagUseCase(repository)
            .execute()
            .asLiveData(Dispatchers.Main)
    }

    fun setFirstRunFlag(isFirstRun: Boolean) {
        SetFirstRunFlagUseCase(repository)
            .execute(SetFirstRunFlagUseCase.Params(isFirstRun))
    }

    fun logout() {
        ClearUserDataUseCase(repository)
            .execute()
    }
}
