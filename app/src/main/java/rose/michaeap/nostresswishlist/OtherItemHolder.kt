package rose.michaeap.nostresswishlist

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.item_card_view.view.*

class OtherItemHolder(itemView: View, var adapter:FriendListAdapter):RecyclerView.ViewHolder(itemView) {

    fun bind(item:Item,commit:Boolean){
        itemView.item_name.setText(item.name)
        if (item.priority)
            itemView.setBackgroundColor(itemView.resources.getColor(R.color.colorGold))
        if (commit)
            itemView.setBackgroundColor(itemView.resources.getColor(R.color.colorGreen))
        itemView.setOnClickListener {adapter.inspectItem(item)}
    }
}