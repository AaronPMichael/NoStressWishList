package rose.michaeap.nostresswishlist

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class GroupAdapter(var context: Context?,var user:String):RecyclerView.Adapter<GroupHolder>() {
    var groups = ArrayList<GroupRelation>()
    var groupRef = FirebaseFirestore.getInstance().collection("groupRefs")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.group_card, parent, false)
        return GroupHolder(view, this)
    }

    override fun getItemCount(): Int {
        return groups.size
    }

    override fun onBindViewHolder(holder: GroupHolder, position: Int) {
        holder.bind(groups[position])
    }
    fun loadGroups(){
        var size = groups.size-1
        groups = ArrayList<GroupRelation>()
        notifyItemRangeRemoved(0,size)
        groupRef.whereEqualTo("userName",user).get().addOnSuccessListener{snapshot:QuerySnapshot?->
            for (doc in snapshot!!.documents){
                var group = doc.toObject(GroupRelation::class.java)
                groups.add(group!!)
                notifyItemInserted(groups.size-1)
            }
        }
    }
}