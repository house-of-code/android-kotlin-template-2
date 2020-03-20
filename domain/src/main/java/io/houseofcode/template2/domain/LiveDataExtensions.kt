package io.houseofcode.template2.domain

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