package com.achmadichzan.githubuser.data.remote

import com.achmadichzan.githubuser.model.response.DetailUserResponse
import com.achmadichzan.githubuser.model.response.GithubResponse
import com.achmadichzan.githubuser.model.response.Items
import retrofit2.http.GET
import retrofit2.Call
import retrofit2.http.*

interface ApiService{
    @GET("search/users")
    fun searchUser(@Query("q") query: String): Call<GithubResponse>

    @GET("users/{username}")
    fun getUserDetail(@Path("username") username: String): Call<DetailUserResponse>

    @GET("users/{username}/followers")
    fun getFollowers(@Path("username") username: String): Call<ArrayList<Items>>

    @GET("users/{username}/following")
    fun getFollowing(@Path("username") username: String): Call<ArrayList<Items>>
}
