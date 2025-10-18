package com.kevin7254.blackjack.domain.bank.model

sealed interface GameOutcome {
    data object PlayerBlackJack : GameOutcome
    data object PlayerBust : GameOutcome
    data object Push : GameOutcome
    data object DealerWinAndBlackJack : GameOutcome
    data object PlayerWin : GameOutcome
    data object DealerWin : GameOutcome

    // TODO IDK?
    data object Playing : GameOutcome
}