package rose.michaeap.nostresswishlist


import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),ItemSource {

    val RC_SIGN_IN = 1
    var items = ArrayList<Item>()
    val itemsRef = FirebaseFirestore.getInstance().collection("items")
    var itemDep : ItemAdapter?=null
    lateinit var authListener : FirebaseAuth.AuthStateListener
    var auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        addListener()
        showItemList()
        addAuthListener()
    }

    fun showItemList(){
        var ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container,YourItemFragment())
        ft.commit()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
    fun inputItem(){
        itemDep = null
        var ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container,Item_Add_Fragment(),"add item")
        ft.commit()
    }
    override fun getItemList():ArrayList<Item>{
        return items
    }

    override fun getItemListener(): View.OnClickListener {
        return View.OnClickListener {  }
    }
    override fun closeInspect(item:Item?,ogi:Item?){
        if (item!=null){
            if (ogi==null) {
                item.userID = auth.uid?:""
                itemsRef.add(item)
//                items.add(item)
//                itemsRef.whereEqualTo("name",item.name).get().addOnSuccessListener {snapshot: QuerySnapshot? ->
//                    item.id = snapshot!!.documents[0].id
//                }
            }
            else{
                itemsRef.document(ogi.id).set(item)
            }
        }
        showItemList()
    }

    override fun selectItem(item:Item){
        var c = getIndex(item)
        var ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container,Item_Add_Fragment.newInstance(item),"add item")
        ft.commit()
    }
    fun getIndex(item:Item):Int{
        for ((p,i) in items.withIndex())
            if (i==item)
                return p
        return -1
    }

    override fun removeItem(item: Item):Int {
        val n = getIndex(item)
        if (n<0)
            return -1
        items.removeAt(n)
        itemsRef.document(item.id).delete()
        return n
    }

    fun addListener(){
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
        item.id = change.document.id
        if (change.type == DocumentChange.Type.ADDED){
            items.add(item)
            Log.i("MyTag","*"+(itemDep!=null))
            if (itemDep!=null)
                (itemDep as ItemAdapter).notifyAdded()
        }
        if (change.type == DocumentChange.Type.MODIFIED){
            for ((n,it) in items.withIndex()){
                if (it.id==item.id){
                    items[n]=item
                    if (itemDep!=null)
                        (itemDep as ItemAdapter).itemChanged(n)
                    break
                }
            }
        }
        if (change.type == DocumentChange.Type.REMOVED){
            for ((n,it) in items.withIndex()){
                if (it.id == item.id){
                    items.removeAt(n)
                    if (itemDep!=null)
                        (itemDep as ItemAdapter).itemRemoved(n)
                    break
                }
            }
        }
    }

    override fun registerItemAdapter(adp: ItemAdapter) {
        itemDep = adp
    }
    fun addAuthListener(){
        authListener = FirebaseAuth.AuthStateListener {}
        var user = auth.currentUser
        if (user==null){
            startLogin()
        }


    }
    fun startLogin(){
        val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())
        val loginIntent = AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build()
        startActivityForResult(
            loginIntent,
            RC_SIGN_IN)
    }

}




    interface ItemSource{
        fun getItemList():ArrayList<Item>
        fun getItemListener():View.OnClickListener
        fun closeInspect(item:Item?,ogi:Item?)
        fun selectItem(item:Item)
        fun removeItem(item:Item):Int
        fun registerItemAdapter(adp:ItemAdapter)
    }







