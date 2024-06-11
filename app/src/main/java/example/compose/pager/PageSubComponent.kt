package example.compose.pager

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCaller
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.MergeSubcomponent
import dagger.BindsInstance
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.Subcomponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Named

interface PageScope

typealias PageVmFactory = Map<@JvmSuppressWildcards Class<*>, Any>

@MergeSubcomponent(PageScope::class)
interface PageSubComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(
            @BindsInstance idProvider: ResultCallerIdProvider,
            @BindsInstance registry: ResultLauncherRegistry,
            @BindsInstance lifecycleOwner: LifecycleOwner,
        ): PageSubComponent
    }
}

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

@ContributesTo(PageScope::class)
@Module
class DefaultPageDependencies {

    @Provides
    fun provideLifecycle(lifecycleOwner: LifecycleOwner): Lifecycle {
        return lifecycleOwner.lifecycle
    }

    @Provides
    fun provideLifecycleScope(lifecycleOwner: LifecycleOwner): CoroutineScope {
        return lifecycleOwner.lifecycle.coroutineScope
    }
}
