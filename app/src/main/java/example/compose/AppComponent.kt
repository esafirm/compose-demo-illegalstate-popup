package example.compose

import com.squareup.anvil.annotations.MergeComponent

/**
 * A simple Dagger component component
 */
@MergeComponent(scope = AppScope::class)
interface AppComponent {
    companion object {
        fun get(): AppComponent = DaggerAppComponent.create()
    }
}

interface AppScope
