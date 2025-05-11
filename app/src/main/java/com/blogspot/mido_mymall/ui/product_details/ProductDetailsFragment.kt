package com.blogspot.mido_mymall.ui.product_details

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.children
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.blogspot.mido_mymall.R
import com.blogspot.mido_mymall.databinding.FragmentProductDetailsBinding
import com.blogspot.mido_mymall.domain.models.CartItemModel
import com.blogspot.mido_mymall.domain.models.ProductSpecsModel
import com.blogspot.mido_mymall.domain.models.WishListModel
import com.blogspot.mido_mymall.ui.MainActivity
import com.blogspot.mido_mymall.ui.credentials.SignOutViewModel
import com.blogspot.mido_mymall.ui.my_address.MyAddressViewModel
import com.blogspot.mido_mymall.util.Constants
import com.blogspot.mido_mymall.util.Resource
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductDetailsFragment : Fragment(), MenuProvider {

    companion object {
        private const val TAG = "ProductDetailsFragment"

        var ALREADY_ADDED_TO_WISH_LIST = false

//        var addToWishListFAB: FloatingActionButton? = null

        var ALREADY_ADDED_TO_CART_LIST = false
    }

    private var _binding: FragmentProductDetailsBinding? = null
    private val binding get() = _binding!!

    private val productsImages = arrayListOf<String>()

    private lateinit var productDetailsAdapter: ProductDetailsAdapter

    private var productDescBody: String? = null
    private var productOtherDetails: String? = null
    private val productSpecsModelList = arrayListOf<ProductSpecsModel>()

    private val productDetailsViewModel by viewModels<ProductDetailsViewModel>()
    private val signOutViewModel by viewModels<SignOutViewModel>()
//    private val wishlistViewModel by viewModels<MyWishlistViewModel>()

    private lateinit var tabLayoutMediator: TabLayoutMediator

    private lateinit var productDetailsViewPagerTabLayoutMediator: TabLayoutMediator

    private val args by navArgs<ProductDetailsFragmentArgs>()


    private val wishListIds = arrayListOf<String>()
    private val wishListModelList = arrayListOf<WishListModel>()

    private lateinit var productId: String
    private lateinit var documentSnapshot: DocumentSnapshot

    private val myRatingsIds = arrayListOf<String>()
    private val myRatings = arrayListOf<Long>()

    private var initialRating: Long = 0
    private var starPosition: Long = 0

    private var myCartListIds = arrayListOf<String>()
    private var cartItemModelList = arrayListOf<CartItemModel>()

    private var menuHost: MenuHost? = null

//    private val myRewardsViewModel by viewModels<MyRewardsViewModel>()

    private var productPriceValue: String = ""

    private var fromCart = false

    var productPrice = ""

    private val myAddressViewModel by viewModels<MyAddressViewModel>()

    private var myAddressListSize: Long = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailsBinding.inflate(inflater)


        menuHost = requireActivity()
        menuHost?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.CREATED)


//        productDescBody = "dkjhbdjhbldhbdljhgajhgpUYGPUWEBJEHWBGLJHEBGJEHEBKJHBEHJBEJHEBLJEBLJHBELJB"
//
//        productOtherDetails = "other details"

//        productSpecsModelList.add(ProductSpecsModel(0,"General"))
//        productSpecsModelList.add(ProductSpecsModel(1,"RAM","4GB"))
//        productSpecsModelList.add(ProductSpecsModel(1,"RAM","8GB"))
//        productSpecsModelList.add(ProductSpecsModel(1,"RAM","16GB"))
//        productSpecsModelList.add(ProductSpecsModel(1,"RAM","24GB"))
//        productSpecsModelList.add(ProductSpecsModel(1,"RAM","32GB"))
//        productSpecsModelList.add(ProductSpecsModel(1,"RAM","156GB"))
//        productSpecsModelList.add(ProductSpecsModel(0,"Display"))
//        productSpecsModelList.add(ProductSpecsModel(1,"RAM","454GB"))
//        productSpecsModelList.add(ProductSpecsModel(1,"RAM","853GB"))
//        productSpecsModelList.add(ProductSpecsModel(1,"RAM","163GB"))
//        productSpecsModelList.add(ProductSpecsModel(1,"RAM","245GB"))
//        productSpecsModelList.add(ProductSpecsModel(1,"RAM","325GB"))
//        productSpecsModelList.add(ProductSpecsModel(1,"RAM","1556GB"))
//        productSpecsModelList.add(ProductSpecsModel(0,"General"))
//        productSpecsModelList.add(ProductSpecsModel(1,"RAM","44GB"))
//        productSpecsModelList.add(ProductSpecsModel(1,"RAM","85GB"))
//        productSpecsModelList.add(ProductSpecsModel(1,"RAM","165GB"))
//        productSpecsModelList.add(ProductSpecsModel(1,"RAM","245GB"))
//        productSpecsModelList.add(ProductSpecsModel(1,"RAM","352GB"))
//        productSpecsModelList.add(ProductSpecsModel(1,"RAM","156GB"))
//        productSpecsModelList.add(ProductSpecsModel(0,"Display"))
//        productSpecsModelList.add(ProductSpecsModel(1,"RAM","45GB"))
//        productSpecsModelList.add(ProductSpecsModel(1,"RAM","85GB"))
//        productSpecsModelList.add(ProductSpecsModel(1,"RAM","156GB"))
//        productSpecsModelList.add(ProductSpecsModel(1,"RAM","245GB"))
//        productSpecsModelList.add(ProductSpecsModel(1,"RAM","325GB"))
//        productSpecsModelList.add(ProductSpecsModel(1,"RAM","1565GB"))

        tabLayoutMediator = TabLayoutMediator(
            binding.productImageViewPager.viewPagerIndicator,
            binding.productImageViewPager.productImagesViewPager
        ) { tab: TabLayout.Tab?, position: Int ->

            container?.clipChildren = false

        }

        productId = args.productID

        productDetailsViewPagerTabLayoutMediator = TabLayoutMediator(
            binding.productDescriptionLayout.productDetailsTabLayout,
            binding.productDescriptionLayout.productDetailsViewPager
        ) { tab: TabLayout.Tab?, position: Int ->
            Log.d(TAG, "productDetailsViewPagerTabLayoutMediator: ${tab?.text}")
            when (position) {
                0 -> {
                    tab?.text = resources.getString(R.string.description)
                }

                1 -> {
                    tab?.text = resources.getString(R.string.specifications)
                }

                2 -> {
                    tab?.text = resources.getString(R.string.other_details)
                }
            }
        }


//        addToWishListFAB = binding.productImageViewPager.addToWishListFAB

//        productDetailsAdapter.notifyDataSetChanged()

        return binding.root
    }


    fun setStyleForTab(tab: TabLayout.Tab, style: Int) {
        tab.view.children.find { it is TextView }?.let { tv ->
            (tv as TextView).post {
                tv.setTypeface(null, style)
            }
        }
    }

    @SuppressLint("StringFormatMatches")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        productsImages.add(R.drawable.image2)
//        productsImages.add(R.drawable.banner)
//        productsImages.add(R.drawable.stripadd)
//        productsImages.add(R.drawable.image5)

        // ============== Products Images ================= //

        productDetailsViewModel.getProductDetails(productId)
        if (signOutViewModel.firebaseAuth.currentUser != null) {
            myAddressViewModel.getAddresses()
        }

        lifecycleScope.launch {
            // استخدام STARTED مناسب لجمع حالة الواجهة
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                myAddressViewModel.myAddresses.collect { response ->
                    when (response) {
                        is Resource.Loading -> {

                        }

                        is Resource.Success<*> -> {
                            response.data?.let {
                                myAddressListSize = it["list_size"] as? Long ?: 0L
                            }
                        }

                        is Resource.Error<*> -> {
                            return@collect
                        }

                        else -> {}
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                productDetailsViewModel.productDetails.collect { response ->
                    when (response) {

                        is Resource.Loading -> { // progress bar
                        }

                        is Resource.Success -> {
                            val documentSnapshot = response.data

                            if (documentSnapshot != null) {

                                this@ProductDetailsFragment.documentSnapshot = documentSnapshot

                                for (index in 1..(documentSnapshot.get("product_images_count") as Long)) {

                                    productsImages.add(
                                        documentSnapshot.get("product_image_$index").toString()
                                    )

                                }
                                val productImagesAdapter = ProductImagesAdapter(productsImages)
//                                productImagesAdapter.notifyDataSetChanged()

                                binding.productImageViewPager.productImagesViewPager.clipToPadding =
                                    false
                                binding.productImageViewPager.productImagesViewPager.clipChildren =
                                    false
                                binding.productImageViewPager.productImagesViewPager.adapter =
                                    productImagesAdapter


                                if (!tabLayoutMediator.isAttached) {
                                    tabLayoutMediator.attach()
                                }



                                binding.apply {

                                    val totalRatings = documentSnapshot["total_ratings"] as Long

                                    productImageViewPager.productName.text =
                                        documentSnapshot["product_name"].toString()

                                    productImageViewPager.averageRatingMiniViewTV.text =
                                        documentSnapshot["average_rating"].toString()

                                    productImageViewPager.productTotalRatingMiniViewTV.text =
                                        resources.getString(
                                            R.string.total_ratings,
                                            totalRatings
                                        )

                                    productImageViewPager.productPrice.text =
                                        getString(
                                            R.string.egp_price,
                                            documentSnapshot["product_price"]
                                        )

                                    productPriceValue =
                                        documentSnapshot["product_price"].toString()

                                    productImageViewPager.productCuttedPrice.text =

                                        getString(
                                            R.string.egp_price,
                                            documentSnapshot["cutted_price"]
                                        )

                                    if (documentSnapshot.get("COD") as Boolean) {
                                        productImageViewPager.apply {
                                            codImageView.visibility = View.VISIBLE
                                            codTextView.visibility = View.VISIBLE
                                            codIndicatorTV.visibility = View.VISIBLE
                                        }
                                    } else {
                                        productImageViewPager.apply {
                                            codImageView.visibility = View.GONE
                                            codTextView.visibility = View.GONE
                                            codIndicatorTV.visibility = View.GONE
                                        }
                                    }

                                    val freeCoupons = documentSnapshot["free_coupons"] as Long

                                    if (freeCoupons > 0) {

                                        rewardWithProductLayout.rewardTitle.text =
                                            "$freeCoupons ${documentSnapshot["free_coupone_title"].toString()}"

                                        rewardWithProductLayout.rewardBody.text =
                                            documentSnapshot["free_coupone_body"].toString()
                                    } else {
                                        rewardWithProductLayout.root.visibility = View.GONE
                                    }

                                    if (documentSnapshot["use_tab_layout"] as Boolean) {

                                        productDescriptionLayout.root.visibility = View.VISIBLE
                                        productDetailsOnly.root.visibility = View.GONE


                                        productDescBody =
                                            documentSnapshot["product_description"].toString()
                                        productOtherDetails =
                                            documentSnapshot["product_other_details"].toString()


                                        for (x in 1..documentSnapshot["total_specs_titles"] as Long) {
                                            productSpecsModelList.add(
                                                ProductSpecsModel(
                                                    0,
                                                    documentSnapshot["specs_title_$x"].toString()
                                                )
                                            )
                                            for (y in 1..documentSnapshot["specs_title_" + x + "_total_fields"] as Long) {
//
//                                    Log.e(TAG, "test " + documentSnapshot.get("specs_title_" + x + "_total_field_" + y + "_name"));
//                                    Log.e(TAG, "test " + documentSnapshot.get("specs_title_" + x + "_total_field_" + y + "_value"));

                                                val englishFieldName =
                                                    documentSnapshot["specs_title_" + x + "_total_field_" + y + "_name"].toString()
                                                val fieldValue =
                                                    documentSnapshot["specs_title_" + x + "_total_field_" + y + "_value"].toString()

                                                val translatedFieldName =
                                                    getTranslatedSpecFieldName(
                                                        requireContext(),
                                                        englishFieldName
                                                    )


                                                productSpecsModelList.add(
                                                    ProductSpecsModel(
                                                        1,
                                                        featureName = translatedFieldName,
                                                        featureValue = fieldValue
                                                    )
                                                )
                                            }
                                        }


                                        productDetailsAdapter = ProductDetailsAdapter(
                                            childFragmentManager,
                                            requireActivity().lifecycle,
                                            productDescBody!!,
                                            productOtherDetails!!,
                                            productSpecsModelList
                                        )

                                        binding.productDescriptionLayout.productDetailsViewPager.adapter =
                                            productDetailsAdapter

                                        if (!productDetailsViewPagerTabLayoutMediator.isAttached) {
                                            productDetailsViewPagerTabLayoutMediator.attach()
                                        }

                                    } else {
                                        productDescriptionLayout.root.visibility = View.GONE
                                        productDetailsOnly.root.visibility = View.VISIBLE
                                        productDetailsOnly.productDetailsBody.text =
                                            documentSnapshot["product_description"].toString()
                                    }

                                    ratingsLayout.averageRatingTV.text =
                                        documentSnapshot["average_rating"].toString()
                                    ratingsLayout.secondTotalRatingsTV.text =
                                        if (totalRatings == 1L) {
                                            getString(R.string.one_rate, totalRatings)
                                        } else {
                                            getString(
                                                R.string.ratings_count,
                                                totalRatings
                                            )
                                        }


                                    for (i in 0 until 5) {
                                        val rating =
                                            binding.ratingsLayout.ratingNumbersContainer.getChildAt(
                                                i
                                            ) as TextView
                                        rating.text =
                                            (documentSnapshot[(5 - i).toString() + "_star"] as Long).toString()
                                    }

                                    binding.ratingsLayout.totalRatingsFigure.text =
                                        (documentSnapshot["total_ratings"] as Long).toString()

                                    for (i in 0 until 5) {
                                        val progressBar =
                                            binding.ratingsLayout.ratingProgressBarContainer.getChildAt(
                                                i
                                            ) as ProgressBar

                                        val maxProgress =
                                            (Integer.valueOf((documentSnapshot["total_ratings"] as Long).toString()))
                                        //                            Log.d(TAG, "total ratings test: " + (long) documentSnapshot.get("total_ratings"));
                                        progressBar.max = maxProgress
                                        progressBar.progress =
                                            (documentSnapshot[(5 - i).toString() + "_star"] as Long).toString()
                                                .toInt()
                                    }

                                    if (signOutViewModel.firebaseAuth.currentUser != null) {

                                        if (wishListIds.isEmpty()) {
                                            productDetailsViewModel.getWishListIds()
                                        }

                                        if (myRatingsIds.isEmpty()) {
                                            productDetailsViewModel.getRatings()
                                        }

                                        if (myCartListIds.isEmpty()) {
                                            productDetailsViewModel.getMyCartList()
                                        }

//                                        myRewardsViewModel.getRewards()


                                    } else {
                                        Constants.signInSignUpDialog(
                                            requireContext(),
                                            R.id.productDetailsFragment,
                                            layoutInflater,
                                            binding.root
                                        )
                                    }
                                }

                                if (documentSnapshot.get("in_stock") as Boolean) {
                                    binding.addToCartButton.setOnClickListener {
                                        if (signOutViewModel.firebaseAuth.currentUser == null) {
                                            Constants.signInSignUpDialog(
                                                requireContext(),
                                                R.id.productDetailsFragment,
                                                layoutInflater,
                                                binding.root
                                            )
                                        } else {
                                            if (ALREADY_ADDED_TO_CART_LIST) {

//                    ALREADY_ADDED_TO_CART_LIST = false


                                                val index = myCartListIds.indexOf(productId)

                                                productDetailsViewModel.removeFromCartList(
                                                    myCartListIds, index
                                                )

//
//                                                Toast.makeText(
//                                                    requireContext(),
//                                                    "Already added to cart",
//                                                    Toast.LENGTH_SHORT
//                                                ).show()

                                            } else {

                                                productDetailsViewModel.saveCartListIds(
                                                    productId,
                                                    myCartListIds.size
                                                )

                                            }
                                        }
                                    }

                                } else {
                                    binding.buyNowButton.visibility = View.GONE

                                    val outOfStock =
                                        binding.addToCartButton.getChildAt(0) as TextView
                                    outOfStock.text = getString(R.string.out_of_stock)
                                    outOfStock.setTextColor(resources.getColor(R.color.couponRed))
                                    outOfStock.setCompoundDrawables(null, null, null, null)
                                    binding.linearLayout7.weightSum = 1F
                                }

                            }


                            //FOR THE BUY NOW DIRECTLY

                            productPrice = documentSnapshot?.get("product_price").toString()
                        }

                        is Resource.Error -> {
                            Log.e(TAG, "productDetails: ${response.message.toString()}")

                        }

                        else -> {}
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                productDetailsViewModel.removeCartListState.collect {

                    if (it is Resource.Success) {
                        ALREADY_ADDED_TO_CART_LIST = false

                        requireActivity().invalidateMenu()

                        updateAddToCartButtonAppearance(
                            "ADD TO CART",
                            textSizeRes = resources.getDimensionPixelSize(R.dimen._16ssp)
                                .toFloat(),
                            textColorRes = R.color.colorPrimary,
                            drawableRes = R.drawable.cart_white
                        )


                    } else if (it is Resource.Error) {
                        Log.e(TAG, "onViewCreated: ${it.message.toString()}")
                    }

                }
            }
        }


        binding.productDescriptionLayout.productDetailsTabLayout.addOnTabSelectedListener(object :
            OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                Log.d(TAG, "onTabSelected: ${tab.position}")
                binding.productDescriptionLayout.productDetailsViewPager.currentItem =
                    tab.position

                setStyleForTab(tab, Typeface.BOLD)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                setStyleForTab(tab, Typeface.NORMAL)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })


        binding.buyNowButton.setOnClickListener {
            // التحقق من تسجيل الدخول
            if (signOutViewModel.firebaseAuth.currentUser != null) {

                // التحقق من أن بيانات المنتج الحالي متاحة قبل المتابعة
                if (productId.isNotEmpty() /* && باقي الحقول الضرورية */) {

                    // 1. إنشاء كائن CartItemModel للمنتج الحالي فقط
                    val buyNowItem = CartItemModel(
                        productId = productId,
                        productImage = documentSnapshot["product_image_1"].toString(),
                        productName = documentSnapshot["product_name"].toString(),
                        freeCoupons = documentSnapshot["free_coupons"] as Long,
                        productPrice = documentSnapshot["product_price"].toString(),
                        cuttedPrice = documentSnapshot["cutted_price"].toString(),
                        productQuantity = 1,
                        maxQuantity = documentSnapshot["max_quantity"] as Long,
                        stockQuantity = documentSnapshot["stock_quantity"] as Long,
                        offersApply = documentSnapshot["offers_applied"] as Long,
                        couponsApplied = 0,
                        inStock = documentSnapshot["in_stock"] as Boolean,
                        qtyIDs = arrayListOf(),
                        selectedCouponId = null,
                        discountedPrice = null,
                        productRating = 0
                    )

                    // 2. إنشاء قوائم جديدة تحتوي فقط على هذا العنصر
                    val singleItemList = arrayListOf(buyNowItem)
                    val singleItemIdList = arrayListOf(this.productId)

                    // 3. حساب الإجمالي لهذا العنصر الواحد
                    // تأكد من استخدام السعر الصحيح (productPrice أو discountedPrice إذا كان هناك خصم)
//                    val totalAmountSingle = (this.productPrice.toFloatOrNull() ?: 0.0f) * 1 // السعر * الكمية
//
//                    Log.d(
//                        TAG,
//                        "Buy Now clicked. Navigating with single item: $buyNowItem, Total: $totalAmountSingle"
//                    )

                    // 4. الانتقال إلى DeliveryFragment مع القوائم الجديدة

                    if (myAddressListSize == 0L) {
                        findNavController()
                            .navigate(
                                ProductDetailsFragmentDirections
                                    .actionProductDetailsFragmentToAddAddressFragment(
                                        intent = "add_new_address",
                                        cartItemModelList = singleItemList.toTypedArray(),
                                        fromCart = false,
                                        cartListIds = singleItemIdList.toTypedArray(),
                                        addressPosition = 0
                                    )
                            )
                    } else {
                        findNavController().navigate(
                            ProductDetailsFragmentDirections
                                .actionProductDetailsFragmentToDeliveryFragment(
                                    cartListIds = singleItemIdList.toTypedArray(), // قائمة IDs بعنصر واحد
                                    cartItemModelList = singleItemList.toTypedArray(), // قائمة المنتجات بعنصر واحد
                                    fromCart = false // <-- مهم: هذا ليس قادمًا من السلة الرئيسية
                                )
                        )
                    }


                } else {
                    // خطأ: بيانات المنتج غير مكتملة
                    Log.e(TAG, "Buy Now failed: Product details not fully loaded or available.")
                    Toast.makeText(
                        context,
                        "Cannot proceed, product details missing.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } else {
                // المستخدم غير مسجل الدخول: عرض ديالوج التسجيل
                Constants.signInSignUpDialog(
                    requireContext(),
                    R.id.productDetailsFragment,
                    layoutInflater,
                    requireView()
                )
            }
            // نهاية setupBuyNowButton
        }


//        binding.buyNowButton.setOnClickListener {
//
//            if (signOutViewModel.firebaseAuth.currentUser != null) {
//
//                if (!myCartListIds.contains(productId)) {
//                    myCartListIds.add(productId)
////                    productDetailsViewModel.
//
//                    FirebaseFirestore.getInstance().collection("PRODUCTS")
//                        .document(productId)
//                        .get().addOnSuccessListener {
//
//                            cartItemModelList.add(
//                                CartItemModel(
////                                                CartItemModel.CART_ITEM,
//                                    productId = it.id,
//                                    productImage = documentSnapshot["product_image_1"].toString(),
//                                    productName = documentSnapshot["product_name"].toString(),
//                                    freeCoupons = documentSnapshot["free_coupons"] as Long,
//                                    productPrice = documentSnapshot["product_price"].toString(),
//                                    cuttedPrice = documentSnapshot["cutted_price"].toString(),
//                                    productQuantity = 1,
//                                    maxQuantity = documentSnapshot["max_quantity"] as Long,
//                                    stockQuantity = documentSnapshot["stock_quantity"] as Long,
//                                    offersApply = documentSnapshot["offers_applied"] as Long,
//                                    couponsApplied = 0,
//                                    inStock = documentSnapshot["in_stock"] as Boolean,
//                                    qtyIDs = arrayListOf(),
//                                    selectedCouponId = null,
//                                    discountedPrice = null,
//                                )
//                            ).also {
//                                findNavController().navigate(
//                                    ProductDetailsFragmentDirections
//                                        .actionProductDetailsFragmentToDeliveryFragment(
//                                            cartListIds = myCartListIds.toTypedArray(),
//                                            cartItemModelList = cartItemModelList.toTypedArray(),
//                                            fromCart = fromCart,
//                                            totalAmount = productPrice.toFloat()
//                                        )
//                                )
//                            }
//
//                        }.addOnFailureListener {
//                            Log.e(TAG, "cartListIds: ${it.message.toString()}")
//                        }
//
//                    ALREADY_ADDED_TO_CART_LIST = true
//
//                } else {
//                    findNavController().navigate(
//                        ProductDetailsFragmentDirections
//                            .actionProductDetailsFragmentToDeliveryFragment(
//                                cartListIds = myCartListIds.toTypedArray(),
//                                cartItemModelList = cartItemModelList.toTypedArray(),
//                                fromCart = fromCart,
//                                totalAmount = productPrice.toFloat()
//                            )
//                    )
//                }
//
//
//            } else {
//                Constants.signInSignUpDialog(
//                    requireContext(),
//                    R.id.productDetailsFragment,
//                    layoutInflater,
//                    requireView()
//                )
//            }
//        }

        //=============== WISH LIST ================//

        binding.productImageViewPager.addToWishListFAB.setOnClickListener {
            if (signOutViewModel.firebaseAuth.currentUser == null) {
                Constants.signInSignUpDialog(
                    requireContext(),
                    R.id.productDetailsFragment,
                    layoutInflater,
                    binding.root
                )
            } else {

                if (ALREADY_ADDED_TO_WISH_LIST) {

                    ALREADY_ADDED_TO_WISH_LIST = false

                    val index = wishListIds.indexOf(productId)

                    productDetailsViewModel.removeFromWishList(
                        wishListIds,
                        wishListModelList,
                        index
                    )

                    binding.productImageViewPager.addToWishListFAB.imageTintList =
                        resources.getColorStateList(
                            R.color.favoritesUnselectedIconColor,
                            binding.productImageViewPager.addToWishListFAB.context.theme
                        )

                } else {

                    productDetailsViewModel.saveWishListIds(productId, wishListIds.size)

                }
            }
        }


        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                productDetailsViewModel.wishListIds.collect { response ->
                    when (response) {

                        is Resource.Success -> {
                            val listSize = response.data?.get("list_size") as Long
                            for (i in 0 until listSize) {

                                wishListIds.add(response.data["product_id_$i"].toString())

                                if (wishListIds.contains(productId)) {
                                    ALREADY_ADDED_TO_WISH_LIST = true
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        binding.productImageViewPager.addToWishListFAB.imageTintList =
                                            resources.getColorStateList(
                                                R.color.favoritesSelectedIconColor,
                                                requireContext().theme
                                            )
                                    } else {
                                        binding.productImageViewPager.addToWishListFAB.imageTintList =
                                            AppCompatResources
                                                .getColorStateList(
                                                    requireContext(),
                                                    R.color.favoritesSelectedIconColor
                                                )
                                    }
                                } else {

                                    ALREADY_ADDED_TO_WISH_LIST = false

                                    binding.productImageViewPager.addToWishListFAB.imageTintList =
                                        resources.getColorStateList(
                                            R.color.favoritesUnselectedIconColor,
                                            requireContext().theme
                                        )
                                }

                                FirebaseFirestore.getInstance().collection("PRODUCTS")
                                    .document(response.data["product_id_$i"].toString())
                                    .get().addOnSuccessListener {

                                        wishListModelList.add(
                                            WishListModel(
                                                productId,
                                                documentSnapshot["product_image_1"].toString(),
                                                documentSnapshot["product_full_title"].toString(),
                                                documentSnapshot["free_coupons"] as Long,
                                                documentSnapshot["average_rating"].toString(),
                                                documentSnapshot["total_ratings"] as Long,
                                                documentSnapshot["product_price"].toString(),
                                                documentSnapshot["product_title"].toString(),
                                                documentSnapshot["COD"] as Boolean,
                                                documentSnapshot["in_stock"] as Boolean
                                            )
                                        )

                                    }.addOnFailureListener {
                                        Log.e(TAG, "wishListIds: ${it.message.toString()}")
                                    }
                            }

                        }

                        is Resource.Error -> {
                            Log.e(TAG, "onViewCreated: ${response.message.toString()}")
                        }

                        else -> {}
                    }
                }
            }
        }


        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                productDetailsViewModel.saveWishListIdsState.collect { response ->

                    when (response) {

                        is Resource.Success -> {

                            Log.d(TAG, "saveWishListIdsState: ${response.data.toString()}")

                            if (wishListModelList.size != 0) {
                                wishListModelList.add(
                                    WishListModel(
                                        productId,
                                        documentSnapshot["product_image_1"].toString(),
                                        documentSnapshot["product_name"].toString(),
                                        documentSnapshot["free_coupons"] as Long,
                                        documentSnapshot["average_rating"].toString(),
                                        documentSnapshot["total_ratings"] as Long,
                                        documentSnapshot["product_price"].toString(),
                                        documentSnapshot["cutted_price"].toString(),
                                        documentSnapshot["COD"] as Boolean,
                                        documentSnapshot["in_stock"] as Boolean
                                    )
                                )

                            }

                            ALREADY_ADDED_TO_WISH_LIST = true

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                binding.productImageViewPager.addToWishListFAB.imageTintList =
                                    resources.getColorStateList(
                                        R.color.favoritesSelectedIconColor,
                                        binding.productImageViewPager.addToWishListFAB.context.theme
                                    )
                            } else {
                                binding.productImageViewPager.addToWishListFAB.imageTintList =
                                    AppCompatResources
                                        .getColorStateList(
                                            binding.productImageViewPager.addToWishListFAB.context,
                                            R.color.favoritesSelectedIconColor
                                        )
                            }

                            wishListIds.add(productId)
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.product_added_to_wish_list),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is Resource.Error -> {
                            Log.e(
                                TAG,
                                "productDetailsViewModel.saveWishListIdsState: ${response.message.toString()}",
                            )
                        }

                        else -> {}
                    }

                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                productDetailsViewModel.removeWishListState.collect {

                    if (it is Resource.Success) {
                        ALREADY_ADDED_TO_WISH_LIST = false

                        Toast.makeText(
                            requireContext(),
                            getString(R.string.product_removed_from_wishlist),
                            Toast.LENGTH_SHORT
                        ).show()

                    } else if (it is Resource.Error) {

                        Log.e(TAG, "onViewCreated: ${it.message.toString()}")
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                            binding.productImageViewPager.addToWishListFAB.imageTintList =
//                                resources.getColorStateList(
//                                    R.color.fabColor,
//                                    binding.productImageViewPager.addToWishListFAB.context.theme
//                                )
//                        } else {
//                            binding.productImageViewPager.addToWishListFAB.imageTintList =
//                                AppCompatResources
//                                    .getColorStateList(
//                                        binding.productImageViewPager.addToWishListFAB.context,
//                                        R.color.fabColor
//                                    )
//                        }
                    }

                }
            }
        }


        //================= Rating ==================//

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                productDetailsViewModel.ratingsIds.collect { response ->

                    when (response) {

                        is Resource.Success -> {
                            response.data?.let {
                                val listSize = it.get("list_size") as Long

                                for (i in 0 until listSize) {
                                    myRatingsIds.add(it.get("product_id_$i").toString())
                                    myRatings.add(it.get("rating_$i") as Long)

                                    val ratingId = it.get("product_id_$i").toString()

                                    if (ratingId.equals(productId)) {
                                        setRating(it.get("rating_$i") as Long, true)
                                    }
                                }

                            }
                        }

                        is Resource.Error -> {
                            Log.e(TAG, "onViewCreated: ${response.message.toString()}")
                        }

                        else -> {}
                    }
                }
            }

        }

        // التقييمات متوقفة

//        for (i in 0 until binding.ratingsLayout.rateNowContainer.childCount) {
//
//            Log.d(TAG, "index: $i")
//
//            binding.ratingsLayout.rateNowContainer.getChildAt(i).setOnClickListener { view ->
//
//
//                starPosition = binding.ratingsLayout.rateNowContainer.indexOfChild(view).toLong()
//
//
//                // get the star position from the view's tag or id
//
//                Log.d(TAG, "starPosition: $starPosition")
//
//                if (signOutViewModel.firebaseAuth.currentUser == null) {
//                    Constants.signInSignUpDialog(
//                        requireContext(),
//                        R.id.productDetailsFragment,
//                        layoutInflater,
//                        requireView()
//                    )
//                } else {
//                    setRating(starPosition, false)
//
//                    if (myRatingsIds.contains(productId)) {
//
//
////                        updateRating.put("${initialRating+1}_star", )
////                        updateRating.put("${starPosition+1}_star",
////                        updateRating.put("average_rating",))
//
//                        val oldRating =
//                            binding.ratingsLayout.ratingNumbersContainer.getChildAt((5 - initialRating - 1).toInt()) as TextView
//                        val finalRating =
//                            binding.ratingsLayout.ratingNumbersContainer.getChildAt((5 - starPosition - 1).toInt()) as TextView
//
////                        Log.d(TAG, "oldRating: ")
////
////                        val oldStar =
////                            ((documentSnapshot.get("${initialRating + 1}_star") as Long) - 1)
////                        val newStar =
////                            ((documentSnapshot.get("${starPosition + 1}_star") as Long) + 1)
////                        val averageRating = (calculateAverageRating2(starPosition + 1).toString())
////
////
////                        productDetailsViewModel.updateRatings(
////                            productId,
////                            initialRating,
////                            oldRating.text.toString().toLong() - 1,
////                            finalRating.text.toString().toLong() + 1,
////                            starPosition,
////                            averageRating.toFloat(),
////                            myRatingsIds,
////                            myRatings
////                        )
//
//                    } else {
//
//                        Log.d(TAG, "starPosition: $starPosition")
//
//                        productDetailsViewModel.setRatings(
//                            productId, starPosition,
//                            averageRating = calculateAverageRating(starPosition + 1).toString(),
//                            totalRatings = documentSnapshot["total_ratings"] as Long + 1,
//                            myRatingsIds,
//                            myRatings
//
//                        )
//                    }
//                }
//            }
//
//        }
        //نهاية النقر على التقييم

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                productDetailsViewModel.setRatingState.collect { response ->
                    if (response is Resource.Success) {

                        myRatingsIds.add(productId)
                        myRatings.add(starPosition + 1)

                        val ratingTV = binding.ratingsLayout
                            .ratingNumbersContainer.getChildAt((5 - starPosition - 1).toInt()) as TextView

                        ratingTV.text = (ratingTV.text.toString().toInt() + 1).toString()


                        binding.productImageViewPager.productTotalRatingMiniViewTV.text =
                            "(" + documentSnapshot["total_ratings"] as Long + 1 + ") Ratings"

                        binding.ratingsLayout.secondTotalRatingsTV.text =
                            (documentSnapshot["total_ratings"] as Long + 1).toString() + " ratings"


                        binding.ratingsLayout.totalRatingsFigure.text =
                            (documentSnapshot["total_ratings"] as Long + 1).toString()

                        binding.ratingsLayout.averageRatingTV.text =
                            calculateAverageRating(starPosition + 1)
                                .toString()

                        binding.productImageViewPager.averageRatingMiniViewTV.text =
                            calculateAverageRating(
                                starPosition + 1
                            ).toString()

                        for (x in 0..4) {
                            val ratingFigures =
                                binding.ratingsLayout.ratingNumbersContainer.getChildAt(x) as TextView
                            val progressBar =
                                binding.ratingsLayout.ratingProgressBarContainer.getChildAt(x) as ProgressBar
                            val maxProgress =
                                Integer.valueOf((documentSnapshot["total_ratings"] as Long + 1).toString())
                            //
                            progressBar.max = maxProgress
                            progressBar.progress = ratingFigures.text.toString()
                                .toInt()
                        }



                        Toast.makeText(
                            requireContext(),
                            "Thanks for rating",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else if (response is Resource.Error) {
                        setRating(initialRating, true)
                        Log.e(TAG, "setRatingState: ${response.message.toString()}")
                    }
                }
            }
        }


        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                productDetailsViewModel.updateRatingState.collect { response ->
                    if (response is Resource.Success) {


                        myRatings.set(myRatingsIds.indexOf(productId), starPosition + 1)

                        val oldRating =
                            binding.ratingsLayout.ratingNumbersContainer.getChildAt((5 - initialRating - 1).toInt()) as TextView
                        val finalRating =
                            binding.ratingsLayout.ratingNumbersContainer.getChildAt((5 - starPosition - 1).toInt()) as TextView

                        oldRating.text = (oldRating.text.toString().toInt() - 1).toString()
                        finalRating.text = (finalRating.text.toString().toInt() + 1).toString()


                        val avgRating = calculateAverageRating(starPosition + 1).toString()


                        Log.v(TAG, "avgRating: $avgRating")

                        binding.ratingsLayout.averageRatingTV.text = avgRating


                        binding.productImageViewPager.averageRatingMiniViewTV.text =
                            calculateAverageRating(
                                starPosition + 1
                            ).toString()

                        for (x in 0..4) {
                            val ratingFigures =
                                binding.ratingsLayout.ratingNumbersContainer.getChildAt(x) as TextView
                            val progressBar =
                                binding.ratingsLayout.ratingProgressBarContainer.getChildAt(x) as ProgressBar
                            val maxProgress =
                                Integer.valueOf((documentSnapshot["total_ratings"] as Long + 1).toString())
                            //
                            progressBar.max = maxProgress
                            progressBar.progress = ratingFigures.text.toString()
                                .toInt()
                        }

                        initialRating = starPosition


//                        Toast.makeText(requireContext(), "Thanks for rating", Toast.LENGTH_SHORT)
//                            .show()
                    } else if (response is Resource.Error) {
                        setRating(initialRating, true)
                        Log.e(TAG, "setRatingState: ${response.message.toString()}")
                    }
                }
            }
        }


        //================= My Cart ==================//

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                productDetailsViewModel.myCartId.collect { response ->
                    when (response) {

                        is Resource.Success -> {
                            val listSize = response.data?.get("list_size") as Long
                            for (i in 0 until listSize) {

                                myCartListIds.add(response.data["product_id_$i"].toString())

                                requireActivity().invalidateMenu()

                                ALREADY_ADDED_TO_CART_LIST = myCartListIds.contains(productId)

                                if (ALREADY_ADDED_TO_CART_LIST) {

                                    Log.d(
                                        TAG,
                                        "ALREADY_ADDED_TO_CART_LIST: $ALREADY_ADDED_TO_CART_LIST"
                                    )

                                    updateAddToCartButtonAppearance(
                                        getString(R.string.remove),
                                        textSizeRes = resources.getDimensionPixelSize(R.dimen._16ssp)
                                            .toFloat(),
                                        textColorRes = R.color.removeColor,
                                        drawableRes = R.drawable.baseline_shopping_cart_red
                                    )
                                }

                                FirebaseFirestore.getInstance().collection("PRODUCTS")
                                    .document(response.data["product_id_$i"].toString())
                                    .get().addOnSuccessListener {

                                        cartItemModelList.add(
                                            CartItemModel(
//                                                CartItemModel.CART_ITEM,
                                                productId = it.id,
                                                productImage = documentSnapshot["product_image_1"].toString(),
                                                productName = documentSnapshot["product_name"].toString(),
                                                freeCoupons = documentSnapshot["free_coupons"] as Long,
                                                productPrice = documentSnapshot["product_price"].toString(),
                                                cuttedPrice = documentSnapshot["cutted_price"].toString(),
                                                productQuantity = 1,
                                                maxQuantity = documentSnapshot["max_quantity"] as Long,
                                                stockQuantity = documentSnapshot["stock_quantity"] as Long,
                                                offersApply = documentSnapshot["offers_applied"] as Long,
                                                couponsApplied = 0,
                                                inStock = documentSnapshot["in_stock"] as Boolean,
                                                qtyIDs = arrayListOf(),
                                                selectedCouponId = null,
                                                discountedPrice = null,
                                                productRating = 0,
                                            )
                                        )

                                    }.addOnFailureListener {
                                        Log.e(TAG, "cartListIds: ${it.message.toString()}")
                                    }
                            }

                        }

                        is Resource.Error -> {
                            Log.e(TAG, "cartListIds: ${response.message.toString()}")
                        }

                        else -> {}
                    }
                }
            }
        }



        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                productDetailsViewModel.saveCartListIdsState.collect { response ->

                    when (response) {

                        is Resource.Success -> {

                            Log.d(TAG, "saveCartListIdsState: ${response.data.toString()}")

                            ALREADY_ADDED_TO_CART_LIST = true


                            myCartListIds.add(productId)
                            requireActivity().invalidateMenu()

                            updateAddToCartButtonAppearance(
                                getString(R.string.remove),
                                textColorRes = R.color.removeColor,
                                textSizeRes = resources.getDimensionPixelSize(R.dimen._16ssp)
                                    .toFloat(),
                                drawableRes = R.drawable.baseline_shopping_cart_red
                            )

//                            Toast.makeText(
//                                requireContext(),
//                                "Product added to Cart list successfully",
//                                Toast.LENGTH_SHORT
//                            ).show()
                        }

                        is Resource.Error -> {
                            Log.e(
                                TAG,
                                "productDetailsViewModel.saveCartListIdsState: ${response.message.toString()}",
                            )
                        }

                        else -> {}
                    }

                }
            }
        }


    }

    private fun updateAddToCartButtonAppearance(
        buttonText: String,
        textColorRes: Int,
        textSizeRes: Float,
        drawableRes: Int
    ) {
        binding.addToCartTextView.apply {

            text = buttonText
            gravity = Gravity.CENTER
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setTextColor(resources.getColor(textColorRes))
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeRes)
            setTypeface(ResourcesCompat.getFont(context, R.font.arial), Typeface.BOLD)
            //                                drawableTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.btnRed))
            compoundDrawablePadding = resources.getDimensionPixelSize(R.dimen._6dp)
            val cartDrawable = ContextCompat.getDrawable(context, drawableRes)
            cartDrawable?.setBounds(
                0,
                0,
                cartDrawable.intrinsicWidth,
                cartDrawable.intrinsicHeight
            )

            val tintList = ColorStateList.valueOf(ContextCompat.getColor(context, textColorRes))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                compoundDrawableTintList = tintList
                TextViewCompat.setCompoundDrawableTintList(binding.addToCartTextView, tintList)

            } else {
                DrawableCompat.setTintList(cartDrawable!!, tintList)
            }

//            DrawableCompat.setTint(cartDrawable!!, ContextCompat.getColor(context, drawableTintRes))
            setCompoundDrawablesRelative(null, null, cartDrawable, null)

            //                                compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.btnRed))

        }
    }

//    private fun calculateProductAmountDetails() {
//        var totalItems = 0
//        var totalItemsPrice = 0
//        var deliveryPrice: String
//        var totalAmount: Int
//        var savedAmount = 0
//        //                var i = 0
//        for (i in 0 until cartItemModelList.size) {
//
//            if (cartItemModelList[i].inStock) {
//
//                val quantity = cartItemModelList[i].productQuantity
//
//                totalItems = (totalItemsPrice + quantity!!).toInt()
//
//                totalItemsPrice += if (cartItemModelList[i].selectedCouponId.isNullOrEmpty()) {
//                    cartItemModelList[i].productPrice?.toInt()!! * quantity.toInt()
//                } else {
//                    cartItemModelList[i].discountedPrice?.toInt()!! * quantity.toInt()
//                }
//
//                if (cartItemModelList[i].cuttedPrice?.isNotEmpty()!!) {
//                    savedAmount += (cartItemModelList[i].cuttedPrice?.toInt()!! - cartItemModelList[i].productPrice?.toInt()!!) * quantity.toInt()
//
//                    if (!cartItemModelList[i].selectedCouponId.isNullOrEmpty()) {
//                        savedAmount += (cartItemModelList[i].productPrice?.toInt()!! - cartItemModelList[i].discountedPrice?.toInt()!!) * quantity.toInt()
//                    }
//
//                } else {
//                    if (cartItemModelList[i].selectedCouponId?.isNotEmpty()!!) {
//                        savedAmount += (cartItemModelList[i].productPrice?.toInt()!! - cartItemModelList[i].discountedPrice?.toInt()!!) * quantity.toInt()
//                    }
//                }
//
//                if (totalItemsPrice > 500) {
//                    deliveryPrice = "Free"
//                    totalAmount = totalItemsPrice
//                } else {
//                    deliveryPrice = "60"
//                    totalAmount = totalItemsPrice + 60
//                }
//                cartItemModelList[i].totalItems = totalItems
//                cartItemModelList[i].totalItemsPrice = totalItemsPrice
//                cartItemModelList[i].deliveryPrice = deliveryPrice
//                cartItemModelList[i].totalAmount = totalAmount
//                cartItemModelList[i].savedAmount = savedAmount
//
//            }
//        }
//    }

    private fun calculateAverageRating(currentUserRating: Long): Float {
        var totalStars: Long = 0
        for (i in 1..5) {
            totalStars += documentSnapshot[i.toString() + "_star"] as Long

            Log.d(
                TAG,
                "calculateAverageRating: ${documentSnapshot.get(i.toString() + "_star")}"
            )
        }
        totalStars += currentUserRating
        return (totalStars / (documentSnapshot["total_ratings"] as Long + 1)).toFloat()
    }

//    private fun calculateAverageRating2(currentUserRating: Long): String {
//        var totalStars: Double = 0.0
//        val totalRatings = binding.ratingsLayout.totalRatingsFigure.text.toString().toLong() + 1
//
//        for (i in 1 until 6) {
//            val ratingNo = binding.ratingsLayout.ratingNumbersContainer.getChildAt(5 - i) as TextView
//            totalStars += (ratingNo.text.toString().toLong()) * i
//        }
//
//        totalStars += currentUserRating
//
//        val averageRating = totalStars / totalRatings
//
//
//        Log.d(TAG, "calculateAverageRating2: $averageRating")
//
//        return String.format(locale = Locale.ENGLISH,"%.1f", averageRating)
//    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).apply {
            supportActionBar?.title = getString(R.string.product_details)
            getToolBar().title = getString(R.string.product_details)
            actionBarLogo.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        findNavController(binding.root).clearBackStack()
        productsImages.clear()
        wishListIds.clear()
        myCartListIds.clear()
        cartItemModelList.clear()
//        (requireActivity()as MainActivity).fragmentTitleAndActionBar()
        _binding = null
//        menuHost?.removeMenuProvider(this)
    }


    @Suppress("DEPRECATION")
    private fun setRating(starPosition: Long, isGetRating: Boolean) {
        for (i in 0 until binding.ratingsLayout.rateNowContainer.childCount) {

            val starBtn = binding.ratingsLayout.rateNowContainer.getChildAt(i) as ImageView

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                starBtn.imageTintList =
                    ColorStateList.valueOf(
                        resources.getColor(
                            R.color.mediumGray,
                            requireContext().theme
                        )
                    )
            } else {
                starBtn.imageTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.mediumGray))
            }

            if (isGetRating) {
                if (i < starPosition) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        starBtn.imageTintList =
                            ColorStateList.valueOf(
                                resources.getColor(
                                    R.color.ratingColor,
                                    requireContext().theme
                                )
                            )
                    } else {
                        starBtn.imageTintList =
                            ColorStateList.valueOf(resources.getColor(R.color.ratingColor))
                    }
                }
            } else {
                if (i <= starPosition) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        starBtn.imageTintList =
                            ColorStateList.valueOf(
                                resources.getColor(
                                    R.color.ratingColor,
                                    requireContext().theme
                                )
                            )
                    } else {
                        starBtn.imageTintList =
                            ColorStateList.valueOf(resources.getColor(R.color.ratingColor))
                    }
                }
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

        menuInflater.inflate(R.menu.fragment_product_details_menu, menu)

        val cartItem = menu.findItem(R.id.action_cart)
        cartItem.setActionView(R.layout.cart_badge_layout)

        (requireActivity() as MainActivity).apply {
            supportActionBar?.title = getString(R.string.product_details)
            getToolBar().title = getString(R.string.product_details)
        }

//        val badgeIcon = cartItem.actionView!!.findViewById(R.id.badge_icon) as ImageView
//        badgeIcon.setImageResource(R.drawable.baseline_shopping_cart)

        val badgeCount: TextView = cartItem.actionView!!.findViewById(R.id.cart_badge_count)

        if (signOutViewModel.firebaseAuth.currentUser != null) {
            if (myCartListIds.isEmpty()) {
                badgeCount.visibility = View.INVISIBLE

            } else {
                badgeCount.visibility = View.VISIBLE
                if (myCartListIds.size < 99) {
                    badgeCount.text = myCartListIds.size.toString()
                } else {
                    badgeCount.text = "99"
                }
            }
        }
        cartItem.actionView!!.setOnClickListener { view: View? ->
            findNavController(requireView())
                .navigate(ProductDetailsFragmentDirections.actionProductDetailsFragmentToNavMyCart())
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_search -> {
                true
            }

            R.id.action_cart -> {
                findNavController(binding.root)
                    .navigate(ProductDetailsFragmentDirections.actionProductDetailsFragmentToNavMyCart())
                true
            }

            else -> false
        }
    }


    fun getTranslatedSpecFieldName(context: Context, englishFieldName: String): String {

        @StringRes val resourceId = when (englishFieldName) {
            "Model" -> R.string.spec_name_model
            "Brand" -> R.string.spec_name_brand
            "Country of manufacture" -> R.string.spec_name_country_of_manufacture
            "Main material" -> R.string.spec_name_main_material
            "Color" -> R.string.spec_name_color
            "Item Weight" -> R.string.spec_name_item_weight
            "Material" -> R.string.spec_name_material
            "Special Feature" -> R.string.spec_name_special_feature
            "Product Dimensions" -> R.string.spec_name_product_dimensions
            "Handle Material" -> R.string.spec_name_handle_material
            "Part Number" -> R.string.spec_name_part_number
            "Size" -> R.string.spec_name_size
            "Dimensions" -> R.string.spec_name_product_dimensions
            "Max Weight" -> R.string.spec_name_max_weight
            "Model Number" -> R.string.spec_name_model_number
            "Frame Material" -> R.string.spec_name_frame_material
            "Recommended room type" -> R.string.spec_name_recommended_room_type
            "Display Size In Inches" -> R.string.spec_name_display_size_in_inches
            "Processor CPU" -> R.string.spec_name_processor_cpu
            "Internal Memory Capacity In GB" -> R.string.spec_name_internal_memory_capacity_in_gb
            "Memory RAM In GB" -> R.string.spec_name_memory_ram_in_gb
            "Rear Camera" -> R.string.spec_name_rear_camera
            "Front Camera" -> R.string.spec_name_front_camera
            "Battery" -> R.string.spec_name_battery
            "Author" -> R.string.spec_name_author
            "Publisher" -> R.string.spec_name_publisher
            "Language" -> R.string.spec_name_language
            "Hardcover Pages" -> R.string.spec_name_hardcover_pages
            "Reading age" -> R.string.spec_name_reading_age
            else -> 0 // القيمة 0 تشير إلى عدم العثور على معرف مورد مطابق
        }

        return if (resourceId != 0) {
            try {
                context.getString(resourceId)
            } catch (e: Exception) {
                // في حال حدوث خطأ غير متوقع (نادر)، عد بالاسم الأصلي
                Log.e(TAG, "getTranslatedSpecFieldName: ${e.toString()}")
                englishFieldName
            }
        } else {
            // إذا لم يتم العثور على ترجمة معرفة في when،
            // أعد اسم الحقل الإنجليزي الأصلي كقيمة احتياطية
            englishFieldName
        }
    }
}