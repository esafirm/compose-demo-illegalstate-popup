package example.compose.pager

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
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import example.compose.AppScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    private val scope: CoroutineScope,
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

    fun callLongTask(delayInMs: Long = 10_000L) {
        val job = scope.launch {
            println("VM:: Long task started - delay: $delayInMs")
            delay(delayInMs)
            println("VM:: Long task completed inside launch")
        }
        job.invokeOnCompletion { cause ->
            println("VM:: Long task completed cause: $cause")
        }
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
            Button(onClick = vm::callLongTask) {
                Text(text = "Call Long Task")
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

    companion object {

        @Provides
        @Named("GreetingPage")
        fun greetingPage(): Page = GreetingPage()
    }
}

/**
 * We're using this for now
 * because using Any::class as `boundType` resulting a compile error in Anvil
 */
@ContributesTo(PageScope::class)
@Module
interface PageVmModule {

    @Binds
    @IntoMap
    @ClassKey(GreetingViewModel::class)
    fun bindViewModel(impl: GreetingViewModel): Any
}
