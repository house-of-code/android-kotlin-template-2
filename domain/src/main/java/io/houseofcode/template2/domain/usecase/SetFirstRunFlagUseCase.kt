package io.houseofcode.template2.domain.usecase

import io.houseofcode.template2.domain.interactor.FlowInteractor
import io.houseofcode.template2.domain.repository.PersistentStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * Use case for setting value in PersistentStorageRepository (generic interface for SharedPreferences).
 * This use case returns the preference on execution, though it might not be used,
 * so we can pass the data through the #build and #process methods, similar to the LiveDataInteractor.
 */
class SetFirstRunFlagUseCase(private val repository: PersistentStorageRepository): FlowInteractor<Void, SetFirstRunFlagUseCase.Params>() {

    override fun build(params: Params?): Flow<Void> {
        val state = checkNotNull(params) { "Params must not be null" }
        repository.setValue(PersistentStorageRepository.PREF_FIRST_LAUNCH, state.isFirstRun)
        return emptyFlow()
    }

    data class Params(val isFirstRun: Boolean)
}