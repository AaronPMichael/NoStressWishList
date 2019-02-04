package rose.michaeap.nostresswishlist

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore

class FriendsAdapter(var context: Context?,var userName:String):RecyclerView.Adapter<FriendHolder>() {
    var groupRef = FirebaseFirestore.getInstance().collection("groups")
    var ingroupRef = FirebaseFirestore.getInstance().collection("groupRefs")
    lateinit var friendListener:FriendListener


    var people = ArrayList<Person>()
    override fun getItemCount(): Int {
        return people.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.friend_card, parent, false)
        return FriendHolder(view, this)
    }
    override fun onBindViewHolder(holder: FriendHolder, position: Int) {
        holder.bind(people[position])
    }
    fun loadFriends(){
        var size = people.size
        notifyItemRangeRemoved(0,size)
        people = ArrayList<Person>()
        ingroupRef.get().addOnSuccessListener {
            var groups = ArrayList<GroupRelation>()
            var groupNames = ArrayList<String>()
            for (doc in it.documents){
                var rel = doc.toObject(GroupRelation::class.java)
                if (rel?.userName == userName && !groupNames.contains(rel.group)){
                    groupNames.add(rel.group)
                }
                if (rel!=null && !groups.contains(rel)) {
                    groups.add(rel)
                }
            }
            for (rel in groups){
                if (groupNames.contains(rel.group) && rel.userName!=userName){
                    people.add(Person(rel.userName))
                    notifyItemInserted(people.size-1)
                }
            }
        }
    }
}