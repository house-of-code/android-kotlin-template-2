package io.houseofcode.template2.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.houseofcode.template2.TemplateApp
import io.houseofcode.template2.domain.model.Item
import io.houseofcode.template2.domain.model.LoginToken
import io.houseofcode.template2.domain.model.Resource
import io.houseofcode.template2.domain.usecase.AddItemUseCase
import io.houseofcode.template2.domain.usecase.GetItemUseCase
import io.houseofcode.template2.domain.usecase.GetItemsUseCase
import io.houseofcode.template2.domain.usecase.LoginUseCase

class ItemViewModel: ViewModel() {

    private val repository = TemplateApp.cachedRepository

    /**
     * Login with email and password.
     */
    fun login(email: String, password: String): LiveData<Resource<LoginToken>> = LoginUseCase(repository)
        .execute(LoginUseCase.Params(email, password))

    /**
     * Get item by id.
     */
    fun getItem(id: String): LiveData<Resource<Item>> = GetItemUseCase(repository)
        .execute(GetItemUseCase.Params(id))

    /**
     * Get all items.
     */
    fun getItems(): LiveData<Resource<List<Item>>> = GetItemsUseCase(repository)
        .execute()

    /**
     * Add new item.
     */
    fun addItem(item: Item): LiveData<Resource<Item>> = AddItemUseCase(repository)
        .execute(AddItemUseCase.Params(item))
}