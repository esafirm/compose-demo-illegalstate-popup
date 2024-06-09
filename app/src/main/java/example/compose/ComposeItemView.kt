package example.compose

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup

class ComposeItemView(context: Context) : AbstractComposeView(context) {

    private var itemPosition = mutableIntStateOf(0)

    @Composable
    override fun Content() {

        val currView = LocalView.current
        if (currView.isAttachedToWindow.not()) {
            val parentCount = generateSequence(currView.parent) { it.parent }.count()
            Log.d(
                "ItemV",
                "Item not attached to window pos: ${itemPosition.intValue} -- parentCount: $parentCount"
            )
        }

        // This basically the condition that introduce the bug.
        // This is just a sample scenario, it can be anything else.
        val needToShowTooltip = itemPosition.intValue % 10 == 0
        
        // This is the state that will be used to show/hide the tooltip
        var showTooltip by remember(itemPosition.intValue) { mutableStateOf(needToShowTooltip) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "This is item in position: ${itemPosition.intValue}",
                fontSize = 22.sp,
                modifier = Modifier.clickable(onClick = {
                    showTooltip = true
                })
            )
        }

        // This can be avoided by checkin the state of the view
        // e.g: is it attached to window or not
        if (showTooltip) {
            SimplePopup {
                showTooltip = false
            }
        }
    }

    @Composable
    private fun SimplePopup(
        onDismiss: () -> Unit
    ) {
        Popup(
            alignment = Alignment.Center,
            onDismissRequest = onDismiss,
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(Color.White)
                    .shadow(2.dp)
                    .padding(16.dp),
            ) {
                Text(
                    text = "This is item in position: ${itemPosition.intValue}",
                    fontSize = 22.sp,
                )
            }
        }
    }

    fun bindData(pos: Int) {
        itemPosition.intValue = pos
    }
}
