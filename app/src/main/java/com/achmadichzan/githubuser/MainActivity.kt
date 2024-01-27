package com.achmadichzan.githubuser

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.achmadichzan.githubuser.databinding.ActivityMainBinding
import com.achmadichzan.githubuser.ui.favorite.FavoriteFragment
import com.achmadichzan.githubuser.ui.home.HomeFragment
import com.achmadichzan.githubuser.ui.settings.SettingsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        val viewMain = binding.root
        ViewCompat.setOnApplyWindowInsetsListener(viewMain) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            (view.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
                topMargin = insets.top
                bottomMargin = insets.bottom
                leftMargin = insets.left
                rightMargin = insets.right
            }

            WindowInsetsCompat.CONSUMED
        }

        setContentView(binding.root)

        binding.bottonNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val homeFragment = HomeFragment()
                    replaceFragment(homeFragment)

                    true
                }
                R.id.favorite -> {
                    val favoriteFragment = FavoriteFragment()
                    replaceFragment(favoriteFragment)

                    true
                }
                R.id.settings -> {
                    val settingsFragment = SettingsFragment()
                    replaceFragment(settingsFragment)

                    true
                }
                else -> { false }
            }
        }

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(
                    R.id.main_container,
                    HomeFragment(),
                    HomeFragment::class.java.simpleName
                )
            }
        }

        Log.d(TAG, "onCreate: $savedInstanceState")
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTag = fragmentManager.findFragmentByTag(fragment::class.java.simpleName)?.tag

        fragmentManager.popBackStack(fragmentTag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        fragmentManager.commit {
            replace(
                R.id.main_container,
                fragment,
                fragmentTag
            )
            addToBackStack(fragmentTag)
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}