package com.achmadichzan.githubuser.utils

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.achmadichzan.githubuser.repository.FavoriteRepository
import com.achmadichzan.githubuser.ui.detail.DetailViewModel
import com.achmadichzan.githubuser.ui.favorite.FavoriteViewModel
import com.achmadichzan.githubuser.ui.home.HomeViewModel

class ViewModelFactory private constructor(
    private val application: Application,
    private val pref: SettingPreferences
) : ViewModelProvider.AndroidViewModelFactory(application) {

    private val favoriteRepository: FavoriteRepository by lazy {
        FavoriteRepository(application)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            HomeViewModel::class.java -> HomeViewModel(pref) as T
            DetailViewModel::class.java -> DetailViewModel(favoriteRepository) as T
            FavoriteViewModel::class.java -> FavoriteViewModel(favoriteRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(application: Application, preferences: SettingPreferences): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelFactory(application, preferences).also { INSTANCE = it }
            }
        }
    }
}
