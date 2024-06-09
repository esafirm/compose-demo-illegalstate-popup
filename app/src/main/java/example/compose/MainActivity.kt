package example.compose

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val items = (0 until 100).map { "Item $it" }
        val view = RecyclerView(this).apply {
            adapter = SimpleAdapter(items)
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        setContentView(view.apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        })
    }
}
