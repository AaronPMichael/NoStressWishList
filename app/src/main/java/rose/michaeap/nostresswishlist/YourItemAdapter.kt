package rose.michaeap.nostresswishlist

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import rose.michaeap.nostresswishlist.R.id.parent

class YourItemAdapter(var context: Context):RecyclerView.Adapter<YourItemHolder>() {

    var items = ArrayList<Item>()



    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): YourItemHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.item_card_view,parent,false)
        return YourItemHolder(view,this)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(p0: YourItemHolder, p1: Int) {
        p0.bind(items[0])
    }

}