package com.srm325.navsafe.ui.features.feed

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.srm325.navsafe.R

class PostViewHolder(item : View) : RecyclerView.ViewHolder(item){
    val postContainer : CardView = item.findViewById(R.id.post_container)
    var postImage : ImageView = item.findViewById(R.id.post_image)
    var profileImage : ImageView = item.findViewById(R.id.profile_image)
    var userName : TextView = item.findViewById(R.id.user_name)
    var description : TextView = item.findViewById(R.id.description)
    var address : TextView = item.findViewById(R.id.address)
}