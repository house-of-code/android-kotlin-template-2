package io.houseofcode.template2.domain.usecase

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import io.houseofcode.template2.domain.CoroutineTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class GenerateItemUseCaseTest: CoroutineTest() {

    // Setup use case.
    private val useCase: GenerateItemUseCase by lazy { GenerateItemUseCase() }

    @Test
    fun testGeneratedItem() {
        val itemId = "1"

        // Execute use case and receive newly created item.
        val item = useCase.execute(
            GenerateItemUseCase.Params(itemId)
        )

        // Evaluate result.
        Truth.assertThat(item.id).isEqualTo(itemId)
        Truth.assertThat(item.createdAt.time).isAtMost(Date().time)
    }
}