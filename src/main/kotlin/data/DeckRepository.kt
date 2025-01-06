package data

interface DeckRepository {
    fun createStandardDeck(shuffle: Boolean): Deck
}