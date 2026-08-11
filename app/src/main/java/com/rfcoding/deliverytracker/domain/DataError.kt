package com.rfcoding.deliverytracker.domain

sealed interface DataError: Error {

    enum class Remote: DataError {
        SERIALIZATION,
        NOT_FOUND,
        UNKNOWN
    }
}