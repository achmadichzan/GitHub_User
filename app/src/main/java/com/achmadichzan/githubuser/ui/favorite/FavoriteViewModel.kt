package com.achmadichzan.githubuser.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.achmadichzan.githubuser.data.local.entity.FavoriteUser
import com.achmadichzan.githubuser.repository.FavoriteRepository

class FavoriteViewModel(private val favoriteRepository: FavoriteRepository) : ViewModel() {
    fun getFavoriteUsers(): LiveData<List<FavoriteUser>> = favoriteRepository.getAllFavorite()
}
