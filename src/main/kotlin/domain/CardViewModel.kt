package domain

import data.GameState
import data.Hand
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.DeckRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CardViewModel(
    private val deckRepository: DeckRepository,
    private val dealCardUseCase: DealCardUseCase,
    private val blackjackRules: BlackjackRules,
    private val dealerTurnUseCase: DealerTurnUseCase,
    private val flipCardUseCase: FlipCardUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(createInitialGameState())
    val uiState: StateFlow<GameState> = _uiState.asStateFlow()

    init {
        dealInitialCardsSequentially()
    }

    /**
     * Called when the player chooses to "Hit".
     */
    fun onPlayerHit() {
        dealOneCardToPlayer()
    }

    fun onPlayerStand() {
        viewModelScope.launch {
            // First delay and reveal dealer's second card
            delay(1500)
            revealDealerSecondCard()

            _uiState.update { state ->
                val (newDeck, newDealerHand) = dealerTurnUseCase(state.deck, state.dealerCards)
                val result = blackjackRules.evaluateResult(state.playerCards, newDealerHand)
                state.copy(deck = newDeck, dealerCards = newDealerHand, gameResult = result)
            }
        }
    }


    fun revealDealerSecondCard() {
        _uiState.update { state ->
            val updatedDealerHand = flipCardUseCase(state.dealerCards, 1) // index 1 = second card
            state.copy(dealerCards = updatedDealerHand)
        }
    }


    /**
     * Resets the game to a fresh state and deals initial cards again.
     */
    fun onGameReset() {
        _uiState.value = createInitialGameState()
        dealInitialCardsSequentially()
    }


    /**
     * Deals cards in the order: Player -> Dealer -> Player -> Dealer.
     */
    private fun dealInitialCardsSequentially() {
        viewModelScope.launch {
            dealOneCardToPlayer()
            delay(1500)
            dealOneCardToDealer()
            delay(1500)
            dealOneCardToPlayer()
            delay(1500)
            dealOneCardToDealer(faceUp = false)
        }
    }

    private fun dealOneCardToPlayer(faceUp: Boolean = true) {
        _uiState.update { currentState ->
            val (updatedDeck, updatedHand) = dealCardUseCase(
                deck = currentState.deck,
                hand = currentState.playerCards,
                faceUp = faceUp,
            )
            //check if player already has BJ
            val result = blackjackRules.evaluateResult(updatedHand, currentState.dealerCards)
            currentState.copy(deck = updatedDeck, playerCards = updatedHand, gameResult = result)
        }
    }

    private fun dealOneCardToDealer(faceUp: Boolean = true) {
        _uiState.update { currentState ->
            val (updatedDeck, updatedHand) = dealCardUseCase(
                deck = currentState.deck,
                hand = currentState.dealerCards,
                faceUp = faceUp,
            )
            currentState.copy(deck = updatedDeck, dealerCards = updatedHand)
        }
    }

    private fun createInitialGameState() = GameState(
        deck = deckRepository.createStandardDeck(shuffle = true),
        playerCards = Hand(),
        dealerCards = Hand(),
    )
}