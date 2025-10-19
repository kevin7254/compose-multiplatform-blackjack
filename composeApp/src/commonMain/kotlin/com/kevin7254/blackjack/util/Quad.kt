package com.kevin7254.blackjack.util

import kotlinx.serialization.Serializable

/**
 * Represents a quartet of values
 *
 * There is no meaning attached to values in this class, it can be used for any purpose.
 * Quad exhibits value semantics, i.e., two Quads are equal if all four parts are equal.
 * An example of decomposing it into values:
 *
 * @param A type of the first value.
 * @param B type of the second value.
 * @param C type of the third value.
 * @param D type of the fourth value.
 * @property first First value.
 * @property second Second value.
 * @property third Third value.
 * @property fourth Fourth value.
 */
@Serializable
data class Quad<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
) {
    override fun toString(): String = "($first, $second, $third, $fourth)"
}