package com.srm325.navsafe.ui.features.feed

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.srm325.navsafe.R
import com.srm325.navsafe.data.model.Post
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.feed_fragment.*

class FeedFragment : Fragment(), FeedFragmentCallback {

    companion object {
        const val TAG = "FeedFragment"
        fun newInstance() = FeedFragment()
    }

    private lateinit var viewModel: FeedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.feed_fragment, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FeedViewModel::class.java)
        val user = Firebase.auth.currentUser

        val postList:MutableList<Post> = mutableListOf()
        val db = Firebase.firestore
        db.collection("posts")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val post = document.toObject(Post::class.java)
                    postList.add(post)
                    postList.reverse()
                }
                val adapter = PostAdapter(postList, this)
                feed_recyclerview.adapter = adapter
            }

    }

    override fun checkCurrentUser(email: String) = viewModel.checkCurrentUser(email)

    override fun getCurrentUserEmail() = viewModel.getCurrentUser()?.email



}