package example.compose.pager

import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract

class ComposePageActivityResultCaller(
    private val registry: ActivityResultRegistry,
    private val idProvider: ResultCallerIdProvider,
    private val launcherRegistry: ResultLauncherRegistry,
) : ActivityResultCaller {

    override fun <I, O> registerForActivityResult(
        contract: ActivityResultContract<I, O>,
        callback: ActivityResultCallback<O>
    ): ActivityResultLauncher<I> {
        val key = idProvider.nextId()
        val launcher = registry.register(key, contract, callback)
        launcherRegistry.add(launcher)
        return launcher
    }

    override fun <I, O> registerForActivityResult(
        contract: ActivityResultContract<I, O>,
        registry: ActivityResultRegistry,
        callback: ActivityResultCallback<O>
    ): ActivityResultLauncher<I> {
        error("You need to use the registry provided in the constructor")
    }
}