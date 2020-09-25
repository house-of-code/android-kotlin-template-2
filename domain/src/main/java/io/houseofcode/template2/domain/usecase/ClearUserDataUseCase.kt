package io.houseofcode.template2.domain.usecase

import io.houseofcode.template2.domain.interactor.GenericInteractor
import io.houseofcode.template2.domain.repository.PersistentStorageRepository

/**
 * Clear all data unique to user, which needs to be removed if user logs out.
 */
class ClearUserDataUseCase(private val repository: PersistentStorageRepository): GenericInteractor<Unit, Void>() {

    override fun build(params: Void?) {
        repository.clearVolatileData(
            listOf(
                PersistentStorageRepository.PREF_LOGIN_TOKEN
            )
        )
    }
}