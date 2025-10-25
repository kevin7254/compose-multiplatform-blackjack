package com.kevin7254.blackjack.domain.bank

import com.kevin7254.blackjack.domain.bank.model.Bankroll
import com.kevin7254.blackjack.domain.bank.model.BetOutcome
import com.kevin7254.blackjack.domain.bank.model.BetState
import com.kevin7254.blackjack.domain.bank.model.Chips
import com.kevin7254.blackjack.domain.bank.model.GameOutcome
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Interactor for handling betting related operations.
 */
interface BettingInteractor {
    /**
     * The current bankroll state.
     */
    val bankroll: StateFlow<Bankroll>

    /**
     * The current bet state.
     */
    val bet: StateFlow<BetState>

    /**
     * The stack of chips placed in the current betting session.
     * Useful for undo functionality before the round starts.
     */
    val placedChips: StateFlow<List<Chips>>

    /**
     * Places a bet with the specified chip amount.
     * Deducts from bankroll if sufficient funds are available.
     */
    fun placeBet(chips: Chips)

    /**
     * Clears all placed bets and returns chips to bankroll.
     */
    fun clearBet()

    /**
     * Removes the last placed chip and returns it to bankroll.
     */
    fun undoLastChip()

    /**
     * Adds chips to the bankroll.
     */
    fun buyIn(amount: Chips)

    /**
     * Locks the bet for the current round.
     * Player cannot modify bets until the round is settled.
     */
    fun lockBet(): BetState

    /**
     * Settles the round outcome and updates bankroll accordingly.
     * Resets bet state for the next round.
     */
    fun settle(outcome: GameOutcome): BetOutcome
}

/**
 * In-memory implementation of [BettingInteractor].
 * Manages betting state, bankroll, and chip placement history.
 */
class InMemoryBettingInteractor(
    initialBankroll: Bankroll = Bankroll(Chips(1000)),
) : BettingInteractor {

    private val _bankroll = MutableStateFlow(initialBankroll)
    override val bankroll: StateFlow<Bankroll> = _bankroll.asStateFlow()

    private val _bet = MutableStateFlow(BetState())
    override val bet: StateFlow<BetState> = _bet.asStateFlow()

    private val _placedChips = MutableStateFlow<List<Chips>>(emptyList())
    override val placedChips: StateFlow<List<Chips>> = _placedChips.asStateFlow()

    override fun placeBet(chips: Chips) {
        val currentBet = _bet.value
        if (!currentBet.canPlaceBet) return

        val currentBankroll = _bankroll.value
        if (currentBankroll.balance.amount < chips.amount) return

        _bet.value = currentBet.copy(currentBet = currentBet.currentBet + chips)
        _bankroll.value = currentBankroll.copy(balance = currentBankroll.balance - chips)
        _placedChips.value += chips
    }

    override fun clearBet() {
        val totalBet = _bet.value.currentBet
        _bankroll.value = _bankroll.value.copy(balance = _bankroll.value.balance + totalBet)

        _bet.value = _bet.value.copy(currentBet = Chips(0))
        _placedChips.value = emptyList()
    }

    override fun undoLastChip() {
        val lastChip = _placedChips.value.lastOrNull() ?: return

        _bet.value = _bet.value.copy(currentBet = _bet.value.currentBet - lastChip)
        _bankroll.value = _bankroll.value.copy(balance = _bankroll.value.balance + lastChip)
        _placedChips.value = _placedChips.value.dropLast(1)
    }

    override fun buyIn(amount: Chips) {
        _bankroll.value = _bankroll.value.copy(balance = _bankroll.value.balance + amount)
    }

    override fun lockBet(): BetState {
        _bet.value = _bet.value.copy(canPlaceBet = false)
        return _bet.value
    }

    override fun settle(outcome: GameOutcome): BetOutcome {
        val currentBet = _bet.value.currentBet

        val payout = when (outcome) {
            is GameOutcome.PlayerBlackJack -> currentBet * 2.5
            is GameOutcome.PlayerWin -> currentBet * 2.0
            is GameOutcome.Push -> currentBet * 1.0
            is GameOutcome.PlayerBust,
            is GameOutcome.DealerWin,
            is GameOutcome.DealerWinAndBlackJack -> Chips(0)
        }

        _bankroll.value = _bankroll.value.copy(balance = _bankroll.value.balance + payout)
        _bet.value = BetState(currentBet = Chips(0), canPlaceBet = true)
        _placedChips.value = emptyList()

        return BetOutcome(payout = payout, newBankroll = _bankroll.value)
    }
}