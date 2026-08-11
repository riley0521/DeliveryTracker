package com.rfcoding.deliverytracker.domain

sealed interface Result<out D, out E: Error> {
    data class Success<out D>(val data: D): Result<D, Nothing>
    data class Failure<out E: Error>(val error: E): Result<Nothing, E>
}

inline fun <T, E: Error, R> Result<T, E>.map(block: (T) -> R): Result<R, E> {
    return when (this) {
        is Result.Failure -> this
        is Result.Success -> Result.Success(block(this.data))
    }
}

interface Error