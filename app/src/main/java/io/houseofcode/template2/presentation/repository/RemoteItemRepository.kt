package io.houseofcode.template2.presentation.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.houseofcode.template2.TemplateApp
import io.houseofcode.template2.data.ItemService
import io.houseofcode.template2.data.getResponseResource
import io.houseofcode.template2.domain.model.LoginCredentials
import io.houseofcode.template2.domain.model.LoginToken
import io.houseofcode.template2.domain.model.Item
import io.houseofcode.template2.domain.model.Resource
import io.houseofcode.template2.domain.repository.ItemRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Implementation of item repository for remote data access.
 * A cache, handled by OkHttp, is used when a cache directory is provided.
 * @param isDebug True of build type is debug.
 * @param cacheFile Cache directory.
 * @param isNetworkAvailable Function for checking is network currently is available.
 */
class RemoteItemRepository(isDebug: Boolean,
                           cacheFile: File?,
                           getToken: () -> LoginToken?,
                           isNetworkAvailable: () -> Boolean): ItemRepository {

    // Create service from data layer.
    // This is the only place where we should access the data layer from the presentation layer.
    private val itemService = ItemService.create(isDebug, cacheFile, getToken, isNetworkAvailable)

    override fun login(email: String, password: String): LiveData<Resource<LoginToken>> {
        val mutableLiveData = MutableLiveData<Resource<LoginToken>>()

        CoroutineScope(Dispatchers.Main).launch {
            mutableLiveData.value = withContext(Dispatchers.IO) {
                itemService.login(
                    LoginCredentials(
                        email,
                        password
                    )
                ).getResponseResource()
            }
        }

        return mutableLiveData
    }

    override fun saveToken(loginToken: LoginToken) {
        // Save token into [SharedPreferences].
        TemplateApp.pref.loginToken = loginToken
    }

    override fun getItem(id: String): LiveData<Resource<Item>> {
        val mutableLiveData = MutableLiveData<Resource<Item>>()

        CoroutineScope(Dispatchers.Main).launch {
            mutableLiveData.value = withContext(Dispatchers.IO) {
                itemService.getItem(id).getResponseResource()
            }
        }

        return mutableLiveData
    }

    override fun getItems(): LiveData<Resource<List<Item>>> {
        val mutableLiveData = MutableLiveData<Resource<List<Item>>>()

        CoroutineScope(Dispatchers.Main).launch {
            mutableLiveData.value = withContext(Dispatchers.IO) {
                itemService.getItems().getResponseResource()
            }
        }

        return mutableLiveData
    }

    override fun addItem(item: Item): LiveData<Resource<Item>> {
        val mutableLiveData = MutableLiveData<Resource<Item>>()

        CoroutineScope(Dispatchers.Main).launch {
            mutableLiveData.value = withContext(Dispatchers.IO) {
                itemService.addItem(item).getResponseResource()
            }
        }

        return mutableLiveData
    }
}
