package com.example.pr10

import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun increment_addsOne() {
        assertEquals(6, nextCount(5, 1))
    }

    @Test
    fun decrement_neverGoesBelowZero() {
        assertEquals(0, nextCount(0, -1))
    }
}
