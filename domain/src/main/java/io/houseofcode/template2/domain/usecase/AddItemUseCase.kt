package io.houseofcode.template2.domain.usecase

import androidx.lifecycle.LiveData
import io.houseofcode.template2.domain.interactor.LiveDataInteractor
import io.houseofcode.template2.domain.model.Item
import io.houseofcode.template2.domain.model.Resource
import io.houseofcode.template2.domain.repository.ItemRepository
import java.util.*

/**
 * Simple use case for pushing new item to repository.
 */
class AddItemUseCase(private val repository: ItemRepository): LiveDataInteractor<Resource<Item>, AddItemUseCase.Params>() {

    override fun build(params: Params?): LiveData<Resource<Item>> {
        val state = checkNotNull(params) { "Params must not be null" }
        check(!state.item.createdAt.after(Date())) { "Item must not be created with timestamp in the future" }

        return repository.addItem(state.item)
    }

    data class Params(val item: Item)
}