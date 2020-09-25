package io.houseofcode.template2.domain.usecase

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import io.houseofcode.template2.domain.CoroutineTest
import io.houseofcode.template2.domain.repository.PersistentStorageRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyList

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ClearUserDataUseCaseTest: CoroutineTest() {

    private val savedData: MutableMap<String, Any> = mutableMapOf()

    // Get mocked repository.
    private val repository: PersistentStorageRepository = mock {
        onBlocking { clearVolatileData(anyList<String>()) }.then { invocation ->
            (invocation.arguments.first() as? List<String>)?.let { keys ->
                keys.forEach { savedData.remove(it) }
            }
        }
    }

    // Setup use case.
    private val useCase: ClearUserDataUseCase by lazy { ClearUserDataUseCase(repository) }

    override fun setUp() {
        super.setUp()

        savedData[PersistentStorageRepository.PREF_LOGIN_TOKEN] = "abc123"
    }

    @Test
    fun testClearUserData() {
        // Check that value is set before clearing user data.
        val initialValue = savedData[PersistentStorageRepository.PREF_LOGIN_TOKEN] as? String
        assertThat(initialValue).isNotNull()

        // Clear user data.
        useCase.execute()

        // Check that user data has been cleared.
        val clearedValue = savedData[PersistentStorageRepository.PREF_LOGIN_TOKEN] as? String
        assertThat(clearedValue).isNull()
    }
}