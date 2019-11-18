package io.houseofcode.template2.domain.usecase

import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.houseofcode.template2.domain.CoroutineTest
import io.houseofcode.template2.domain.getTestValue
import io.houseofcode.template2.domain.model.Item
import io.houseofcode.template2.domain.model.Resource
import io.houseofcode.template2.domain.pushValue
import io.houseofcode.template2.domain.repository.ItemRepository
import io.houseofcode.template2.domain.toDate
import kotlinx.coroutines.*
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class GetItemsUseCaseTest: CoroutineTest() {

    private val now = LocalDateTime.now()
    private val items = listOf(
        Item("3", "Item 3", now.minusDays(3).toDate()),
        Item("1", "Item 1", now.minusDays(1).toDate()),
        Item("2", "Item 2", now.minusDays(2).toDate())
    )

    // Get mocked repository.
    private val repository: ItemRepository = mock {
        onBlocking { getItems() }.doReturn(
            MutableLiveData<Resource<List<Item>>>().pushValue(
                Resource.success(items)
            )
        )
    }

    // Setup use case.
    private val useCase: GetItemsUseCase by lazy { GetItemsUseCase(repository) }

    @Test
    fun testGetItemsSorting() {
        // Execute use case and receive result as LiveData.
        val liveData = useCase.execute()
        // Extract value from LiveData once.
        val resource = liveData.getTestValue()

        // Evaluate result.
        assertThat(resource.status).isEqualTo(Resource.Status.SUCCESS)
        assertThat(resource.data).isEqualTo(items.sortedBy { it.createdAt })
    }
}