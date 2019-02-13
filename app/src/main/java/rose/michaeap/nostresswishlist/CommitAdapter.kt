package rose.michaeap.nostresswishlist

import android.content.Context
import android.opengl.Visibility
import android.support.v7.view.menu.MenuView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class CommitAdapter(var context: Context?, var username:String) : RecyclerView.Adapter<CommitItemHolder>(){
    var commits = ArrayList<Item>()
    var commitRef = FirebaseFirestore.getInstance().collection("commitment")
    var itemsRef = FirebaseFirestore.getInstance().collection("items")

    override fun getItemCount(): Int {
        return commits.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommitItemHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_card_view, parent, false)
        return CommitItemHolder(view,this)
    }

    override fun onBindViewHolder(holder: CommitItemHolder, position: Int) {
        holder.bind(commits[position])
    }
    fun addListeners(text:TextView){
        username = FirebaseAuth.getInstance().currentUser!!.displayName!!
        commitRef.whereEqualTo("user",username).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException!=null){
                Log.e("MyTag",firebaseFirestoreException.message)
                return@addSnapshotListener
            }
            for (doc in querySnapshot!!.documentChanges){
                processChange(doc,text)
            }
        }
    }
    fun processChange(doc:DocumentChange,text: TextView){
        var commit = doc.document.toObject(Commitment::class.java)
        if (doc.type==DocumentChange.Type.ADDED){
            itemsRef.get().addOnSuccessListener {
                for (doc in it.documents){
                    if (doc.id == commit.itemID) {
                        var item = doc.toObject(Item::class.java)
                        item!!.id = doc.id
                        commits.add(item!!)
                        notifyItemInserted(commits.size - 1)
                        text.visibility = View.GONE
                    }
                }
            }
        }
        if (doc.type==DocumentChange.Type.REMOVED){
            for ((pos,item) in commits.withIndex()){
                if (item.id==commit.itemID){
                    commits.removeAt(pos)
                    notifyItemRemoved(pos)
                }
            }
        }
    }
    fun inspect(item:Item){
        (context as MainActivity).inspectCommitment(item)
    }
}