package example.compose.anvil

import com.squareup.anvil.annotations.ContributesTo
import dagger.BindsInstance
import dagger.MembersInjector
import example.compose.AppComponent

interface AnvilAndroidInjector<T> {

    val injector: MembersInjector<T>

    fun inject(target: T) {
        injector.injectMembers(target)
    }

    interface Factory<T> {
        fun create(@BindsInstance instance: T): AnvilAndroidInjector<T>
    }
}

@ContributesTo(AppComponent::class)
interface AnvilAndroidInjectorProvider {
    fun dispatchingAnvilInjector(): DispatchingAnvilInjector
}

typealias DispatchingAnvilInjector = Map<@JvmSuppressWildcards Class<*>, @JvmSuppressWildcards AnvilAndroidInjector.Factory<*>>
