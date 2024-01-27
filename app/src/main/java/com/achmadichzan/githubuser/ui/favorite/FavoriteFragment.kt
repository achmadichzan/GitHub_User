package com.achmadichzan.githubuser.ui.favorite

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.achmadichzan.githubuser.R
import com.achmadichzan.githubuser.data.adapter.favorite.FavoriteAdapter
import com.achmadichzan.githubuser.data.local.entity.FavoriteUser
import com.achmadichzan.githubuser.databinding.FragmentFavoriteBinding
import com.achmadichzan.githubuser.ui.detail.DetailFragment
import com.achmadichzan.githubuser.utils.SettingPreferences
import com.achmadichzan.githubuser.utils.ViewModelFactory
import com.achmadichzan.githubuser.utils.dataStore

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferences: SettingPreferences
    private val favoriteViewModel by viewModels<FavoriteViewModel> {
        ViewModelFactory.getInstance(requireActivity().application, preferences)
    }
    private var favoriteAdapter: FavoriteAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = SettingPreferences.getInstance(requireActivity().application.dataStore)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeFavoriteUsers()

    }

    private fun setupRecyclerView() {
        favoriteAdapter = FavoriteAdapter(emptyList(), onFavoriteClickListener)
        binding.rvFavorite.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = favoriteAdapter
        }
    }

    private fun observeFavoriteUsers() {
        favoriteViewModel.getFavoriteUsers().observe(viewLifecycleOwner) { favoriteList ->
            favoriteAdapter?.submitList(favoriteList)
        }
    }

    private val onFavoriteClickListener = object : FavoriteAdapter.OnFavoriteClickListener {
        override fun onUserClicked(user: FavoriteUser) {
            navigateToDetailFragment(user)
        }
    }

    private fun navigateToDetailFragment(user: FavoriteUser) {
        val detailFragment = DetailFragment()
        val bundle = Bundle()
        bundle.putParcelable(DetailFragment.USER_LOGIN, user)
        detailFragment.arguments = bundle
        Log.d(TAG, "onViewCreated: $arguments")

        parentFragmentManager.commit {
            replace(
                R.id.favorite_fragment,
                detailFragment,
                DetailFragment::class.java.simpleName
            )
            addToBackStack(null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private val TAG = FavoriteFragment::class.java.simpleName
    }
}
