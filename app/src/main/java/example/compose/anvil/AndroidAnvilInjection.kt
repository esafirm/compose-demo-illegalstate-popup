package example.compose.anvil

import example.compose.AppComponentInstance
import example.compose.pager.Page

/**
 * Helper to inject [Page]
 *
 * This is not working right now because we haven't setup the multibinding
 */
object AndroidAnvilInjection {

    fun <T : Page> inject(page: T) {

        val injectorProvider = AppComponentInstance.get() as AnvilAndroidInjectorProvider

        val injector = injectorProvider.dispatchingAnvilInjector()[page::class.java] ?: error(
            """
            No injector found for ${page::class.qualifiedName}, 
            you probably forget to annotate page with @ContributesInjector
            """.trimIndent()
        )

        @Suppress("UNCHECKED_CAST")
        (injector as AnvilAndroidInjector.Factory<Page>)
            .create(page)
            .inject(page)
    }
}