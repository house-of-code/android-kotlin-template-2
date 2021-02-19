package io.houseofcode.template2.domain.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

/**
 * Combine/merge two different LiveData into a single LiveData.
 * Resulting LiveData omits data when both LiveData is set.
 */
fun <A, B, R> combinePair(liveDataA: LiveData<A>, liveDataB: LiveData<B>, block: (A?, B?) -> R): LiveData<R> {
    return MediatorLiveData<R>().apply {
        var lastA: A? = null
        var lastB: B? = null

        fun update() {
            if (lastA != null && lastB != null) {
                this.value = block.invoke(lastA, lastB)
            }
        }

        addSource(liveDataA) {
            lastA = it
            update()
        }
        addSource(liveDataB) {
            lastB = it
            update()
        }
    }
}

/**
 * Combine/merge three different LiveData into a single LiveData.
 * Resulting LiveData omits data when all three LiveData is set.
 */
fun <A, B, C, R> combineTriple(liveDataA: LiveData<A>, liveDataB: LiveData<B>, liveDataC: LiveData<C>, block: (A?, B?, C?) -> R): LiveData<R> {
    return MediatorLiveData<R>().apply {
        var lastA: A? = null
        var lastB: B? = null
        var lastC: C? = null

        fun update() {
            if (lastA != null && lastB != null && lastC != null) {
                this.value = block.invoke(lastA, lastB, lastC)
            }
        }

        addSource(liveDataA) {
            lastA = it
            update()
        }
        addSource(liveDataB) {
            lastB = it
            update()
        }
        addSource(liveDataC) {
            lastC = it
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
