package com.kevin7254.blackjack.domain.model


/**
 * Round phase enum.
 */
// TODO: move/delete?
enum class RoundPhase {
    PlacingBet, Dealing, PlayerTurn, DealerTurn, Settling, RoundOver
}