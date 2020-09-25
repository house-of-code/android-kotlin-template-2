package io.houseofcode.template2.domain.usecase

import io.houseofcode.template2.domain.interactor.FlowInteractor
import io.houseofcode.template2.domain.repository.PersistentStorageRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for retrieving value from SharedPreferences wrapped in custom LiveData.
 */
class GetFirstRunFlagUseCase(private val repository: PersistentStorageRepository): FlowInteractor<Boolean, Void>() {

    override fun build(params: Void?): Flow<Boolean> {
        return repository.observeValue(PersistentStorageRepository.PREF_FIRST_LAUNCH, true)
    }
}