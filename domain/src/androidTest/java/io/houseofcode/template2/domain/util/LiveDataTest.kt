package io.houseofcode.template2.domain.util

import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import io.houseofcode.template2.domain.CoroutineTest
import io.houseofcode.template2.domain.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test of LiveData.
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class LiveDataTest: CoroutineTest() {

    // Values of emitted data.
    private val firstText = "Value 1"
    private val firstNumber = 1
    private val secondNumber = 2
    private val firstBoolean = false
    private val secondBoolean = true

    @Test
    fun testCombineTwoLiveData() {
        // Setup original LiveData.
        val liveData1 = MutableLiveData<String>()
        val liveData2 = MutableLiveData<Int>()

        // Combine original data into pair of latest.
        val combinedLiveData = combinePair(liveData1, liveData2) { text, number ->
            // Return values as pair.
            Pair(text, number)
        }

        // Post value for each original LiveData.
        liveData1.postValue(firstText)
        liveData2.postValue(firstNumber)

        // Get first combined data.
        val firstCombinedData = combinedLiveData.getOrAwaitValue()

        // Check first emitted item from combined LiveData.
        assertThat(firstCombinedData.first).isEqualTo(firstText)
        assertThat(firstCombinedData.second).isEqualTo(firstNumber)

        // Emit new item on second original LiveData.
        liveData2.postValue(secondNumber)

        // Get second combined data.
        val secondCombinedData = combinedLiveData.getOrAwaitValue()

        // Check second emitted item from combined LiveData.
        assertThat(secondCombinedData.first).isEqualTo(firstText)
        assertThat(secondCombinedData.second).isEqualTo(secondNumber)
    }

    @Test
    fun testCombineThreeLiveData() {
        // Setup original LiveData.
        val liveData1 = MutableLiveData<String>()
        val liveData2 = MutableLiveData<Int>()
        val liveData3 = MutableLiveData<Boolean>()

        // Combine original data into pair of latest.
        val combinedLiveData = combineTriple(liveData1, liveData2, liveData3) { text, number, boolean ->
            // Return values as triple.
            Triple(text, number, boolean)
        }

        // Post value for each original LiveData.
        liveData1.postValue(firstText)
        liveData2.postValue(firstNumber)
        liveData3.postValue(firstBoolean)

        // Get first combined data.
        val firstCombinedData = combinedLiveData.getOrAwaitValue()

        // Check first emitted item from combined LiveData.
        assertThat(firstCombinedData.first).isEqualTo(firstText)
        assertThat(firstCombinedData.second).isEqualTo(firstNumber)
        assertThat(firstCombinedData.third).isEqualTo(firstBoolean)

        // Emit new item on second original LiveData.
        liveData2.postValue(secondNumber)
        liveData3.postValue(secondBoolean)

        // Get second combined data.
        val secondCombinedData = combinedLiveData.getOrAwaitValue()

        // Check second emitted item from combined LiveData.
        assertThat(secondCombinedData.first).isEqualTo(firstText)
        assertThat(secondCombinedData.second).isEqualTo(secondNumber)
        assertThat(secondCombinedData.third).isEqualTo(secondBoolean)
    }

    @Test
    fun testCombineAnyLiveData() {
        // Setup LiveData of different data types (string/int/boolean).
        val listDataList = listOf(
            MutableLiveData<String>(),
            MutableLiveData<Int>(),
            MutableLiveData<Boolean>()
        )

        // Combine all types.
        val combinedLiveData = combineAny(listDataList)

        // Post value to each.
        (listDataList[0] as MutableLiveData<String>).postValue("a")
        (listDataList[1] as MutableLiveData<Int>).postValue(1)
        (listDataList[2] as MutableLiveData<Boolean>).postValue(true)

        // Get combined data and check values.
        val firstCombinedData = combinedLiveData.getOrAwaitValue()
        assertThat(firstCombinedData[0]).isEqualTo("a")
        assertThat(firstCombinedData[1]).isEqualTo(1)
        assertThat(firstCombinedData[2]).isEqualTo(true)

        // Post new value.
        (listDataList[2] as MutableLiveData<Boolean>).postValue(false)

        // Get combined data and check that new value was updated.
        val secondCombinedData = combinedLiveData.getOrAwaitValue()
        assertThat(secondCombinedData[0]).isEqualTo("a")
        assertThat(secondCombinedData[1]).isEqualTo(1)
        assertThat(secondCombinedData[2]).isEqualTo(false)
    }

    @Test
    fun testCombineLiveData() {
        // Setup LiveData of single data type (string).
        val listDataList = listOf(
            MutableLiveData<String>(),
            MutableLiveData<String>(),
            MutableLiveData<String>()
        )

        // Combine all.
        val combinedLiveData = combine<String>(
            listDataList[0], listDataList[1], listDataList[2]
        )

        // Post value to each.
        listDataList[0].postValue("a")
        listDataList[1].postValue("b")
        listDataList[2].postValue("c")

        // Get combined data and check values.
        val firstCombinedData = combinedLiveData.getOrAwaitValue()
        assertThat(firstCombinedData[0]).isEqualTo("a")
        assertThat(firstCombinedData[1]).isEqualTo("b")
        assertThat(firstCombinedData[2]).isEqualTo("c")

        // Post new value.
        listDataList[2].postValue("d")

        // Get combined data and check that new value was updated.
        val secondCombinedData = combinedLiveData.getOrAwaitValue()
        assertThat(secondCombinedData[0]).isEqualTo("a")
        assertThat(secondCombinedData[1]).isEqualTo("b")
        assertThat(secondCombinedData[2]).isEqualTo("d")
    }
}
