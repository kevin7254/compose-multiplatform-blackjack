package presentation.viewmodel

import data.GameState

sealed interface BlackjackUiState {
    /**
     * The game is currently being set up or reset.
     */
    data object Loading : BlackjackUiState

    /**
     * The game has loaded and is ready to be played or has finished.
     */
    data class Success(val gameState: GameState) : BlackjackUiState

    /**
     * An error occurred during game setup or play.
     */
    data class Error(val message: String) : BlackjackUiState
}
