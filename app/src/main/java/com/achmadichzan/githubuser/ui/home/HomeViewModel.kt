package com.achmadichzan.githubuser.ui.home

import android.util.Log
import androidx.lifecycle.*
import com.achmadichzan.githubuser.data.remote.ApiConfig
import com.achmadichzan.githubuser.model.response.GithubResponse
import com.achmadichzan.githubuser.model.response.Items
import com.achmadichzan.githubuser.utils.SettingPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(private val pref: SettingPreferences): ViewModel() {
    private val _listUser = MutableLiveData<ArrayList<Items>?>()
    val listUser: LiveData<ArrayList<Items>?> get() = _listUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isDarkModeActive = MutableLiveData<Boolean>()
    val isDarkModeActive: LiveData<Boolean> get() = _isDarkModeActive

    init {
        viewModelScope.launch {
            _isDarkModeActive.value = pref.getThemeSetting().first()
        }
        fetchData("achmad")
    }

    fun fetchData(query: String) {
        searchUsers(query)
    }

    private fun searchUsers(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val client = ApiConfig.getApiService().searchUser(query)
            client.enqueue(object : Callback<GithubResponse> {
                override fun onResponse(
                    call: Call<GithubResponse>,
                    response: Response<GithubResponse>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        _listUser.value = response.body()?.items
                    } else {
                        Log.e(TAG, "onFailure: ${response.message()}")
                    }
                }
                override fun onFailure(call: Call<GithubResponse>, t: Throwable) {
                    _isLoading.value = false
                    Log.e(TAG, "Failed to get users: ${t.message}")
                }
            })
        }
    }

    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
            _isDarkModeActive.value = (isDarkModeActive)
        }
    }

    companion object {
        private val TAG = HomeViewModel::class.java.simpleName
    }
}