package com.example.authapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.authapp.R
import com.example.authapp.databinding.LayoutImageItemBinding
import com.example.authapp.model.Image_Item

class RVAdapter : RecyclerView.Adapter<MainViewHolder>() {

    private var mItemClickListener: ItemClickListener? = null

    interface ItemClickListener {
        fun onItemClick(position: Int, action: Int)
    }

    fun addItemClickListener(listener: ItemClickListener?) {
        mItemClickListener = listener
    }

    var imageItems = mutableListOf<Image_Item>()

    fun setImageList(results: List<Image_Item>) {
        this.imageItems = results.toMutableList()
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val binding = LayoutImageItemBinding.inflate(inflater, parent, false)
        return MainViewHolder(binding)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val result = imageItems[position]
        Glide.with(holder.itemView.context).load(result.download_url)
            .placeholder(R.drawable.placeholder)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.binding.ivIcon)
        if (result.selected) {
            holder.binding.ivSelection.setImageDrawable(
                holder.itemView.context.getResources().getDrawable(R.drawable.ic_circle_selected)
            )
        } else {
            holder.binding.ivSelection.setImageDrawable(
                holder.itemView.context.getResources().getDrawable(R.drawable.ic_circle)
            )
        }

        holder.binding.ivIcon.setOnClickListener {
            if (imageItems[position].selected) {
                mItemClickListener!!.onItemClick(position, 1)
            } else {
                mItemClickListener!!.onItemClick(position, 0)
            }
        }
    }

    override fun getItemCount(): Int {
        return imageItems.size
    }
}

class MainViewHolder(val binding: LayoutImageItemBinding) : RecyclerView.ViewHolder(binding.root) {}