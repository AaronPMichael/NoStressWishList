package rose.michaeap.nostresswishlist

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.item_card_view.view.*

class ItemHolder(itemView: View, var adapter:YourItemAdapter):RecyclerView.ViewHolder(itemView) {

    fun bind(item:Item){
        itemView.item_name.text = item.name
        itemView.setOnClickListener {adapter.selectItem(item)}
        itemView.setOnLongClickListener {adapter.removeItem(item)}
        if (item.priority){
            (itemView as CardView).setCardBackgroundColor(itemView.resources.getColor(R.color.colorGold))
        }
    }
}