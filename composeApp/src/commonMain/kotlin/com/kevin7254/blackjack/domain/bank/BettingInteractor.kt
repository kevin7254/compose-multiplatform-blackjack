package com.kevin7254.blackjack.domain.bank

import com.kevin7254.blackjack.domain.bank.model.Bankroll
import com.kevin7254.blackjack.domain.bank.model.BetOutcome
import com.kevin7254.blackjack.domain.bank.model.BetState
import com.kevin7254.blackjack.domain.bank.model.Chips
import com.kevin7254.blackjack.domain.bank.model.GameOutcome
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface BettingInteractor {
    val bankrollState: StateFlow<Bankroll>
    val betState: StateFlow<BetState>
    fun placeBet(chips: Chips)
    fun clearBet()

    fun buyIn(amount: Chips)

    fun settle(outcome: GameOutcome): BetOutcome
}

class InMemoryBettingInteractor(
    initialBankroll: Bankroll = Bankroll(Chips(1000)),
) : BettingInteractor {
    override val bankrollState = MutableStateFlow(initialBankroll)
    override val betState = MutableStateFlow(BetState())
    override fun placeBet(chips: Chips) {
        betState.value = betState.value.copy(currentBet = chips)
    }

    override fun clearBet() {
        betState.value = betState.value.copy(currentBet = Chips(0))
    }

    override fun buyIn(amount: Chips) {
        bankrollState.value = bankrollState.value.copy(balance = bankrollState.value.balance + amount)
    }

    override fun settle(outcome: GameOutcome): BetOutcome {
        val bet = betState.value.currentBet
        val addBack = when (outcome) {
            is GameOutcome.PlayerBlackJack -> bet * 2.5
            is GameOutcome.PlayerBust -> bet * 0.0
            is GameOutcome.Push -> bet * 1.0
            is GameOutcome.DealerWinAndBlackJack -> bet * 0.0
            is GameOutcome.PlayerWin -> bet * 2.0
            is GameOutcome.DealerWin -> bet * 0.0
            is GameOutcome.Playing -> throw IllegalArgumentException("Playing game outcome should not be settled.")
        }
        bankrollState.value = bankrollState.value.copy(balance = bankrollState.value.balance + addBack)
        betState.value = betState.value.copy(currentBet = Chips(0), canPlaceBet = true)
        return BetOutcome(payout = addBack, newBankroll = bankrollState.value)
    }
}