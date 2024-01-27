package com.achmadichzan.githubuser.data.adapter.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.achmadichzan.githubuser.databinding.ItemUserBinding
import com.achmadichzan.githubuser.model.response.Items
import com.bumptech.glide.Glide

class UserAdapter(
    private var userList: ArrayList<Items>,
    private val listener: OnUserClickListener
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    fun setListUser(listUser: ArrayList<Items>) {
        val diffCallback = UserDiffCallback(userList, listUser)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        userList.clear()
        userList.addAll(listUser)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user, listener)
    }

    override fun getItemCount() = userList.size

    inner class UserViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: Items, listener: OnUserClickListener) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(user.avatarUrl)
                    .circleCrop()
                    .into(ivUser)
                tvUser.text = user.login
                itemView.setOnClickListener {
                    listener.onUserClicked(user)
                }
            }
        }
    }

    interface OnUserClickListener {
        fun onUserClicked(user: Items)
    }
}