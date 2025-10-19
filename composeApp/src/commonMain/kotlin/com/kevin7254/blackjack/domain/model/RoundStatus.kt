package com.kevin7254.blackjack.domain.model

import com.kevin7254.blackjack.domain.bank.model.GameOutcome

/**
 * Represents whether the current round is still being played or has finished with an outcome.
 */
sealed interface RoundStatus {
    /** The round is still in progress (cards could still be dealt or revealed). */
    data object InProgress : RoundStatus

    /** The round has concluded with an outcome. */
    data class Finished(val outcome: GameOutcome) : RoundStatus
}
