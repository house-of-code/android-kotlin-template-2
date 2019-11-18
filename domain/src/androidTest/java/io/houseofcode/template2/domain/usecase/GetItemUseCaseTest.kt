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
import kotlinx.coroutines.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class GetItemUseCaseTest: CoroutineTest() {

    // Get mocked repository.
    private val repository: ItemRepository = mock {
        onBlocking { getItem("0") }.doReturn(
            MutableLiveData<Resource<Item>>().pushValue(
                Resource.success(Item("0", "Item 0", Date()))
            )
        )
    }

    // Setup use case.
    private val useCase: GetItemUseCase by lazy { GetItemUseCase(repository) }

    @Test
    fun testGetItemCompleted() {
        // Execute use case and receive result as LiveData.
        val liveData = useCase.execute(GetItemUseCase.Params("0"))
        // Extract value from LiveData once.
        val resource = liveData.getTestValue()

        // Evaluate result.
        assertThat(resource.status).isEqualTo(Resource.Status.SUCCESS)
        assertThat(resource.data).isInstanceOf(Item::class.java)
    }
}