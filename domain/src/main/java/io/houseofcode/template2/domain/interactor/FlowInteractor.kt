package io.houseofcode.template2.domain.interactor

import kotlinx.coroutines.flow.Flow

/**
 * Interactor / use case for returning Flow.
 * All Flow use cases should implement this interface for easier testing and execution.
 * Optional parameters can be passed to #build method and the #process method can be used to process returned data.
 * @param T Return type that will be wrapped as Flow object.
 * @param Params Type of optional parameter data class.
 */
abstract class FlowInteractor<T, in Params: Any?> {

    /**
     * Build use case from optionally provided parameters and return data to #process method.
     * @param params Optional parameters for building use case and retrieving data response.
     * @return Raw data response from use case.
     */
    protected abstract fun build(params: Params?): Flow<T>

    /**
     * Optional post processing of data returned by #build.
     * All business logic associated with the use case should be contained in this method
     * to improve testability of the applications features.
     * Data can be passed right through this method, if no post processing is necessary.
     * @param flow Data for optional post processing.
     * @return Post processed data.
     */
    protected open fun process(flow: Flow<T>, params: Params?): Flow<T> = flow

    /**
     * Overridable execution method for returning data as Flow.
     * @param params Optional parameters passed to #build method.
     * @return Processed data returned from use case, wrapped as Flow.
     */
    open fun execute(params: Params? = null): Flow<T> {
        return process(build(params), params)
    }
}