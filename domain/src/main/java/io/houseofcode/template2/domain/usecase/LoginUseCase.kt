package io.houseofcode.template2.domain.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import io.houseofcode.template2.domain.interactor.LiveDataInteractor
import io.houseofcode.template2.domain.model.Resource
import io.houseofcode.template2.domain.repository.ItemRepository

/**
 * Login and get new token.
 * Token is saved into persistent storage if login request is successful.
 */
class LoginUseCase(private val repository: ItemRepository): LiveDataInteractor<Resource<String>, LoginUseCase.Params>() {

    override fun build(params: Params?): LiveData<Resource<String>> {
        val state = checkNotNull(params) { "Params must not be null" }

        return repository.login(state.email, state.password)
    }

    override fun process(liveData: LiveData<Resource<String>>, params: Params?): LiveData<Resource<String>> {
        return Transformations.map(liveData) { resource ->
            if (resource.status == Resource.Status.SUCCESS) {
                resource.data?.let { loginToken ->
                    // Saving new token into persistent storage.
                    repository.saveToken(loginToken)
                }
            }
            resource
        }
    }

    data class Params(val email: String, val password: String)
}