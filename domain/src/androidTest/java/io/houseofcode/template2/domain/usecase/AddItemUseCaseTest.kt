package io.houseofcode.template2.domain.usecase

import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
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
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class AddItemUseCaseTest: CoroutineTest() {

    private val items = arrayListOf<Item>()

    // Get mocked repository.
    private val repository: ItemRepository = mock {
        onBlocking { addItem(any<Item>()) }.thenAnswer { invocation ->
            val item = invocation.getArgument(0, Item::class.java)

            items.add(item)
            MutableLiveData<Resource<Item>>().pushValue(
                Resource(Resource.Status.SUCCESS, item, null)
            )
        }
    }

    // Setup use case.
    private val useCase: AddItemUseCase by lazy { AddItemUseCase(repository) }

    @Test
    fun testAddItemCompleted() {
        val item = Item("0", "Item 0", Date())

        // Execute use case and receive result as LiveData.
        val liveData = useCase.execute(AddItemUseCase.Params(item))
        // Extract value from LiveData once.
        val resource = liveData.getOrAwaitValue()

        // Evaluate result.
        assertThat(resource.status).isEqualTo(Resource.Status.SUCCESS)
        assertThat(resource.data).isInstanceOf(Item::class.java)
        assertThat(resource.data).isEqualTo(item)

        assertThat(resource.data?.id).isEqualTo(item.id)
    }
}