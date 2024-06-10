package example.compose.pager

import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
        val prefixIdentifier = rememberSaveable { UUID.randomUUID().toString() }
        val idProvider = remember { IncrementalIdProvider(prefixIdentifier) }
        val callerRegistry = remember { SimpleResultLauncherRegistry() }

        DisposableEffect(prefixIdentifier, idProvider) {
            onDispose {
                callerRegistry.unregister()
            }
        }

        return remember {
            val component = AppComponentInstance.get().pageComponentFactor().create(
                idProvider,
                callerRegistry
            )
            val vmProvider = component as PageVmFactoryProvider
            val vm = vmProvider.getFactory()[T::class.java]
            vm as T
        }
    }
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

