package com.kevin7254.blackjack.domain.model


/**
 * Round phase enum.
 * Represents high-level UI phases of a round.
 */
enum class RoundPhase {
    PlacingBet,
    Dealing,
    PlayerTurn,
    RoundOver,
}