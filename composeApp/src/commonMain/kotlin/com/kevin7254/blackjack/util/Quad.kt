package com.kevin7254.blackjack.util

import kotlinx.serialization.Serializable

@Serializable
data class Quad<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
) {
    override fun toString(): String = "($first, $second, $third, $fourth)"
}