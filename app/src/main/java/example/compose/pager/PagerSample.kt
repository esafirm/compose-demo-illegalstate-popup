package example.compose.pager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerSample(
    items: List<Page>
) {
    val pagerState = rememberPagerState { items.size }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
    ) { index ->
        items[index].Content()
    }
}

