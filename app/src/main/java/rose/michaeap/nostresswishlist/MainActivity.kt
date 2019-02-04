package rose.michaeap.nostresswishlist


import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.DocumentChange

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_user_name.view.*

class MainActivity : AppCompatActivity(),ItemSource {

    val RC_SIGN_IN = 1
    var items = ArrayList<Item>()
    val itemsRef = FirebaseFirestore.getInstance().collection("items")
    val personRef = FirebaseFirestore.getInstance().collection("people")
    var itemDep : ItemAdapter?=null
    lateinit var authListener : FirebaseAuth.AuthStateListener
    var auth = FirebaseAuth.getInstance()
    var userID:String=""
    var userName:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        //addListener()
        //showItemList()
        addAuthListener()
    }

    fun showItemList(){
        toolbar.menu.setGroupVisible(R.id.login_dependent,true)
        //toolbar.title = "$userName's Wish List"
        var ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container,YourItemFragment.newInstance(userName))
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
            R.id.action_logout ->{logout();true}
            R.id.action_yourList->{showItemList();true}
            R.id.action_groups->{openGroupFragment();true}
            else -> super.onOptionsItemSelected(item)
        }
    }
    fun logout(){
        auth.signOut()
        userName=""
        userID=""
        items = ArrayList<Item>()
        switchToLoginScreen()
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
                item.ownerName = auth.currentUser!!.displayName?:""
                itemsRef.add(item)
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

    override fun registerItemAdapter(adp: ItemAdapter) {
        itemDep = adp
    }
    fun addAuthListener() {
        authListener = FirebaseAuth.AuthStateListener {}
        var user = auth.currentUser
        if (user == null) {
            //startLogin()
            switchToLoginScreen()
        }
        else{
            checkName()
        }
    }

    fun switchToLoginScreen(){
        toolbar.title = "No Stress Wish List"
        toolbar.menu.setGroupVisible(R.id.login_dependent,false)
        var fr = supportFragmentManager.beginTransaction()
        fr.replace(R.id.fragment_container,LoginHandler())
        fr.commit()
    }
    fun onLogin(){
        addAuthListener()
    }
    fun checkName(){
        userName = auth.currentUser?.displayName?:""
        userID = auth.currentUser?.uid?:""
        if (userName==null || userName=="") {
            var builder = AlertDialog.Builder(this)
            var view = LayoutInflater.from(this).inflate(R.layout.add_user_name, null, false)
            builder.setView(view)
            builder.setTitle("Set name")
            builder.setPositiveButton(android.R.string.ok) { _, _ ->
                var name = view.name_to_add.text.toString()
                var change = UserProfileChangeRequest.Builder().setDisplayName(name).build()
                auth.currentUser!!.updateProfile(change)
                //checkName()
                showItemList()
                return@setPositiveButton
            }
            builder.create().show()
        }
        else {
            showItemList()
        }
    }
    fun openGroupFragment(){
        var fr = supportFragmentManager.beginTransaction()
        fr.replace(R.id.fragment_container,GroupPageHandler.newInstance(userName),"group")
        fr.commit()
    }
    fun showFriendList(friendName:String){
        var fr = supportFragmentManager.beginTransaction()
        fr.addToBackStack("group")
        fr.replace(R.id.fragment_container,FriendListHandler.newInstance(userName,friendName),"friend")
        fr.commit()
    }
    fun inspectFriendItem(item:Item,userName:String,friendName:String){
        var fr = supportFragmentManager.beginTransaction()
        fr.addToBackStack("friend")
        fr.replace(R.id.fragment_container,FriendItemFragment.newInstance(item,userName,friendName))
        fr.commit()
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







