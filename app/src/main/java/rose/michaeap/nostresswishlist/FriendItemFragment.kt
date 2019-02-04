package rose.michaeap.nostresswishlist

import android.opengl.Visibility
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.friend_item_fragment.view.*

class FriendItemFragment:Fragment() {

    lateinit var item:Item
    lateinit var name:String
    lateinit var userName:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            item = it.getParcelable(ARG_ITEM)
            name = it.getString(ARG_NAME)
            userName = it.getString(ARG_USERNAME)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.friend_item_fragment,container,false)
        view.o_item_name.text = item.name
        view.o_requested_by.text = "requested by $name"
        if (!item.mult)
            view.o_mult_req.visibility = View.GONE
        if (!item.online)
            view.o_online.visibility = View.GONE
        view.comments.text = item.comments
        return view
    }






    companion object {

        val ARG_ITEM = "item"
        val ARG_NAME = "name"
        val ARG_USERNAME = "username"

        @JvmStatic
        fun newInstance(item:Item,userName:String,name:String) =
            FriendItemFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_ITEM, item)
                    putString(ARG_NAME,name)
                    putString(ARG_USERNAME,userName)
                }
            }
    }
}