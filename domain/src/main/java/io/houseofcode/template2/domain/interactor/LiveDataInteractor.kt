package io.houseofcode.template2.domain.interactor

import androidx.lifecycle.LiveData

/**
 * Interactor / use case for returning LiveData.
 * All LiveData use cases should implement this interface for easier testing and execution.
 * Optional parameters can be passed to #build method and the #process method can be used to process returned data.
 * @param T Return type that will be wrapped as LiveData object.
 * @param Params Type of optional parameter data class.
 */
abstract class LiveDataInteractor<T, in Params: Any?> {

    /**
     * Build use case from optionally provided parameters and return data to #process method.
     * @param params Optional parameters for building use case and retrieving data response.
     * @return Raw data response from use case.
     */
    protected abstract fun build(params: Params?): LiveData<T>

    /**
     * Optional post processing of data returned by #build.
     * All business logic associated with the use case should be contained in this method
     * to improve testability of the applications features.
     * Data can be passed right through this method, if no post processing is necessary.
     * @param liveData Data for optional post processing.
     *
     * @return Post processed data.
     */
    protected open fun process(liveData: LiveData<T>, params: Params?): LiveData<T> = liveData

    /**
     * Overridable execution method for returning data as LiveData.
     * @param params Optional parameters passed to #build method.
     * @return Processed data returned from use case, wrapped as LiveData.
     */
    open fun execute(params: Params? = null): LiveData<T> {
        return process(build(params), params)
    }
}