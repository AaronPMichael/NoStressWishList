package rose.michaeap.nostresswishlist

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.add_group.view.*
import kotlinx.android.synthetic.main.group_card.view.*

class GroupHolder(var groupView: View, var adapter:GroupAdapter):RecyclerView.ViewHolder(groupView) {
    fun bind(group:GroupRelation){
        groupView.group_name.text = group.group
    }
}