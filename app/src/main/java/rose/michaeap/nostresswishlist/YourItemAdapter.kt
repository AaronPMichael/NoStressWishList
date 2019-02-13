package rose.michaeap.nostresswishlist

import android.content.Context
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class YourItemAdapter(var context: Context?,var name:String):RecyclerView.Adapter<ItemHolder>(),ItemAdapter {

    var items = ArrayList<Item>()
    val itemsRef = FirebaseFirestore.getInstance().collection("items")
    val commitRef = FirebaseFirestore.getInstance().collection("commitment")
    val compRef = FirebaseFirestore.getInstance().collection("commitDeletionNotices")
    val userRef = FirebaseFirestore.getInstance().collection("users")
    val source = context as MainActivity
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ItemHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_card_view, parent, false)
        return ItemHolder(view, this)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(p0: ItemHolder, p1: Int) {
        p0.bind(items[p1])
    }

    fun selectItem(item: Item) {
        source.selectItem(item)
    }

    fun removeItem(item: Item): Boolean {
        var builder = AlertDialog.Builder(context!!)
        builder.setView(R.layout.remove_item)
        builder.setTitle(item.name)
        var n = -1
        for ((pos,it) in items.withIndex())
            if (it.id==item.id)
                n = pos
        builder.setPositiveButton(android.R.string.yes, { _, _ ->
            if (n != -1) {
                checkItemDeletion(n)
//                items.removeAt(n)
//                notifyItemRemoved(n)
//                itemsRef.document(item.id).delete()
            }
        })
        builder.setNegativeButton(android.R.string.no, { _, _ -> })
        builder.create().show()
        return true;
    }

    override fun itemChanged(n:Int){
        notifyItemChanged(n)
    }

    override fun notifyAdded() {
        notifyItemInserted(items.size-1)
    }

    override fun itemRemoved(n: Int) {
        notifyItemRemoved(n)
    }

    fun addListener(){
        if (!items.isEmpty())
            notifyItemRangeRemoved(0,items.size-1)
        items = ArrayList<Item>()
        itemsRef.addSnapshotListener { snapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) {
                Log.e("MyTag", firebaseFirestoreException.message)
                return@addSnapshotListener
            }
            for (documentChange in snapshot!!.documentChanges) {
                processChange(documentChange)
            }
        }
    }
    fun processChange(change: DocumentChange){
        var item = change.document.toObject(Item::class.java)
        if (item.ownerName!=name)
            return
        item.id = change.document.id
        if (change.type == DocumentChange.Type.ADDED){
            items.add(item)
                notifyAdded()
        }
        if (change.type == DocumentChange.Type.MODIFIED){
            for ((n,it) in items.withIndex()){
                if (it.id==item.id){
                    items[n]=item
                        itemChanged(n)
                    break
                }
            }
        }
        if (change.type == DocumentChange.Type.REMOVED){
            for ((n,it) in items.withIndex()){
                if (it.id == item.id){
                    items.removeAt(n)
                        itemRemoved(n)
                    break
                }
            }
        }
    }
    fun checkItemDeletion(n:Int){
        var item=items[n]
        commitRef.whereEqualTo("itemID",item.id).get().addOnSuccessListener {
            if (it.documents.isEmpty()) {
                items.removeAt(n)
                notifyItemRemoved(n)
                itemsRef.document(item.id).delete()
                return@addOnSuccessListener
            }
            var builder = AlertDialog.Builder(context!!)
            var view = LayoutInflater.from(context).inflate(R.layout.commit_fallback, null, false)
            builder.setView(view)
            builder.setTitle("Remove item: ${item.name}")
            builder.setNegativeButton(android.R.string.no, null)
            builder.setPositiveButton(android.R.string.yes, { _, _ ->
                Toast.makeText(context,"Notifying involved users",Toast.LENGTH_SHORT)
                removeCommittedItem(item)
            })
            builder.create().show()
        }
    }
    fun removeCommittedItem(item:Item){
        var userNames = HashSet<String>()
        commitRef.whereEqualTo("itemID",item.id).get().addOnSuccessListener {
            for (doc in it.documents){
                var commit = doc.toObject(Commitment::class.java)
                userNames.add(commit!!.user)
                commitRef.document(doc.id).delete()
            }
            notifyRemoval(item,userNames)
            itemsRef.document(item.id).delete()
        }
    }
    fun notifyRemoval(item:Item,userNames:HashSet<String>){
        for (user in userNames){
            userRef.whereEqualTo("name",user).get().addOnSuccessListener {
                for (doc in it.documents) {
                    var user = doc.toObject(User::class.java)
                    compRef.add(DeletionNotice(user!!.email,item.name,item.ownerName))
                }
            }
        }
    }
}
    interface ItemAdapter{
        fun itemChanged(n:Int)
        fun notifyAdded()
        fun itemRemoved(n:Int)
    }
