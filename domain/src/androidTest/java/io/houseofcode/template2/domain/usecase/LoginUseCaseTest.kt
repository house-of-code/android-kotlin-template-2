package io.houseofcode.template2.domain.usecase

import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.houseofcode.template2.domain.CoroutineTest
import io.houseofcode.template2.domain.getOrAwaitValue
import io.houseofcode.template2.domain.model.Resource
import io.houseofcode.template2.domain.pushValue
import io.houseofcode.template2.domain.repository.ItemRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class LoginUseCaseTest: CoroutineTest() {

    private val testToken = "test_token"
    private var savedToken: String? = null

    // Get mocked repository.
    private val repository: ItemRepository = mock {
        onBlocking { login(anyString(), anyString()) }.doReturn(
            MutableLiveData<Resource<String>>().pushValue(
                Resource.success(testToken)
            )
        )
        onBlocking { saveToken(anyString()) }.then { invocation ->
            invocation.getArgument<String>(0).let { loginToken ->
                savedToken = loginToken
            }
            return@then Unit
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
        assertThat(resource.data).isEqualTo(testToken)
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
        assertThat(savedToken).isEqualTo(testToken)
    }
}