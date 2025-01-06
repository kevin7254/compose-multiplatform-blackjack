package di

import data.DeckRepository
import data.DeckRepositoryImpl
import domain.*
import org.koin.dsl.module

// fun nativeConfig() : KoinAppDeclaration


val appModule = module {

    // Domain Use-Cases
    single { DealCardUseCase() }
    single { BlackjackRules() }
    single { FlipCardUseCase() }
    single { DealerTurnUseCase(dealCardUseCase = get(), blackjackRules = get()) }

    // Repository
    single<DeckRepository> { DeckRepositoryImpl() }

    // ViewModel
    single {
        CardViewModel(
            deckRepository = get(),
            dealCardUseCase = get(),
            blackjackRules = get(),
            dealerTurnUseCase = get(),
            flipCardUseCase = get(),
        )
    }
}
