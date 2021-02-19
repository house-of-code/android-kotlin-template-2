package io.houseofcode.template2.domain.util

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.Serializable
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GenericUtilsTest {

    @Serializable
    data class Person(var name: String)

    @Test
    fun testDeepCopy() {
        // Create original list of items.
        val list = listOf(
            Person("Alex"),
            Person("Anna"),
            Person("Austin")
        )
        // Create shallow copy of original list, still keeping references to original list.
        val listShallowCopy = list.toMutableList()
        // Create deep copy of original list.
        val listDeepCopy = list.deepCopy()

        // Modify first item in original list.
        list.first().name = "Alice"

        // Check that lists still contain the same number of items.
        assertThat(list.size).isEqualTo(listShallowCopy.size)
        assertThat(list.size).isEqualTo(listDeepCopy.size)

        // Check that all lists still has first item.
        assertThat(list.firstOrNull()).isNotNull()
        assertThat(listShallowCopy.firstOrNull()).isNotNull()
        assertThat(listDeepCopy.firstOrNull()).isNotNull()

        // Check shallow copy is keeping it's reference to original list.
        assertThat(list.firstOrNull()).isEqualTo(listShallowCopy.firstOrNull())
        assertThat(list.firstOrNull().hashCode()).isEqualTo(listShallowCopy.firstOrNull().hashCode())
        assertThat(listShallowCopy.firstOrNull()?.name).isEqualTo("Alice")

        // Check that deep copy has a new object after original list was changed.
        assertThat(list.firstOrNull()).isNotEqualTo(listDeepCopy.firstOrNull())
        assertThat(list.first().hashCode()).isNotEqualTo(listDeepCopy.first().hashCode())
        assertThat(listDeepCopy.firstOrNull()?.name).isEqualTo("Alex")
    }
}
