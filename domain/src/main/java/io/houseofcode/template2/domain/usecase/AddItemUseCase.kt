package io.houseofcode.template2.domain.usecase

import androidx.lifecycle.LiveData
import io.houseofcode.template2.domain.model.Item
import io.houseofcode.template2.domain.model.Resource
import io.houseofcode.template2.domain.repository.ItemRepository
import java.util.*

/**
 * Simple use case for pushing new item to repository.
 */
class AddItemUseCase(private val itemRepository: ItemRepository): LiveDataInteractor<Resource<Item>, AddItemUseCase.Params>() {

    override fun build(params: Params?): LiveData<Resource<Item>> {
        val state = checkNotNull(params) { "Params must not be null" }
        check(!state.item.createdAt.after(Date())) { "Item must not be created with timestamp in the future" }

        return itemRepository.addItem(state.item)
    }

    // No post processing is necessary in this use case.
    override fun process(liveData: LiveData<Resource<Item>>): LiveData<Resource<Item>> = liveData

    data class Params(val item: Item)
}