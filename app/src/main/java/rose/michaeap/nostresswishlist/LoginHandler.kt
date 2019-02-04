package rose.michaeap.nostresswishlist

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.fragment_your_items.view.*
import kotlinx.android.synthetic.main.login_fragment.view.*

class LoginHandler : Fragment(){
    var RC_SIGN_IN = 1
    var source:MainActivity?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var frgmt = inflater.inflate(R.layout.login_fragment, container, false)
        frgmt.login_button.setOnClickListener {startLogin()}
        return frgmt
    }
    fun startLogin(){
        source = activity as MainActivity
        val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build(),AuthUI.IdpConfig.PhoneBuilder().build(),AuthUI.IdpConfig.EmailBuilder().build())
        val loginIntent = AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build()
        AuthUI.getInstance().createSignInIntentBuilder()
        startActivityForResult(
            loginIntent,
            RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        source!!.onLogin()
    }
}