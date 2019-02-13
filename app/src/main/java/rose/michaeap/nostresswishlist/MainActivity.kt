package rose.michaeap.nostresswishlist


import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_user_name.view.*

class MainActivity : AppCompatActivity() {

    val RC_SIGN_IN = 1
    val itemsRef = FirebaseFirestore.getInstance().collection("items")
    val personRef = FirebaseFirestore.getInstance().collection("people")
    var itemDep : ItemAdapter?=null
    lateinit var authListener : FirebaseAuth.AuthStateListener
    var auth = FirebaseAuth.getInstance()
    var userID:String=""
    var userName:String=""
    var commitRef = FirebaseFirestore.getInstance().collection("commitment")
    val userRef = FirebaseFirestore.getInstance().collection("users")

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
        ft.replace(R.id.fragment_container,YourItemFragment.newInstance(userName),"list")
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
        //while (supportFragmentManager.backStackEntryCount>0)
        //    supportFragmentManager.popBackStack()
        return when (item.itemId) {
            R.id.action_logout ->{logout();true}
            R.id.action_yourList->{showItemList();true}
            R.id.action_groups->{openGroupFragment();true}
            R.id.action_commitments->{openCommitmentFragment(userName);true}
            R.id.action_reset->{holidayResetMessage();true}
            else -> super.onOptionsItemSelected(item)
        }
    }
    fun logout(){
        auth.signOut()
        userName=""
        userID=""
        switchToLoginScreen()
    }

    fun inputItem(){
        var ft = supportFragmentManager.beginTransaction()
        ft.addToBackStack("list")
        ft.replace(R.id.fragment_container,Item_Add_Fragment.newInstance(null),"add item")
        ft.commit()
    }

    fun closeInspect(item:Item?,ogi:Item?){
        if (item!=null){
            item.ownerName = auth.currentUser!!.displayName?:""
            if (ogi==null) {
                itemsRef.add(item)
            }
            else{
                itemsRef.document(ogi.id).set(item)
            }
        }
        showItemList()
    }

    fun selectItem(item:Item){
        var ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container,Item_Add_Fragment.newInstance(item),"add item")
        ft.commit()
    }

    fun registerItemAdapter(adp: ItemAdapter) {
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
        var email = auth.currentUser?.email?:""
        if (userName==null || userName=="") {
            var builder = AlertDialog.Builder(this)
            var view = LayoutInflater.from(this).inflate(R.layout.add_user_name, null, false)
            builder.setView(view)
            builder.setTitle("Set name")
            if (email!="")
                view.email_to_add.visibility = View.GONE
            builder.setPositiveButton(android.R.string.ok) { _, _ ->
                var name = view.name_to_add.text.toString()
                var change = UserProfileChangeRequest.Builder().setDisplayName(name)
                auth.currentUser!!.updateProfile(change.build())
                //checkName()
                if (email==null || email=="")
                    email = view.email_to_add.text.toString()
                checkUser(name,email)
                userName = name
                showItemList()
                return@setPositiveButton
            }
            builder.create().show()
        }
        else {
            checkUser(userName,email)
            showItemList()
        }
    }
    fun checkUser(name:String,email:String){
        userRef.whereEqualTo("name",name).get().addOnSuccessListener{
            if (it.isEmpty)
                userRef.add(User(name,email))
        }
    }
    fun openGroupFragment(){
        toolbar.title = "Your groups"
        var fr = supportFragmentManager.beginTransaction()
        fr.replace(R.id.fragment_container,GroupPageHandler.newInstance(userName),"group")
        fr.commit()
    }
    fun showFriendList(friendName:String){
        toolbar.title = "$friendName's Wish List"
        var fr = supportFragmentManager.beginTransaction()
        fr.addToBackStack("group")
        fr.replace(R.id.fragment_container,FriendListHandler.newInstance(userName,friendName),"friend")
        fr.commit()
    }
    fun inspectFriendItem(item:Item,userName:String,friendName:String){
        toolbar.title = "No Stress Wish List"
        var fr = supportFragmentManager.beginTransaction()
        fr.addToBackStack("friend")
        fr.replace(R.id.fragment_container,FriendItemFragment.newInstance(item,userName,friendName,false))
        fr.commit()
    }
    fun commitToGift(name:String,id:String){
        val commitment = Commitment(name,id)
        commitRef.add(commitment)
        Toast.makeText(this,"You have committed to a gift",Toast.LENGTH_SHORT).show()
        openGroupFragment()
    }
    fun openCommitmentFragment(name:String){
        toolbar.title = "Your commits"
        var fr = supportFragmentManager.beginTransaction()
        fr.replace(R.id.fragment_container,CommitmentViewer.newInstance(name),"commits")
        fr.commit()
    }
    fun inspectCommitment(item:Item){
        toolbar.title = "No Stress Wish List"
        var fr = supportFragmentManager.beginTransaction()
        fr.addToBackStack("commits")
        fr.replace(R.id.fragment_container,FriendItemFragment.newInstance(item,userName,item.ownerName,true))
        fr.commit()
    }
    fun uncommitToGift(userName:String,itemID:String){
        Log.d("MyTag","$userName,$itemID")
        commitRef.whereEqualTo("user",userName).whereEqualTo("itemID",itemID).get().addOnSuccessListener {
            if (it.isEmpty){
                Toast.makeText(this,"No commitment found",Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }
            for (doc in it.documents)
                commitRef.document(doc.id).delete()
            Toast.makeText(this,"Commitment deleted",Toast.LENGTH_SHORT)
        }
        supportFragmentManager.popBackStack()
        openCommitmentFragment(userName)
    }
    fun holidayResetMessage(){
        var builder = AlertDialog.Builder(this)
        var view = LayoutInflater.from(this).inflate(R.layout.holiday_reset,null,false)
        builder.setTitle("Clear list?")
        builder.setView(view)
        builder.setNegativeButton(android.R.string.no,null)
        builder.setPositiveButton(android.R.string.ok,{_,_->
            holidayReset()
        })
    }
    fun holidayReset(){
        var ids = HashSet<String>()
        itemsRef.whereEqualTo("ownerName",userName).get().addOnSuccessListener {
            for (doc in it.documents){
                ids.add(doc.toObject(Item::class.java)!!.id)
            }
            commitRef.get().addOnSuccessListener {
                for (doc in it.documents){
                    var commit = doc.toObject(Commitment::class.java)
                    if (ids.contains(commit!!.itemID))
                        commitRef.document(doc.id).delete()
                }
            }
            for (id in ids)
                itemsRef.document(id).delete()
        }
    }
}







