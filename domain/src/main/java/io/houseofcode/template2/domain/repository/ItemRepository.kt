package io.houseofcode.template2.domain.repository

import androidx.lifecycle.LiveData
import io.houseofcode.template2.domain.model.Item
import io.houseofcode.template2.domain.model.Resource

interface ItemRepository {

    /**
     * Login with email and password.
     */
    fun login(email: String, password: String): LiveData<Resource<String>>

    /**
     * Get item by id.
     * @param id Item id.
     * @return Item response resource.
     */
    fun getItem(id: String): LiveData<Resource<Item>>

    /**
     * Get all items.
     * @return Resource with all items.
     */
    fun getItems(): LiveData<Resource<List<Item>>>

    /**
     * Add item.
     * @param item New item to submit.
     * @return Response resource of newly added item.
     */
    fun addItem(item: Item): LiveData<Resource<Item>>
}
