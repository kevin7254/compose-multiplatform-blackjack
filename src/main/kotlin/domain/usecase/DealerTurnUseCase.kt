package domain.usecase

import domain.rules.BlackjackRules
import domain.model.Deck
import domain.model.Hand


class DealerTurnUseCase(
    private val dealCardUseCase: DealCardUseCase,
    private val blackjackRules: BlackjackRules,
) {
    operator fun invoke(deck: Deck, dealerHand: Hand): Pair<Deck, Hand> {
        var localDeck = deck
        var localHand = dealerHand
        while (blackjackRules.shouldDealerDraw(localHand)) {
            val (updatedDeck, updatedHand) = dealCardUseCase(localDeck, localHand)
            localDeck = updatedDeck
            localHand = updatedHand
        }
        return localDeck to localHand
    }
}
