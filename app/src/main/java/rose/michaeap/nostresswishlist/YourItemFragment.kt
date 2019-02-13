package rose.michaeap.nostresswishlist

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_your_items.view.*

class YourItemFragment: Fragment() {

    lateinit var button : Button
    lateinit var adapter : YourItemAdapter
    var name=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            name = it.getString(ARG_NAME)
        }
    }

    override fun onResume() {
        super.onResume()
        (context as MainActivity).toolbar.title = "$name's Wish List"
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var source = context as MainActivity
        source.toolbar.title = "$name's Wish List"
        var frgmt = inflater.inflate(R.layout.fragment_your_items, container, false)
        var rclvw = frgmt.yi_recycler_view as RecyclerView
        frgmt.new_item_button.setOnClickListener {source.inputItem() }
        adapter = YourItemAdapter(activity,name)
        adapter.addListener()
        rclvw.layoutManager = LinearLayoutManager(context)
        rclvw.adapter = adapter

        //adapter!!.loadItems()
        return frgmt
    }


    companion object {

        val ARG_NAME = "name"

        @JvmStatic
        fun newInstance(name: String) =
            YourItemFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_NAME, name)
                }
            }
    }
}