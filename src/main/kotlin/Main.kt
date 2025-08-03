import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import di.appModule
import org.koin.core.context.GlobalContext.startKoin
import presentation.screens.BlackjackScreen
import presentation.screens.MyApp

fun main() = application {
    startKoin {
        modules(appModule)
    }.koin

    Window(
        onCloseRequest = ::exitApplication,
        title = "BlackJack Game",
        state = rememberWindowState(width = 1200.dp, height = 1000.dp),
    ) {
        MyApp {
            BlackjackScreen()
        }
    }
}
