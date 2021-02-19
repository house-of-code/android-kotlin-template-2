package io.houseofcode.template2.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.houseofcode.template2.domain.model.Item
import io.houseofcode.template2.domain.model.Resource
import io.houseofcode.template2.domain.repository.ItemRepository
import io.houseofcode.template2.domain.repository.PersistentStorageRepository
import io.houseofcode.template2.domain.usecase.AddItemUseCase
import io.houseofcode.template2.domain.usecase.GetItemUseCase
import io.houseofcode.template2.domain.usecase.GetItemsUseCase
import io.houseofcode.template2.domain.usecase.LoginUseCase

/**
 * Items from remote repository.
 */
class ItemViewModel(private val remoteRepository: ItemRepository,
                    private val persistentStorageRepository: PersistentStorageRepository): ViewModel() {

    // Factory for providing repositories to view model.
    class Factory(private val remoteRepository: ItemRepository,
                  private val persistentStorageRepository: PersistentStorageRepository): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ItemViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ItemViewModel(remoteRepository, persistentStorageRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    /**
     * Login with email and password.
     */
    fun login(email: String, password: String): LiveData<Resource<String>> =
        LoginUseCase(remoteRepository, persistentStorageRepository)
            .execute(LoginUseCase.Params(email, password))

    /**
     * Get item by id.
     */
    fun getItem(id: String): LiveData<Resource<Item>> = GetItemUseCase(remoteRepository)
        .execute(GetItemUseCase.Params(id))

    /**
     * Get all items.
     */
    fun getItems(): LiveData<Resource<List<Item>>> = GetItemsUseCase(remoteRepository)
        .execute()

    /**
     * Add new item.
     */
    fun addItem(item: Item): LiveData<Resource<Item>> = AddItemUseCase(remoteRepository)
        .execute(AddItemUseCase.Params(item))
}
