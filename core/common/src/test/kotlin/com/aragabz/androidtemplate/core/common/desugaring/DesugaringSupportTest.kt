package com.aragabz.androidtemplate.core.common.desugaring

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.stream.Collectors

/**
 * Tests to verify desugaring support for Java 8+ APIs on Android API 26+.
 *
 * These tests confirm that java.time.*, java.util.stream, and other Java 8+ APIs
 * work correctly thanks to desugaring.
 */
class DesugaringSupportTest {
    // region java.time.* API tests

    @Test
    fun `Instant API is available and works`() {
        val now = Instant.now()
        val later = now.plusSeconds(60)

        assertNotNull(now)
        assertTrue(later.isAfter(now))
    }

    @Test
    fun `LocalDate API is available and works`() {
        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)

        assertNotNull(today)
        assertTrue(tomorrow.isAfter(today))
        assertEquals(1, tomorrow.dayOfMonth - today.dayOfMonth)
    }

    @Test
    fun `LocalDateTime API is available and works`() {
        val now = LocalDateTime.now()
        val nextHour = now.plusHours(1)

        assertNotNull(now)
        assertTrue(nextHour.isAfter(now))
    }

    @Test
    fun `Duration API is available and works`() {
        val duration = Duration.ofMinutes(30)

        assertEquals(30L, duration.toMinutes())
        assertEquals(1800L, duration.seconds)
    }

    @Test
    fun `DateTimeFormatter API is available and works`() {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val dateTime = LocalDateTime.of(2026, 7, 30, 17, 30, 0)
        val formatted = dateTime.format(formatter)

        assertEquals("2026-07-30 17:30:00", formatted)
    }

    @Test
    fun `ZoneId API is available and works`() {
        val utc = ZoneId.of("UTC")
        val cairo = ZoneId.of("Africa/Cairo")

        assertNotNull(utc)
        assertNotNull(cairo)
        assertEquals("UTC", utc.id)
        assertEquals("Africa/Cairo", cairo.id)
    }

    @Test
    fun `Instant to epoch millis conversion works`() {
        val instant = Instant.ofEpochMilli(1722352200000L)
        val millis = instant.toEpochMilli()

        assertEquals(1722352200000L, millis)
    }

    @Test
    fun `Duration between two Instants works`() {
        val start = Instant.ofEpochMilli(1000000)
        val end = Instant.ofEpochMilli(2000000)
        val duration = Duration.between(start, end)

        assertEquals(1000L, duration.seconds)
    }

    // endregion

    // region java.util.stream API tests

    @Test
    fun `Stream API map operation works`() {
        val numbers = listOf(1, 2, 3, 4, 5)
        val doubled = numbers
            .stream()
            .map { it * 2 }
            .collect(Collectors.toList())

        assertEquals(listOf(2, 4, 6, 8, 10), doubled)
    }

    @Test
    fun `Stream API filter operation works`() {
        val numbers = listOf(1, 2, 3, 4, 5, 6)
        val evens = numbers
            .stream()
            .filter { it % 2 == 0 }
            .collect(Collectors.toList())

        assertEquals(listOf(2, 4, 6), evens)
    }

    @Test
    fun `Stream API reduce operation works`() {
        val numbers = listOf(1, 2, 3, 4, 5)
        val sum = numbers
            .stream()
            .reduce(0) { acc, num -> acc + num }

        assertEquals(15, sum)
    }

    @Test
    fun `Stream API sorted operation works`() {
        val numbers = listOf(5, 2, 8, 1, 9, 3)
        val sorted = numbers
            .stream()
            .sorted()
            .collect(Collectors.toList())

        assertEquals(listOf(1, 2, 3, 5, 8, 9), sorted)
    }

    @Test
    fun `Stream API distinct operation works`() {
        val numbers = listOf(1, 2, 2, 3, 3, 3, 4)
        val distinct = numbers
            .stream()
            .distinct()
            .collect(Collectors.toList())

        assertEquals(listOf(1, 2, 3, 4), distinct)
    }

    @Test
    fun `Stream API limit operation works`() {
        val numbers = listOf(1, 2, 3, 4, 5)
        val limited = numbers
            .stream()
            .limit(3)
            .collect(Collectors.toList())

        assertEquals(listOf(1, 2, 3), limited)
    }

    @Test
    fun `Collectors joining works`() {
        val words = listOf("Hello", "World", "from", "Desugaring")
        val joined = words
            .stream()
            .collect(Collectors.joining(" "))

        assertEquals("Hello World from Desugaring", joined)
    }

    @Test
    fun `Collectors groupingBy works`() {
        val numbers = listOf(1, 2, 3, 4, 5, 6)
        val grouped = numbers
            .stream()
            .collect(Collectors.groupingBy { it % 2 == 0 })

        assertEquals(listOf(1, 3, 5), grouped[false])
        assertEquals(listOf(2, 4, 6), grouped[true])
    }

    // endregion

    // region Optional API tests

    @Test
    fun `Optional API is available and works`() {
        val present = java.util.Optional.of("value")
        val empty = java.util.Optional.empty<String>()

        assertTrue(present.isPresent)
        assertTrue(empty.isEmpty)
        assertEquals("value", present.get())
    }

    @Test
    fun `Optional map works`() {
        val optional = java.util.Optional.of("hello")
        val mapped = optional.map { it.uppercase() }

        assertEquals("HELLO", mapped.get())
    }

    @Test
    fun `Optional orElse works`() {
        val empty = java.util.Optional.empty<String>()
        val value = empty.orElse("default")

        assertEquals("default", value)
    }

    // endregion

    // region Real-world usage examples

    @Test
    fun `DateTimeUtils example - calculate age from birthdate`() {
        val birthdate = LocalDate.of(1990, 1, 1)
        val now = LocalDate.of(2026, 7, 30)
        val age = java.time.Period
            .between(birthdate, now)
            .years

        assertTrue(age >= 36) // At least 36 years old
    }

    @Test
    fun `DateTimeUtils example - format timestamp for UI`() {
        val timestamp = Instant.ofEpochMilli(1722352200000L)
        val localDateTime = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")
        val formatted = localDateTime.format(formatter)

        assertTrue(formatted.isNotEmpty())
    }

    @Test
    fun `Stream example - process todo items`() {
        data class Todo(
            val id: Int,
            val title: String,
            val completed: Boolean,
        )

        val todos = listOf(
            Todo(1, "Task 1", true),
            Todo(2, "Task 2", false),
            Todo(3, "Task 3", true),
            Todo(4, "Task 4", false),
        )

        val completedCount = todos
            .stream()
            .filter { it.completed }
            .count()

        assertEquals(2L, completedCount)
    }

    // endregion
}
