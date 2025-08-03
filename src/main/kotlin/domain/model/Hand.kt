package domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Hand(
    val cards: List<Card> = emptyList()
) : Comparable<Hand> {

    fun addCard(card: Card): Hand {
        return copy(cards = cards + card)
    }

    fun removeCard(card: Card): Hand {
        return copy(cards = cards - card)
    }

    fun clearCards(): Hand {
        return copy(cards = emptyList())
    }

    fun totalValue(): Int {
        val baseSum = cards.sumOf { card ->
            if (!card.isFaceUp) 0
            else if (card.rank == Rank.ACE) 1
            else card.rank.value.toInt()
        }

        val aceCount = cards.count { it.isFaceUp && it.rank == Rank.ACE }
        return if (aceCount > 0 && baseSum + 10 <= 21) {
            baseSum + 10
        } else {
            baseSum
        }
    }

    /**
     * Returns ALL possible totals for the face-up cards.
     * Example: If you have [Ace (faceUp), 6 (faceUp)], this might return {7, 17}.
     */
    fun possibleValues(): Set<Int> {
        val faceUpCards = cards.filter { it.isFaceUp }
        var totals = setOf(0)

        faceUpCards.forEach { card ->
            val newTotals = mutableSetOf<Int>()

            if (card.rank == Rank.ACE) {
                totals.forEach { t ->
                    newTotals.add(t + 1)   // Ace as 1
                    newTotals.add(t + 11)  // Ace as 11
                }
            } else {
                totals.forEach { t ->
                    newTotals.add(t + card.rank.value.toInt())
                }
            }
            totals = newTotals
        }
        return totals
    }

    /**
     * For convenience, returns the "best" value that does not exceed 21,
     * or the smallest total if they all bust.
     *
     * E.g. If [7, 17] are possible, returns 17. If all > 21, returns the minimum.
     */
    fun bestValue(): Int {
        val allTotals = possibleValues()
        val validTotals = allTotals.filter { it <= 21 }
        return if (validTotals.isNotEmpty()) {
            validTotals.max() // The best valid total.
        } else {
            allTotals.min()   // If everything is over 21, pick the smallest bust
        }
    }

    override fun compareTo(other: Hand): Int {
        return this.totalValue().compareTo(other.totalValue())
    }
}