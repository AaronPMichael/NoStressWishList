package rose.michaeap.nostresswishlist

import android.content.Context
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class YourItemAdapter(var context: Context?):RecyclerView.Adapter<YourItemHolder>(),ItemAdapter {

    lateinit var items: ArrayList<Item>
    lateinit var source: ItemSource
    val itemsRef = FirebaseFirestore.getInstance().collection("items")

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): YourItemHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_card_view, parent, false)
        return YourItemHolder(view, this)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(p0: YourItemHolder, p1: Int) {
        p0.bind(items[p1])
    }

    fun loadItems() {
        source = context as ItemSource
        source.registerItemAdapter(this)
        items = source.getItemList()
        Log.i("MyTag",items.size.toString())
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
}
    interface ItemAdapter{
        fun itemChanged(n:Int)
        fun notifyAdded()
        fun itemRemoved(n:Int)
    }
