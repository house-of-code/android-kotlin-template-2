package io.houseofcode.template2.domain.model

/**
 * A generic class that holds a response of a resource request.
 */
data class Resource<T>(val status: Status, var data: T?, val errorMessage: String?) {
    companion object {
        /**
         * Get successful resource response.
         * @param data Successful data response.
         * @return Successful response resource.
         */
        fun <T> success(data: T?): Resource<T> {
            return Resource(
                Status.SUCCESS,
                data,
                null
            )
        }

        /**
         * Get failed resource response.
         * @param msg Error message.
         * @param data Optional data response.
         * @return Failed response resource.
         */
        fun <T> error(msg: String, data: T? = null): Resource<T> {
            return Resource(
                Status.ERROR,
                data,
                msg
            )
        }
    }

    /**
     * Status of provided resource.
     */
    enum class Status {
        SUCCESS,
        ERROR
    }
}