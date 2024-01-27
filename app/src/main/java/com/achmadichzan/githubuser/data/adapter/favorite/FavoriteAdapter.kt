package com.achmadichzan.githubuser.data.adapter.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.achmadichzan.githubuser.data.local.entity.FavoriteUser
import com.achmadichzan.githubuser.databinding.ItemFavoriteBinding
import com.bumptech.glide.Glide

class FavoriteAdapter(
    private var favoriteList: List<FavoriteUser>,
    private val listener: OnFavoriteClickListener
) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    fun submitList(listFavorite: List<FavoriteUser>) {
        val diffCallback = FavoriteDiffCallback(favoriteList, listFavorite)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        favoriteList = listFavorite
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemFavoriteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val user = favoriteList[position]
        holder.bind(user, listener)
    }

    override fun getItemCount() = favoriteList.size

    inner class FavoriteViewHolder(private val binding: ItemFavoriteBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: FavoriteUser, listener: OnFavoriteClickListener) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(user.avatarUrl)
                    .circleCrop()
                    .into(ivFavorite)
                tvFavorite.text = user.username
                itemView.setOnClickListener {
                    listener.onUserClicked(user)
                }
            }
        }
    }

    interface OnFavoriteClickListener {
        fun onUserClicked(user: FavoriteUser)
    }
}