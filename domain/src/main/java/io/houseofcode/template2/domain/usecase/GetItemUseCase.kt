package io.houseofcode.template2.domain.usecase

import androidx.lifecycle.LiveData
import io.houseofcode.template2.domain.model.Item
import io.houseofcode.template2.domain.model.Resource
import io.houseofcode.template2.domain.repository.ItemRepository

/**
 * Simple use case for retrieving item by id from repository.
 */
class GetItemUseCase(private val itemRepository: ItemRepository): LiveDataInteractor<Resource<Item>, GetItemUseCase.Params>() {

    override fun build(params: Params?): LiveData<Resource<Item>> {
        val state = checkNotNull(params) { "Params must not be null" }

        return itemRepository.getItem(state.id)
    }

    // No post processing is necessary in this use case.
    override fun process(liveData: LiveData<Resource<Item>>): LiveData<Resource<Item>> = liveData

    data class Params(val id: String)
}