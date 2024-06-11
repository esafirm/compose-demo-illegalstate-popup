package example.compose.anvil

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
import example.compose.pager.GreetingViewModel
import example.compose.pager.Page
import javax.inject.Inject

class MemberInjectionGreetingPage : Page {

    @Inject
    lateinit var vm: GreetingViewModel

    init {
        /**
         * This could work but,
         *
         * With this approach we cannot provide the [ActivityResultCaller] or any class that bound
         * to composable lifecycle.
         */
        AndroidAnvilInjection.inject(this)
    }

    @Composable
    override fun Content() {
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
