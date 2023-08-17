package com.example.mymall_kotlin.util

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {

    class Ideal<T> : Resource<T>()

    class Success<T>(data: T?) : Resource<T>(data)

    class Error<T>(message: String?) : Resource<T>(message = message)

    class Loading<T> : Resource<T>()
}
