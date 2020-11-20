package com.srm325.navsafe.ui.features.feed

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.srm325.navsafe.R
import com.srm325.navsafe.core.makeItGone
import com.srm325.navsafe.core.makeItVisible
import com.srm325.navsafe.data.model.Post
import com.bumptech.glide.Glide
import timber.log.Timber

class PostAdapter(var postList: List<Post>, val callback: FeedFragmentCallback) : RecyclerView.Adapter<PostViewHolder>() {

    private lateinit var context : Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {

        context = parent.context
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.single_post_item, parent, false)
        return PostViewHolder(view)

    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {

        Timber.d(postList[position].image)

        Glide
            .with(context)
            .load(postList[position].image)
            .dontAnimate()
            .placeholder(R.drawable.ic_loading)
            .into(holder.postImage)

        Glide
            .with(context)
            .load(postList[position].user.image)
            .dontAnimate()
            .placeholder(R.drawable.ic_loading)
            .into(holder.profileImage)

        holder.userName.text = postList[position].user.userName
        holder.description.text = postList[position].description
        holder.address.text = postList[position].address
        

        if(postList[position].image.isEmpty())
            holder.postImage.makeItGone()
        else
            holder.postImage.makeItVisible()

    }

    override fun getItemCount() = postList.size
}