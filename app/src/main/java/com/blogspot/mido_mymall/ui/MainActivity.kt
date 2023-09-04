package com.blogspot.mido_mymall.ui

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
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
import com.blogspot.mido_mymall.util.Constants
import com.blogspot.mido_mymall.util.Resource
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONObject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), PaymentResultWithDataListener {

    private lateinit var mAppBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var navGraph: NavGraph

    private lateinit var drawer: DrawerLayout

    private val signOutViewModel by viewModels<SignOutViewModel>()

    private val mainActivityViewModel by viewModels<MainActivityViewModel>()

    var signOutItem: MenuItem? = null

    lateinit var navHeaderMainBinding: NavHeaderMainBinding

    var userName = ""
    var userEmail = ""
    var userImage = ""

    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(Constants.WEB_CLIENT_ID).requestProfile()
        .requestEmail().build()

    val googleSignInClient by lazy { GoogleSignIn.getClient(this, gso) }

    lateinit var actionBarLogo :ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_MyMallKotlin)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        drawer = binding.drawerLayout

        actionBarLogo = binding.actionBarLogo


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
            navController.navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
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
            R.id.nav_my_wishlist, R.id.nav_my_account, R.id.nav_sign_out
        ).setOpenableLayout(drawer)
            .build()

        setupActionBarWithNavController(this, navController, mAppBarConfiguration)
        setupWithNavController(binding.navigationView, navController)



        navController.addOnDestinationChangedListener { _: NavController?, destination: NavDestination, _: Bundle? ->
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START)
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
                    fragmentTitleAndActionBar("My Orders")
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
                    fragmentTitleAndActionBar("My Cart")
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
                    fragmentTitleAndActionBar("My Wishlist")
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
                    fragmentTitleAndActionBar("My Rewards")
                    changeToolbarAndStatusBarColor(
                        "#" + Integer.toHexString(
                            ContextCompat.getColor(
                                this,
                                R.color.myRewardsToolBarAndStatusBarColor
                            )
                        )
                    )
                }

                R.id.nav_my_account -> fragmentTitleAndActionBar("My account")

                R.id.viewAllFragment ->{
                    binding.actionBarLogo.visibility = View.GONE
//                    fragmentTitleAndActionBar("Deals of the Day")

                }

                R.id.editUserInfoFragment->{
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
            }
//            drawer.closeDrawer(GravityCompat.START); // or GravityCompat.END
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START)
            true
        }

        if(signOutViewModel.firebaseAuth.currentUser != null){
            mainActivityViewModel.updateLastSeen()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                mainActivityViewModel.lastSeenUpdateState.collect{

                    if(it is Resource.Success){

//                        Toast.makeText(
//                            this@MainActivity,
//                            "update last seen success",
//                            Toast.LENGTH_SHORT
//                        ).show()

                    }else if(it is Resource.Error){
                        Log.e(TAG, "lastSeenUpdateState: ${it.message.toString()}" )
                    }
                }

            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                mainActivityViewModel.userInfo.collect{
                    if(it is Resource.Success){
                        it.data?.let { documentSnapshot->
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

                    }else if(it is Resource.Error){
                        Log.e(TAG, "onCreate: ${it.message.toString()}" )
                    }
                }
            }
        }


    }

//    override fun onStart() {
//        super.onStart()
//    }



    fun startPayment(orderId:String,amount: String) {
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
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("order_id", orderId)
            options.put("amount", amount )//pass amount in currency subunits

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
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
//            e.printStackTrace()
            Log.e(TAG, "startPayment: ${e.message.toString()}")
            Log.e(TAG, "startPayment: ${e.cause.toString()}")
        }
    }


    fun fragmentTitleAndActionBar(title: String?) {
        invalidateOptionsMenu()
        binding.actionBarLogo.visibility = View.GONE
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = title
    }

    private fun changeToolbarAndStatusBarColor(color: String) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.parseColor(color)
        binding.toolbar.setBackgroundColor(Color.parseColor(color))
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
            mainActivityViewModel.updateOrderStatus(p1.orderId,"Paid", orderStatus = "ORDERED")
        }

    }

    override fun onPaymentError(errorCode: Int, p1: String?, p2: PaymentData?) {
                Log.e(TAG, "onPaymentError: $p1")
        mainActivityViewModel.updatePaymentState(false)

        p2?.orderId?.let { mainActivityViewModel.getOrderId(it) }
        when (errorCode) {
            Checkout.NETWORK_ERROR -> {
                mainActivityViewModel.updateOrderStatus(p2?.orderId!!,"not paid","NETWORK_ERROR")
            }
            Checkout.INVALID_OPTIONS -> {
                mainActivityViewModel.updateOrderStatus(p2?.orderId!!,"not paid","INVALID_OPTIONS")

            }
            Checkout.PAYMENT_CANCELED -> {
                mainActivityViewModel.updateOrderStatus(p2?.orderId!!,"not paid","CANCELED")

            }
            Checkout.TLS_ERROR->{
                mainActivityViewModel.updateOrderStatus(p2?.orderId!!,"not paid","TLS_ERROR")

            }
        }

    }
}