import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import di.appModule
import org.koin.core.context.GlobalContext.startKoin
import ui.screens.BlackjackScreen
import ui.screens.MyApp

fun main() = application {
    startKoin {
        modules(appModule)
    }.koin

    Window(
        onCloseRequest = ::exitApplication,
        title = "BlackJack Game",
        state = rememberWindowState(width = 1200.dp, height = 800.dp),
    ) {
        MyApp {
            BlackjackScreen()
        }
    }
}
