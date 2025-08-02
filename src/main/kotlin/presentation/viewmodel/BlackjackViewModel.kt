package presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import di.DefaultDispatcher
import domain.model.GameState
import domain.usecase.GameAnimationUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlin.coroutines.cancellation.CancellationException

/**
 * ViewModel for the Blackjack game.
 * Handles user interactions, manages game state, and orchestrates animations.
 */
class BlackjackViewModel(
    private val gameAnimationUseCase: GameAnimationUseCase,
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
    private fun executeFlow(flowProvider: () -> Flow<GameState>) {
        // Cancel any job that is currently running.
        gameFlowJob?.cancel()

        gameFlowJob = viewModelScope.launch(dispatcher) {
            flowProvider()
                .catch { e ->
                    if (e !is CancellationException) {
                        _uiState.value = BlackjackUiState.Error("An error occurred: ${e.message}")
                    }
                }
                .onCompletion {
                    val finalState = _uiState.value
                    if (finalState is BlackjackUiState.Success) {
                        _uiState.value = finalState.copy(isAnimating = false)
                    }
                }
                .collect { newGameState ->
                    _uiState.value = BlackjackUiState.Success(newGameState, isAnimating = true)
                }
        }
    }

    fun onErrorDismissed() {
        onGameReset()
    }
}