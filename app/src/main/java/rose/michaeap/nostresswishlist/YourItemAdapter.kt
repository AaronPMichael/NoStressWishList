package rose.michaeap.nostresswishlist

import android.content.Context
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class YourItemAdapter(var context: Context?,var name:String):RecyclerView.Adapter<ItemHolder>(),ItemAdapter {

    var items = ArrayList<Item>()
    lateinit var source: ItemSource
    val itemsRef = FirebaseFirestore.getInstance().collection("items")

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

    fun loadItems() {
        source = context as ItemSource
        source.registerItemAdapter(this)
        items = source.getItemList()
        notifyItemRangeInserted(0, items.size)
    }

    fun selectItem(item: Item) {
        source.selectItem(item)
    }

    fun removeItem(item: Item): Boolean {
        var builder = AlertDialog.Builder(context!!)
        builder.setView(R.layout.remove_item)
        builder.setTitle(item.name)
        builder.setPositiveButton(android.R.string.yes, { _, _ ->
            val n = source.removeItem(item)
            if (n != -1)
                notifyItemRemoved(n)
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
}
    interface ItemAdapter{
        fun itemChanged(n:Int)
        fun notifyAdded()
        fun itemRemoved(n:Int)
    }
