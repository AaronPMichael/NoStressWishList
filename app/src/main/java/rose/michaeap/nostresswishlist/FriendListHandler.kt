package rose.michaeap.nostresswishlist

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.friend_list.view.*

class FriendListHandler : Fragment(){

    var userName=""
    var friendName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userName = it.getString(ARG_USER_NAME)
            friendName = it.getString(ARG_FRIEND_NAME)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.friend_list,container,false)
        var adapter = FriendListAdapter(context,userName,friendName)
        view.friend_list_recview.adapter = adapter
        adapter.addListener()
        view.friend_list_recview.layoutManager = LinearLayoutManager(context)
        return view
    }




    companion object {

        val ARG_USER_NAME = "username"
        val ARG_FRIEND_NAME = "friend name"

        @JvmStatic
        fun newInstance(userName: String,friendName:String) =
            FriendListHandler().apply {
                arguments = Bundle().apply {
                    putString(ARG_USER_NAME, userName)
                    putString(ARG_FRIEND_NAME, friendName)
                }
            }
    }
}