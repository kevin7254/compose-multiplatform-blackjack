package presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import di.DefaultDispatcher
import domain.model.GameState
import domain.usecase.GameAnimationUseCase
import domain.usecase.OptimalStrategyUseCase
import domain.usecase.StrategyAction
import domain.usecase.StrategyRecommendation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
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
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _uiState = MutableStateFlow<BlackjackUiState>(BlackjackUiState.Loading)
    val uiState: StateFlow<BlackjackUiState> = _uiState.asStateFlow()

    private var gameFlowJob: Job? = null

    init {
        onGameReset()
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
        // Set the initial state to Loading, then execute the flow for a new game.
        _uiState.value = BlackjackUiState.Loading
        executeFlow {
            gameAnimationUseCase.newGameWithAnimation()
        }
    }

    /**
     * A higher-order function to handle any action that requires a valid, non-animating
     * game state (e.g., Hit, Stand). It handles all the precondition checks.
     *
     * @param action A lambda that takes the current [GameState] and returns a [Flow] of new states.
     */
    private fun runStatefulAction(action: (GameState) -> Flow<GameState>) {
        val currentSuccessState = uiState.value as? BlackjackUiState.Success

        // Precondition check: Do nothing if we are not in a success state or are already busy.
        if (currentSuccessState == null || currentSuccessState.isAnimating) {
            return
        }

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
    private fun executeFlow(flowProvider: () -> Flow<GameState>) {
        gameFlowJob?.cancel()

        gameFlowJob = viewModelScope.launch(dispatcher) {
            flowProvider()
                .distinctUntilChanged()
                .flatMapLatest { gs ->
                    if (gs.doneDealingCards()) {
                        gs.recommendationFlow()
                            .map { rec -> gs to rec }
                            .onStart { emit(gs to waitingRec) } // emit initial waitingRec before rec
                    } else {
                        flowOf(gs to waitingRec)
                    }
                }
                .catch { e ->
                    if (e !is CancellationException) {
                        e.printStackTrace()
                        _uiState.value =
                            BlackjackUiState.Error("An error occurred: ${e.message}")
                    }
                }
                .onCompletion {
                    (_uiState.value as? BlackjackUiState.Success)
                        ?.let { _uiState.value = it.copy(isAnimating = false) }
                }
                .collect { (state, rec) ->
                    _uiState.value = BlackjackUiState.Success(
                        gameState = state,
                        recommendation = rec,
                        isAnimating = true,
                    )
                }
        }
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


    fun onErrorDismissed() {
        onGameReset()
    }
}