package com.aragabz.androidtemplate.core.common.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Converts ISO 8601 date string to LocalDateTime.
 */
fun String.toLocalDateTime(): LocalDateTime {
    val instant = Instant.parse(this)
    return LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
}

/**
 * Converts LocalDateTime to ISO 8601 string.
 */
fun LocalDateTime.toIsoString(): String {
    val instant = this.atZone(ZoneId.systemDefault()).toInstant()
    return DateTimeFormatter.ISO_INSTANT.format(instant)
}
