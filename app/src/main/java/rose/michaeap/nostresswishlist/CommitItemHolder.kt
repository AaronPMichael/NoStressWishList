package rose.michaeap.nostresswishlist

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.add_user_name.view.*
import kotlinx.android.synthetic.main.item_card_view.view.*

class CommitItemHolder(var view: View, var adapter:CommitAdapter):RecyclerView.ViewHolder(view) {

    fun bind(item:Item){
        view.item_name.text = item.name
        view.setOnClickListener {
            adapter.inspect(item)
        }
    }
}