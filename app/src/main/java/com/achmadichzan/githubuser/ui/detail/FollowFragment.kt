package com.achmadichzan.githubuser.ui.detail

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
import com.achmadichzan.githubuser.data.adapter.user.UserAdapter
import com.achmadichzan.githubuser.databinding.FragmentFollowBinding
import com.achmadichzan.githubuser.model.response.Items
import com.achmadichzan.githubuser.utils.SettingPreferences
import com.achmadichzan.githubuser.utils.ViewModelFactory
import com.achmadichzan.githubuser.utils.dataStore

class FollowFragment : Fragment() {

    private lateinit var preferences: SettingPreferences
    private val homeViewModel by viewModels<DetailViewModel>{
        ViewModelFactory.getInstance(requireActivity().application, preferences)
    }
    private var position: Int? = null
    private var username: String? = null
    private var adapter: UserAdapter? = null

    private var _binding: FragmentFollowBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = SettingPreferences.getInstance(requireActivity().application.dataStore)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFollowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvFollow.layoutManager = LinearLayoutManager(context)

        val emptyArray = arrayListOf<Items>()
        adapter = UserAdapter(emptyArray, object : UserAdapter.OnUserClickListener {
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
                    add(
                        R.id.detail_fragment,
                        detailFragment,
                        tag
                    )
                    addToBackStack(tag)
                }
            }
        })
        binding.rvFollow.adapter = adapter

        arguments?.let { arguments ->
            position = arguments.getInt(ARG_POSITION)
            username = arguments.getString(ARG_USERNAME)
            if (position == 1) {
                homeViewModel.followers.observe(viewLifecycleOwner) { followers ->
                    adapter?.setListUser(followers)
                }
                username?.let { homeViewModel.getFollowers(it) }
            } else {
                homeViewModel.following.observe(viewLifecycleOwner) { following ->
                    adapter?.setListUser(following)
                }
                username?.let { homeViewModel.getFollowing(it) }
            }
        }

        homeViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(ARG_POSITION, position ?: -1)
        outState.putString(ARG_USERNAME, username)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        adapter = null
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbFollow.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val ARG_POSITION = "position"
        const val ARG_USERNAME = "username"
        private val TAG = FollowFragment::class.java.simpleName
    }
}