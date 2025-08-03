package di

import domain.controller.GameController
import domain.rules.BlackjackRules
import domain.repository.DeckRepository
import domain.repository.DeckRepositoryImpl
import domain.usecase.DealCardUseCase
import domain.usecase.DealerTurnUseCase
import domain.usecase.FlipCardUseCase
import domain.usecase.GameAnimationUseCase
import domain.usecase.GameUseCase
import domain.usecase.OptimalStrategyUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module
import presentation.viewmodel.BlackjackViewModel

val appModule = module {

    // Domain Use-Cases
    single { DealCardUseCase() }
    single { BlackjackRules() }
    single { FlipCardUseCase() }
    single { DealerTurnUseCase(dealCardUseCase = get(), blackjackRules = get()) }
    single { GameUseCase(deckRepository = get(), blackjackRules = get()) }
    single { GameAnimationUseCase(gameUseCase = get()) }
    single { GameController(gameUseCase = get(), gameAnimationUseCase = get()) }
    single { OptimalStrategyUseCase(dispatcher = get(qualifier<DefaultDispatcher>())) }

    single<CoroutineDispatcher>(qualifier<DefaultDispatcher>()) { Dispatchers.Default }
    single<CoroutineDispatcher>(qualifier<IODispatcher>()) { Dispatchers.IO }

    // Repository
    single<DeckRepository> { DeckRepositoryImpl() }

    // ViewModel
    single {
        BlackjackViewModel(
            gameAnimationUseCase = get(),
            optimalStrategyUseCase = get(),
            dispatcher = get(qualifier<DefaultDispatcher>()),
        )
    }
}
