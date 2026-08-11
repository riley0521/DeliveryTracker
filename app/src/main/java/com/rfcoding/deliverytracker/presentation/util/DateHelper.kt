package com.rfcoding.deliverytracker.presentation.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun LocalDateTime.format(pattern: String = "yyyy-MM-dd hh:mm a"): String {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return formatter.format(this)
}