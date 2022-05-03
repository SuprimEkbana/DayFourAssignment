package com.np.suprimpoudel.daythreeassignment.features.shared.view_holder

import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.np.suprimpoudel.daythreeassignment.R
import com.np.suprimpoudel.daythreeassignment.features.shared.model.PostDetails

class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(postDetails: PostDetails) {
        name.text = postDetails.postBy
        post.text = postDetails.postContent
        dateTime.text = postDetails.time

        Glide.with(itemView)
            .load(postDetails.postUserPhoto)
            .error(R.drawable.ic_baseline_profile_24)
            .placeholder(R.drawable.ic_baseline_profile_24)
            .into(photo)
    }

    private val photo: ImageView = itemView.findViewById(R.id.imgPostUserVariable)
    private val name: TextView = itemView.findViewById(R.id.txtPostUserName)
    private val post: TextView = itemView.findViewById(R.id.txtPostContent)
    private val dateTime: TextView = itemView.findViewById(R.id.txtDateTime)
}