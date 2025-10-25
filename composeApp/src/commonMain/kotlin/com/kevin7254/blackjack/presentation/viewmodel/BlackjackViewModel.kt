package com.kevin7254.blackjack.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevin7254.blackjack.di.DefaultDispatcher
import com.kevin7254.blackjack.domain.bank.BettingInteractor
import com.kevin7254.blackjack.domain.bank.model.Chips
import com.kevin7254.blackjack.domain.model.GameState
import com.kevin7254.blackjack.domain.model.RoundPhase
import com.kevin7254.blackjack.domain.model.RoundStatus
import com.kevin7254.blackjack.domain.usecase.GameAnimationUseCase
import com.kevin7254.blackjack.domain.usecase.OptimalStrategyUseCase
import com.kevin7254.blackjack.domain.usecase.StrategyAction
import com.kevin7254.blackjack.domain.usecase.StrategyRecommendation
import com.kevin7254.blackjack.util.Quad
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlin.coroutines.cancellation.CancellationException

/**
 * ViewModel for the Blackjack game.
 * Handles user interactions, manages game state, and orchestrates animations.
 */
class BlackjackViewModel(
    private val gameAnimationUseCase: GameAnimationUseCase,
    private val optimalStrategyUseCase: OptimalStrategyUseCase,
    private val bettingInteractor: BettingInteractor,
    @param:DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _uiState = MutableStateFlow<BlackjackUiState>(BlackjackUiState.Loading)
    val uiState: StateFlow<BlackjackUiState> = _uiState.asStateFlow()
    val chipState = bettingInteractor.placedChips

    private var gameFlowJob: Job? = null

    init {
        showBettingPhase()
    }

    /**
     * Called when the player wants to start a new game.
     */
    fun onDeal() {
        bettingInteractor.lockBet()
        _uiState.value = BlackjackUiState.Loading
        executeFlow { gameAnimationUseCase.newGameWithAnimation() }
    }

    /**
     * Called when the player wants to hit. Delegates to our stateful action runner.
     */
    fun onPlayerHit() = runStatefulAction { gameState ->
        gameAnimationUseCase.playerHitWithAnimation(gameState)
    }

    /**
     * Called when the player wants to stand. Delegates to our stateful action runner.
     */
    fun onPlayerStand() = runStatefulAction { gameState ->
        gameAnimationUseCase.playerStandWithAnimation(gameState)
    }

    /**
     * Resets the game to a new state. This is a special case as it doesn't
     * depend on a previous success state.
     */
    fun onGameReset() {
        showBettingPhase()
    }

    /**
     * Called when the player wants to place a bet.
     */
    fun onChipClicked(amount: Int) = bettingInteractor.placeBet(Chips(amount))

    fun onClearBet() = bettingInteractor.clearBet()

    fun onUndoLastChip() = bettingInteractor.undoLastChip()

    /**
     * A higher-order function to handle any action that requires a valid, non-animating
     * game state (e.g., Hit, Stand). It handles all the precondition checks.
     *
     * @param action A lambda that takes the current [GameState] and returns a [Flow] of new states.
     */
    private fun runStatefulAction(action: (GameState) -> Flow<GameState>) {
        val currentSuccessState = uiState.value as? BlackjackUiState.Success ?: return

        // Set the animating flag immediately for UI responsiveness.
        _uiState.value = currentSuccessState.copy(isAnimating = true)

        // Execute the flow provided by the action lambda.
        executeFlow {
            action(currentSuccessState.gameState)
        }
    }

    /**
     * The core engine for executing all game animations. This private function contains
     * all the shared logic for job management, flow collection, and finalization.
     *
     * @param flowProvider A lambda that creates the [Flow<GameState>] to be executed.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun executeFlow(
        animate: Boolean = true,
        flowProvider: () -> Flow<GameState>
    ) {
        gameFlowJob?.cancel()

        gameFlowJob = viewModelScope.launch(dispatcher) {
            val gameFlow = flowProvider()
                .distinctUntilChanged()
                .flatMapLatest { gs ->
                    if (gs.doneDealingCards()) {
                        gs.recommendationFlow()
                            .map { rec -> gs to rec }
                            .onStart { emit(gs to waitingRec) }
                    } else {
                        flowOf(gs to waitingRec)
                    }
                }

            combine(
                gameFlow,
                bettingInteractor.bet,
                bettingInteractor.bankroll,
            ) { (gs, rec), bs, br -> Quad(gs, rec, bs, br) }
                .onCompletion {
                    if (animate) {
                        (_uiState.value as? BlackjackUiState.Success)?.let {
                            _uiState.value = it.copy(isAnimating = false)
                        }
                    }
                }
                .catch { e ->
                    if (e !is CancellationException) _uiState.value =
                        BlackjackUiState.Error("An error occurred: ${e.message}")
                }
                .collect { (gs, rec, bs, br) ->
                    settleIfOver(gs)
                    _uiState.value = BlackjackUiState.Success(
                        gameState = gs,
                        recommendation = rec,
                        isAnimating = animate,
                        roundPhase = derivePhaseFrom(gs),
                        bankroll = br,
                        betState = bs,
                    )
                }
        }
    }

    private fun derivePhaseFrom(gs: GameState): RoundPhase = when {
        !gs.dealerCards.cards.any() && !gs.playerCards.cards.any() -> RoundPhase.PlacingBet
        gs.dealerCards.cards.size + gs.playerCards.cards.size < 4 -> RoundPhase.Dealing
        gs.status is RoundStatus.Finished -> RoundPhase.RoundOver
        else -> RoundPhase.PlayerTurn //TODO : improve probably
    }

    private fun showBettingPhase() {
        executeFlow(animate = false) { flowOf(GameState.empty()) }
    }

    private fun settleIfOver(gs: GameState) {
        val finished = gs.status as? RoundStatus.Finished ?: return
        bettingInteractor.settle(finished.outcome)
    }

    private val waitingRec = StrategyRecommendation(
        action = StrategyAction.WAITING,
        reason = "Waiting for dealer card…",
    )


    private fun GameState.doneDealingCards() =
        dealerCards.cards.size == 2 &&
                playerCards.cards.size >= 2 &&
                dealerCards.cards.count { it.isFaceUp } == 1

    private fun GameState.recommendationFlow(): Flow<StrategyRecommendation> =
        optimalStrategyUseCase(
            playerHand = playerCards,
            dealerUpCard = dealerCards.cards.first(),
            //TODO: Improve
            currentDeck = deck,
        )
}