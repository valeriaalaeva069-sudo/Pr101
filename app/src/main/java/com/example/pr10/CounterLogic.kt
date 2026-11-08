package com.example.pr10

/** Новое значение счётчика: ниже нуля не уходит. */
fun nextCount(current: Int, delta: Int): Int = (current + delta).coerceAtLeast(0)
