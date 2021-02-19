package io.houseofcode.template2.domain.integration

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import io.houseofcode.template2.domain.CoroutineTest
import io.houseofcode.template2.domain.repository.PersistentStorageRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.mockito.ArgumentMatchers.anyList
import org.mockito.ArgumentMatchers.anyString

@ExperimentalCoroutinesApi
class PersistentStorageRepositoryTest: CoroutineTest() {

    private val storageMap: MutableMap<String, Any> = mutableMapOf()

    private val storageRepository: PersistentStorageRepository = mock {
        onBlocking { setValue(anyString(), any()) }.then { invocation ->
            val key = invocation.getArgument<String>(0)
            val value = invocation.getArgument<Any>(1)

            storageMap[key] = value
            return@then Unit
        }
        onBlocking { getValue<String?>(anyString(), anyOrNull()) }.thenAnswer { invocation ->
            val key = invocation.getArgument<String>(0)
            val defaultValue = invocation.getArgument<String?>(1)

            storageMap[key] ?: defaultValue
        }
        onBlocking { observeValue<String?>(anyString(), anyOrNull()) }.thenAnswer { invocation ->
            val key = invocation.getArgument<String>(0)
            val defaultValue = invocation.getArgument<String?>(1)

            flow {
                emit(storageMap[key] ?: defaultValue)
            }
        }
        onBlocking { clearVolatileData(anyList()) }.then { invocation ->
            val keys = invocation.getArgument<List<String>>(0)
            keys.forEach { key ->
                storageMap.remove(key)
            }
        }
    }

    override fun tearDown() {
        super.tearDown()

        // Clear out any stored values.
        storageMap.clear()
    }

    @Test
    fun testSetAndGet() {
        // Create data, which we can save and retrieve from repository.
        val key = "myStringKey"
        val newValue = "Hello World!"

        // Get initial value, before we set anything, and assure that no data is present.
        val initialSavedValue = storageRepository.getValue<String?>(key, null)
        assertThat(initialSavedValue).isNull()

        // Save value into repository and assure that value can be retrieved afterwards.
        storageRepository.setValue(key, newValue)
        val newSavedValue = storageRepository.getValue<String?>(key, null)
        assertThat(newSavedValue).isEqualTo(newValue)
    }

    @Test
    fun testObservingStorage() {
        val key = "hello"
        val value = "world"

        runBlockingTest {
            val observableFlow = storageRepository.observeValue<String?>(key, null)

            // Set initial value by key, and check that no value has been set.
            val initialValue = observableFlow.first()
            assertThat(initialValue).isNull()

            // Set first value.
            storageRepository.setValue(key, value)

            // Get new value and check that value is now set,
            val newValue = observableFlow.first()
            assertThat(newValue).isEqualTo(value)
        }
    }

    @Test
    fun testVolatileData() {
        // Create data set.
        val key1 = "key1"; val value1 = "value1"
        val key2 = "key2"; val value2 = "value2"
        val key3 = "key"; val value3 = "value3"
        val data = mapOf(
            key1 to value1,
            key2 to value2,
            key3 to value3
        )
        // Save data set in repository.
        data.forEach { (key, value) ->
            storageRepository.setValue(key, value)
        }

        // Get initial values from saved data.
        var one = storageRepository.getValue<String?>(key1, null)
        var two = storageRepository.getValue<String?>(key2, null)
        var three = storageRepository.getValue<String?>(key3, null)

        // Check that initial values are set and retrieved correctly.
        assertThat(one).isEqualTo(value1)
        assertThat(two).isEqualTo(value2)
        assertThat(three).isEqualTo(value3)

        // Clear some saved data.
        storageRepository.clearVolatileData(
            listOf(key2, key3)
        )

        // Update values from saved data.
        one = storageRepository.getValue<String?>(key1, null)
        two = storageRepository.getValue<String?>(key2, null)
        three = storageRepository.getValue<String?>(key3, null)

        // Check that some data has been removed and some is still present.
        assertThat(one).isEqualTo(value1)
        assertThat(two).isNull()
        assertThat(three).isNull()
    }
}
