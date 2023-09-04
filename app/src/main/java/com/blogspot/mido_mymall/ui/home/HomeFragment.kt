package com.blogspot.mido_mymall.ui.home

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.blogspot.mido_mymall.R
import com.blogspot.mido_mymall.databinding.FragmentHomeBinding
import com.blogspot.mido_mymall.domain.models.CategoryModel
import com.blogspot.mido_mymall.domain.models.HomePageModel
import com.blogspot.mido_mymall.domain.models.HorizontalProductScrollModel
import com.blogspot.mido_mymall.domain.models.SliderModel
import com.blogspot.mido_mymall.domain.models.WishListModel
import com.blogspot.mido_mymall.ui.MainActivity
import com.blogspot.mido_mymall.ui.credentials.SignOutViewModel
import com.blogspot.mido_mymall.ui.my_cart.MyCartViewModel
import com.blogspot.mido_mymall.ui.my_wish_list.WishlistAdapter
import com.blogspot.mido_mymall.ui.notification.NotificationViewModel
import com.blogspot.mido_mymall.util.Constants.signInSignUpDialog
import com.blogspot.mido_mymall.util.Resource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(), MenuProvider {

    companion object {
        private const val TAG = "HomeFragment"
    }

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    private val categoryList = arrayListOf<CategoryModel>()

    private val categoryAdapter = CategoryAdapter()

    private val sliderModelList = arrayListOf<SliderModel>()

    private val homePageList = arrayListOf<HomePageModel>()

    private lateinit var homePageAdapter: HomePageAdapter

    private val gridLayoutList = arrayListOf<HorizontalProductScrollModel>()

    private var menuHost: MenuHost? = null

    private val signOutViewModel by viewModels<SignOutViewModel>()

    private lateinit var cartBadgeCount: TextView

    private val homeViewModel by viewModels<HomeViewModel>()

    private lateinit var categoriesJob: Job

    private val myCartViewModel by viewModels<MyCartViewModel>()

    private var myCartListSize: Long = 0

//    private val myCartListIds = arrayListOf<String>()

    private val productsSearchList = arrayListOf<WishListModel>()

    private val wishlistAdapter = WishlistAdapter(false)

    private val idsList = arrayListOf<String>()

    private var searchQueryKeyword = ""

    private val notificationViewModel by viewModels<NotificationViewModel>()

    private var notificationListSize: Long = 0
    private lateinit var notificationCount: TextView

    var unread = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        menuHost = requireActivity()
        menuHost?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.STARTED)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //CATEGORIES
        categoriesJob = homeViewModel.getCategories()
//        initCategory()
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.categories.collect { result ->
                    when (result) {
                        is Resource.Loading -> {
//                            Toast.makeText(requireContext(), "Loading...", Toast.LENGTH_SHORT)
//                                .show()
                            showShimmerEffect()
                        }

                        is Resource.Success -> {
                            result.data?.let { documentSnapshots ->

                                binding.refreshLayout.isRefreshing = false
                                hideShimmerEffect()
                                categoryList.clear()


                                documentSnapshots.forEach {
                                    categoryList.add(
                                        CategoryModel(
                                            it.get("categoryName").toString(),
                                            it.get("icon").toString()
                                        )
                                    )
                                }


                            }
                            categoryAdapter.asyncListDiffer.submitList(categoryList)

                        }

                        is Resource.Error -> {
                            Log.e(TAG, "categories: ${result.message.toString()}")
                        }

                        else -> {}
                    }
                }
            }
        }
        binding.categoryRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }

        //================= Banner slider == TOP DEALS =================//
//        initBannerSlider()
        homePageAdapter = HomePageAdapter(homePageList)

        homeViewModel.getTopDeals()


        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.topDeals.collect { response ->

                    when (response) {

                        is Resource.Loading -> {
                            showShimmerEffect()
                        }


                        is Resource.Success -> {
                            response.data?.forEach { documentSnapshot ->

//                                if(homePageList.isNotEmpty()){
//                                    homePageList.clear()
//                                }

                                hideShimmerEffect()

//                                val index = documentSnapshot.get("index") as Long?
//
////                                Log.d(TAG, "topDeals index: $index")
//
//                                Log.d(TAG, "topDeals: ${documentSnapshot.data.toString()}")

                                binding.refreshLayout.isRefreshing = false

                                if (homePageList.size < 5) {

                                    when ((documentSnapshot.get("view_type") as Long)) {

                                        0L -> {

//                                            if (sliderModelList.isNotEmpty()) {
//                                                sliderModelList.clear()
//                                            }

                                            val bannerCount =
                                                documentSnapshot.get("banners_count") as Long

                                            for (i in 1..bannerCount) {
                                                sliderModelList.add(
                                                    SliderModel(
                                                        documentSnapshot.get("banner_$i")
                                                            .toString(),
                                                        documentSnapshot.get("banner_" + i + "_background")
                                                            .toString()
                                                    )
                                                )
                                            }

                                            homePageList.add(HomePageModel(0, sliderModelList))

                                        }

                                        1L -> {
                                            homePageList.add(
                                                HomePageModel(
                                                    1,
                                                    documentSnapshot.get("strip_ad_banner")
                                                        .toString(),
                                                    documentSnapshot.get("background_color")
                                                        .toString()
                                                )
                                            )
                                        }

                                        2L -> {
                                            val viewAllProductList =
                                                arrayListOf<WishListModel>()

                                            val horizontalScrollList =
                                                arrayListOf<HorizontalProductScrollModel>()

                                            val productsCount =
                                                documentSnapshot["products_count"] as Long


                                            for (x in 1..productsCount) {

                                                horizontalScrollList.add(
                                                    HorizontalProductScrollModel(
                                                        productID = documentSnapshot["product_id_$x"].toString(),
                                                        productImage = documentSnapshot["product_image_$x"].toString(),
                                                        productName = documentSnapshot.get("product_name_$x").toString(),
                                                        productSubtitle = documentSnapshot["product_subtitle_$x"].toString(),
                                                        productPrice = documentSnapshot["product_price_$x"].toString()
                                                    )
                                                )

//                                                val productNameTest = documentSnapshot.get("product_name_$x").toString()
//
//                                                Log.d(TAG, "onViewCreated: $productNameTest")

                                                viewAllProductList.add(


                                                    WishListModel(
                                                        productID = documentSnapshot["product_id_$x"].toString(),
                                                        productImage = documentSnapshot["product_image_$x"].toString(),
                                                        productName = documentSnapshot.getString("product_name_$x").toString(),
                                                        freeCoupons = documentSnapshot["free_coupons_$x"] as Long,
                                                        averageRating = documentSnapshot["average_rating_$x"].toString(),
                                                        totalRatings = documentSnapshot["total_ratings_$x"] as Long,
                                                        productPrice = documentSnapshot["product_price_$x"].toString(),
                                                        cuttedPrice = documentSnapshot.get("cutted_price_$x").toString(),
                                                        isCOD = documentSnapshot["COD_$x"] as Boolean,
                                                        isInStock = documentSnapshot["in_stock_$x"] as Boolean
                                                    )
                                                )

                                            }

                                            homePageList.add(
                                                HomePageModel(
                                                    type = 2,
                                                    productName = documentSnapshot.get("layout_title")
                                                        .toString(),
                                                    horizontalProductScrollModelList = horizontalScrollList,
                                                    viewAllProductList = viewAllProductList,
                                                    backgroundColor = documentSnapshot.get("background_color")
                                                        .toString()
                                                )
                                            )
                                        }

                                        3L -> {
                                            val productsCount =
                                                documentSnapshot.get("products_count") as Long

                                            for (i in 1..productsCount) {
                                                gridLayoutList.add(
                                                    HorizontalProductScrollModel(
                                                        productID = documentSnapshot.get("product_id_$i")
                                                            .toString(),
                                                        productImage = documentSnapshot.get("product_image_$i")
                                                            .toString(),
                                                        productName = documentSnapshot.get("product_title_$i")
                                                            .toString(),
                                                        productSubtitle = documentSnapshot.get("product_subtitle_$i")
                                                            .toString(),
                                                        productPrice = documentSnapshot.get("product_price_$i")
                                                            .toString()
                                                    )
                                                )
                                            }

                                            homePageList.add(
                                                HomePageModel(
                                                    type = 3,
                                                    productName = documentSnapshot.get("layout_title")
                                                        .toString(),
                                                    horizontalProductScrollModelList = gridLayoutList,
                                                    backgroundColor = documentSnapshot.get("background_color")
                                                        .toString()
                                                )
                                            )
                                        }
                                    }
                                }

                            }



                            homePageAdapter.notifyDataSetChanged()
                        }

                        is Resource.Error -> {
                            Log.e(TAG, "topDeals: ${response.message.toString()}")
                        }

                        else -> {}
                    }


                }
            }
        }



        binding.homePageRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = homePageAdapter
        }

        binding.refreshLayout.setOnRefreshListener {
            binding.refreshLayout.isRefreshing = true
            showShimmerEffect()
            categoryList.clear()
            homePageList.clear()

            homeViewModel.getCategories()
            homeViewModel.getTopDeals()
        }

        //=====================  MY CART ====================== //

        if (signOutViewModel.firebaseAuth.currentUser != null) {
            myCartViewModel.getMyCartListIds()
            notificationViewModel.getNotifications(false)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                myCartViewModel.myCartListIds.collect { response ->
                    when (response) {
                        is Resource.Success -> {

                            myCartListSize = response.data?.get("list_size") as Long


//                            requireActivity().invalidateOptionsMenu()
                            requireActivity().invalidateMenu()


//                            for (i in 0 until listSize) {
//                                myCartListIds.add(response.data["product_id_$i"].toString())
//
//                            }


                        }

                        is Resource.Error -> {
                            Log.e(
                                TAG,
                                "onViewCreated: ${response.message.toString()}"
                            )
                        }

                        else -> {}
                    }
                }
            }
        }

        binding.productsRV.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = wishlistAdapter
        }

        //Search
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.searchProducts.collect { response ->

                    when (response) {

                        is Resource.Loading -> {

                        }


                        is Resource.Success -> {
                            response.data?.documents?.forEach { documentSnapshot ->
                                binding.apply {
                                    homeFragment.visibility = View.GONE
                                    productsRV.visibility = View.VISIBLE
                                }
                                val model = WishListModel(
                                    productID = documentSnapshot.id,
                                    productImage = documentSnapshot["product_image_1"].toString(),
                                    productName = documentSnapshot["product_name"].toString(),
                                    freeCoupons = documentSnapshot["free_coupons"] as Long,
                                    averageRating = documentSnapshot["average_rating"].toString(),
                                    totalRatings = documentSnapshot["total_ratings"] as Long,
                                    productPrice = documentSnapshot["product_price"].toString(),
                                    cuttedPrice = documentSnapshot["cutted_price"].toString(),
                                    isCOD = documentSnapshot["COD"] as Boolean,
                                    isInStock = documentSnapshot["in_stock"] as Boolean
                                )


                                if (!idsList.contains(model.productID!!)) {
                                    productsSearchList.add(model)
                                    idsList.add(model.productID!!)
                                }


                            }.also {

                                wishlistAdapter.asyncListDiffer.submitList(productsSearchList)

//                                Log.d(TAG, "onCreate: ${productsSearchList[0].productName}")

                                if (wishlistAdapter.asyncListDiffer.currentList.isEmpty()) {
                                    binding.apply {
                                        productNotFoundTV.visibility = View.VISIBLE
                                        homeFragment.visibility = View.VISIBLE
                                        productsRV.visibility = View.GONE
                                    }
                                }
                            }
                        }


                        is Resource.Error -> {
                            Log.e(TAG, "topDeals: ${response.message.toString()}")
                        }

                        else -> {}
                    }


                }
            }
        }


        //Notification
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                notificationViewModel.notification.collect { response ->

                    when (response) {

                        is Resource.Loading -> {}

                        is Resource.Success -> {
                            response.data?.let {

                                val listSize = it.get("list_size") as Long?

                                if (listSize != null) {
                                    notificationListSize = listSize
                                }

//                                 unread = 0
                                for (i in 0 until notificationListSize) {

                                    val beenRead = it.get("been_read_$i") as Boolean

                                    if (!beenRead) {
                                        unread++

                                        if (this@HomeFragment::notificationCount.isInitialized) {

                                            if (unread > 0) {

                                                notificationCount.visibility = View.VISIBLE
                                                if (unread < 99) {
                                                    notificationCount.text = unread.toString()
                                                } else {
                                                    notificationCount.text = "99"
                                                }
                                            } else {
                                                notificationCount.visibility = View.INVISIBLE
                                            }
                                        }
                                    }
                                }

                            }.also {
                                requireActivity().invalidateMenu()
                            }
                        }

                        is Resource.Error -> {
                            Log.e(TAG, "onViewCreated: ${response.message.toString()}")
                        }

                        else -> {

                        }
                    }
                }
            }
        }


    }

    private fun showShimmerEffect() {
        binding.apply {
            shimmerLayout.visibility = View.VISIBLE
            categoryRecyclerView.visibility = View.INVISIBLE
            homePageRecyclerView.visibility = View.INVISIBLE
        }
    }

    private fun hideShimmerEffect() {
        binding.apply {
            shimmerLayout.stopShimmer()
            shimmerLayout.visibility = View.GONE
            categoryRecyclerView.visibility = View.VISIBLE
            homePageRecyclerView.visibility = View.VISIBLE

        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: called")
        Log.d(TAG, "onResume: ${homePageList.size}")
//        if (homePageList.size == 10) {
//            homePageList.clear()
//            homeViewModel.getTopDeals()
//        }
    }

    override fun onPause() {
        super.onPause()

        Log.d(TAG, "onPause: called")

        Log.d(TAG, "onPause: ${homePageList.size}")
    }


    override fun onDestroyView() {
        super.onDestroyView()
//        myCartListIds.clear()

        sliderModelList.clear()
        gridLayoutList.clear()
        notificationListSize = 0
        unread = 0
        productsSearchList.clear()
        idsList.clear()
        wishlistAdapter.asyncListDiffer.submitList(null)
        binding.apply {
            homeFragment.visibility = View.VISIBLE
            productsRV.layoutManager?.removeAllViews()
            productsRV.visibility = View.GONE
            productNotFoundTV.visibility = View.GONE
        }

        homePageList.clear()
        _binding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.home_menu, menu)
        menu.getItem(0).isEnabled = signOutViewModel.firebaseAuth.currentUser != null


        if (signOutViewModel.firebaseAuth.currentUser != null) {

            val cartItem = menu.findItem(R.id.action_cart)
            cartItem.setActionView(R.layout.cart_badge_layout)

            cartBadgeCount = cartItem.actionView!!.findViewById(R.id.cart_badge_count) as TextView


            if (myCartListSize == 0L) {
                cartBadgeCount.visibility = View.INVISIBLE

            } else {
                cartBadgeCount.visibility = View.VISIBLE
                if (myCartListSize < 99) {
                    cartBadgeCount.text = myCartListSize.toString()
                } else {
                    cartBadgeCount.text = "99"
                }
            }


            cartItem.actionView!!.setOnClickListener { view: View? ->
                if (findNavController(binding.root).currentDestination?.id == R.id.homeFragment) {
                    findNavController(binding.root)
                        .navigate(HomeFragmentDirections.actionHomeFragmentToMyCartFragment())
                }
            }

            val notificationItem = menu.findItem(R.id.action_notification)
            notificationItem.setActionView(R.layout.badge_notification_layout)


            notificationCount =
                notificationItem.actionView!!.findViewById(R.id.notification_badge_count) as TextView


            if (unread == 0) {
                notificationCount.visibility = View.INVISIBLE

            } else {
                notificationCount.visibility = View.VISIBLE
                if (unread < 99) {
                    notificationCount.text = unread.toString()
                } else {
                    notificationCount.text = "99"
                }
            }

            notificationItem.actionView!!.setOnClickListener { view: View? ->
                if (findNavController(binding.root).currentDestination?.id == R.id.homeFragment) {
                    findNavController(binding.root)
                        .navigate(HomeFragmentDirections.actionHomeFragmentToNotificationFragment())
                }
            }

        }

        val searchManager =
            requireContext().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
        searchView.queryHint = resources.getString(R.string.searchForProducts)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(keyword: String): Boolean {

//                (requireActivity() as MainActivity).actionBarLogo.visibility = View.GONE

                searchQueryKeyword = keyword

//                productsSearchList.clear()
//                idsList.clear()

                if (keyword.isEmpty()) {
                    Snackbar.make(
                        binding.root,
                        "please enter keyword to search",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
//                itemArrayList.clear()
//                this@HomeFragment.keyword = keyword

                val tags = keyword.lowercase().split(" ")

                tags.forEach {
                    homeViewModel.searchForProducts(it)
                }



                return false

            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        searchView.setOnCloseListener {

            if (searchQueryKeyword.isEmpty()) {
                return@setOnCloseListener false
            }

            productsSearchList.clear()
            idsList.clear()
            binding.apply {
                homeFragment.visibility = View.VISIBLE
                productsRV.layoutManager?.removeAllViews()
                productsRV.visibility = View.GONE
                productNotFoundTV.visibility = View.GONE
            }

            false
        }
    }


    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_logout -> {
                if (signOutViewModel.firebaseAuth.currentUser != null) {
                    signOutViewModel.signOut((requireActivity() as MainActivity).googleSignInClient)
                    findNavController(requireView())
                        .navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
                } else {
                    menuItem.isEnabled = false
                }
                true
            }

            R.id.action_notification -> {
                findNavController(requireView()).navigate(HomeFragmentDirections.actionHomeFragmentToNotificationFragment())
                true
            }

            R.id.action_cart -> {
                //            Navigation.findNavController(requireView()).navigate(
                //                    HomeFragmentDirections.actionHomeFragmentToMyCartFragment());
                if (signOutViewModel.firebaseAuth.currentUser == null) {
                    signInSignUpDialog(
                        requireContext(),
                        R.id.homeFragment,
                        layoutInflater,
                        binding.root
                    )
                } else {
                    findNavController(binding.root)
                        .navigate(HomeFragmentDirections.actionHomeFragmentToMyCartFragment())
                }
                true
            }

            else -> false
        }
    }


}