package io.houseofcode.template2.domain.usecase

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import io.houseofcode.template2.domain.CoroutineTest
import io.houseofcode.template2.domain.repository.PersistentStorageRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.ArgumentMatchers.anyString

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SetFirstRunFlagUseCaseTest: CoroutineTest() {

    private var runFlagValue: Boolean? = null

    // Get mocked repository.
    private val repository: PersistentStorageRepository = mock {
        onBlocking { setValue(anyString(), anyBoolean()) }.then { invocation ->
            val key: String = invocation.getArgument<String>(0)
            val newValue: Boolean = invocation.getArgument<Boolean>(1)

            if (key == PersistentStorageRepository.PREF_FIRST_LAUNCH) {
                runFlagValue = newValue
            }

            return@then Unit
        }
    }

    // Setup use case.
    private val useCase: SetFirstRunFlagUseCase by lazy { SetFirstRunFlagUseCase(repository) }

    @Test
    fun testSetRunFlag() {
        // Check that saved value has not yet been set.
        assertThat(runFlagValue).isNull()

        // Set new value.
        val newRunFlagValue = true
        useCase.execute(SetFirstRunFlagUseCase.Params(newRunFlagValue))

        // Check that saved value matches what we just set.
        assertThat(runFlagValue).isEqualTo(newRunFlagValue)
    }
}
