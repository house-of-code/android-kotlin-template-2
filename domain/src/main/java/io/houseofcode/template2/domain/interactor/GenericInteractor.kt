package io.houseofcode.template2.domain.interactor

/**
 * Interactor / use case for generic data.
 * Optional parameters can be passed to #build method and the #process method can be used to process returned data.
 * @param T Generic return type.
 * @param Params Type of optional parameter data class.
 */
abstract class GenericInteractor<T, in Params: Any?> {

    /**
     * Build use case from optionally provided parameters and return data to #process method.
     * @param params Optional parameters for building use case.
     * @return Generic data.
     */
    protected abstract fun build(params: Params?): T

    /**
     * Post process data returned by #build.
     * All business logic associated with the use case should be contained in this method
     * to improve testability of the applications features.
     * Data can be passed right through this method, if no post processing is necessary.
     * @param data Data for optional post processing.
     * @return Post processed data.
     */
    protected open fun process(data: T, params: Params?): T = data

    /**
     * Overridable execution method for returning generic data.
     * @param params Optional parameters passed to #build method.
     * @return Processed data returned from use case.
     */
    open fun execute(params: Params? = null): T {
        return process(build(params), params)
    }
}