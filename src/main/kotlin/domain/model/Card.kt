package domain.model


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
        return "${suit.ordinal}${rank.displayValue}".toInt()
    }


    companion object {
        fun fromInt(i: Int): Card {
            val rank = Rank.entries[i % 13]
            val suit = Suit.entries[i / 13]
            return Card(rank, suit)
        }
    }
}