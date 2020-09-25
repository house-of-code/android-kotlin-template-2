package io.houseofcode.template2.domain.integration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth.assertThat
import io.houseofcode.template2.domain.CoroutineTest
import io.houseofcode.template2.domain.getOrAwaitValue
import io.houseofcode.template2.domain.model.Item
import io.houseofcode.template2.domain.model.Resource
import io.houseofcode.template2.domain.repository.ItemRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Test
import java.util.*

@ExperimentalCoroutinesApi
class ItemRepositoryTest: CoroutineTest() {

    companion object {
        // Login token returned by repository.
        const val LOGIN_TOKEN = "my-login-token"
    }

    // Login token and item list saved by repository.
    var savedLoginToken: String? = null
    var items = mutableListOf<Item>()

    // Mocked repository.
    private val mockedRepository: ItemRepository = object: ItemRepository {
        // Returns successful login token.
        override fun login(email: String, password: String): LiveData<Resource<String>> {
            return MutableLiveData<Resource<String>>().apply {
                value = Resource.success(LOGIN_TOKEN)
            }
        }

        // Saves token locally within test.
        override fun saveToken(loginToken: String) {
            savedLoginToken = loginToken
        }

        // Returns item from saved items if present, an error is returned otherwise.
        override fun getItem(id: String): LiveData<Resource<Item>> {
            return MutableLiveData<Resource<Item>>().apply {
                val item = items.find { it.id == id }
                value = if (item == null) {
                    Resource.error("Not found")
                } else {
                    Resource.success(item)
                }
            }
        }

        // Returns all saved items.
        override fun getItems(): LiveData<Resource<List<Item>>> {
            return MutableLiveData<Resource<List<Item>>>().apply {
                value = Resource.success(items)
            }
        }

        // Adds item to locally saved items and returns added item.
        override fun addItem(item: Item): LiveData<Resource<Item>> {
            items.add(item)
            return MutableLiveData<Resource<Item>>().apply {
                value = Resource.success(item)
            }
        }
    }

    @Before
    override fun setUp() {
        super.setUp()

        // Add initially saved items.
        items.addAll(
            listOf(
                Item(id = "1", title = "Item 1", createdAt = Date()),
                Item(id = "2", title = "Item 2", createdAt = Date()),
                Item(id = "3", title = "Item 3", createdAt = Date())
            )
        )
    }

    override fun tearDown() {
        super.tearDown()

        // Reset saved login token.
        savedLoginToken = null

        // Clear saved items.
        items.clear()
    }

    @Test
    fun testLogin() {
        // Perform login with mocked credentials.
        val liveData = mockedRepository.login("test@houseofcode.io", "s3cr3t")
        val resource = liveData.getOrAwaitValue()

        // Check that login request is successful.
        assertThat(resource.status).isEqualTo(Resource.Status.SUCCESS)

        // Check that returned login token is the expected value.
        assertThat(resource.data).isNotNull()
        assertThat(resource.data).isEqualTo(LOGIN_TOKEN)
    }

    @Test
    fun testSaveToken() {
        // Check that no login token has previously been saved.
        assertThat(savedLoginToken).isNull()

        // Save login token.
        mockedRepository.saveToken(LOGIN_TOKEN)

        // Check that login token is now saved.
        assertThat(savedLoginToken).isEqualTo(LOGIN_TOKEN)
    }

    @Test
    fun testGetItem() {
        val itemId = items.first().id

        // Get item by id.
        val liveDate = mockedRepository.getItem(itemId)
        val resource = liveDate.getOrAwaitValue()

        // Check that response is successful.
        assertThat(resource.status).isEqualTo(Resource.Status.SUCCESS)

        // Check that returned item id matches.
        assertThat(resource.data).isNotNull()
        assertThat(resource.data?.id).isEqualTo(itemId)
    }

    @Test
    fun testGetItems() {
        // Get all items.
        val liveData = mockedRepository.getItems()
        val resource = liveData.getOrAwaitValue()

        // Check that request is successful.
        assertThat(resource.status).isEqualTo(Resource.Status.SUCCESS)

        // Check that returned items match the number of locally saved items.
        assertThat(resource.data).isNotNull()
        assertThat(resource.data?.size).isEqualTo(items.size)
    }

    @Test
    fun testAddItem() {
        /*
         * Add new item.
         */
        val newItem = Item(id = "4", title = "Item 4", createdAt = Date())

        // Perform request to add new item.
        val addedItemLiveData = mockedRepository.addItem(newItem)
        val newItemResource = addedItemLiveData.getOrAwaitValue()

        // Check that request is successful.
        assertThat(newItemResource.status).isEqualTo(Resource.Status.SUCCESS)

        // Check that returned item matches.
        assertThat(newItemResource.data).isNotNull()
        assertThat(newItemResource.data).isEqualTo(newItem)

        /*
         * Get all items.
         */
        val allItemsLiveData = mockedRepository.getItems()
        val allItemsResource = allItemsLiveData.getOrAwaitValue()

        // Check that returned items contains newly added item.
        assertThat(allItemsResource.status).isEqualTo(Resource.Status.SUCCESS)
        assertThat(allItemsResource.data).isNotNull()
        assertThat(allItemsResource.data?.size).isEqualTo(items.size)
        assertThat(allItemsResource.data).contains(newItem)
    }
}