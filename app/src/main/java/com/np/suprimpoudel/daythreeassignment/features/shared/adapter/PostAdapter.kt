package com.np.suprimpoudel.daythreeassignment.features.shared.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.np.suprimpoudel.daythreeassignment.R
import com.np.suprimpoudel.daythreeassignment.features.shared.model.PostDetails
import com.np.suprimpoudel.daythreeassignment.features.shared.view_holder.PostViewHolder

class PostAdapter(private val post: ArrayList<PostDetails>) :
    RecyclerView.Adapter<PostViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_post_tile, parent, false)
        return PostViewHolder(v)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(post[position])
    }

    override fun getItemCount(): Int = post.size

    @SuppressLint("NotifyDataSetChanged")
    fun addMessage(postDetails: PostDetails) {
        post.add(postDetails)
        notifyDataSetChanged()
    }
}