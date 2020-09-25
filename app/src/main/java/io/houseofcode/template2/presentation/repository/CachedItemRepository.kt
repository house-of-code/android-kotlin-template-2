package io.houseofcode.template2.presentation.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.houseofcode.template2.R
import io.houseofcode.template2.TemplateApp
import io.houseofcode.template2.data.*
import io.houseofcode.template2.data.dao.CacheEntryDao
import io.houseofcode.template2.data.dao.ItemDao
import io.houseofcode.template2.data.model.CacheEntry
import io.houseofcode.template2.domain.model.Item
import io.houseofcode.template2.domain.model.LoginCredentials
import io.houseofcode.template2.domain.model.Resource
import io.houseofcode.template2.domain.repository.ItemRepository
import io.houseofcode.template2.presentation.viewmodel.SharedPreferencesViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*
import kotlin.math.abs

/**
 * Implementation of item repository for cached remote data access.
 * Responses from remote service is cached, if old cache is expired, and returned when available.
 * @param context Application context.
 * @param cacheDao Data access object of cache entries, storing when a request was last cached.
 * @param itemDao Data access object of cached items, storing item responses as cache.
 * @param itemService Service from data layer.
 */
class CachedItemRepository(val context: Context,
                           private val cacheDao: CacheEntryDao,
                           private val itemDao: ItemDao,
                           private val itemService: ItemService,
                           private val sharedPreferenceViewModel: SharedPreferencesViewModel): ItemRepository {

    override fun login(email: String, password: String): LiveData<Resource<String>> {
        val mutableLiveData = MutableLiveData<Resource<String>>()

        CoroutineScope(Dispatchers.Main).launch {
            // We do not cache the login response as token is stored manually for later requests.
            mutableLiveData.value = withContext(Dispatchers.IO) {
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

        return mutableLiveData
    }

    override fun saveToken(loginToken: String) {
        // Save token into [SharedPreferences].
        sharedPreferenceViewModel.setLoginToken(loginToken)
    }

    override fun getItem(id: String): LiveData<Resource<Item>> {
        val cacheKey = "getItem:$id"
        val mutableLiveData = MutableLiveData<Resource<Item>>()

        CoroutineScope(Dispatchers.Main).launch {
            getCacheEntity(
                cacheKey = cacheKey,
                mutableLiveData = mutableLiveData,
                getCachedEntity = { itemDao.getItem(id)?.mapToItem() },
                getRemoteEntity = {
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
                },
                addToCache = { item -> itemDao.addItem(item.mapToEntity()) },
                cacheMinutes = 1
            )
        }

        return mutableLiveData
    }

    override fun getItems(): LiveData<Resource<List<Item>>> {
        val cacheKey = "getItems:all"
        val mutableLiveData = MutableLiveData<Resource<List<Item>>>()

        CoroutineScope(Dispatchers.Main).launch {
            getCacheEntity(
                cacheKey = cacheKey,
                mutableLiveData = mutableLiveData,
                getCachedEntity = { itemDao.getItems().map { it.mapToItem() } },
                getRemoteEntity = {
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
                                error.localizedMessage ?: context.getString(R.string.error_request_get_items)
                            )
                        }
                    )
                },
                addToCache = { items -> itemDao.addItems(items.map { it.mapToEntity() }) },
                cacheMinutes = 1
            )
        }

        return mutableLiveData
    }

    override fun addItem(item: Item): LiveData<Resource<Item>> {
        val mutableLiveData = MutableLiveData<Resource<Item>>()

        CoroutineScope(Dispatchers.Main).launch {
            putCacheEntity(
                mutableLiveData = mutableLiveData,
                putRemoteEntity = {
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
                },
                addToCache = { returnedItem -> itemDao.addItem(returnedItem.mapToEntity()) }
            )
        }

        return mutableLiveData
    }

    /**
     * Get cached entity.
     *
     * If cache exists and is not yet expired, the cache is not updated from the remote, to
     * avoid making too many requests.
     * You can adjust the cacheMinutes parameter for how long you want your cache to be valid,
     * if you want your cache to be updated on all requests you can set cacheMinutes to 0.
     *
     * When a cached entity is available, even when expired, it's emitted by the returned LiveData,
     * and a updated entity is emitted when returned successfully from the remote.
     */
    private suspend fun <T> getCacheEntity(cacheKey: String,
                                           mutableLiveData: MutableLiveData<Resource<T>>,
                                           getCachedEntity: suspend () -> T?,
                                           getRemoteEntity: suspend () -> Resource<T>,
                                           addToCache: suspend (T) -> Unit,
                                           cacheMinutes: Int = 1) {

        // Get cache entry so we can assert whether cache is still valid (i.e. not expired).
        val cacheEntry = withContext(Dispatchers.IO) {
            cacheDao.getCacheEntry(cacheKey)
        }

        // Get cached entity if present.
        val cachedEntity = withContext(Dispatchers.IO) {
            getCachedEntity()
        }

        // Time when cache expires, which here is 1 minute in the past.
        val cacheExpireTime = Calendar.getInstance().apply {
            timeZone = TimeZone.getDefault()
            time = Date()
            // Amount of time items are cached.
            add(Calendar.MINUTE, -abs(cacheMinutes))
        }.time

        // Return cache if present.
        if (cachedEntity != null) {
            Timber.d("Provide entity from cache: $cacheKey")
            mutableLiveData.value = Resource.success(cachedEntity)
        }

        // Request entity, and store it in cache, if cache does not exist or cache is expired.
        if (cacheEntry == null || cachedEntity == null || cacheEntry.cachedAt.before(cacheExpireTime)) {
            Timber.d("Requesting entity from remote: $cacheKey")

            val resource = getRemoteEntity()
            mutableLiveData.value = resource

            // Cache response if successful.
            if (resource.status == Resource.Status.SUCCESS) {
                resource.data?.let {
                    withContext(Dispatchers.IO) {
                        Timber.d("Updating cache: $cacheKey")

                        // Add entity to cache.
                        addToCache(it)

                        // Add cache entry to store when entity was last cached.
                        cacheDao.addCacheEntry(
                            CacheEntry(cacheKey, Date())
                        )
                    }
                }
            }
        }
    }

    /**
     * Put into remote and cache returned entity.
     *
     * No cache entry is added, as we do not want to cache a PUT/POST requests.
     */
    private suspend fun <T> putCacheEntity(mutableLiveData: MutableLiveData<Resource<T>>,
                                           putRemoteEntity: suspend () -> Resource<T>,
                                           addToCache: suspend (T) -> Unit) {

        val resource = putRemoteEntity()
        mutableLiveData.value = resource

        // Cache response if successful.
        if (resource.status == Resource.Status.SUCCESS) {
            resource.data?.let {
                withContext(Dispatchers.IO) {
                    Timber.d("Adding new entity to cache")

                    // Add new entity to cache.
                    addToCache(it)
                }
            }
        }
    }
}