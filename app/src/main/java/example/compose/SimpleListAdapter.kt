package example.compose

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class SimpleAdapter(
    private val items: List<String>
) : RecyclerView.Adapter<SimpleAdapter.ViewHolder>() {

    private val ref = mutableSetOf<Int>()

    class ViewHolder(
        itemView: ComposeItemView
    ) : RecyclerView.ViewHolder(itemView) {
        fun bindData(pos: Int) {
            (itemView as ComposeItemView).bindData(pos)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ComposeItemView(parent.context))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hashCode = items[position].hashCode()

        if (ref.contains(hashCode)) {
            Log.d("Adapter", "binding existing item at position $position")
        } else {
            Log.d("Adapter", "binding new item at position $position")
            ref.add(hashCode)
        }

        holder.bindData(position)
    }

    override fun getItemCount(): Int = items.size
}