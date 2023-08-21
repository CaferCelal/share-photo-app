package com.example.firebasetrain

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_row.view.*

class FeedRecyclerAdapter(val postList:ArrayList<post>):RecyclerView.Adapter<FeedRecyclerAdapter.PostHolder>() {
    class PostHolder(itemView:View):RecyclerView.ViewHolder(itemView){

    }
    override fun getItemCount(): Int {
        return postList.size
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val inflater=LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recycler_row,parent,false)
        return PostHolder(view)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.itemView.displayUserEmail.setText(postList.get(position).email)
        holder.itemView.displayPostComment.setText(postList.get(position).postComment)
        Picasso.get().load(postList.get(position).postUri).into(holder.itemView.displayPostImage)
    }



}