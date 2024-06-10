package example.compose.pager

import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.MergeSubcomponent
import dagger.Binds
import dagger.BindsInstance
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.Subcomponent
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
    @Named("PageCaller") caller: ActivityResultCaller,
) {

    private val launcher =
        caller.registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            println("VM:: take picture result: ${it.hashCode()}")
        }

    init {
        println("This is initiated ")
    }

    fun greet() {
        displayer.displayGreeting(generator.generate())
    }

    fun takePicture() {
        launcher.launch()
    }
}

/* --------------------------------------------------- */
/* > Page */
/* --------------------------------------------------- */

class GreetingPage : CommonPage() {
    @Composable
    override fun Content() {
        val launcher =
            rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) {
                println("Composable:: take picture result: ${it.hashCode()}")
            }

        val vm = rememberViewModel<GreetingViewModel>()
        vm.greet()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Check your Logcat!")
            Button(onClick = vm::takePicture) {
                Text(text = "Take Picture")
            }
            Button(onClick = { launcher.launch() }) {
                Text(text = "Take Picture in Compose")
            }
        }
    }
}

/* --------------------------------------------------- */
/* > DI */
/* --------------------------------------------------- */

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

/* --------------------------------------------------- */
/* > DI - Subcomponent */
/* --------------------------------------------------- */

interface PageScope

typealias PageVmFactory = Map<@JvmSuppressWildcards Class<*>, Any>

@ContributesTo(PageScope::class)
interface PageVmFactoryProvider {
    fun getFactory(): PageVmFactory
}

@ContributesTo(PageScope::class)
@Module
class PageActivityResultCallerModule {

    @Named("PageCaller")
    @Reusable
    @Provides
    fun provideActivityResultCaller(
        activity: ComponentActivity,
        idProvider: ResultCallerIdProvider,
        registry: ResultLauncherRegistry,
    ): ActivityResultCaller {
        return ComposePageActivityResultCaller(
            activity.activityResultRegistry,
            idProvider,
            registry
        )
    }
}

@MergeSubcomponent(PageScope::class)
interface PageSubComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(
            @BindsInstance idProvider: ResultCallerIdProvider,
            @BindsInstance registry: ResultLauncherRegistry,
        ): PageSubComponent
    }
}
