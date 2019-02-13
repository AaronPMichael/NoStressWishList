package rose.michaeap.nostresswishlist

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.commitment_fragment.view.*

class CommitmentViewer: Fragment() {
    var name=""
    lateinit var noCommitMessage:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        name = arguments?.getString(name)?:""
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.commitment_fragment,container,false)
        var recview = view.commit_rv
        var adapter = CommitAdapter(context,name)
        recview.adapter = adapter
        noCommitMessage = view.no_commit_message
        adapter.addListeners(noCommitMessage)
        recview.layoutManager = LinearLayoutManager(context)
        return view
    }






    companion object {

        val ARG_NAME = "name"

        @JvmStatic
        fun newInstance(name: String) =
            CommitmentViewer().apply {
                arguments = Bundle().apply {
                    putString(ARG_NAME, name)
                }
            }
    }



}