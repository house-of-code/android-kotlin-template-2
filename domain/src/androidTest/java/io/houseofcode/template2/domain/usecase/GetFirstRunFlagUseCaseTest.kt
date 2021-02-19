package io.houseofcode.template2.domain.usecase

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import io.houseofcode.template2.domain.CoroutineTest
import io.houseofcode.template2.domain.repository.PersistentStorageRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class GetFirstRunFlagUseCaseTest: CoroutineTest() {

    private val runFlagValue = false

    // Get mocked repository.
    private val repository: PersistentStorageRepository = mock {
        onBlocking { observeValue(PersistentStorageRepository.PREF_FIRST_LAUNCH, true) }.thenAnswer {
            return@thenAnswer flow {
                emit(runFlagValue)
            }
        }
    }

    // Setup use case.
    private val useCase: GetFirstRunFlagUseCase by lazy { GetFirstRunFlagUseCase(repository) }

    @Test
    fun testObservableRunFlag() {
        runBlockingTest {
            // Execute and get first emitted value.
            val runFlag = useCase.execute().firstOrNull()

            // Check that returned values matches what repository emits.
            assertThat(runFlag).isNotNull()
            assertThat(runFlag).isEqualTo(runFlag)
        }
    }
}