package com.blogspot.mido_mymall.util

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.Navigation
import com.blogspot.mido_mymall.R
import com.blogspot.mido_mymall.databinding.SignInDialogBinding
import com.blogspot.mido_mymall.ui.home.HomeFragmentDirections
import com.blogspot.mido_mymall.ui.product_details.ProductDetailsFragmentDirections

import com.blogspot.mido_mymall.BuildConfig
import com.blogspot.mido_mymall.ui.my_account.MyAccountFragmentDirections
import com.blogspot.mido_mymall.ui.my_cart.MyCartFragmentDirections
import com.blogspot.mido_mymall.ui.my_rewards.MyRewardsFragmentDirections
import androidx.navigation.findNavController
import com.blogspot.mido_mymall.ui.MainActivity
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import javax.inject.Singleton

object Constants {

    private const val TAG = "Constants"

    const val WEB_CLIENT_ID =
        "119751966934-4tmtpcpheeid9r1j7foa7d6uf27rgv33.apps.googleusercontent.com"


    const val RAZORPAY_BASE_URL = "https://api.razorpay.com/v1/"

    const val RAZORPAY_API_PASSWORD = BuildConfig.RAZORPAY_API_PASSWORD


    //Addresses Constants
    const val SELECT_ADDRESS_MODE = 0
    const val MANAGE_ADDRESS = 1

    private var AD_REQUEST_BUILDER = AdRequest.Builder()
    private lateinit var consentInformation: ConsentInformation
    private var isNPA: Boolean = false
    private val params: ConsentRequestParameters.Builder = ConsentRequestParameters.Builder()
        .setTagForUnderAgeOfConsent(false)
    private val extras = Bundle()


    @JvmStatic
    fun hasInternetConnection(context: Context): Boolean {

        val connectivityManager = context.applicationContext.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities =
            connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> true
            else -> false
        }

    }



    @JvmStatic
    fun applySelectedDayNightMode(mode: Int) {
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    @JvmStatic
    fun signInSignUpDialog(
        context: Context,
        destination: Int,
        layoutInflater: LayoutInflater,
        view: View
    ) {
        val dialog = Dialog(context)
        val signInDialogBinding: SignInDialogBinding =
            SignInDialogBinding.inflate(layoutInflater, null, false)
        dialog.setContentView(signInDialogBinding.root)
        signInDialogBinding.signInButton.setOnClickListener {
//            Toast.makeText(context, "sign in", Toast.LENGTH_SHORT).show()
            dialog.cancel()

            when (destination) {
                R.id.homeFragment -> {
                    view.findNavController()
                        .navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
                }

                R.id.productDetailsFragment -> {
                    view.findNavController()
                        .navigate(ProductDetailsFragmentDirections.actionGlobalLoginFragment())
                }

                R.id.nav_my_cart -> {
                    view.findNavController()
                        .navigate(MyCartFragmentDirections.actionNavMyCartToLoginFragment())
                }

                R.id.nav_my_rewards -> {
                    view.findNavController()
                        .navigate(MyRewardsFragmentDirections.actionNavMyRewardsToLoginFragment())
                }

                R.id.nav_my_account -> {
                    view.findNavController()
                        .navigate(MyAccountFragmentDirections.actionNavMyAccountToLoginFragment())

                }
            }
        }
        signInDialogBinding.signUpButton.setOnClickListener {
            dialog.cancel()
            when (destination) {
                R.id.homeFragment -> {
                    view.findNavController()
                        .navigate(HomeFragmentDirections.actionHomeFragmentToRegisterFragment())
                }

                R.id.productDetailsFragment -> {
                    view.findNavController()
                        .navigate(ProductDetailsFragmentDirections.actionProductDetailsFragmentToRegisterFragment())
                }

                R.id.nav_my_cart -> {
                    view.findNavController()
                        .navigate(MyCartFragmentDirections.actionNavMyCartToRegisterFragment())
                }

                R.id.nav_my_rewards -> {
                    view.findNavController()
                        .navigate(MyRewardsFragmentDirections.actionNavMyRewardsToRegisterFragment())
                }

                R.id.nav_my_account -> {
                    view.findNavController()
                        .navigate(MyAccountFragmentDirections.actionNavMyAccountToRegisterFragment())
                }
            }
        }
        dialog.show()
    }

    fun setProgressDialog(context: Context): AlertDialog {
        val llPadding = 30
        val ll = LinearLayout(context)
        ll.orientation = LinearLayout.HORIZONTAL
        ll.setPadding(llPadding, llPadding, llPadding, llPadding)
        ll.gravity = Gravity.CENTER
        var llParam = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        llParam.gravity = Gravity.CENTER
        ll.layoutParams = llParam
        val progressBar = ProgressBar(context)
        progressBar.isIndeterminate = true
        progressBar.setPadding(0, 0, llPadding, 0)
        progressBar.layoutParams = llParam
        llParam = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        llParam.gravity = Gravity.CENTER
        val tvText = TextView(context)
        tvText.text = "Loading ..."

        tvText.setTextColor(context.resources.getColor(R.color.black, context.theme))

        tvText.textSize = 20f
        tvText.layoutParams = llParam
        ll.addView(progressBar)
        ll.addView(tvText)
        val builder = AlertDialog.Builder(context)
        builder.setCancelable(false)
        builder.setView(ll)
        val dialog = builder.create()
        //        dialog.show();
        val window = dialog.window
        if (window != null) {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window!!.attributes)
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            dialog.window!!.attributes = layoutParams
        }
        return dialog
    }

    @JvmStatic
    fun callAndBuildAdRequest(): AdRequest {

        return if (isNPA) {
            extras.putString("npa", "1")
            AD_REQUEST_BUILDER.addNetworkExtrasBundle(AdMobAdapter::class.java, extras).build()
        } else {
            AD_REQUEST_BUILDER.build()
        }
    }

    @JvmStatic
    fun getAdSize(activity: AppCompatActivity): AdSize {
        //        @RequiresApi(Build.VERSION_CODES.R)

        val outMetrics = DisplayMetrics()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                display = activity?.display!!
            activity.display?.getRealMetrics(outMetrics)
        } else {

            @Suppress("DEPRECATION")
            val display = activity.windowManager.defaultDisplay
            @Suppress("DEPRECATION")
            display?.getMetrics(outMetrics)

        }

        val density = outMetrics.density

        var adWidthPixels = if(activity is MainActivity) {
            activity.findViewById<View>(R.id.drawer_layout).width.toFloat()
        }else {
            activity.findViewById<View>(R.id.productDetailsFragment).width.toFloat()
        }
        if (adWidthPixels == 0f) {
            adWidthPixels = outMetrics.widthPixels.toFloat()
        }

        val adWidth = (adWidthPixels / density).toInt()
        val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
            activity.applicationContext,
            adWidth
        )
        return adSize
    }

    @JvmStatic
    @Singleton
    fun requestTheLatestConsentInformation(appCompatActivity: AppCompatActivity) {
        // Set tag for underage of consent. Here false means users are not underage.

//        val debugSettings = ConsentDebugSettings.Builder(appCompatActivity)
////            .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_NOT_EEA)
//            .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
////            .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_DISABLED)
//            .addTestDeviceHashedId("048DC5C3C06FBD17D9AD205151167F48")
//            .build()
//        params.setConsentDebugSettings(debugSettings)

        consentInformation = UserMessagingPlatform.getConsentInformation(appCompatActivity)
        consentInformation.requestConsentInfoUpdate(
            appCompatActivity,
            params.build(),
            {
                // The consent information state was updated.
                // You are now ready to check if a form is available.

                if (consentInformation.isConsentFormAvailable) {
                    loadForm(appCompatActivity)
                }


            },
            { formError ->
                // Handle the error.
                Log.e(TAG, "loadForm: ${formError.message}")
            })
    }


    @JvmStatic
    @Singleton
    fun loadForm(appCompatActivity: AppCompatActivity) {
        UserMessagingPlatform.loadConsentForm(
            appCompatActivity,
            { consentForm ->
//                Constants.consentForm = consentForm

                if (consentInformation.consentStatus == ConsentInformation.ConsentStatus.NOT_REQUIRED
                    || consentInformation.consentStatus == ConsentInformation.ConsentStatus.OBTAINED
                ) {
                    isNPA = false


                } else if (consentInformation.consentStatus == ConsentInformation.ConsentStatus.REQUIRED ||
                    consentInformation.consentStatus == ConsentInformation.ConsentStatus.UNKNOWN
                ) {

                    isNPA = true

                    consentForm.show(appCompatActivity
                    ) {
                        Log.d(TAG, "loadForm: dismissed")
                    }
                }


            },
            { formError ->
                // Handle dismissal by reloading form.
                Log.e(TAG, "loadForm: ${formError.message.toString()}")



//                loadForm(appCompatActivity)
            }
        )


        when (consentInformation.consentStatus) {
            ConsentInformation.ConsentStatus.NOT_REQUIRED, ConsentInformation.ConsentStatus.OBTAINED -> {
                Log.d(TAG, "isUserFromEEA: ${consentInformation.consentStatus}")
            }
            ConsentInformation.ConsentStatus.REQUIRED -> {
                Log.d(TAG, "isUserFromEEA: ${consentInformation.consentStatus}")

            }
            else -> {
                Log.d(TAG, "isUserFromEEA: ${consentInformation.consentStatus}")
            }
        }
    }

}