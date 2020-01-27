package io.houseofcode.template2.domain.interactor

import androidx.lifecycle.LiveData

/**
 * Interactor / use case for covariant return types of LiveData.
 * Use cases which return a subtype of LiveData should implement this interface for easier testing and execution.
 * Optional parameters can be passed to #build method and the #process method can be used to process returned data.
 * Returned data will be wrapped as a subclass of LiveData when executed.
 * @param T Return type that will be wrapped as LiveData object.
 * @param Params Type of optional parameter data class.
 */
abstract class CovariantInteractor<T: LiveData<*>, in Params: Any?> {

    /**
     * Build use case from optionally provided parameters and return data to #process method.
     * Returned data will be wrapped as LiveData when use case is executed.
     * @param params Optional parameters for building use case.
     * @return LiveData subtype from use case.
     */
    protected abstract fun build(params: Params? = null): T

    /**
     * Post process data returned by #build.
     * All business logic associated with the use case should be contained in this method
     * to improve testability of the applications features.
     * Data can be passed right through this method, if no post processing is necessary.
     * @param data Data for optional post processing.
     * @return Post processed data.
     */
    protected open fun process(data: T): T = data

    /**
     * Overridable execution method for returning data as subtype of LiveData.
     * @param params Optional parameters passed to #build method.
     * @return Processed data returned from use case, wrapped as subtype of LiveData.
     */
    open fun execute(params: Params? = null): T {
        return process(build(params))
    }
}