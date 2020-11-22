package com.srm325.navsafe.ui.features.feed

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.provider.Settings.Global.getString
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.srm325.navsafe.MainActivity
import com.srm325.navsafe.R
import com.srm325.navsafe.core.makeItGone
import com.srm325.navsafe.core.makeItVisible
import com.srm325.navsafe.data.model.Post
import timber.log.Timber

class PostAdapter(var postList: List<Post>, val callback: FeedFragmentCallback) : RecyclerView.Adapter<PostViewHolder>() {
    var currentAdminArea : String = "Los Angeles County"
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
        val tsLong = System.currentTimeMillis() / 1000
        val ts = tsLong.toInt()
        val posttime = postList[position].id.toInt()
        val ts1 = ts-posttime
        val postadminarea = getCityFromAddress(context, postList[position].address)
        Timber.e("post$postadminarea")
        Timber.e("current$currentAdminArea")
        val i = 1
        if (ts1 < 7200 ) {
            if (currentAdminArea == postadminarea){
            val toast = Toast.makeText(context, "Crime happened recently near you", Toast.LENGTH_LONG)
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show()
                val builder = NotificationCompat.Builder(context)
                    .setContentTitle("NavSafe crime alert!")
                    .setContentText("Please open app to check details")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                val notificationManager = NotificationManagerCompat.from(context)
                notificationManager.notify(i, builder.build())
            }
        }
        if(postList[position].image.isEmpty())
            holder.postImage.makeItGone()
        else
            holder.postImage.makeItVisible()

    }
    private fun getCityFromAddress(context: Context?, strAddress: String?): String? {
        val coder = Geocoder(context)
        val address: List<Address>?
        var p1: String = ""
        try {
            address = coder.getFromLocationName(strAddress, 5)
            if (address == null) {
                Timber.e("Address not found")
            } else {
                val location: Address = address[0]
                p1 = location.subAdminArea
                Timber.e(p1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return p1
    }
    override fun getItemCount() = postList.size

    fun getCityFromLatLng(context: Context?, latlong: LatLng?): String? {
        val coder = Geocoder(context)
        val address: List<Address>?
        var p1: String = ""
        try {
            if (latlong != null) {
                address = coder.getFromLocation(latlong.latitude, latlong.longitude, 1)
                if (address == null) {
                    Timber.e("Address not found")
                } else {
                    val location: Address = address[0]
                    p1 = location.subAdminArea
                    Timber.e(p1)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return p1
    }

}