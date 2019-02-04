package rose.michaeap.nostresswishlist

import android.app.AlertDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.add_group.view.*
import kotlinx.android.synthetic.main.group_page_fragment.view.*

class GroupPageHandler:Fragment(),FriendListener {
    var userName = ""
    var groupRef = FirebaseFirestore.getInstance().collection("groups")
    var gRelationRef = FirebaseFirestore.getInstance().collection("groupRefs")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userName = it.getString(ARG_NAME)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.group_page_fragment, container, false)
        var groupAdapter = GroupAdapter(context, userName)
        groupAdapter.loadGroups()
        view.groups_recview.adapter = groupAdapter
        view.groups_recview.layoutManager = LinearLayoutManager(context)
        var fadapter = FriendsAdapter(context, userName)
        view.people_recview.adapter = fadapter
        view.people_recview.layoutManager = LinearLayoutManager(context)
        fadapter.friendListener = this
        fadapter.loadFriends()
        view.create_group_button.setOnClickListener {
            var builder = AlertDialog.Builder(context)
            builder.setTitle("Enter group name")
            var view = LayoutInflater.from(context).inflate(R.layout.add_group, null, false)
            builder.setView(view)
            builder.setPositiveButton(android.R.string.ok, { _, _ ->
                var name = view.enter_group_name.text.toString()
                var group = Group(name)
                groupRef.whereEqualTo("name", name).get().addOnSuccessListener {
                    if (!it.isEmpty) {
                        Toast.makeText(context, "A group by that name already exists", Toast.LENGTH_SHORT)
                    } else {
                        groupRef.add(group)
                        Toast.makeText(context, "Your group has been created", Toast.LENGTH_SHORT)
                        var ref = GroupRelation(name, userName)
                        gRelationRef.add(ref)
                        groupAdapter.loadGroups()
                        fadapter.loadFriends()
                    }
                }
            })
            builder.create().show()
        }
        view.join_group_button.setOnClickListener {
            var builder = AlertDialog.Builder(context)
            var view = LayoutInflater.from(context).inflate(R.layout.add_group, null, false)
            builder.setView(view)
            builder.setPositiveButton(android.R.string.ok, { _, _ ->
                var name = view.enter_group_name.text.toString()
                groupRef.whereEqualTo("name", name).get().addOnSuccessListener {
                    if (it.isEmpty) {
                        Toast.makeText(context, "No group exists with that name", Toast.LENGTH_SHORT)
                        return@addOnSuccessListener
                    }
                    gRelationRef.whereEqualTo("group", name).whereEqualTo("userName", userName).get()
                        .addOnSuccessListener {
                            if (!it.isEmpty) {
                                Toast.makeText(context, "You are already in that group", Toast.LENGTH_SHORT).show()
                                return@addOnSuccessListener
                            }
                            var gr = GroupRelation(name, userName)
                            gRelationRef.add(gr)
                            Toast.makeText(context, "You have joined $name", Toast.LENGTH_SHORT).show()
                            groupAdapter.loadGroups()
                            fadapter.loadFriends()
                            fadapter.friendListener=this
                        }
                }
            })
            builder.create().show()
        }
        return view
    }

    override fun showFriendList(friendName: String) {
        (context as MainActivity).showFriendList(friendName)
    }


    companion object {

        val ARG_NAME = "item"

        @JvmStatic
        fun newInstance(name: String) =
            GroupPageHandler().apply {
                arguments = Bundle().apply {
                    putString(ARG_NAME, name)
                }
            }
    }
}
    interface FriendListener{
        fun showFriendList(friendName: String)
    }