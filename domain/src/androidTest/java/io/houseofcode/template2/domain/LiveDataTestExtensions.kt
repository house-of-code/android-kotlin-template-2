package io.houseofcode.template2.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Get the value from a LiveData object. We're waiting for LiveData to emit, for 2 seconds.
 * Once we got a notification via onChanged, we stop observing.
 */
fun <T> LiveData<T>.getTestValue(): T {
    val data = arrayOfNulls<Any>(1)
    val latch = CountDownLatch(1)
    val observer = object: Observer<T> {
        override fun onChanged(t: T) {
            data[0] = t
            latch.countDown()
            this@getTestValue.removeObserver(this)
        }
    }
    this.observeForever(observer)
    latch.await(2, TimeUnit.SECONDS)
    return data[0] as T
}

/**
 * Set value on MutableLiveData in Coroutine.
 */
fun <T> MutableLiveData<T>.pushValue(value: T): MutableLiveData<T> {
    CoroutineScope(Dispatchers.Main).launch {
        this@pushValue.value = withContext(Dispatchers.IO) {
            value
        }
    }

    return this
}