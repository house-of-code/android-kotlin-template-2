package io.houseofcode.template2.domain.integration

import com.google.common.truth.Truth.assertThat
import io.houseofcode.template2.domain.CoroutineTest
import io.houseofcode.template2.domain.repository.PersistentStorageRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.junit.Test

@ExperimentalCoroutinesApi
class PersistentStorageRepositoryTest: CoroutineTest() {

    val storageMap: MutableMap<String, Any> = mutableMapOf()

    private val repository: PersistentStorageRepository = object: PersistentStorageRepository {
        override fun setValue(key: String, value: Any) {
            storageMap[key] = value
        }

        override fun <T> getValue(key: String, defaultValue: T): T {
            return storageMap[key] as? T ?: defaultValue
        }

        override fun <T> observeValue(key: String, defaultValue: T): Flow<T> {
            return flow {
                emit(storageMap[key] as? T ?: defaultValue)
            }
        }

        override fun clearVolatileData(keys: List<String>) {
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
        val initialSavedValue = repository.getValue<String?>(key, null)
        assertThat(initialSavedValue).isNull()

        // Save value into repository and assure that value can be retrieved afterwards.
        repository.setValue(key, newValue)
        val newSavedValue = repository.getValue<String?>(key, null)
        assertThat(newSavedValue).isEqualTo(newValue)
    }

    @Test
    fun testObservingStorage() {
        val key = "hello"
        val value = "world"

        runBlocking {
            val observableFlow = repository.observeValue<String?>(key, null)

            // Set initial value by key, and check that no value has been set.
            val initialValue = observableFlow.first()
            assertThat(initialValue).isNull()

            // Set first value.
            repository.setValue(key, value)

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
            repository.setValue(key, value)
        }

        // Get initial values from saved data.
        var one = repository.getValue<String?>(key1, null)
        var two = repository.getValue<String?>(key2, null)
        var three = repository.getValue<String?>(key3, null)

        // Check that initial values are set and retrieved correctly.
        assertThat(one).isEqualTo(value1)
        assertThat(two).isEqualTo(value2)
        assertThat(three).isEqualTo(value3)

        // Clear some saved data.
        repository.clearVolatileData(
            listOf(key2, key3)
        )

        // Update values from saved data.
        one = repository.getValue<String?>(key1, null)
        two = repository.getValue<String?>(key2, null)
        three = repository.getValue<String?>(key3, null)

        // Check that some data has been removed and some is still present.
        assertThat(one).isEqualTo(value1)
        assertThat(two).isNull()
        assertThat(three).isNull()
    }
}