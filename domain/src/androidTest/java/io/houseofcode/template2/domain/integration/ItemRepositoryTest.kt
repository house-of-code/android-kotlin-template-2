package io.houseofcode.template2.domain.integration

import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import io.houseofcode.template2.domain.CoroutineTest
import io.houseofcode.template2.domain.getOrAwaitValue
import io.houseofcode.template2.domain.model.Item
import io.houseofcode.template2.domain.model.Resource
import io.houseofcode.template2.domain.pushValue
import io.houseofcode.template2.domain.repository.ItemRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import java.util.*

@ExperimentalCoroutinesApi
class ItemRepositoryTest: CoroutineTest() {

    companion object {
        // Login token returned by repository.
        const val LOGIN_TOKEN = "my-login-token"
    }

    // Login token and item list saved by repository.
    private var savedLoginToken: String? = null
    private var items = mutableListOf<Item>()

    // Mocked repository.
    private val mockedRepository: ItemRepository = mock {
        onBlocking { login(anyString(), anyString()) }.thenAnswer {
            // Returns successful login token.
            MutableLiveData<Resource<String>>().pushValue(
                Resource.success(LOGIN_TOKEN)
            )
        }
        onBlocking { getItem(anyString()) }.thenAnswer { invocation ->
            val itemId: String = invocation.getArgument<String>(0)
            val item = items.find { it.id == itemId }

            // Returns item from saved items if present, an error is returned otherwise.
            MutableLiveData<Resource<Item>>().pushValue(
                if (item == null) {
                    Resource.error("Not found")
                } else {
                    Resource.success(item)
                }
            )
        }
        onBlocking { getItems() }.thenAnswer {
            MutableLiveData<Resource<List<Item>>>().pushValue(
                Resource.success(items)
            )
        }
        onBlocking { addItem(any<Item>()) }.thenAnswer { invocation ->
            val newItem = invocation.getArgument<Item>(0)
            items.add(newItem)
            MutableLiveData<Resource<Item>>().pushValue(
                Resource.success(newItem)
            )
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
