package example.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.squareup.anvil.annotations.ContributesTo
import example.compose.pager.Page
import example.compose.pager.PagerSample
import javax.inject.Inject
import javax.inject.Named

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val component = AppComponentInstance.create(this) as MainDepsProvider
        val mainDependencies = component.getMainDeps()

        val pages = listOf(
            mainDependencies.greetingPage,
            mainDependencies.sPageFirst,
            mainDependencies.sPageSecond
        )

        setContent {
            PagerSample(items = pages)
        }
    }
}

@ContributesTo(AppScope::class)
interface MainDepsProvider {
    fun getMainDeps(): MainDependencies
}

class MainDependencies @Inject constructor(
    @Named("GreetingPage") val greetingPage: Page,
    @Named("SimplePageFirst") val sPageFirst: Page,
    @Named("SimplePageSecond") val sPageSecond: Page
)