package rose.michaeap.nostresswishlist

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.friend_card.view.*

class FriendHolder(var cardView: View, var adapter:FriendsAdapter):RecyclerView.ViewHolder(cardView) {

    fun bind(person:Person){
        cardView.friend_name.setText(person.name)
        cardView.setOnClickListener {
            adapter.friendListener.showFriendList(person.name)
        }
    }
}