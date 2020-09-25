package io.houseofcode.template2.domain.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

/**
 * Zip or merge two [LiveData] into a single [LiveData] of different type.
 */
fun <T, K, R> combineLatest(a: LiveData<T>, b: LiveData<K>, block: (T, K) -> R ): LiveData<R> {
    return MediatorLiveData<R>().apply {
        var lastA: T? = null
        var lastB: K? = null

        fun update() {
            val localLastA = lastA
            val localLastB = lastB
            if (localLastA != null && localLastB != null) {
                this.value = block.invoke(localLastA, localLastB)
            }
        }

        addSource(a) {
            lastA = it
            update()
        }
        addSource(b) {
            lastB = it
            update()
        }
    }
}

/**
 * Merge set of LiveData, of any data type, into single LiveData with all data.
 * Resulting LiveData omits data when values of all LiveData is set.
 */
fun combineAny(liveDataList: List<LiveData<out Any>>): LiveData<List<Any>> {
    // Create list of last values set by original LiveData.
    val lastLiveDataList = arrayOfNulls<Any>(liveDataList.size)

    return MediatorLiveData<List<Any>>().apply {
        // Omits all values, if all values are set.
        fun update() {
            if (lastLiveDataList.all { it != null }) {
                this.value = lastLiveDataList.filterNotNull()
            }
        }

        // Observe each original LiveData.
        liveDataList.forEachIndexed { index, liveData ->
            this.addSource(liveData) {
                // Set value from original LiveData and attempt LiveData update.
                lastLiveDataList[index] = it
                update()
            }
        }
    }
}

/**
 * Merge set of LiveData, of single data type, info single LiveData with all data.
 * Resulting LiveData omits data when values of all LiveData is set.
 */
inline fun <reified T> combine(vararg liveDataList: LiveData<T>): LiveData<Array<T?>> {
    // Create list of last values set by original LiveData.
    val lastLiveDataList = arrayOfNulls<T>(liveDataList.size)

    return MediatorLiveData<Array<T?>>().apply {
        liveDataList.forEachIndexed { index, liveData ->
            // Observe each original LiveData.
            this.addSource(liveData) {
                // Set value from original LiveData.
                lastLiveDataList[index] = it

                // Omits all values, if all values are set.
                if (lastLiveDataList.all { last -> last != null }) {
                    this.value = lastLiveDataList
                }
            }
        }
    }
}
