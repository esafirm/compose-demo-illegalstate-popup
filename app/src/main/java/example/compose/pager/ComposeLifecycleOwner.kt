package example.compose.pager

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

class ComposeLifecycleOwner : LifecycleOwner {

    private val lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)
    override val lifecycle: Lifecycle = lifecycleRegistry

    fun moveToState(state: Lifecycle.State) {
        lifecycleRegistry.currentState = state
    }
}

@Composable
fun rememberLifecycleOwner(): ComposeLifecycleOwner {
    val lifecycleOwner = remember {
        ComposeLifecycleOwner().apply {
            moveToState(Lifecycle.State.INITIALIZED)
        }
    }

    DisposableEffect(Unit) {
        lifecycleOwner.moveToState(Lifecycle.State.STARTED)
        lifecycleOwner.moveToState(Lifecycle.State.RESUMED)
        onDispose {
            lifecycleOwner.moveToState(Lifecycle.State.DESTROYED)
        }
    }

    return lifecycleOwner
}