package com.srm325.navsafe.ui.features.menu

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.srm325.navsafe.R
import com.srm325.navsafe.ui.features.login.LoginActivity
import kotlinx.android.synthetic.main.menu_fragment.*


class MenuFragment : Fragment() {
    lateinit var profileimage: ImageView
    lateinit var username: TextView
    companion object {
        fun newInstance() = MenuFragment()
    }

    private lateinit var viewModel: MenuViewModel
    private lateinit var googleSignInClient : GoogleSignInClient

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.menu_fragment, container, false)
        username = view.findViewById(R.id.user_name)
        profileimage = view.findViewById(R.id.profile_image)
        return view
    }

    fun checkCurrentUser(email: String) = viewModel.checkCurrentUser(email)
    fun getCurrentUserEmail() = viewModel.getCurrentUser()?.email

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MenuViewModel::class.java)
        username.text = viewModel.getCurrentUser().userName
        val imgurl: String = viewModel.getCurrentUser().image
        Glide.with(this).load(imgurl).into(profileimage)


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        log_out_button.setOnClickListener {
            Firebase.auth.signOut()
            googleSignInClient.signOut().addOnCompleteListener(requireActivity()) {
                startActivity(Intent(requireActivity(), LoginActivity::class.java))
                requireActivity().finish()
            }

        }
    }

}