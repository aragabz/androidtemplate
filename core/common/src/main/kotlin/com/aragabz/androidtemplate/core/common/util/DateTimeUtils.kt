package com.aragabz.androidtemplate.core.common.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility functions for date/time formatting.
 */
object DateTimeUtils {
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    /**
     * Formats epoch milliseconds to a readable date string.
     * @param epochMillis The timestamp in milliseconds since epoch
     * @return Formatted date string (e.g., "Jul 30, 2026")
     */
    fun formatDate(epochMillis: Long): String = dateFormat.format(Date(epochMillis))

    /**
     * Formats epoch milliseconds to a readable date and time string.
     * @param epochMillis The timestamp in milliseconds since epoch
     * @return Formatted date-time string (e.g., "Jul 30, 2026 14:30")
     */
    fun formatDateTime(epochMillis: Long): String = dateTimeFormat.format(Date(epochMillis))

    /**
     * Formats epoch milliseconds to a time string.
     * @param epochMillis The timestamp in milliseconds since epoch
     * @return Formatted time string (e.g., "14:30")
     */
    fun formatTime(epochMillis: Long): String = timeFormat.format(Date(epochMillis))

    /**
     * Formats epoch milliseconds to a relative time string (e.g., "2 hours ago", "yesterday").
     * @param epochMillis The timestamp in milliseconds since epoch
     * @return Relative time string
     */
    fun formatRelativeTime(epochMillis: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - epochMillis

        return when {
            diff < 0 -> "in the future"
            diff < 60_000 -> "just now"
            diff < 3_600_000 -> "${diff / 60_000} minutes ago"
            diff < 86_400_000 -> "${diff / 3_600_000} hours ago"
            diff < 172_800_000 -> "yesterday"
            diff < 604_800_000 -> "${diff / 86_400_000} days ago"
            else -> formatDate(epochMillis)
        }
    }
}
