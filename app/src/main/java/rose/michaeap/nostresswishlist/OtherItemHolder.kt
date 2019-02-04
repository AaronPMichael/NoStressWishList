package rose.michaeap.nostresswishlist

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.item_card_view.view.*

class OtherItemHolder(itemView: View, var adapter:FriendListAdapter):RecyclerView.ViewHolder(itemView) {

    fun bind(item:Item){
        itemView.item_name.setText(item.name)
        itemView.setOnClickListener {adapter.inspectItem(item)}
    }
}