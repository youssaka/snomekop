package com.loyou.snomekop.core.util

typealias SimpleResource = Resource<Unit>

sealed class Resource<T>(open val data: T? = null, val message: String? = null) {
    class Loading<T>(data: T? = null): Resource<T>(data)
    class Success<T>(override val data: T): Resource<T>(data)
    class Error<T>(message: String, data: T? = null): Resource<T>(data, message)
}