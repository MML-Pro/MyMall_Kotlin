package com.blogspot.mido_mymall.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.blogspot.mido_mymall.databinding.ActivitySplashScreenBinding
import com.blogspot.mido_mymall.ui.settings.SettingsViewModel
import com.blogspot.mido_mymall.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {


    private var _binding: ActivitySplashScreenBinding? = null
    private val binding get() = _binding!!
//    private lateinit var settingsViewModel: SettingsViewModel

//    private val settingsViewModel by viewModels<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivitySplashScreenBinding.inflate(layoutInflater)

        setContentView(binding.root)

//        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

//        checkAndApplySelectedMode()

//        if(this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
        binding.splashScreenTitle.animate().translationY(400F).setDuration(1000).startDelay = 0
//        }else {
//            binding.splashScreenTitle.animate().translationY(50F).setDuration(1500).startDelay = 0
//        }

        Handler(Looper.getMainLooper()).postDelayed({ /* Create an Intent that will start the Menu-Activity. */

            val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)

//                startActivity(intent)

            this@SplashScreenActivity.startActivity(intent)
            this@SplashScreenActivity.finish()
        }, 2500)


    }

//    private fun checkAndApplySelectedMode() {
//
//        lifecycleScope.launch {
//
//            repeatOnLifecycle(Lifecycle.State.CREATED) {
//                settingsViewModel.getSelectedDayNightMode.collect {
//                    Constants.applySelectedDayNightMode(it)
//                }
//            }
//
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()
        binding.splashScreenTitle.clearAnimation()
        _binding = null
    }
}