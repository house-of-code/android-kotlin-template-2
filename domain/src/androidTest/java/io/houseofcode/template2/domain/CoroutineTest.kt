package io.houseofcode.template2.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import java.util.concurrent.Executors

/**
 * Setup main thread for Kotlin Coroutines and handle all LiveData synchronously.
 * The #setUp and #tearDown methods are used to manage surrogate UI thread, make sure to
 * call super on these methods if overridden.
 */
@ExperimentalCoroutinesApi
open class CoroutineTest {

    // Handle LiveData synchronously.
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // New UI thread for coroutines.
    private val mainThreadSurrogate = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    @Before
    open fun setUp() {
        // Setup main thread for coroutines.
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    open fun tearDown() {
        // Reset main dispatcher.
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }
}