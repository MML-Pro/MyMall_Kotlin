package com.blogspot.mido_mymall.ui.home

import android.annotation.SuppressLint
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
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
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
import com.blogspot.mido_mymall.util.Constants
import com.blogspot.mido_mymall.util.Constants.signInSignUpDialog
import com.blogspot.mido_mymall.util.Resource
import com.blogspot.mido_mymall.util.onItemClick
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
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

    private var unread = 0

    private val horizontalScrollList = arrayListOf<HorizontalProductScrollModel>()

    private val viewAllProductList = arrayListOf<WishListModel>()

    private var adView: AdView? = null
    private var adRequest: AdRequest? = null

    //    private var adViewContainer: FrameLayout? = null
    var stripBannerCounter = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        menuHost = requireActivity()
        menuHost?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.STARTED)

//        adView = binding.bannerAdView

        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


//        val testDeviceIds =
//            listOf("B3EEABB8EE11C2BE770B684D95219ECB", "B3EEABB8EE11C2BE770B684D95219ECB")
//        val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
//        MobileAds.setRequestConfiguration(configuration)

//        ConsentDebugSettings.Builder(requireContext()).addTestDeviceHashedId("B3EEABB8EE11C2BE770B684D95219ECB").build()


        if(signOutViewModel.firebaseAuth.currentUser != null){
            (requireActivity()as MainActivity).signOutItem?.isEnabled = true
        }else {
            (requireActivity()as MainActivity).signOutItem?.isEnabled = false
        }

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

                                    Log.d(
                                        TAG,
                                        "onViewCreated: ${it.get("categoryName").toString()}"
                                    )

                                    val catName: String = when (it.get("categoryName").toString()) {


                                        "Home" -> getString(R.string.home)
                                        "Mobiles" -> getString(R.string.mobiles)
                                        "Electronics" -> getString(R.string.electronics)
                                        "Appliances" -> getString(R.string.appliances)
                                        "Furniture" -> getString(R.string.furniture)
                                        "Fashion" -> getString(R.string.fashion)
                                        "Books" -> getString(R.string.books)
                                        "Sports" -> getString(R.string.sports)
                                        "Toys" -> getString(R.string.toys)
                                        "Wall arts" -> getString(R.string.Wallarts)
                                        "Shoes" -> getString(R.string.shoes)


                                        else -> {
                                            it.get("categoryName").toString()
                                        }
                                    }

                                    categoryList.add(
                                        CategoryModel(
                                            catName,
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
                            val newList = mutableListOf<HomePageModel>()


//                            var stripBannerCounter = 0

                            // 3. تعريف القوائم الفرعية المؤقتة (إذا كانت تُستخدم داخل الحلقة)
                            //    يجب إنشاؤها هنا لضمان عدم مشاركة نفس الكائن بين عناصر مختلفة
                            val sliderModelListTemp = ArrayList<SliderModel>()
                            val horizontalScrollListTemp = ArrayList<HorizontalProductScrollModel>()
                            val viewAllProductListTemp = ArrayList<WishListModel>()
                            val gridLayoutListTemp = ArrayList<HorizontalProductScrollModel>()


                            // 4. الدوران على البيانات وتعبئة القائمة المؤقتة
                            response.data?.forEach { documentSnapshot ->

                                // --- إزالة هذا الشرط للسماح بمعالجة كل البيانات ---
                                // if (homePageList.size < 5) {

                                val viewType = documentSnapshot.getLong("view_type")

                                when (viewType) {

                                    0L -> { // Banner Slider
                                        sliderModelListTemp.clear() // مسح القائمة قبل البدء
                                        val bannerCount = documentSnapshot.getLong("banners_count") ?: 0
                                        for (i in 1..bannerCount) {
                                            val bannerUrl = documentSnapshot.getString("banner_$i")
                                            val bannerBg = documentSnapshot.getString("banner_" + i + "_background")
                                            if (bannerUrl != null) {
                                                sliderModelListTemp.add(SliderModel(bannerUrl, bannerBg ?: "#FFFFFF"))
                                            }
                                        }
                                        if (sliderModelListTemp.isNotEmpty()) {
                                            // إضافة نسخة جديدة من القائمة المؤقتة إلى القائمة الرئيسية المؤقتة
                                            newList.add(HomePageModel(0, ArrayList(sliderModelListTemp)))
                                        }
                                    }

                                    1L -> { // Strip Banner (Image or Ad)
                                        stripBannerCounter++
                                        if (stripBannerCounter % 2 != 0) { // فردي = صورة
                                            Log.d(TAG, "Adding Image Banner at strip count: $stripBannerCounter")
                                            val imageUrl = documentSnapshot.getString("strip_ad_banner")
                                            val bgColor = documentSnapshot.getString("background_color")
                                            if (!imageUrl.isNullOrEmpty()) {
                                                newList.add(HomePageModel(type = 1, Image = imageUrl, backgroundColor = bgColor)) // إضافة إلى newList
                                            } else {
                                                Log.w(TAG, "Image URL missing for strip banner at count $stripBannerCounter, skipping image.")
                                                // يمكنك اختيار إضافة إعلان بديل هنا إذا أردت
                                                // newList.add(HomePageModel(type = 1, forAdFlag = true))
                                            }
                                        } else { // زوجي = اعلان


                                            Log.d(TAG, "Adding Ad Banner at strip count: $stripBannerCounter")
                                            newList.add(HomePageModel(type = 1, forAdFlag = true)) // إضافة إلى newList
                                        }
                                    }

                                    2L -> { // Horizontal Products
                                        horizontalScrollListTemp.clear() // مسح القوائم قبل البدء
                                        viewAllProductListTemp.clear()
                                        val productsCount = documentSnapshot.getLong("products_count") ?: 0
                                        for (x in 1..productsCount) {
                                            // جلب بيانات المنتج بأمان
                                            val productId = documentSnapshot.getString("product_id_$x") ?: ""
                                            val productImage = documentSnapshot.getString("product_image_$x") ?: ""
                                            val productName = documentSnapshot.getString("product_name_$x") ?: ""
                                            val productSubtitle = documentSnapshot.getString("product_subtitle_$x") ?: ""
                                            val productPrice = documentSnapshot.getString("product_price_$x") ?: "0"
                                            val cuttedPrice = documentSnapshot.getString("cutted_price_$x") ?: ""
                                            val freeCoupons = documentSnapshot.getLong("free_coupons_$x") ?: 0L
                                            val avgRating = documentSnapshot.getString("average_rating_$x") ?: "0.0"
                                            val totalRatings = documentSnapshot.getLong("total_ratings_$x") ?: 0L
                                            val isCOD = documentSnapshot.getBoolean("COD_$x") ?: false
                                            val isInStock = documentSnapshot.getBoolean("in_stock_$x") ?: true

                                            horizontalScrollListTemp.add(HorizontalProductScrollModel(productId, productImage, productName, productSubtitle, productPrice))
                                            viewAllProductListTemp.add(WishListModel(productId, productImage, productName, freeCoupons, avgRating, totalRatings, productPrice, cuttedPrice, isCOD, isInStock))
                                        }

                                        if (horizontalScrollListTemp.isNotEmpty()) {
                                            val layoutTitle = documentSnapshot.getString("layout_title") ?: ""
                                            val backgroundColor = documentSnapshot.getString("background_color") ?: "#FFFFFF"
                                            // إضافة نسخ جديدة من القوائم المؤقتة إلى القائمة الرئيسية المؤقتة
                                            newList.add(HomePageModel(
                                                type = 2,
                                                productName = layoutTitle, // قم بترجمة العنوان إذا لزم الأمر هنا
                                                horizontalProductScrollModelList = ArrayList(horizontalScrollListTemp),
                                                viewAllProductList = ArrayList(viewAllProductListTemp),
                                                backgroundColor = backgroundColor
                                            ))
                                        }
                                    }

                                    3L -> { // Grid Products
                                        gridLayoutListTemp.clear() // مسح القائمة قبل البدء
                                        val productsCount = documentSnapshot.getLong("products_count") ?: 0
                                        Log.d(TAG, "Grid Products - productsCount: $productsCount")
                                        for (i in 1..productsCount) {
                                            // جلب بيانات المنتج بأمان
                                            val productId = documentSnapshot.getString("product_id_$i") ?: ""
                                            val productImage = documentSnapshot.getString("product_image_$i") ?: ""
                                            val productName = documentSnapshot.getString("product_title_$i") ?: documentSnapshot.getString("product_name_$i") ?: ""
                                            val productSubtitle = documentSnapshot.getString("product_subtitle_$i") ?: ""
                                            val productPrice = documentSnapshot.getString("product_price_$i") ?: "0"

                                            gridLayoutListTemp.add(HorizontalProductScrollModel(productId, productImage, productName, productSubtitle, productPrice))
                                            Log.d(TAG, "Added Grid Product $i: ${gridLayoutListTemp.lastOrNull()}")
                                        }

                                        if (gridLayoutListTemp.isNotEmpty()) {
                                            val layoutTitle = documentSnapshot.getString("layout_title") ?: ""
                                            val backgroundColor = documentSnapshot.getString("background_color") ?: "#FFFFFF"
                                            // إضافة نسخة جديدة من القائمة المؤقتة إلى القائمة الرئيسية المؤقتة
                                            newList.add(
                                                HomePageModel(
                                                    type = 3,
                                                    productName = layoutTitle, // قم بترجمة العنوان إذا لزم الأمر هنا
                                                    horizontalProductScrollModelList = ArrayList(gridLayoutListTemp),
                                                    backgroundColor = backgroundColor
                                                )
                                            )
                                            Log.d(TAG, "Grid Products added to newList")

                                            // !!! تأكد من إزالة أي استدعاء لـ notifyDataSetChanged من هنا !!!

                                        } else {
                                            Log.e(TAG, "No products found for Grid layout")
                                        }
                                    }
                                    else -> {
                                        Log.w(TAG, "Unknown view_type encountered: $viewType")
                                    }
                                }
                                // --- نهاية الشرط الذي تم إزالته ---
                                // } // نهاية if (homePageList.size < 5)

                            } // نهاية forEach

                            // 5. تحديث القائمة الأصلية للـ Adapter والإخطار مرة واحدة فقط
                            homePageList.clear()
                            homePageList.addAll(newList)
                            homePageAdapter.notifyDataSetChanged() // <<< الإخطار الوحيد والمكان الصحيح
                            Log.d(TAG, "Adapter notified. Final list size: ${homePageList.size}")

                         // نهاية is Resource.Success

//                            response.data?.forEach { documentSnapshot ->
//
////                                if(homePageList.isNotEmpty()){
////                                    homePageList.clear()
////                                }
//
//                                hideShimmerEffect()
//
////                                val index = documentSnapshot.get("index") as Long?
////
//////                                Log.d(TAG, "topDeals index: $index")
////
////                                Log.d(TAG, "topDeals: ${documentSnapshot.data.toString()}")
//
//                                binding.refreshLayout.isRefreshing = false
//
//                                if (homePageList.size < 5) {
//
//                                    when ((documentSnapshot.get("view_type") as Long)) {
//
//                                        0L -> {
//
////                                            if (sliderModelList.isNotEmpty()) {
////                                                sliderModelList.clear()
////                                            }
//
//                                            val bannerCount =
//                                                documentSnapshot.get("banners_count") as Long
//
//                                            for (i in 1..bannerCount) {
//                                                sliderModelList.add(
//                                                    SliderModel(
//                                                        documentSnapshot.get("banner_$i")
//                                                            .toString(),
//                                                        documentSnapshot.get("banner_" + i + "_background")
//                                                            .toString()
//                                                    )
//                                                )
//                                            }
//
//                                            homePageList.add(HomePageModel(0, sliderModelList))
//
//                                        }
//
//                                        1L -> { // Strip Banner (Image or Ad)
//                                            // زيادة العداد لهذا النوع
//                                            stripBannerCounter++
//
//                                            // التحقق إذا كان العداد فرديًا (إعلان) أم زوجيًا (صورة)
//                                            if (stripBannerCounter % 2 != 0) {
//                                                // *** الحالة الفردية: إضافة إعلان ***
//                                                Log.d(TAG, "Adding Ad Banner at strip count: $stripBannerCounter")
//                                                homePageList.add(
//                                                    HomePageModel(
//                                                        type = 1,
//                                                        forAdFlag = true // استخدام الكونستركتور الخاص بالإعلان
//                                                    )
//                                                )
//                                            } else {
//                                                // *** الحالة الزوجية: إضافة صورة من Firebase ***
//                                                Log.d(TAG, "Adding Image Banner at strip count: $stripBannerCounter")
//                                                val imageUrl = documentSnapshot.getString("strip_ad_banner")
//                                                val bgColor = documentSnapshot.getString("background_color")
//
//                                                // تأكد من وجود رابط للصورة قبل إضافتها
//                                                if (!imageUrl.isNullOrEmpty()) {
//                                                    homePageList.add(
//                                                        HomePageModel(
//                                                            type = 1,
//                                                            Image = imageUrl, // استخدام الكونستركتور الخاص بالصورة
//                                                            backgroundColor = bgColor // تمرير لون الخلفية (قد يكون null)
//                                                            // isAd ستكون false بشكل افتراضي هنا
//                                                        )
//                                                    )
//                                                } else {
//                                                    // اختياري: ماذا تفعل إذا لم يكن هناك رابط للصورة؟
//                                                    // يمكنك إضافة إعلان بدلاً من ذلك، أو تخطي هذا العنصر، أو إضافة صورة placeholder
//                                                    Log.w(TAG, "Image URL missing for strip banner at count $stripBannerCounter, potentially skipping.")
//                                                    // مثال: إضافة إعلان كبديل
//                                                    // newList.add(HomePageModel(type = 1, forAdFlag = true))
//                                                    // أو تخطي:
//                                                    // stripBannerCounter-- // إذا أردت أن يكون العنصر التالي هو الصورة
//                                                }
//                                            }
//                                        }
//
//                                        2L -> {
//
//
//                                            val productsCount =
//                                                documentSnapshot["products_count"] as Long
//
//
//                                            for (x in 1..productsCount) {
//
//                                                Log.d(
//                                                    TAG,
//                                                    "horizontal Products List: $horizontalScrollList"
//                                                )
//
//                                                horizontalScrollList.add(
//                                                    HorizontalProductScrollModel(
//                                                        productID = documentSnapshot["product_id_$x"].toString(),
//                                                        productImage = documentSnapshot["product_image_$x"].toString(),
//                                                        productName = documentSnapshot.get("product_name_$x")
//                                                            .toString(),
//                                                        productSubtitle = documentSnapshot["product_subtitle_$x"].toString(),
//                                                        productPrice = documentSnapshot["product_price_$x"].toString()
//                                                    )
//                                                )
//
////                                                val productNameTest = documentSnapshot.get("product_name_$x").toString()
////
////                                                Log.d(TAG, "onViewCreated: $productNameTest")
//
//                                                viewAllProductList.add(
//
//
//                                                    WishListModel(
//                                                        productID = documentSnapshot["product_id_$x"].toString(),
//                                                        productImage = documentSnapshot["product_image_$x"].toString(),
//                                                        productName = documentSnapshot.getString("product_name_$x")
//                                                            .toString(),
//                                                        freeCoupons = documentSnapshot["free_coupons_$x"] as Long,
//                                                        averageRating = documentSnapshot["average_rating_$x"].toString(),
//                                                        totalRatings = documentSnapshot["total_ratings_$x"] as Long,
//                                                        productPrice = documentSnapshot["product_price_$x"].toString(),
//                                                        cuttedPrice = documentSnapshot.get("cutted_price_$x")
//                                                            .toString(),
//                                                        isCOD = documentSnapshot["COD_$x"] as Boolean,
//                                                        isInStock = documentSnapshot["in_stock_$x"] as Boolean
//                                                    )
//                                                )
//
//                                            }
//
//                                            val layoutTitle =
//                                                if (documentSnapshot.get("layout_title").toString()
//                                                        .equals("Best selling products")
//                                                ) {
//                                                    getString(R.string.best_selling_products)
//                                                } else {
//                                                    documentSnapshot.get("layout_title").toString()
//                                                }
//
//                                            Log.d(TAG, "onViewCreated: layout title $layoutTitle")
//
//                                            homePageList.add(
//                                                HomePageModel(
//                                                    type = 2,
//                                                    productName = layoutTitle,
//                                                    horizontalProductScrollModelList = horizontalScrollList,
//                                                    viewAllProductList = viewAllProductList,
//                                                    backgroundColor = documentSnapshot.get("background_color")
//                                                        .toString()
//                                                )
//                                            )
//                                        }
//
//                                        3L -> {
//                                            val productsCount =
//                                                documentSnapshot.get("products_count") as? Long ?: 0
//                                            Log.d(
//                                                TAG,
//                                                "Grid Products - productsCount: $productsCount"
//                                            )
//
//                                            if (productsCount > 0) {
//                                                for (i in 1..productsCount) {
//                                                    gridLayoutList.add(
//                                                        HorizontalProductScrollModel(
//                                                            productID = documentSnapshot.get("product_id_$i")
//                                                                ?.toString() ?: "",
//                                                            productImage = documentSnapshot.get("product_image_$i")
//                                                                ?.toString() ?: "",
//                                                            productName = documentSnapshot.get("product_title_$i")
//                                                                ?.toString() ?: "",
//                                                            productSubtitle = documentSnapshot.get("product_subtitle_$i")
//                                                                ?.toString() ?: "",
//                                                            productPrice = documentSnapshot.get("product_price_$i")
//                                                                ?.toString() ?: "0"
//                                                        )
//                                                    )
//                                                    Log.d(
//                                                        TAG,
//                                                        "Added Grid Product $i: ${gridLayoutList.last()}"
//                                                    )
//                                                }
//
//                                                val layoutTitle =
//                                                    if (documentSnapshot.get("layout_title")
//                                                            .toString() == "Deals of the Day"
//                                                    ) {
//                                                        getString(R.string.deals_of_the_day)
//                                                    } else {
//                                                        documentSnapshot.get("layout_title")
//                                                            .toString()
//                                                    }
//                                                Log.d(TAG, "Grid Layout Title: $layoutTitle")
//
//                                                homePageList.add(
//                                                    HomePageModel(
//                                                        type = 3,
//                                                        productName = layoutTitle,
//                                                        horizontalProductScrollModelList = gridLayoutList,
//                                                        backgroundColor = documentSnapshot.get("background_color")
//                                                            ?.toString() ?: "#FFFFFF"
//                                                    )
//                                                )
//                                                Log.d(
//                                                    TAG,
//                                                    "Grid Products added to homePageList, size: ${homePageList.size}"
//                                                )
//
//                                                // تحديث واجهة المستخدم
//                                                binding.homePageRecyclerView.post {
//                                                    homePageAdapter.notifyDataSetChanged()
//                                                    Log.d(TAG, "Adapter notified for Grid Products")
//                                                }
//                                            } else {
//                                                Log.e(TAG, "No products found for Grid layout")
//                                            }
//                                        }
//
////                                        3L -> {
////                                            val productsCount =
////                                                documentSnapshot.get("products_count") as? Long ?: 0
////
////                                            Log.d(TAG, "Grid Products List: $gridLayoutList")
////                                            Log.d(
////                                                TAG,
////                                                "Grid Products List size: ${gridLayoutList.size}"
////                                            )
////
////                                            if (productsCount > 0) {
////                                                for (i in 1..productsCount) {
////                                                    gridLayoutList.add(
////                                                        HorizontalProductScrollModel(
////                                                            productID = documentSnapshot.get("product_id_$i")
////                                                                .toString(),
////                                                            productImage = documentSnapshot.get("product_image_$i")
////                                                                .toString(),
////                                                            productName = documentSnapshot.get("product_title_$i")
////                                                                .toString(),
////                                                            productSubtitle = documentSnapshot.get("product_subtitle_$i")
////                                                                .toString(),
////                                                            productPrice = documentSnapshot.get("product_price_$i")
////                                                                .toString()
////                                                        )
////                                                    )
////                                                }
////
////                                                val layoutTitle =
////                                                    if (documentSnapshot.get("layout_title")
////                                                            .toString() == "Deals of the Day"
////                                                    ) {
////                                                        getString(R.string.deals_of_the_day)
////                                                    } else {
////                                                        documentSnapshot.get("layout_title")
////                                                            .toString()
////                                                    }
////
////                                                Log.d(
////                                                    TAG,
////                                                    "onViewCreated: layout title $layoutTitle"
////                                                )
////
////                                                homePageList.add(
////                                                    HomePageModel(
////                                                        type = 3,
////                                                        productName = layoutTitle,
////                                                        horizontalProductScrollModelList = gridLayoutList,
////                                                        backgroundColor = documentSnapshot.get("background_color")
////                                                            .toString()
////                                                    )
////                                                ).also {
////
////                                                }
////                                            } else {
////                                                Log.e(TAG, "No products found for Grid layout")
////                                            }
////                                        }
//                                    }
//                                }
//
//                            }
//
//
//
//                            homePageAdapter.notifyDataSetChanged()
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

            setOnTouchListener { view, event ->
                // Enable touch events propagation to the parent RecyclerView
                view.parent.requestDisallowInterceptTouchEvent(false)
                false
            }
        }

        binding.refreshLayout.setOnRefreshListener {
            binding.refreshLayout.isRefreshing = true
            showShimmerEffect()
            categoryList.clear()
//            homePageList.clear()

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

            onItemClick { _, pos, _ ->
                // 1. احصل على NavController بالطريقة الصحيحة (باستخدام دالة الامتداد للـ Fragment)
                val navController = findNavController()

                // 2. قم بإنشاء الـ arguments باستخدام Safe Args Directions كالمعتاد
                val direction = HomeFragmentDirections
                    .actionHomeFragmentToProductDetailsFragment(
                        productID = wishlistAdapter.asyncListDiffer.currentList[pos].productID.toString()
                    )
                val args = direction.arguments // نحتاج الـ arguments بشكل منفصل
                val actionId = direction.actionId // نحتاج الـ action ID بشكل منفصل

                // 3. قم ببناء NavOptions برمجيًا لتحديد سلوك popUpToInclusive
                val navOptions = navOptions { // استخدام Kotlin DSL builder
                    popUpTo(R.id.homeFragment) { // حدد الـ ID الخاص بالـ Fragment الذي تريد العودة إليه وإزالته
                        inclusive =
                            true // اجعل الإزالة شاملة (نفس مفعول app:popUpToInclusive="true")
                    }
                    // يمكنك أيضاً تحديد الـ Animations هنا برمجيًا إذا أردت،
                    // ولكن عادةً ما تُستخدم الـ Animations المعرفة في الـ action بالـ XML تلقائيًا.
                    // anim {
                    //    enter = R.anim.slide_in_right
                    //    exit = R.anim.slide_out_left
                    //    popEnter = R.anim.slide_in_left
                    //    popExit = R.anim.slide_out_right
                    // }
                }

                // 4. استدعِ دالة navigate مع الـ action ID والـ arguments والـ NavOptions
                // استخدم النسخة (overload) من navigate التي تقبل هذه المعاملات الثلاثة
                try {
                    navController.navigate(actionId, args, navOptions)
                } catch (e: Exception) {
                    // قد يحدث خطأ إذا كان ID غير صحيح أو هناك مشكلة أخرى
                    Log.e("HomeFragment", "Navigation failed: ${e.message}")
                }
            }
        }

        //Search
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.searchProducts.collect { response ->

                    when (response) {

                        is Resource.Loading -> {
                            showShimmerEffect()
                        }


                        is Resource.Success -> {
                            response.data?.documents?.forEach { documentSnapshot ->
                                binding.apply {
                                    homeFragment.visibility = View.GONE
                                    productsRV.visibility = View.VISIBLE
                                }

                                if (binding.productNotFoundTV.isVisible) {
                                    binding.productNotFoundTV.visibility = View.GONE
                                }

                                hideShimmerEffect()
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
                                        homeFragment.visibility = View.GONE
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

                                    val beenRead: Boolean =
                                        (it.get("been_read_$i") ?: false) as Boolean

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
        if ((requireActivity() as MainActivity).getToolBar().title != null
            && (requireActivity() as MainActivity).getToolBar().title.isNotEmpty()
        ) {
            (requireActivity() as MainActivity).apply {
                supportActionBar?.title = ""
                getToolBar().title = ""
            }
        }
    }

    override fun onPause() {
        super.onPause()

        Log.d(TAG, "onPause: called")

        Log.d(TAG, "onPause: ${homePageList.size}")
    }


    override fun onDestroyView() {
        super.onDestroyView()
//        myCartListIds.clear()

        adView?.destroy()
        binding.adViewContainer.apply {
            removeAllViews()
            visibility = View.GONE
        }
//        adViewContainer?.removeAllViews()

        viewAllProductList.clear()
        horizontalScrollList.clear()
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
        menu[0].isEnabled = signOutViewModel.firebaseAuth.currentUser != null


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
                if (findNavController().currentDestination?.id == R.id.homeFragment) {
                    findNavController()
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
                if (findNavController().currentDestination?.id == R.id.homeFragment) {
                    findNavController()
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
//            override fun onQueryTextSubmit(keyword: String): Boolean {
//
////                (requireActivity() as MainActivity).actionBarLogo.visibility = View.GONE
//
//                searchQueryKeyword = keyword
//
////                productsSearchList.clear()
////                idsList.clear()
//
//                if (keyword.isEmpty()) {
//                    Snackbar.make(
//                        binding.root,
//                        "please enter keyword to search",
//                        Snackbar.LENGTH_SHORT
//                    ).show()
//                }
////                itemArrayList.clear()
////                this@HomeFragment.keyword = keyword
//
//                val tags = keyword.lowercase().split(" ")
//
//                tags.forEach {
//                    homeViewModel.searchForProducts(it)
//                }
//
//
//
//                return false
//
//            }

            override fun onQueryTextSubmit(keyword: String): Boolean {
                searchQueryKeyword = keyword
                if (keyword.isEmpty()) {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.please_enter_keyword_to_search), Snackbar.LENGTH_SHORT
                    ).show()
                    return false // أو true
                }

                val lowerKeyword = keyword.lowercase()
                val searchTerms =
                    mutableSetOf<String>() // استخدم Set لتجنب تكرار نفس الكلمة إذا كانت كلمة البحث واحدة

                // أضف العبارة الكاملة
                searchTerms.add(lowerKeyword)

                // أضف الكلمات المنفصلة
                lowerKeyword.split(" ").forEach { word ->
                    if (word.isNotEmpty()) {
                        searchTerms.add(word)
                    }
                }

                // قم بتحويل الـ Set إلى List وأرسله للـ ViewModel
                Log.d("HomeFragment", "Searching for terms: ${searchTerms.toList()}")
                homeViewModel.searchForProducts(searchTerms.toList()) // <--- استدعاء واحد بقائمة

                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        searchView.setOnCloseListener {

            if (searchQueryKeyword.isEmpty()) {
                return@setOnCloseListener false
            }

            homeViewModel.resetSearchState()
            productsSearchList.clear()
            idsList.clear()
            binding.apply {
                homeFragment.visibility = View.VISIBLE
                productsRV.layoutManager?.removeAllViews()
                productsRV.visibility = View.GONE
                productNotFoundTV.visibility = View.GONE
            }
            homeViewModel.getCategories()
            homeViewModel.getTopDeals()

            false
        }
    }


    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_logout -> {
                if (signOutViewModel.firebaseAuth.currentUser != null) {
                    (requireActivity()as MainActivity).destroyAdAfterLogOut()
                    signOutViewModel.signOut((requireActivity() as MainActivity).googleSignInClient)
                    findNavController()
                        .navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
                } else {
                    menuItem.isEnabled = false
                }
                true
            }

            R.id.action_notification -> {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToNotificationFragment())
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
                    findNavController()
                        .navigate(HomeFragmentDirections.actionHomeFragmentToMyCartFragment())
                }
                true
            }

            else -> false
        }
    }

//     fun passAdViewContainer(adView: AdView) {
//        this.adView = adView
//    }
//
//private fun requestHomeBanner() {
//
//
//    adRequest = Constants.callAndBuildAdRequest()
//    adView?.adListener = object : AdListener() {
//
//        override fun onAdFailedToLoad(adError: LoadAdError) {
//            Log.e(TAG, "onAdFailedToLoad: ${adError.cause.toString()}")
//            Log.e(TAG, "onAdFailedToLoad: ${adError.responseInfo.toString()}")
//        }
//
//    }
//    adRequest?.let { adView?.loadAd(it) }
//
//}


}