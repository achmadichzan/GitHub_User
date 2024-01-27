package com.achmadichzan.githubuser.ui.home

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.achmadichzan.githubuser.R
import com.achmadichzan.githubuser.data.adapter.user.UserAdapter
import com.achmadichzan.githubuser.databinding.FragmentHomeBinding
import com.achmadichzan.githubuser.model.response.Items
import com.achmadichzan.githubuser.ui.detail.DetailFragment
import com.achmadichzan.githubuser.utils.SettingPreferences
import com.achmadichzan.githubuser.utils.ViewModelFactory
import com.achmadichzan.githubuser.utils.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferences: SettingPreferences
    private val homeViewModel by viewModels<HomeViewModel>{
        ViewModelFactory.getInstance(requireActivity().application, preferences)
    }
    private var adapter: UserAdapter? = null
    private lateinit var swipeRefresh: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = SettingPreferences.getInstance(requireActivity().application.dataStore)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            topBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.search -> {
                        handleSearchMenuItemClick()
                        true
                    }
                    else -> { false}
                }
            }
        }

        swipeRefresh = binding.swipeRefresh

        swipeRefresh.setOnRefreshListener {
            refreshData()

            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED){
                    delay(1000)
                    swipeRefresh.isRefreshing = false
                }
            }
        }

        binding.rvUsers.layoutManager = LinearLayoutManager(requireContext())

        homeViewModel.listUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                onUserSelected(it)
            }
        }

        homeViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }
    }

    private var searchJob: Job? = null
    private fun handleSearchMenuItemClick() {
        val searchManager = context?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = binding.topBar.menu.findItem(R.id.search)?.actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
        searchView.queryHint = resources.getString(R.string.search_hint)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchJob?.cancel()
                lifecycleScope.launch(Dispatchers.Unconfined) {
                    refreshData(query)
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch(Dispatchers.Unconfined) {
                    delay(1000)
                    refreshData(newText)
                }
                return true
            }
        })
    }

    private fun refreshData(query: String = "") {
        homeViewModel.fetchData(query)
    }

    private fun onUserSelected(users: ArrayList<Items>) {
        adapter = UserAdapter(users, object : UserAdapter.OnUserClickListener {
            override fun onUserClicked(user: Items) {
                val detailFragment = DetailFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(DetailFragment.USER_LOGIN, user)
                        putString(DetailFragment.UNIQUE_ARG, "detail_${user.login}")
                    }
                }
                Log.d(TAG, "onViewCreated: $arguments")

                val tag = "detail_${user.login}"
                parentFragmentManager.commit {
                    replace(
                        R.id.home_fragment,
                        detailFragment,
                        tag
                    )
                    addToBackStack(tag)
                }
            }
        })
        binding.rvUsers.adapter = adapter
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbUser.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        private val TAG = HomeFragment::class.java.simpleName
    }
}