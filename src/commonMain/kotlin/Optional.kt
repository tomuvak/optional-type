package com.tomuvak.optional

sealed class Optional<out T> {
    object None : Optional<Nothing>()
    data class Value<out T>(val value: T) : Optional<T>()
}
