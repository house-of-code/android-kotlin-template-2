package io.houseofcode.template2.presentation.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.houseofcode.template2.R
import io.houseofcode.template2.data.ItemService
import io.houseofcode.template2.data.executeSafely
import io.houseofcode.template2.domain.model.Item
import io.houseofcode.template2.domain.model.LoginCredentials
import io.houseofcode.template2.domain.model.Resource
import io.houseofcode.template2.domain.repository.ItemRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Implementation of item repository for remote data access.
 * A cache, handled by OkHttp, is used when a cache directory is provided.
 * @param context Application context.
 * @param itemService Service from data layer, where we can perform requests.
 */
class RemoteItemRepository(val context: Context,
                           private val itemService: ItemService): ItemRepository {

    override fun login(email: String, password: String): LiveData<Resource<String>> {
        val mutableLiveData = MutableLiveData<Resource<String>>()

        CoroutineScope(Dispatchers.Main).launch {
            mutableLiveData.apply {
                value = withContext(Dispatchers.IO) {
                    executeSafely(
                        request = {
                            itemService.login(
                                LoginCredentials(email, password)
                            )
                        },
                        onSuccess = { loginToken ->
                            Resource.success(loginToken?.token)
                        },
                        onFailed = { errorMessage, loginToken ->
                            Resource.error(
                                errorMessage ?: context.getString(R.string.error_request_login),
                                loginToken?.token
                            )
                        },
                        onException = { error ->
                            Resource.error(
                                error.localizedMessage ?: context.getString(R.string.error_request_login)
                            )
                        }
                    )
                }
            }
        }

        return mutableLiveData
    }

    override fun getItem(id: String): LiveData<Resource<Item>> {
        val mutableLiveData = MutableLiveData<Resource<Item>>()

        CoroutineScope(Dispatchers.Main).launch {
            mutableLiveData.apply {
                value = withContext(Dispatchers.IO) {
                    executeSafely(
                        request = { itemService.getItem(id) },
                        onSuccess = { item ->
                            Resource.success(item)
                        },
                        onFailed = { errorMessage, item ->
                            Resource.error(
                                errorMessage ?: context.getString(R.string.error_request_get_item),
                                item
                            )
                        },
                        onException = { error ->
                            Resource.error(
                                error.localizedMessage ?: context.getString(R.string.error_request_get_item)
                            )
                        }
                    )
                }
            }
        }

        return mutableLiveData
    }

    override fun getItems(): LiveData<Resource<List<Item>>> {
        val mutableLiveData = MutableLiveData<Resource<List<Item>>>()

        CoroutineScope(Dispatchers.Main).launch {
            mutableLiveData.apply {
                value = withContext(Dispatchers.IO) {
                    executeSafely(
                        request = { itemService.getItems() },
                        onSuccess = { items ->
                            Resource.success(items)
                        },
                        onFailed = { errorMessage, items ->
                            Resource.error(
                                errorMessage ?: context.getString(R.string.error_request_get_items),
                                items
                            )
                        },
                        onException = { error ->
                            Resource.error(
                                error.localizedMessage
                                    ?: context.getString(R.string.error_request_get_items)
                            )
                        }
                    )
                }
            }
        }

        return mutableLiveData
    }

    override fun addItem(item: Item): LiveData<Resource<Item>> {
        val mutableLiveData = MutableLiveData<Resource<Item>>()

        CoroutineScope(Dispatchers.Main).launch {
            mutableLiveData.apply {
                value = withContext(Dispatchers.IO) {
                    executeSafely(
                        request = { itemService.addItem(item) },
                        onSuccess = { item ->
                            Resource.success(item)
                        },
                        onFailed = { errorMessage, item ->
                            Resource.error(
                                errorMessage ?: context.getString(R.string.error_request_add_item),
                                item
                            )
                        },
                        onException = { error ->
                            Resource.error(
                                error.localizedMessage ?: context.getString(R.string.error_request_add_item),
                                item
                            )
                        }
                    )
                }
            }
        }

        return mutableLiveData
    }
}
