package presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import di.DefaultDispatcher
import domain.rules.BlackjackRules
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import domain.usecase.GameUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest

class CardViewModel(
    private val gameUseCase: GameUseCase,
    @DefaultDispatcher
    private val dispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val actionStateFlow = MutableSharedFlow<Action>()

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<BlackjackUiState> = actionStateFlow
        .onStart { emit(Action.Reset) }
        .transformLatest { action ->
            // TODO: Nasty.
            val currentState = (uiState.value as? BlackjackUiState.Success)?.gameState

            when (action) {
                Action.Reset -> {
                    emit(BlackjackUiState.Loading)
                    gameUseCase.newGame()
                        .catch { emit(BlackjackUiState.Error("Failed to start a new game.")) }
                        .collect { gameState -> emit(BlackjackUiState.Success(gameState)) }
                }
                is Action.Hit -> {
                    if (currentState != null) {
                        val newState = gameUseCase.playerHit(currentState)
                        emit(BlackjackUiState.Success(newState))
                    }
                }
                is Action.Stand -> {
                    if (currentState != null) {
                        val newState = gameUseCase.playerStand(currentState)
                        emit(BlackjackUiState.Success(newState))
                    }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = BlackjackUiState.Loading,
        )


    fun onPlayerHit() {
        // TODO: Nasty.
        if ((uiState.value as? BlackjackUiState.Success)?.gameState?.gameResult == BlackjackRules.GameResult.PLAYING) {
            viewModelScope.launch(dispatcher) {
                actionStateFlow.emit(Action.Hit)
            }
        }
    }

    fun onPlayerStand() {
        // TODO: Nasty.
        if ((uiState.value as? BlackjackUiState.Success)?.gameState?.gameResult == BlackjackRules.GameResult.PLAYING) {
            viewModelScope.launch(dispatcher) {
                actionStateFlow.emit(Action.Stand)
            }
        }
    }


    fun onGameReset() {
        viewModelScope.launch(dispatcher) {
            actionStateFlow.emit(Action.Reset)
        }
    }

    private sealed interface Action {
        object Hit : Action
        object Stand : Action
        object Reset : Action
    }

}