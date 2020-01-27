package io.houseofcode.template2.domain.usecase

import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.houseofcode.template2.domain.CoroutineTest
import io.houseofcode.template2.domain.getOrAwaitValue
import io.houseofcode.template2.domain.model.LoginToken
import io.houseofcode.template2.domain.model.Resource
import io.houseofcode.template2.domain.pushValue
import io.houseofcode.template2.domain.repository.ItemRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class LoginUseCaseTest: CoroutineTest() {

    private val testToken = "test_token"
    private var savedToken: LoginToken? = null

    // Get mocked repository.
    private val repository: ItemRepository = mock {
        onBlocking { login(any<String>(), any<String>()) }.doReturn(
            MutableLiveData<Resource<LoginToken>>().pushValue(
                Resource.success(
                    LoginToken(testToken)
                )
            )
        )
        onBlocking { saveToken(any<LoginToken>()) }.then { invocation ->
            val loginToken = invocation.arguments[0]
            if (loginToken is LoginToken) {
                savedToken = loginToken
            }
        }
    }

    // Setup use case.
    private val useCase: LoginUseCase by lazy { LoginUseCase(repository) }

    @Test
    fun testReceivedToken() {
        // Execute use case and receive result as LiveData.
        val liveData = useCase.execute(LoginUseCase.Params("lani", "s3cr3t"))
        // Extract value from LiveData once.
        val resource = liveData.getOrAwaitValue()

        // Check response status.
        assertThat(resource.status).isEqualTo(Resource.Status.SUCCESS)

        // Check response.
        assertThat(resource.data).isNotNull()
        assertThat(resource.data?.token).isEqualTo(testToken)
    }

    @Test
    fun testSavedToken() {
        // Execute use case and receive result as LiveData.
        val liveData = useCase.execute(LoginUseCase.Params("lani", "s3cr3t"))
        // Extract value from LiveData once.
        val resource = liveData.getOrAwaitValue()

        // Check response status.
        assertThat(resource.status).isEqualTo(Resource.Status.SUCCESS)

        // Check saved token.
        assertThat(savedToken?.token).isEqualTo(testToken)
    }
}