package example.compose.pager

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import example.compose.AppComponent

interface Page {
    @Composable
    fun Content()
}

abstract class CommonPage : Page {

    @Composable
    inline fun <reified T : Any> rememberViewModel(): T {
        return remember {
            val vmProvider = AppComponent.get() as PageVmFactoryProvider
            val vm = vmProvider.getFactory()[T::class.java]
            vm as T
        }
    }
}