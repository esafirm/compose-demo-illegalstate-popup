package example.compose.pager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerSample(
    items: List<Page>
) {
    val pagerState = rememberPagerState { items.size }

    HorizontalPager(state = pagerState) { index ->
        items[index].Content()
    }
}

