package com.achmadichzan.githubuser.ui.detail

import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.achmadichzan.githubuser.R
import com.achmadichzan.githubuser.data.local.entity.FavoriteUser
import com.achmadichzan.githubuser.databinding.FragmentDetailBinding
import com.achmadichzan.githubuser.model.response.DetailUserResponse
import com.achmadichzan.githubuser.model.response.Items
import com.achmadichzan.githubuser.utils.SectionsPagerAdapter
import com.achmadichzan.githubuser.utils.SettingPreferences
import com.achmadichzan.githubuser.utils.ViewModelFactory
import com.achmadichzan.githubuser.utils.dataStore
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator

class DetailFragment : Fragment() {

    private lateinit var preferences: SettingPreferences
    private val detailViewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(requireActivity().application, preferences)
    }
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = SettingPreferences.getInstance(requireActivity().application.dataStore)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (val user = arguments?.parcelable<Parcelable>(USER_LOGIN)) {
            is Items -> {
                user.login?.let { username ->
                    detailViewModel.getUserDetail(username)
                    observeDetailUser()
                    setupViewPager(username)
                    setupFavoriteButton(username)
                    observeFavoriteStatus(username)
                }
            }
            is FavoriteUser -> {
                user.username.let { username ->
                    detailViewModel.getUserDetail(username)
                    observeDetailUser()
                    setupViewPager(username)
                    setupFavoriteButton(username)
                    observeFavoriteStatus(username)
                }
            }
            else -> { requireActivity().finish() }
        }

        Log.d(TAG, "onViewCreated: $arguments")

        detailViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }
    }

    private fun observeDetailUser() {
        detailViewModel.detailUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                setupData(user)
                checkFavoriteStatus(user.login)
            }
        }
    }

    private fun setupFavoriteButton(username: String) {
        binding.detail.fab.setOnClickListener {
            detailViewModel.detailUser.value?.let { user ->
                user.login?.let { userLogin ->
                    val favoriteUser = FavoriteUser(userLogin, user.avatarUrl)
                    val isCurrentlyFavorite = isFavorite

                    if (isCurrentlyFavorite) {
                        removeFromFavoritesAndShowToast(favoriteUser, "$username dihapus dari favorit")
                    } else {
                        addToFavoritesAndShowToast(username, user, favoriteUser)
                    }
                }
            }
        }
    }

    private fun removeFromFavoritesAndShowToast(favoriteUser: FavoriteUser, message: String) {
        detailViewModel.removeUserFromFavorites(favoriteUser)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        updateFavoriteIcon(false)
    }

    private fun addToFavoritesAndShowToast(username: String, user: DetailUserResponse, favoriteUser: FavoriteUser) {
        detailViewModel.isUserFavorite(username).observeOnce(viewLifecycleOwner) { isUserFavorite ->
            isFavorite = if (isUserFavorite) {
                removeFromFavoritesAndShowToast(favoriteUser, "$username dihapus dari favorit")
                false
            } else {
                detailViewModel.addUserToFavorites(user)
                Toast.makeText(requireContext(), "$username ditambahkan ke favorit", Toast.LENGTH_SHORT).show()
                true
            }
            updateFavoriteIcon(isFavorite)
        }
    }

    private fun observeFavoriteStatus(username: String) {
        detailViewModel.isUserFavorite(username).observe(viewLifecycleOwner) { isUserFavorite ->
            isFavorite = isUserFavorite
            updateFavoriteIcon(isFavorite)
        }
    }

    private fun setupData (users: DetailUserResponse){
        with(users) {
            binding.apply {
                Glide.with(this@DetailFragment)
                    .load(users.avatarUrl)
                    .circleCrop()
                    .into(detail.ivUser)
                detail.tvLogin.text = "@$login"
                detail.tvUsername.text = name
                detail.tvFollower.text = getString(R.string.followers, followers)
                detail.tvFollowing.text = getString(R.string.following, following)
            }
        }
    }

    private fun setupViewPager(username: String) {
        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        sectionsPagerAdapter.username = username
        binding.detail.viewPager.adapter = sectionsPagerAdapter
        TabLayoutMediator(binding.detail.tabLayout, binding.detail.viewPager) { tab, position ->
            tab.text = getString(TAB_TITLES[position])
        }.attach()
    }

    private fun checkFavoriteStatus(username: String?) {
        username?.let {
            detailViewModel.isUserFavorite(it).observe(viewLifecycleOwner) { isFavorite ->
                updateFavoriteIcon(isFavorite)
            }
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        val drawableResId = if (isFavorite) {
            R.drawable.baseline_favorite_24
        } else {
            R.drawable.baseline_favorite_border_24
        }

        val icon = ContextCompat.getDrawable(requireContext(), drawableResId)
        binding.detail.fab.setImageDrawable(icon)
    }

    private fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: Observer<T>) {
        observe(owner, object : Observer<T> {
            override fun onChanged(value: T) {
                observer.onChanged(value)
                removeObserver(this)
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        binding.detail.pbDetailUser.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
        SDK_INT >= 33 -> getParcelable(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelable(key) as? T
    }

    companion object {
        const val USER_LOGIN = "user"
        const val UNIQUE_ARG = "unique_arg"
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_follower,
            R.string.tab_following,
        )
        private val TAG = DetailFragment::class.java.simpleName
    }
}