package rose.michaeap.nostresswishlist

import android.content.Context
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class GroupAdapter(var context: Context?,var user:String):RecyclerView.Adapter<GroupHolder>() {
    var groups = ArrayList<GroupRelation>()
    var groupRelRef = FirebaseFirestore.getInstance().collection("groupRefs")
    var groupRef = FirebaseFirestore.getInstance().collection("groups")


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
        groupRelRef.whereEqualTo("userName",user).get().addOnSuccessListener{ snapshot:QuerySnapshot?->
            for (doc in snapshot!!.documents){
                var group = doc.toObject(GroupRelation::class.java)
                groups.add(group!!)
                notifyItemInserted(groups.size-1)
            }
        }
    }
    fun inspectGroup(name:String,userName:String){
        var builder = AlertDialog.Builder(context!!)
        builder.setTitle("Leave group: $name?")
        builder.setNegativeButton(android.R.string.no,{_,_-> })
        builder.setPositiveButton(android.R.string.ok,{_,_->
            groupRelRef.whereEqualTo("group",name).get().addOnSuccessListener {
                var delete = true
                for (doc in it.documents){
                    var gr = doc.toObject(GroupRelation::class.java)
                    if (gr!!.userName==userName){
                        groupRelRef.document(doc.id).delete()
                    }
                    else{
                        delete=false
                    }
                }
                if (delete){
                    groupRef.whereEqualTo("name",name).get().addOnSuccessListener {
                        for (doc in it.documents) {
                            groupRef.document(doc.id).delete()
                            Toast.makeText(context, "Group $name has been deleted due to lack of participants",Toast.LENGTH_SHORT)
                        }
                    }
                }
                loadGroups()
            }
        })
        builder.create().show()
    }
}