package di

import domain.rules.BlackjackRules
import domain.repository.DeckRepository
import domain.repository.DeckRepositoryImpl
import domain.usecase.DealCardUseCase
import domain.usecase.DealerTurnUseCase
import domain.usecase.FlipCardUseCase
import domain.usecase.GameUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module
import presentation.viewmodel.CardViewModel

// fun nativeConfig() : KoinAppDeclaration


val appModule = module {

    // Domain Use-Cases
    single { DealCardUseCase() }
    single { BlackjackRules() }
    single { FlipCardUseCase() }
    single { DealerTurnUseCase(dealCardUseCase = get(), blackjackRules = get()) }
    single { GameUseCase(deckRepository = get(), blackjackRules = get()) }

    single<CoroutineDispatcher>(qualifier<DefaultDispatcher>()) { Dispatchers.Default }
    single<CoroutineDispatcher>(qualifier<IODispatcher>()) { Dispatchers.IO }


    // Repository
    single<DeckRepository> { DeckRepositoryImpl() }

    // ViewModel
    single {
        CardViewModel(
            gameUseCase = get(),
            dispatcher  = get(qualifier<DefaultDispatcher>()),
            )
    }
}
