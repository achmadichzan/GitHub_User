package com.achmadichzan.githubuser.data.adapter.favorite

import androidx.recyclerview.widget.DiffUtil
import com.achmadichzan.githubuser.data.local.entity.FavoriteUser

class FavoriteDiffCallback(
    private var oldUserList: List<FavoriteUser>,
    private val newUserList: List<FavoriteUser>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldUserList.size
    }

    override fun getNewListSize(): Int {
        return newUserList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldUserList[oldItemPosition].username == newUserList[newItemPosition].username
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldNote = oldUserList[oldItemPosition]
        val newNote = newUserList[newItemPosition]
        return oldNote.username == newNote.username && oldNote.avatarUrl == newNote.avatarUrl
    }
}