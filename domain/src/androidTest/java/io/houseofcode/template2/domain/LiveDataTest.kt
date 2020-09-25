package io.houseofcode.template2.domain

import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import io.houseofcode.template2.domain.util.combineLatest
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
    val firstText = "Value 1"
    val firstNumber = 1
    val secondNumber = 2

    @Test
    fun testCombineLiveData() {
        // Setup original LiveData.
        val liveData1 = MutableLiveData<String>()
        val liveData2 = MutableLiveData<Int>()

        // Combine original data into pair of latest.
        val combinedLiveData = combineLatest(liveData1, liveData2) { text, number ->
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

}