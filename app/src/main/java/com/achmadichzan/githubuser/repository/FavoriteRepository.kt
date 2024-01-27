package com.achmadichzan.githubuser.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.achmadichzan.githubuser.data.local.entity.FavoriteUser
import com.achmadichzan.githubuser.data.local.room.FavoriteDatabase
import com.achmadichzan.githubuser.data.local.room.FavoriteUserDao
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FavoriteRepository(application: Application) {
    private val mFavoriteDao: FavoriteUserDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = FavoriteDatabase.getDatabase(application)
        mFavoriteDao = db.favoriteUserDao()
    }

    fun getAllFavorite(): LiveData<List<FavoriteUser>> = mFavoriteDao.getFavoriteUsers()

    fun getFavoriteUserByUsername(username: String): LiveData<FavoriteUser> = mFavoriteDao.getFavoriteUserByUsername(username)

    fun insert(user: FavoriteUser) {
        executorService.execute { mFavoriteDao.insert(user) }
    }

    fun delete(user: FavoriteUser) {
        executorService.execute { mFavoriteDao.delete(user) }
    }
}
