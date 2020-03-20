package io.houseofcode.template2.domain.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import io.houseofcode.template2.domain.interactor.LiveDataInteractor
import io.houseofcode.template2.domain.model.Item
import io.houseofcode.template2.domain.model.Resource
import io.houseofcode.template2.domain.repository.ItemRepository

/**
 * Simple use case for retrieving all items and sorting them by date.
 */
class GetItemsUseCase(private val repository: ItemRepository): LiveDataInteractor<Resource<List<Item>>, Void>() {

    override fun build(params: Void?): LiveData<Resource<List<Item>>> = repository.getItems()

    override fun process(liveData: LiveData<Resource<List<Item>>>): LiveData<Resource<List<Item>>> {
        return Transformations.map(liveData) { resource ->
            resource.apply {
                this.data = resource.data?.sortedWith(
                    compareBy { it.createdAt }
                )
            }
        }
    }
}