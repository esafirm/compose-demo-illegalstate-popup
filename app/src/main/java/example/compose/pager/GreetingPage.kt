package example.compose.pager

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import example.compose.AppScope
import javax.inject.Inject
import javax.inject.Named

class GreetingGenerator @Inject constructor() {
    fun generate(): String = "Hello World"
}

class GreetingDisplayer @Inject constructor() {
    fun displayGreeting(greeting: String) = println(greeting)
}

class GreetingViewModel @Inject constructor(
    private val generator: GreetingGenerator,
    private val displayer: GreetingDisplayer,
) {
    init {
        println("This is initiated ")
    }

    fun greet() {
        displayer.displayGreeting(generator.generate())
    }
}

/* --------------------------------------------------- */
/* > Page */
/* --------------------------------------------------- */

class GreetingPage : CommonPage() {
    @Composable
    override fun Content() {
        val vm = rememberViewModel<GreetingViewModel>()
        vm.greet()

        Text(text = "Check your Logcat!")
    }
}

/* --------------------------------------------------- */
/* > DI */
/* --------------------------------------------------- */

typealias PageVmFactory = Map<@JvmSuppressWildcards Class<*>, Any>

@ContributesTo(AppScope::class)
interface PageVmFactoryProvider {
    fun getFactory(): PageVmFactory
}

@ContributesTo(AppScope::class)
@Module
interface GreetingPageModule {

    @Binds
    @IntoMap
    @ClassKey(GreetingViewModel::class)
    fun bindViewModel(impl: GreetingViewModel): Any

    companion object {

        @Provides
        @Named("GreetingPage")
        fun greetingPage(): Page = GreetingPage()
    }
}