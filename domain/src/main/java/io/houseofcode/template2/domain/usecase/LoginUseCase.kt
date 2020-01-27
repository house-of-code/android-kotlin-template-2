package io.houseofcode.template2.domain.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import io.houseofcode.template2.domain.interactor.LiveDataInteractor
import io.houseofcode.template2.domain.model.LoginToken
import io.houseofcode.template2.domain.model.Resource
import io.houseofcode.template2.domain.repository.ItemRepository

/**
 * Login and get new token.
 * Token is saved into persistent storage if login request is successful.
 */
class LoginUseCase(private val itemRepository: ItemRepository): LiveDataInteractor<Resource<LoginToken>, LoginUseCase.Params>() {

    override fun build(params: Params?): LiveData<Resource<LoginToken>> {
        val state = checkNotNull(params)

        return itemRepository.login(state.email, state.password)
    }

    override fun process(liveData: LiveData<Resource<LoginToken>>): LiveData<Resource<LoginToken>> {
        return Transformations.map(liveData) { resource ->
            if (resource.status == Resource.Status.SUCCESS) {
                resource.data?.let { loginToken ->
                    // Saving new token into persistent storage.
                    itemRepository.saveToken(loginToken)
                }
            }
            resource
        }
    }

    data class Params(val email: String, val password: String)
}