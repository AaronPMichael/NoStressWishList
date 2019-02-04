package rose.michaeap.nostresswishlist

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.firebase.firestore.*
import com.google.firebase.firestore.model.Document

class FriendListAdapter(var context: Context?, var username:String,var friendname:String):RecyclerView.Adapter<OtherItemHolder>() {
    var items = ArrayList<Item>()
    var itemRef = FirebaseFirestore.getInstance().collection("items")

    override fun getItemCount(): Int {
        return items.size
    }
    override fun onBindViewHolder(holder: OtherItemHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OtherItemHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_card_view, parent, false)
        return OtherItemHolder(view,this)
    }
    fun addListener(){
        itemRef.addSnapshotListener(){snapshot: QuerySnapshot?, exception: FirebaseFirestoreException? ->
            if (exception!=null){
                Log.e("MyTag",exception.message)
                return@addSnapshotListener
            }
            for (document in snapshot!!.documentChanges)
                processChange(document)
        }
    }
    fun processChange(change:DocumentChange){
        var item = change.document.toObject(Item::class.java)
        item.id = change.document.id
        if (item!!.ownerName!=friendname)
            return
        if (change.type== DocumentChange.Type.ADDED){
            items.add(item)
            notifyItemInserted(items.size-1)
        }
        if (change.type == DocumentChange.Type.MODIFIED){
            for ((pos,it) in items.withIndex()){
                if (it.id == item.id){
                    items[pos]=item
                    notifyItemChanged(pos)
                }
            }
        }
        if (change.type==DocumentChange.Type.REMOVED){
            for ((pos,it) in items.withIndex()){
                if (it.id == item.id){
                    items.removeAt(pos)
                    notifyItemRemoved(pos)
                }
            }
        }
    }
    fun inspectItem(item:Item){
        (context as MainActivity).inspectFriendItem(item,username,friendname)
    }
}