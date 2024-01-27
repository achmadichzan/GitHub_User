package com.achmadichzan.githubuser.ui.detail

import android.util.Log
import androidx.lifecycle.*
import com.achmadichzan.githubuser.data.local.entity.FavoriteUser
import com.achmadichzan.githubuser.data.remote.ApiConfig
import com.achmadichzan.githubuser.model.response.DetailUserResponse
import com.achmadichzan.githubuser.model.response.Items
import com.achmadichzan.githubuser.repository.FavoriteRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel(private val favoriteRepository: FavoriteRepository): ViewModel() {
    private val _detailUser = MutableLiveData<DetailUserResponse>()
    val detailUser: MutableLiveData<DetailUserResponse> get() = _detailUser

    private val _listFollowers = MutableLiveData<ArrayList<Items>>()
    val followers: LiveData<ArrayList<Items>> get() = _listFollowers

    private val _listFollowing = MutableLiveData<ArrayList<Items>>()
    val following: LiveData<ArrayList<Items>> get() = _listFollowing

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> get() = _isFavorite

    fun getUserDetail(user: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val client = ApiConfig.getApiService()
            client.getUserDetail(user).enqueue(object : Callback<DetailUserResponse> {
                override fun onResponse(
                    call: Call<DetailUserResponse>,
                    response: Response<DetailUserResponse>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        _detailUser.value = response.body()
                        Log.d(TAG, "onResponse: getUserDetail succeed")
                    }
                }

                override fun onFailure(call: Call<DetailUserResponse>, t: Throwable) {
                    _isLoading.value = false
                    Log.e(TAG, "Failed to get user details: ${t.message}")
                }
            })
        }
    }

    fun getFollowers(username: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val client = ApiConfig.getApiService()
            client.getFollowers(username).enqueue(object : Callback<ArrayList<Items>> {
                override fun onResponse(
                    call: Call<ArrayList<Items>>,
                    response: Response<ArrayList<Items>>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        _listFollowers.value = response.body()
                        Log.d(TAG, "onResponse: getFollowers succeed")
                    }
                }

                override fun onFailure(call: Call<ArrayList<Items>>, t: Throwable) {
                    _isLoading.value = false
                    Log.e(TAG, "Failed to get followers: ${t.message}")
                }
            })
        }
    }

    fun getFollowing(username: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val client = ApiConfig.getApiService()
            client.getFollowing(username).enqueue(object : Callback<ArrayList<Items>> {
                override fun onResponse(
                    call: Call<ArrayList<Items>>,
                    response: Response<ArrayList<Items>>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        _listFollowing.value = response.body()
                        Log.d(TAG, "onResponse: getFollowing succeed")
                    }
                }

                override fun onFailure(call: Call<ArrayList<Items>>, t: Throwable) {
                    _isLoading.value = false
                    Log.e(TAG, "Failed to get following: ${t.message}")
                }
            })
        }
    }

    fun addUserToFavorites(user: DetailUserResponse) {
        val favoriteUser = user.login?.let {
            FavoriteUser(
                username = it,
                avatarUrl = user.avatarUrl
            )
        }
        favoriteUser?.let {
            favoriteRepository.insert(it)
            _isFavorite.value = true
        }
    }

    fun removeUserFromFavorites(user: FavoriteUser) {
        favoriteRepository.delete(user)
        _isFavorite.value = false
    }

    fun isUserFavorite(username: String): LiveData<Boolean> {
        val favoriteUserLiveData = favoriteRepository.getFavoriteUserByUsername(username)
        val isFavoriteLiveData = MediatorLiveData<Boolean>()

        isFavoriteLiveData.addSource(favoriteUserLiveData) { favoriteUser ->
            isFavoriteLiveData.value = favoriteUser != null
        }
        return isFavoriteLiveData
    }

    companion object {
        private val TAG = DetailViewModel::class.java.simpleName
    }
}