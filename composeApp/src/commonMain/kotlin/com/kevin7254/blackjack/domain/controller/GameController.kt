package com.kevin7254.blackjack.domain.controller

import com.kevin7254.blackjack.domain.model.GameResult
import com.kevin7254.blackjack.domain.model.GameState
import com.kevin7254.blackjack.domain.usecase.GameAnimationUseCase
import com.kevin7254.blackjack.domain.usecase.GameUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameController(
    private val gameUseCase: GameUseCase,
    private val gameAnimationUseCase: GameAnimationUseCase,
) {
    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState: StateFlow<GameState?> = _gameState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()


    private val _isAnimating = MutableStateFlow(false)
    val isAnimating: StateFlow<Boolean> = _isAnimating.asStateFlow()


    suspend fun startNewGame(): Result<Unit> {
        return try {
        //    _isLoading.value = true
            _isAnimating.value = true
            _error.value = null

            // Collect with delay for initial card dealing
            gameAnimationUseCase.newGameWithAnimation()
                .collect { gameState ->
                    _gameState.value = gameState
                }

         //   _isLoading.value = false
            _isAnimating.value = false
            Result.success(Unit)
        } catch (e: Exception) {
            _isLoading.value = false
            _error.value = "Failed to start a new game: ${e.message}"
            Result.failure(e)
        }
    }


    suspend fun playerHit(): Result<Unit> {
        return executePlayerActionWithDelay { currentState ->
            gameUseCase.playerHit(currentState)
        }
    }

    suspend fun playerStand(): Result<Unit> {
        val currentState = _gameState.value
        return if (currentState != null && currentState.gameResult == GameResult.PLAYING) {
            try {
                _isAnimating.value = true

                // Use animation use case for dealer's turn
                gameAnimationUseCase.playerStandWithAnimation(currentState)
                    .collect { gameState ->
                        _gameState.value = gameState
                    }

                _isAnimating.value = false
                Result.success(Unit)
            } catch (e: Exception) {
                _isAnimating.value = false
                _error.value = "Failed to stand: ${e.message}"
                Result.failure(e)
            }
        } else {
            Result.failure(IllegalStateException("Cannot stand in current game state"))
        }
    }



    fun isGameOver(): Boolean {
        return _gameState.value?.gameResult?.let { it != GameResult.PLAYING } ?: false
    }


    private suspend inline fun executePlayerActionWithDelay(
        action: (GameState) -> GameState
    ): Result<Unit> {
        val currentState = _gameState.value
        return if (currentState != null && currentState.gameResult == GameResult.PLAYING) {
            try {
                _isAnimating.value = true

                val newState = action(currentState)

                // Add delay for card animation
                delay(CARD_DEAL_DELAY)

                _gameState.value = newState
                _isAnimating.value = false

                Result.success(Unit)
            } catch (e: Exception) {
                _isAnimating.value = false
                _error.value = "Failed to execute action: ${e.message}"
                Result.failure(e)
            }
        } else {
            Result.failure(IllegalStateException("Cannot execute action in current game state"))
        }
    }


    fun clearError() {
        _error.value = null
    }

    companion object {
        private const val CARD_DEAL_DELAY = 800L // ms
    }

}
