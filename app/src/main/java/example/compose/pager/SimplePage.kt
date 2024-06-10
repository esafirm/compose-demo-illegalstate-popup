package example.compose.pager

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import example.compose.AppScope
import javax.inject.Named

class SimplePage(
    private val contentText: String
) : Page {
    @Composable
    override fun Content() {
        Text(text = contentText)
    }
}

@ContributesTo(AppScope::class)
@Module
class SimplePageModule {

    @Named("SimplePageFirst")
    @Provides
    fun provideFirstSimplePage(): Page {
        return SimplePage("#1 Simple Page")
    }

    @Named("SimplePageSecond")
    @Provides
    fun provideSecondSimplePage(): Page {
        return SimplePage("#2 Simple Page")
    }
}