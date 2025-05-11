package com.blogspot.mido_mymall.ui

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isEmpty
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.blogspot.mido_mymall.BaseApplication
import com.blogspot.mido_mymall.BaseApplication.AppOpenAdManager
import com.blogspot.mido_mymall.BuildConfig
import com.blogspot.mido_mymall.R
import com.blogspot.mido_mymall.databinding.ActivityMainBinding
import com.blogspot.mido_mymall.databinding.NavHeaderMainBinding
import com.blogspot.mido_mymall.ui.credentials.SignOutViewModel
import com.blogspot.mido_mymall.ui.home.HomeFragmentDirections
import com.blogspot.mido_mymall.ui.my_account.MyAccountFragmentDirections
import com.blogspot.mido_mymall.ui.my_cart.MyCartFragmentDirections
import com.blogspot.mido_mymall.ui.my_orders.MyOrdersFragmentDirections
import com.blogspot.mido_mymall.ui.my_rewards.MyRewardsFragmentDirections
import com.blogspot.mido_mymall.ui.my_wish_list.MyWishlistFragmentDirections
import com.blogspot.mido_mymall.ui.settings.SettingsFragmentDirections
import com.blogspot.mido_mymall.ui.settings.SettingsViewModel
import com.blogspot.mido_mymall.util.Constants
import com.blogspot.mido_mymall.util.Constants.requestTheLatestConsentInformation
import com.blogspot.mido_mymall.util.NetworkListener
import com.blogspot.mido_mymall.util.Resource
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), PaymentResultWithDataListener, HideShowIconInterface {

    private lateinit var mAppBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var navGraph: NavGraph

    private lateinit var drawer: DrawerLayout

    private val signOutViewModel by viewModels<SignOutViewModel>()

    private val mainActivityViewModel by viewModels<MainActivityViewModel>()

    private val settingsViewModel by viewModels<SettingsViewModel>()

    var signOutItem: MenuItem? = null

    lateinit var navHeaderMainBinding: NavHeaderMainBinding

    var userName = ""
    var userEmail = ""
    var userImage = ""

    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(Constants.WEB_CLIENT_ID).requestProfile()
        .requestEmail().build()

    val googleSignInClient by lazy { GoogleSignIn.getClient(this, gso) }

    lateinit var actionBarLogo: ImageView

    private var contentHasLoaded = false

//    private lateinit var splashScreen: SplashScreen

    private val networkListener by lazy { NetworkListener() }


    private var adView: AdView? = null
    private var adRequest: AdRequest? = null
    private lateinit var appOpenAdManager: AppOpenAdManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

//        setTheme(R.style.Theme_MyMallKotlin)

//        splashScreen = installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top + 16,
                systemBars.right,
                systemBars.bottom + 16
            )
            insets
        }

        appOpenAdManager = (application as BaseApplication).AppOpenAdManager()
        appOpenAdManager.showAdIfAvailable(this)
        adView = AdView(this)



        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                networkListener.checkNetworkAvailability(this@MainActivity).collect { status ->
                    Log.d(TAG, "onCreate: networkListener $status")

                    mainActivityViewModel.networkStatus = status
                    showNetworkStatus()
                }

            }
        }


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                mainActivityViewModel.readBackOnline.collect {
                    mainActivityViewModel.backOnline = it
                }
            }
        }


        setSupportActionBar(binding.toolbar)
        binding.toolbar.setTitleTextColor(resources.getColor(R.color.white))

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingsViewModel.getSelectedDayNightMode.collect {
                    Constants.applySelectedDayNightMode(it)
                }
            }
        }


        drawer = binding.drawerLayout

        actionBarLogo = binding.actionBarLogo
//        actionBarLogo.visibility = View.VISIBLE


        Checkout.preload(this)

        navHeaderMainBinding = NavHeaderMainBinding.bind(binding.navigationView.getHeaderView(0))


        //        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment?

        if (navHostFragment != null) {
            navController = navHostFragment.navController
        }

        navGraph = navController.graph

        if (signOutViewModel.firebaseAuth.currentUser == null) {
            navGraph.setStartDestination(R.id.loginFragment)
            if (navController.currentDestination?.id == R.id.homeFragment) {
                navController.navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
            }
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START)
            Log.d(TAG, "loginFragment: ${navGraph.startDestinationId}")
        } else {
            navGraph.setStartDestination(R.id.homeFragment)
            Log.d(TAG, "HomeFragment: ${navGraph.startDestinationId}")

            mainActivityViewModel.getUserInfo()
        }

//        binding.rootLayout

//        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        //        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = AppBarConfiguration.Builder(
            R.id.homeFragment, R.id.nav_my_orders, R.id.nav_my_rewards, R.id.nav_my_cart,
            R.id.nav_my_wishlist, R.id.nav_my_account, R.id.settingsFragment, R.id.nav_sign_out
        ).setOpenableLayout(drawer)
            .build()

        setupActionBarWithNavController(this, navController, mAppBarConfiguration)
        setupWithNavController(binding.navigationView, navController)



        navController.addOnDestinationChangedListener { _: NavController?, destination: NavDestination, _: Bundle? ->

            if (destination.id == R.id.homeFragment ||
                destination.id == R.id.nav_my_orders ||
                destination.id == R.id.nav_my_rewards ||
                destination.id == R.id.nav_my_cart ||
                destination.id == R.id.nav_my_wishlist ||
                destination.id == R.id.nav_my_account
            ) {
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START)

                binding.toolbar.setNavigationOnClickListener {
                    drawer.openDrawer(GravityCompat.START)
                }
            } else {

                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START)

                }

                binding.toolbar.setNavigationOnClickListener { //do whatever you want here

                    navController.navigateUp()
                }

            }


//            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START)

            when (destination.id) {
                R.id.homeFragment -> {

                    supportActionBar!!.show()
                    invalidateOptionsMenu()
                    binding.actionBarLogo.visibility = View.VISIBLE
                    supportActionBar?.setDisplayShowTitleEnabled(false)

                    changeToolbarAndStatusBarColor(
                        "#" + Integer.toHexString(
                            ContextCompat.getColor(
                                this,
                                R.color.colorPrimary
                            )
                        )
                    )
                }

                R.id.registerFragment -> supportActionBar!!.hide()
                R.id.loginFragment -> {
                    Log.d(TAG, "addOnDestinationChangedListener: called")
                    supportActionBar?.hide()
                    drawer.setDrawerLockMode(
                        DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
                        GravityCompat.START
                    )
                }

                R.id.nav_my_orders -> {
                    fragmentTitleAndActionBar(getString(R.string.my_orders))
                    changeToolbarAndStatusBarColor(
                        "#" + Integer.toHexString(
                            ContextCompat.getColor(
                                this,
                                R.color.colorPrimary
                            )
                        )
                    )

                }

                R.id.nav_my_cart -> {
                    fragmentTitleAndActionBar(getString(R.string.my_cart))
                    changeToolbarAndStatusBarColor(
                        "#" + Integer.toHexString(
                            ContextCompat.getColor(
                                this,
                                R.color.colorPrimary
                            )
                        )
                    )

                }

                R.id.nav_my_wishlist -> {
                    fragmentTitleAndActionBar(getString(R.string.my_wishlist))
                    changeToolbarAndStatusBarColor(
                        "#" + Integer.toHexString(
                            ContextCompat.getColor(
                                this,
                                R.color.colorPrimary
                            )
                        )
                    )

                }

                R.id.nav_my_rewards -> {
                    fragmentTitleAndActionBar(getString(R.string.my_rewards))
                    changeToolbarAndStatusBarColor(
                        "#" + Integer.toHexString(
                            ContextCompat.getColor(
                                this,
                                R.color.myRewardsToolBarAndStatusBarColor
                            )
                        )
                    )
                }

                R.id.nav_my_account -> fragmentTitleAndActionBar(getString(R.string.my_account))

                R.id.viewAllFragment -> {
                    binding.actionBarLogo.visibility = View.GONE
//                    fragmentTitleAndActionBar("Deals of the Day")

                }

                R.id.editUserInfoFragment -> {
                    changeToolbarAndStatusBarColor(
                        "#" + Integer.toHexString(
                            ContextCompat.getColor(
                                this,
                                R.color.colorPrimary
                            )
                        )
                    )
                }
//                R.id.deliveryFragment -> fragmentTitleAndActionBar("Delivery")
//                R.id.viewAllFragment -> fragmentTitleAndActionBar("Deals of the Day")
//                R.id.productDetailsFragment -> fragmentTitleAndActionBar("")

            }
        }

        signOutItem = binding.navigationView.menu.findItem(R.id.nav_sign_out)

        signOutItem?.isEnabled = signOutViewModel.firebaseAuth.currentUser != null


        Log.d(TAG, "signOutItem is enabled: " + signOutItem?.isEnabled)

        signOutItem?.setOnMenuItemClickListener {
            destroyAdAfterLogOut()
            when (navController.currentDestination!!.id) {
                R.id.homeFragment -> {
                    signOutViewModel.signOut(googleSignInClient)
                    navController.navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
                }

                R.id.nav_my_orders -> {
                    signOutViewModel.signOut(googleSignInClient)
                    navController.navigate(MyOrdersFragmentDirections.actionGlobalLoginFragment())
                }

                R.id.nav_my_rewards -> {
                    signOutViewModel.signOut(googleSignInClient)
                    navController.navigate(MyRewardsFragmentDirections.actionGlobalLoginFragment())
                }

                R.id.nav_my_cart -> {
                    signOutViewModel.signOut(googleSignInClient)
                    navController.navigate(MyCartFragmentDirections.actionGlobalLoginFragment())
                }

                R.id.nav_my_wishlist -> {
                    signOutViewModel.signOut(googleSignInClient)
                    navController.navigate(MyWishlistFragmentDirections.actionGlobalLoginFragment())
                }

                R.id.nav_my_account -> {
                    signOutViewModel.signOut(googleSignInClient)
                    navController.navigate(MyAccountFragmentDirections.actionGlobalLoginFragment())
                }

                R.id.settingsFragment -> {
                    signOutViewModel.signOut(googleSignInClient)
                    navController.navigate(SettingsFragmentDirections.actionGlobalLoginFragment())
                }
            }
//            drawer.closeDrawer(GravityCompat.START); // or GravityCompat.END
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START)
            true
        }

        if (signOutViewModel.firebaseAuth.currentUser != null) {
            mainActivityViewModel.updateLastSeen()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainActivityViewModel.lastSeenUpdateState.collect {

                    if (it is Resource.Success) {

//                        contentHasLoaded = true

//                        Toast.makeText(
//                            this@MainActivity,
//                            "update_info last seen success",
//                            Toast.LENGTH_SHORT
//                        ).show()

                    } else if (it is Resource.Error) {
                        Log.e(TAG, "lastSeenUpdateState: ${it.message.toString()}")
                    }
                }

            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainActivityViewModel.userInfo.collect {
                    if (it is Resource.Success) {
                        it.data?.let { documentSnapshot ->
                            Glide.with(this@MainActivity)
                                .load(documentSnapshot.get("profileImage"))
                                .placeholder(R.drawable.account)
                                .into(navHeaderMainBinding.mainProfileImage)


                            this@MainActivity.apply {
                                userName = documentSnapshot.get("userName").toString()
                                userEmail = documentSnapshot.get("email").toString()
                                userImage = documentSnapshot.get("profileImage").toString()
                            }

                            navHeaderMainBinding.mainUserName.text = userName
                            navHeaderMainBinding.mainEmail.text = userEmail
                        }

                        contentHasLoaded = true

                    } else if (it is Resource.Error) {
                        Log.e(TAG, "onCreate: ${it.message.toString()}")
                    }
                }
            }
        }

        if (Constants.hasInternetConnection(this) && signOutViewModel.firebaseAuth.currentUser != null) {

            requestTheLatestConsentInformation(this)
            adRequest = Constants.callAndBuildAdRequest()

            lifecycleScope.launch(Dispatchers.IO) {
                MobileAds.initialize(this@MainActivity) {
                    Log.d(TAG, "onInitCompleted")
                }
            }

            if (adView?.adUnitId.isNullOrEmpty()) {
                adView?.adUnitId = BuildConfig.MAIN_ACTIVITY_BANNER
            }

            adView?.setAdSize(Constants.getAdSize(this))
            requestHomeBanner()

//            binding.adViewContainer.apply {
//                visibility = View.VISIBLE
//                if (isEmpty()) {
//                    addView(adView)
//                }
//            }


        } else {
            binding.adViewContainer.removeAllViews()
            binding.adViewContainer.visibility = View.GONE
        }

    }


    fun getToolBar(): Toolbar {
        return binding.toolbar
    }

    private fun showNetworkStatus() {
        if (!mainActivityViewModel.networkStatus) {
            Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG)
                .show()
            mainActivityViewModel.saveBackOnline(true)
        } else {

            if (mainActivityViewModel.backOnline) {
                Toast.makeText(this, getString(R.string.we_re_back_online), Toast.LENGTH_LONG)
                    .show()
                mainActivityViewModel.saveBackOnline(false)
            }

        }
    }

    fun requestHomeBanner() {
        if (binding.adViewContainer.isEmpty()) {
            if (adView != null) {
                binding.adViewContainer.addView(adView)
            } else {
                adView = AdView(this)
                adView?.setAdSize((Constants.getAdSize(this)))
                binding.adViewContainer.apply {
                    visibility = View.VISIBLE
                    addView(adView)
                }
            }
        }

        adView?.adListener = object : AdListener() {


            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.d(TAG, "onAdLoaded: called")
                if (binding.adViewContainer.isVisible == false) {
                    binding.adViewContainer.visibility = View.VISIBLE


                }
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e(TAG, "onAdFailedToLoad: ${adError.cause.toString()}")
                Log.e(TAG, "onAdFailedToLoad: ${adError.responseInfo.toString()}")
                binding.adViewContainer.visibility = View.GONE
            }

        }
        adRequest?.let { adView?.loadAd(it) }

    }


//    private fun setupSplashScreen(splashScreen: SplashScreen) {
//        val content: View = findViewById(android.R.id.content)
//        content.viewTreeObserver.addOnPreDrawListener(
//            object : ViewTreeObserver.OnPreDrawListener {
//                override fun onPreDraw(): Boolean {
//                    return if (contentHasLoaded) {
//                        content.viewTreeObserver.removeOnPreDrawListener(this)
//                        true
//                    } else false
//                }
//            }
//        )
//
//        splashScreen.setOnExitAnimationListener { splashScreenView ->
//            val slideBack = ObjectAnimator.ofFloat(
//                splashScreenView.view,
//                View.TRANSLATION_X,
//                0f,
//                -splashScreenView.view.width.toFloat()
//            ).apply {
//                interpolator = DecelerateInterpolator()
//                duration = 800L
//                doOnEnd { splashScreenView.remove() }
//            }
//
//            slideBack.start()
//        }
//    }


    fun startPayment(orderId: String, amount: String) {
        /*
        *  You need to pass the current activity to let Razorpay create CheckoutActivity
        * */
        val activity: Activity = this
        val co = Checkout()


        try {
            val options = JSONObject()
            options.put("name", "Razorpay Corp")
            options.put("description", "Demoing Charges")
            //You can omit the image option to fetch the image from the dashboard
            options.put(
                "image",
                "https://firebasestorage.googleapis.com/v0/b/my-mall-7c08e.appspot.com/o/products%2Fdownload.png?alt=media&token=c72c340f-94ac-465f-ad14-20fc07b316cf"
            )
            options.put("theme.color", "#3399cc")
            options.put("currency", "EGP")
            options.put("order_id", orderId)
            options.put("amount", amount)//pass amount in currency subunits

            val retryObj = JSONObject()
            retryObj.put("enabled", true)
            retryObj.put("max_count", 4)
            options.put("retry", retryObj)

            val prefill = JSONObject()
//            prefill.put("email", "gaurav.kumar@example.com")
//            prefill.put("contact", "+201091233565")

            options.put("prefill", prefill)
            co.open(activity, options)
        } catch (e: Exception) {
            Toast.makeText(
                activity,
                getString(R.string.error_in_payment) + e.message, Toast.LENGTH_LONG
            ).show()
//            e.printStackTrace()
            Log.e(TAG, "startPayment: ${e.message.toString()}")
            Log.e(TAG, "startPayment: ${e.cause.toString()}")
        }
    }


    fun fragmentTitleAndActionBar(title: String? = null) {
        invalidateOptionsMenu()
        binding.actionBarLogo.visibility = View.GONE
        supportActionBar?.setDisplayShowTitleEnabled(true)
        if (title != null) {
            supportActionBar?.title = title
        }
    }

    private fun changeToolbarAndStatusBarColor(color: String) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = color.toColorInt()
        binding.toolbar.setBackgroundColor(color.toColorInt())
    }

    override fun onSupportNavigateUp(): Boolean {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_container);
        return (navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp())
    }


    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?, p1: PaymentData?) {
        Log.d(TAG, "onPaymentSuccess: ${p1?.orderId}")

        mainActivityViewModel.updatePaymentState(true)

        p1?.orderId?.let { mainActivityViewModel.getOrderId(orderId = it) }

        if (p1 != null) {
            mainActivityViewModel.updateOrderStatus(p1.orderId, "Paid", orderStatus = "ORDERED")
        }

    }

    override fun onPaymentError(errorCode: Int, p1: String?, p2: PaymentData?) {
        Log.e(TAG, "onPaymentError: $p1")
        mainActivityViewModel.updatePaymentState(false)

        p2?.orderId?.let { mainActivityViewModel.getOrderId(it) }

        when (errorCode) {
            Checkout.NETWORK_ERROR -> {
                mainActivityViewModel.updateOrderStatus(p2?.orderId!!, "not paid", "NETWORK_ERROR")
            }

            Checkout.INVALID_OPTIONS -> {
                mainActivityViewModel.updateOrderStatus(
                    p2?.orderId!!,
                    "not paid",
                    "INVALID_OPTIONS"
                )

            }

            Checkout.PAYMENT_CANCELED -> {
                Log.e(TAG, "onPaymentError: error ${p2.toString()}")

                mainActivityViewModel.updateOrderStatus(p2?.orderId!!, "not paid", "CANCELED")

            }

            Checkout.TLS_ERROR -> {
//                Log.e(TAG, "onPaymentError: error ${p2?.paymentId}", )
                mainActivityViewModel.updateOrderStatus(p2?.orderId!!, "not paid", "TLS_ERROR")

            }
        }


    }

    fun setNoUserInfoAfterSignOut() {
        navHeaderMainBinding.apply {
            mainUserName.text = getString(R.string.not_signed_in)
            mainEmail.text = ""
            mainProfileImage.setImageResource(R.drawable.account)
        }
    }

    override fun showBackIcon() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24)
//        drawerToggle.setDrawerIndicatorEnabled(false)
    }

//    override fun onStop() {
//        super.onStop()
//        adView?.destroy()
//        adView = null
//    }

    override fun onResume() {
        super.onResume()
        if (signOutViewModel.firebaseAuth.currentUser != null && binding.adViewContainer.isEmpty() && adView == null) {
            adView = AdView(this)
            adView?.setAdSize(Constants.getAdSize(this))
            requestHomeBanner()

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        adView?.destroy()
        adView = null
        binding.adViewContainer.apply {
            removeAllViews()
            visibility = View.GONE
        }
    }

    fun destroyAdAfterLogOut() {
        adView?.destroy()
        adView = null
        binding.adViewContainer.apply {
            removeAllViews()
            visibility = View.GONE
        }
    }

//    fun showAdAfterLogin() {
//        if (binding.adViewContainer.isEmpty()) {
//
//            if (adView == null) {
//                adView = AdView(this)
//                adView?.setAdSize(Constants.getAdSize(this))
//                requestHomeBanner()
//            } else {
//                requestHomeBanner()
//            }.also {
//                binding.adViewContainer.apply {
//                    visibility = View.VISIBLE
//                    addView(adView)
//                }
//            }
//
//
//        }
//    }
}

interface HideShowIconInterface {
    //    fun showHamburgerIcon()
    fun showBackIcon()
}

