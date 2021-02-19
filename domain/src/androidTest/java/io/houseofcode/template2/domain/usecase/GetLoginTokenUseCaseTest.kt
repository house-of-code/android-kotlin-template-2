package io.houseofcode.template2.domain.usecase

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import com.nhaarman.mockitokotlin2.mock
import io.houseofcode.template2.domain.CoroutineTest
import io.houseofcode.template2.domain.repository.PersistentStorageRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class GetLoginTokenUseCaseTest: CoroutineTest() {

    private val savedLoginToken = "saved-login-token"

    // Get mocked repository.
    private val repository: PersistentStorageRepository = mock {
        onBlocking { getValue<String?>(PersistentStorageRepository.PREF_LOGIN_TOKEN, null) }.thenAnswer {
            return@thenAnswer savedLoginToken
        }
    }

    // Setup use case.
    private val useCase: GetLoginTokenUseCase by lazy { GetLoginTokenUseCase(repository) }

    @Test
    fun testLoginToken() {
        // Execute use case and receive result as String.
        val loginToken = useCase.execute()

        // Check response.
        Truth.assertThat(loginToken).isNotNull()
        Truth.assertThat(loginToken).isEqualTo(savedLoginToken)
    }
}