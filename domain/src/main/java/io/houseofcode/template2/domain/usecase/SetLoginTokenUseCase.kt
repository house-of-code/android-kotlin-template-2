package io.houseofcode.template2.domain.usecase

import io.houseofcode.template2.domain.interactor.FlowInteractor
import io.houseofcode.template2.domain.repository.PersistentStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * Set login token in persistent storage.
 */
class SetLoginTokenUseCase(private val repository: PersistentStorageRepository): FlowInteractor<Void, SetLoginTokenUseCase.Params>() {

    override fun build(params: Params?): Flow<Void> {
        val state = checkNotNull(params) { "Params must be provided" }
        repository.setValue(PersistentStorageRepository.PREF_LOGIN_TOKEN, state.loginToken)
        return emptyFlow()
    }

    data class Params(val loginToken: String)
}