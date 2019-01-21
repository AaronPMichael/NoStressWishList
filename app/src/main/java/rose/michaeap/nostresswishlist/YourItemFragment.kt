package rose.michaeap.nostresswishlist

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import kotlinx.android.synthetic.main.fragment_your_items.view.*

class YourItemFragment: Fragment() {

    lateinit var button : Button
    lateinit var adapter : YourItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var frgmt = inflater.inflate(R.layout.fragment_your_items, container, false)
        var rclvw = frgmt.yi_recycler_view as RecyclerView
        frgmt.new_item_button.setOnClickListener {(activity as MainActivity).inputItem() }
        adapter = YourItemAdapter(activity)
        rclvw.layoutManager = LinearLayoutManager(context)
        rclvw.adapter = adapter


        adapter!!.loadItems()
        return frgmt
    }


}