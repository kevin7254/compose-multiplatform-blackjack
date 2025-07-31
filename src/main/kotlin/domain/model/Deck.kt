package domain.model

data class Deck(
    val cards: List<Card> = emptyList(),
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
