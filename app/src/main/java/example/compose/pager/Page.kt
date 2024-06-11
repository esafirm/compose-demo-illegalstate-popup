package example.compose.pager

import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import example.compose.AppComponentInstance
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

interface Page {
    @Composable
    fun Content()
}

abstract class CommonPage : Page {

    @Composable
    inline fun <reified T : Any> rememberViewModel(): T {
        return rememberViewModel(T::class.java)
    }
}

@Composable
fun <T : Any> CommonPage.rememberViewModel(clazz: Class<T>): T {
    val prefixIdentifier = rememberSaveable { UUID.randomUUID().toString() }
    val idProvider = remember { IncrementalIdProvider(prefixIdentifier) }
    val callerRegistry = remember { SimpleResultLauncherRegistry() }

    DisposableEffect(prefixIdentifier, idProvider) {
        onDispose {
            callerRegistry.unregister()
        }
    }

    val lifecycleOwner = rememberLifecycleOwner()

    return remember {
        val component = AppComponentInstance.get().pageComponentFactor().create(
            idProvider = idProvider,
            registry = callerRegistry,
            lifecycleOwner = lifecycleOwner
        )
        val vmProvider = component as PageVmFactoryProvider
        val vm = vmProvider.getFactory()[clazz]

        @Suppress("UNCHECKED_CAST")
        vm as T
    }
}

/* --------------------------------------------------- */
/* > Lifecycle */
/* --------------------------------------------------- */

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

/* --------------------------------------------------- */
/* > Id Provider */
/* --------------------------------------------------- */

class IncrementalIdProvider(
    private val prefixIdentifier: String
) : ResultCallerIdProvider {

    private val nextLocalRequestCode = AtomicInteger()

    override fun nextId(): String {
        return "${prefixIdentifier}_${nextLocalRequestCode.getAndIncrement()}"
    }
}

interface ResultCallerIdProvider {
    fun nextId(): String
}

/* --------------------------------------------------- */
/* > Registry */
/* --------------------------------------------------- */

interface ResultLauncherRegistry {
    val registeredResultCallers: Set<ActivityResultLauncher<*>>

    fun add(launcher: ActivityResultLauncher<*>)
    fun unregister()
}

class SimpleResultLauncherRegistry : ResultLauncherRegistry {
    override val registeredResultCallers: MutableSet<ActivityResultLauncher<*>> = mutableSetOf()

    @Synchronized
    override fun add(launcher: ActivityResultLauncher<*>) {
        registeredResultCallers.add(launcher)
    }

    @Synchronized
    override fun unregister() {
        registeredResultCallers.forEach { it.unregister() }
        registeredResultCallers.clear()
    }
}

