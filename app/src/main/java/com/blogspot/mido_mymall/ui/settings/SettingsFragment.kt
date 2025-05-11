package com.blogspot.mido_mymall.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.blogspot.mido_mymall.BuildConfig
import com.blogspot.mido_mymall.R
import com.blogspot.mido_mymall.databinding.AboutUsLayoutBinding
import com.blogspot.mido_mymall.databinding.FragmentSettingsBinding
import com.blogspot.mido_mymall.ui.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "SettingsFragment"

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        (requireActivity() as MainActivity).apply {
            showBackIcon()
            fragmentTitleAndActionBar(getString(R.string.settings))
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            switchToDarkMode.setOnClickListener {

                val nightModeFlags = requireContext().resources.configuration.uiMode and
                        Configuration.UI_MODE_NIGHT_MASK


                val choices = requireContext().resources.getStringArray(
                    R.array.dark_mode_options
                )

                var selectedItemIndex = 2

                lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        settingsViewModel.getSelectedDayNightMode.collect {

                            Log.d(TAG, "getSelectedDayNightMode: called")


                            selectedItemIndex = if (it == -1) {
                                2
                            } else if (it == 0 || nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                                0
                            } else {
                                1
                            }


//                                if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
//                                0
//                            } else if (it == -1) {
//                                2
//                            } else {
//                                1
//                            }
                            Log.d(TAG, "getSelectedDayNightMode:  $it")
                        }
                    }
                }


//            var selectedMode = choices[selectedItemIndex]

                MaterialAlertDialogBuilder(
                    requireContext(),
                    R.style.ThemeOverlay_App_MaterialAlertDialog
                ).setTitle(requireContext().resources.getString(R.string.choose_mode))
                    .setSingleChoiceItems(choices, selectedItemIndex) { _, which ->
                        selectedItemIndex = which


                        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES && which == 0) {
                            Toast.makeText(
                                requireContext(),
                                requireContext().resources.getString(R.string.night_mode_already_in_use),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO && which == 1) {
                            Toast.makeText(
                                requireContext(),
                                requireContext().resources.getString(R.string.light_mode_already_in_use),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (nightModeFlags == Configuration.UI_MODE_NIGHT_UNDEFINED) {
                            Toast.makeText(
                                requireContext(),
                                requireContext().resources.getString(R.string.follow_the_system_mode_already_in_use),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                    .setPositiveButton(requireContext().resources.getString(R.string.ok)) { _, which ->
//                        selectedItemIndex = which
                        when {
                            selectedItemIndex == 0 && nightModeFlags == Configuration.UI_MODE_NIGHT_YES -> {
                                Toast.makeText(
                                    requireContext(),
                                    requireContext().resources.getString(R.string.night_mode_already_in_use),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            selectedItemIndex == 0 && (nightModeFlags == Configuration.UI_MODE_NIGHT_NO
                                    || nightModeFlags == Configuration.UI_MODE_NIGHT_UNDEFINED) -> {

                                settingsViewModel.saveSelectedDayNightMode(2)


                                Log.d(TAG, "night mood selected $which: ")

                                findNavController().navigateUp()
                            }

                            selectedItemIndex == 1 && nightModeFlags == Configuration.UI_MODE_NIGHT_NO -> {
                                Toast.makeText(
                                    requireContext(),
                                    requireContext().resources.getString(R.string.light_mode_already_in_use),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            selectedItemIndex == 1 && (nightModeFlags == Configuration.UI_MODE_NIGHT_YES
                                    || nightModeFlags == Configuration.UI_MODE_NIGHT_UNDEFINED) -> {

                                Log.d(TAG, "light mood now: $which")

                                settingsViewModel.saveSelectedDayNightMode(1)


                                findNavController().navigateUp()
                            }

                            selectedItemIndex == 2 && nightModeFlags == Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                                settingsViewModel.saveSelectedDayNightMode(-1)
//
                                findNavController().navigateUp()
                            }

                            else -> {
                                settingsViewModel.saveSelectedDayNightMode(-1)
                                selectedItemIndex = 2
                                findNavController().navigateUp()
                            }
                        }

                    }
                    .setNegativeButton(requireContext().resources.getString(R.string.cancel)) { dialog, _ ->
                        dialog.cancel()
                    }.setCancelable(true)
                    .show()


            }


            privacyPolicy.setOnClickListener {
                findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToPrivacyPolicyFragment())
            }

            termsAndConditions.setOnClickListener {
                findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToTermsAndConditions())
            }

            rateTheApp.setOnClickListener {

                goToTheGooglePlayMarket()

            }

            moreApps.setOnClickListener {
                val intent =
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://search?q=pub:\"The Exceptional man\"")
                    )

                startActivity(intent)
            }

            aboutUs.setOnClickListener {
                aboutUsDialog()
            }

        }
    }

    private fun goToTheGooglePlayMarket() {
        // There was some problem, continue regardless of the result.
        val uri: Uri =
            Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")
        val goToMarketIntent = Intent(Intent.ACTION_VIEW, uri)
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarketIntent.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )
        try {
            startActivity(goToMarketIntent)
        } catch (e: ActivityNotFoundException) {

            Log.d(TAG, "goToTheGooglePlayMarket: ${e.message.toString()}")

            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")
                )
            )
        }
    }


    private fun aboutUsDialog() {

        val inflater = requireActivity().layoutInflater

//        val rootView: View = inflater.inflate(R.layout.about_us_layout, null, false)

        val binding = AboutUsLayoutBinding.inflate(inflater, null, false)


        val versionCode: String = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            requireContext().packageManager.getPackageInfo(
                requireContext().packageName, PackageManager.PackageInfoFlags.of(0)
            ).versionName

        } else {
            requireContext().packageManager.getPackageInfo(
                requireContext().packageName,
                0
            ).versionName
        }).toString()

        binding.versionCode.text = String.format(
            getString(R.string.app_version), versionCode
        )

        binding.contactUsTV.text = String.format(
            binding.root.context.resources.getString(R.string.contact_us),
            "exceptionalman1991@gmail.com"
        )

        MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setCancelable(true)
            .setView(binding.root).show()

    }


    override fun onDestroyView() {
        super.onDestroyView()
//        (requireActivity() as MainActivity).showHamburgerIcon()
        _binding = null
    }


}