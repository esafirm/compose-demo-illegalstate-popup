package example.compose

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCaller
import com.squareup.anvil.annotations.MergeComponent
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import example.compose.pager.PageSubComponent

/**
 * A simple Dagger component component
 */
@MergeComponent(
    scope = AppScope::class,
    modules = [GenericModule::class, ActivityHostModule::class]
)
interface AppComponent {

    fun pageComponentFactor(): PageSubComponent.Factory

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance activity: MainActivity): AppComponent
    }
}

/**
 * A module that convert the bounded activity to a generic [ComponentActivity]
 */
@Module
class GenericModule {
    @Provides
    fun provideActivity(activity: MainActivity): ComponentActivity = activity
}

/**
 * Default dependencies for host
 */
@Module
class ActivityHostModule {
    @Provides
    fun provideResultCaller(activity: ComponentActivity): ActivityResultCaller = activity
}

interface AppScope

/**
 * A singleton object that holds the AppComponent instance
 */
object AppComponentInstance {

    private var appComponent: AppComponent? = null

    fun create(activity: MainActivity): AppComponent {
        if (appComponent != null) {
            println("AppComponent:: replacing existing component")
        }
        appComponent = DaggerAppComponent.factory().create(activity)
        return appComponent!!
    }

    fun get(): AppComponent {
        if (appComponent == null) {
            error("AppComponent is not initialized. Call create() first.")
        }
        return appComponent!!
    }
}
