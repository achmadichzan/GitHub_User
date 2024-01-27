package com.achmadichzan.githubuser.data.adapter.user

import androidx.recyclerview.widget.DiffUtil
import com.achmadichzan.githubuser.model.response.Items

class UserDiffCallback(private var oldUserList: List<Items>, private val newUserList: List<Items>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldUserList.size
    }

    override fun getNewListSize(): Int {
        return newUserList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldUserList[oldItemPosition].id == newUserList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldNote = oldUserList[oldItemPosition]
        val newNote = newUserList[newItemPosition]
        return oldNote.id == newNote.id && oldNote.login == newNote.login
    }
}