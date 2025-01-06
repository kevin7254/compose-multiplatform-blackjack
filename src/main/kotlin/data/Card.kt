package data

enum class Suit(val shortName: String) {
    CLUBS("C"),
    DIAMONDS("D"),
    HEARTS("H"),
    SPADES("S")
}


enum class Rank(val value: UByte, val displayValue: Int) {
    ACE(1u, 1),
    TWO(2u, 2),
    THREE(3u, 3),
    FOUR(4u, 4),
    FIVE(5u, 5),
    SIX(6u, 6),
    SEVEN(7u, 7),
    EIGHT(8u, 8),
    NINE(9u, 9),
    TEN(10u, 10),
    JACK(10u, 11),
    QUEEN(10u, 12),
    KING(10u, 13);
}

data class Card(
    val rank: Rank,
    val suit: Suit,
    val isFaceUp: Boolean = true,
) : Comparable<Card> {
    val imageName: String
        get() = "${rank.displayValue}${suit.shortName}.png"


    override fun compareTo(other: Card): Int {
        return (this.rank.value - other.rank.value).toInt()
    }

    override fun toString(): String {
        return "${rank.name} of ${suit.name}"
    }

    fun toShortString(): String {
        return "${rank.name}${suit.name}"
    }

    fun toChar(): Char {
        return toShortString()[0]
    }

    fun toInt(): Int {
        return "${rank.value}${suit.ordinal}".toInt()
    }


    companion object {
        fun fromInt(i: Int): Card {
            val rank = Rank.entries[i % 13]
            val suit = Suit.entries[i / 13]
            return Card(rank, suit)
        }
    }
}


data class Deck(
    val cards: List<Card>,
    val numberOfDecks: Int = 1,
) : Iterable<Card> {

    override fun iterator(): Iterator<Card> = cards.iterator()

    fun size(): Int = cards.size

    fun contains(card: Card): Boolean = cards.contains(card)

    companion object {
        // TODO: functional
        fun createStandardDeck(shuffle: Boolean = false): Deck {
            val cards = mutableListOf<Card>()
            for (suit in Suit.entries) {
                for (rank in Rank.entries) {
                    cards.add(Card(rank, suit))
                }
            }
            if (shuffle) cards.shuffle()
            return Deck(cards)
        }

        fun createStandardDeckOfCards(shuffle: Boolean = false): List<Card> {
            return createStandardDeck(shuffle).toList()
        }

        fun createMultiDeckOfCards(numberOfDecks: Int, shuffle: Boolean = false): List<Card> {
            val cards = mutableListOf<Card>()
            for (i in 0 until numberOfDecks) {
                cards.addAll(createStandardDeckOfCards())
            }
            return if (shuffle) cards else cards.shuffled()
        }
    }
}

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
            validTotals.maxOrNull()!! // The best valid total. TODO no double bang..
        } else {
            allTotals.minOrNull()!!   // If everything is over 21, pick the smallest bust
        }
    }

    override fun compareTo(other: Hand): Int {
        return this.totalValue().compareTo(other.totalValue())
    }
}
