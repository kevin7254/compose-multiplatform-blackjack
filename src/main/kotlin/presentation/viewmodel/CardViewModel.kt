package presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import di.DefaultDispatcher
import domain.controller.GameController
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class CardViewModel(
    private val gameController: GameController,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<BlackjackUiState> = combine(
        gameController.gameState,
        gameController.isLoading,
        gameController.error,
    ) { gameState, isLoading, error ->
        when {
            error != null -> BlackjackUiState.Error(error)
            isLoading -> BlackjackUiState.Loading
            gameState != null -> BlackjackUiState.Success(gameState)
            else -> BlackjackUiState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BlackjackUiState.Loading,
    )

    init {
        onGameReset()
    }


    fun onPlayerHit() {
        viewModelScope.launch(dispatcher) {
            gameController.playerHit()
        }
    }

    fun onPlayerStand() {
        viewModelScope.launch(dispatcher) {
            gameController.playerStand()
        }
    }

    fun onGameReset() {
        viewModelScope.launch(dispatcher) {
            gameController.startNewGame()
        }
    }

    fun onErrorDismissed() {
        gameController.clearError()
    }
}
