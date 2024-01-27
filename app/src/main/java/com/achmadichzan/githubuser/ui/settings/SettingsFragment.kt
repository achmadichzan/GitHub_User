package com.achmadichzan.githubuser.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.achmadichzan.githubuser.R
import com.achmadichzan.githubuser.databinding.FragmentSettingsBinding
import com.achmadichzan.githubuser.ui.home.HomeViewModel
import com.achmadichzan.githubuser.utils.SettingPreferences
import com.achmadichzan.githubuser.utils.ViewModelFactory
import com.achmadichzan.githubuser.utils.dataStore
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private lateinit var preferences: SettingPreferences
    private val homeViewModel by viewModels<HomeViewModel>{
        ViewModelFactory.getInstance(requireActivity().application, preferences)
    }
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = SettingPreferences.getInstance(requireActivity().application.dataStore)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lifecycleScope = viewLifecycleOwner.lifecycleScope

        binding.switchTheme.setOnClickListener {
            lifecycleScope.launch {
                    handleSwitchTheme()
            }
        }

    }

    private fun handleSwitchTheme() {

        val switchTheme = binding.switchTheme

        homeViewModel.isDarkModeActive.observe(viewLifecycleOwner) { isDarkModeActive ->

            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                switchTheme.isChecked = true
                switchTheme.thumbDrawable = ContextCompat.getDrawable(requireActivity(), R.drawable.baseline_dark_mode_24)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                switchTheme.isChecked = false
                switchTheme.thumbDrawable = ContextCompat.getDrawable(requireActivity(), R.drawable.baseline_light_mode_24)
            }
        }

        switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            homeViewModel.saveThemeSetting(isChecked)
        }

        val currentNightMode = AppCompatDelegate.getDefaultNightMode()
        switchTheme.isChecked = currentNightMode == AppCompatDelegate.MODE_NIGHT_YES
    }
}