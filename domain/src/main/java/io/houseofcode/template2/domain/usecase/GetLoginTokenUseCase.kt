package io.houseofcode.template2.domain.usecase

import io.houseofcode.template2.domain.interactor.GenericInteractor
import io.houseofcode.template2.domain.repository.PersistentStorageRepository

/**
 * Get login token from persistent storage.
 */
class GetLoginTokenUseCase(private val repository: PersistentStorageRepository): GenericInteractor<String?, Void>() {

    override fun build(params: Void?): String? {
        return repository.getValue(PersistentStorageRepository.PREF_LOGIN_TOKEN, null)
    }
}