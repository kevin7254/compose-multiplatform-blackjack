package com.kevin7254.blackjack.di

import com.kevin7254.blackjack.domain.bank.BettingInteractor
import com.kevin7254.blackjack.domain.bank.InMemoryBettingInteractor
import com.kevin7254.blackjack.domain.rules.BlackjackRules
import com.kevin7254.blackjack.domain.repository.DeckRepository
import com.kevin7254.blackjack.domain.repository.DeckRepositoryImpl
import com.kevin7254.blackjack.domain.usecase.DealCardUseCase
import com.kevin7254.blackjack.domain.usecase.DealerTurnUseCase
import com.kevin7254.blackjack.domain.usecase.FlipCardUseCase
import com.kevin7254.blackjack.domain.usecase.GameAnimationUseCase
import com.kevin7254.blackjack.domain.usecase.GameUseCase
import com.kevin7254.blackjack.domain.usecase.OptimalStrategyUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module
import com.kevin7254.blackjack.presentation.viewmodel.BlackjackViewModel

val appModule = module {

    // Domain Use-Cases
    single { DealCardUseCase() }
    single { BlackjackRules() }
    single { FlipCardUseCase() }
    single { DealerTurnUseCase(dealCardUseCase = get(), blackjackRules = get()) }
    single { GameUseCase(deckRepository = get(), blackjackRules = get()) }
    single { GameAnimationUseCase(gameUseCase = get()) }
    single { OptimalStrategyUseCase(dispatcher = get(qualifier<DefaultDispatcher>())) }

    single<CoroutineDispatcher>(qualifier<DefaultDispatcher>()) { Dispatchers.Default }
   // single<CoroutineDispatcher>(qualifier<IODispatcher>()) { Dispatchers.IO }

    // Repository
    single<DeckRepository> { DeckRepositoryImpl() }

    single<BettingInteractor> { InMemoryBettingInteractor() }

    // ViewModel
    single {
        BlackjackViewModel(
            gameAnimationUseCase = get(),
            optimalStrategyUseCase = get(),
            bettingInteractor = get(),
            dispatcher = get(qualifier<DefaultDispatcher>()),
        )
    }
}
