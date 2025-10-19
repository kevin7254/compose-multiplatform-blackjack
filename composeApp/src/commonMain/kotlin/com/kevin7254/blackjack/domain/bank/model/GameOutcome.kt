package com.kevin7254.blackjack.domain.bank.model

/**
 * The outcome of a game.
 */
sealed interface GameOutcome {
    /**
     * Player wins by getting blackjack (21 points with two cards).
     */
    data object PlayerBlackJack : GameOutcome

    /**
     * Player busts (gets more than 21 points).
     */
    data object PlayerBust : GameOutcome

    /**
     * The player and dealer have the same total.
     */
    data object Push : GameOutcome

    /**
     * Dealer has blackjack (21 points with two cards).
     */
    data object DealerWinAndBlackJack : GameOutcome

    /**
     * Player wins. Either by having more than the dealer's total and less than 21, or by
     * the dealer busting.
     */
    data object PlayerWin : GameOutcome

    /**
     * Dealer wins. Either by having more than the player's total and less than 21, or by
     * the player busting.
     */
    data object DealerWin : GameOutcome

    /**
     * The game is still in progress.
     */
    // TODO IDK?
    data object Playing : GameOutcome
}