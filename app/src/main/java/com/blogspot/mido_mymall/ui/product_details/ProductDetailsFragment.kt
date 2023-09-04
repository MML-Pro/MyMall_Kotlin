package com.blogspot.mido_mymall.ui.product_details

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
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
import com.blogspot.mido_mymall.ui.credentials.SignOutViewModel
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

//        addToWishListFAB = binding.productImageViewPager.addToWishListFAB

//        productDetailsAdapter.notifyDataSetChanged()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        productsImages.add(R.drawable.image2)
//        productsImages.add(R.drawable.banner)
//        productsImages.add(R.drawable.stripadd)
//        productsImages.add(R.drawable.image5)

        // ============== Products Images ================= //

        productDetailsViewModel.getProductDetails(productId)

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
                                    productImageViewPager.productName.text =
                                        documentSnapshot["product_name"].toString()
                                    productImageViewPager.averageRatingMiniViewTV.text =
                                        documentSnapshot["average_rating"].toString()
                                    productImageViewPager.productTotalRatingMiniViewTV.text =
                                        "total ratings (${documentSnapshot["total_ratings"] as Long})"
                                    productImageViewPager.productPrice.text =
                                        "EGP. ${documentSnapshot["product_price"]}/-"

                                    productPriceValue = documentSnapshot["product_price"].toString()

                                    productImageViewPager.productCuttedPrice.text =
                                        "EGP. ${documentSnapshot["cutted_price"]}/-"

                                    if (documentSnapshot.get("COD") as Boolean) {
                                        productImageViewPager.codImageView.visibility = View.VISIBLE
                                        productImageViewPager.codIndicatorTV.visibility =
                                            View.VISIBLE
                                    } else {
                                        productImageViewPager.codImageView.visibility = View.GONE
                                        productImageViewPager.codIndicatorTV.visibility = View.GONE
                                    }

                                    rewardWithProductLayout.rewardTitle.text =
                                        "${documentSnapshot["free_coupons"] as Long} ${documentSnapshot["free_coupone_title"].toString()}"

                                    rewardWithProductLayout.rewardBody.text =
                                        documentSnapshot["free_coupone_body"].toString()

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
                                                productSpecsModelList.add(
                                                    ProductSpecsModel(
                                                        1,
                                                        documentSnapshot["specs_title_" + x + "_total_field_" + y + "_name"].toString(),
                                                        documentSnapshot["specs_title_" + x + "_total_field_" + y + "_value"].toString()
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
                                    } else {
                                        productDescriptionLayout.root.visibility = View.GONE
                                        productDetailsOnly.root.visibility = View.VISIBLE
                                        productDetailsOnly.productDetailsBody.text =
                                            documentSnapshot["product_description"].toString()
                                    }

                                    ratingsLayout.averageRatingTV.text =
                                        documentSnapshot["average_rating"].toString()
                                    ratingsLayout.secondTotalRatingsTV.text =
                                        "${documentSnapshot["total_ratings"] as Long} ratings"


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

                                                Toast.makeText(
                                                    requireContext(),
                                                    "Already added to cart",
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()

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
                                    outOfStock.text = "Out of stock"
                                    outOfStock.setTextColor(resources.getColor(R.color.colorPrimary))
                                    outOfStock.setCompoundDrawables(null, null, null, null)
                                    binding.linearLayout7.weightSum = 1F
                                }

                            }


                            //FOR THE BUY NOW DIRECTLY

                            productPrice = documentSnapshot?.get("product_price").toString()


                            val cartItem = CartItemModel(
                                type = CartItemModel.CART_ITEM,
                                productId = productId,
                                productImage = documentSnapshot?.get("product_image_1")
                                    .toString(),
                                productName = documentSnapshot?.get("product_name").toString(),
                                freeCoupons = documentSnapshot?.get("free_coupons") as Long,
                                productPrice = documentSnapshot["product_price"].toString(),
                                cuttedPrice = documentSnapshot["cutted_price"].toString(),
                                productQuantity = 1,
                                maxQuantity = documentSnapshot["max_quantity"] as Long,
                                stockQuantity = documentSnapshot["stock_quantity"] as Long,
                                offersApply = documentSnapshot["offers_applied"] as Long,
                                couponsApplied = 0,
                                inStock = documentSnapshot["in_stock"] as Boolean,
                                qtyIDs = null,
                                selectedCouponId = null,
                                discountedPrice = null,
                                totalItems = null,
                                totalItemsPrice = null,
                                deliveryPrice = null,
                                totalAmount = null,
                                savedAmount = null
                            )



                            calculateProductAmountDetails()


//                String totalItems = asyncListDiffer.currentList.get(position).getTotalItems();
//                String totalItemPrice = asyncListDiffer.currentList.get(position).getTotalItemPrice();

//                            cartItemModelList.forEach {cartItemModel->
//
//                                if(cartItemModel.type == CartItemModel.CART_ITEM) {
//
//                                }
//                            }


                            cartItemModelList.add(cartItem)
                                .also {
                                    cartItemModelList.add(
                                        CartItemModel(CartItemModel.TOTAL_AMOUNT)
                                    )
                                }
                        }

                        is Resource.Error -> {
                            Log.e(TAG, "productDetails: ${response.message.toString()}")

                        }

                        else -> {}
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
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        binding.buyNowButton.setOnClickListener {

            if (signOutViewModel.firebaseAuth.currentUser != null) {


                findNavController().navigate(
                    ProductDetailsFragmentDirections
                        .actionProductDetailsFragmentToDeliveryFragment(
                            cartListIds = myCartListIds.toTypedArray(),
                            cartItemModelList = cartItemModelList.toTypedArray(),
                            fromCart = fromCart,
                            totalAmount = productPrice.toInt()
                        )
                )
            } else {
                Constants.signInSignUpDialog(
                    requireContext(),
                    R.id.productDetailsFragment,
                    layoutInflater,
                    requireView()
                )
            }
        }

        //=============== WISH LIST ================//

        binding.productImageViewPager.addToWishListFAB.setOnClickListener {

            if (ALREADY_ADDED_TO_WISH_LIST) {

                ALREADY_ADDED_TO_WISH_LIST = false

                val index = wishListIds.indexOf(productId)

                productDetailsViewModel.removeFromWishList(wishListIds, wishListModelList, index)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    binding.productImageViewPager.addToWishListFAB.imageTintList =
                        resources.getColorStateList(
                            R.color.fabColor,
                            binding.productImageViewPager.addToWishListFAB.context.theme
                        )
                } else {
                    binding.productImageViewPager.addToWishListFAB.imageTintList =
                        AppCompatResources
                            .getColorStateList(
                                binding.productImageViewPager.addToWishListFAB.context,
                                R.color.fabColor
                            )
                }

            } else {

                productDetailsViewModel.saveWishListIds(productId, wishListIds.size)

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
                                                R.color.btnRed,
                                                requireContext().theme
                                            )
                                    } else {
                                        binding.productImageViewPager.addToWishListFAB.imageTintList =
                                            AppCompatResources
                                                .getColorStateList(
                                                    requireContext(),
                                                    R.color.btnRed
                                                )
                                    }
                                } else {

                                    ALREADY_ADDED_TO_WISH_LIST = false

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        binding.productImageViewPager.addToWishListFAB.imageTintList =
                                            resources.getColorStateList(
                                                R.color.fabColor,
                                                requireContext().theme
                                            )
                                    } else {
                                        binding.productImageViewPager.addToWishListFAB.imageTintList =
                                            AppCompatResources
                                                .getColorStateList(
                                                    requireContext(),
                                                    R.color.fabColor
                                                )
                                    }
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
                                        R.color.btnRed,
                                        binding.productImageViewPager.addToWishListFAB.context.theme
                                    )
                            } else {
                                binding.productImageViewPager.addToWishListFAB.imageTintList =
                                    AppCompatResources
                                        .getColorStateList(
                                            binding.productImageViewPager.addToWishListFAB.context,
                                            R.color.btnRed
                                        )
                            }

                            wishListIds.add(productId)
                            Toast.makeText(
                                requireContext(),
                                "Product added to wish list successfully",
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
                            "Product removed successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else if (it is Resource.Error) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            binding.productImageViewPager.addToWishListFAB.imageTintList =
                                resources.getColorStateList(
                                    R.color.colorPrimary,
                                    binding.productImageViewPager.addToWishListFAB.context.theme
                                )
                        } else {
                            binding.productImageViewPager.addToWishListFAB.imageTintList =
                                AppCompatResources
                                    .getColorStateList(
                                        binding.productImageViewPager.addToWishListFAB.context,
                                        R.color.colorPrimary
                                    )
                        }
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


        for (i in 0 until binding.ratingsLayout.rateNowContainer.childCount) {

            Log.d(TAG, "index: $i")

            binding.ratingsLayout.rateNowContainer.getChildAt(i).setOnClickListener { view ->


                starPosition = binding.ratingsLayout.rateNowContainer.indexOfChild(view).toLong()


                // get the star position from the view's tag or id

                Log.d(TAG, "starPosition: $starPosition")

                if (signOutViewModel.firebaseAuth.currentUser == null) {
                    Constants.signInSignUpDialog(
                        requireContext(),
                        R.id.productDetailsFragment,
                        layoutInflater,
                        requireView()
                    )
                } else {
                    setRating(starPosition, false)

                    if (myRatingsIds.contains(productId)) {


//                        updateRating.put("${initialRating+1}_star", )
//                        updateRating.put("${starPosition+1}_star",
//                        updateRating.put("average_rating",))

                        val oldRating =
                            binding.ratingsLayout.ratingNumbersContainer.getChildAt((5 - initialRating - 1).toInt()) as TextView
                        val finalRating =
                            binding.ratingsLayout.ratingNumbersContainer.getChildAt((5 - starPosition - 1).toInt()) as TextView

//                        Log.d(TAG, "oldRating: ")
//
//                        val oldStar =
//                            ((documentSnapshot.get("${initialRating + 1}_star") as Long) - 1)
//                        val newStar =
//                            ((documentSnapshot.get("${starPosition + 1}_star") as Long) + 1)
//                        val averageRating = (calculateAverageRating2(starPosition + 1).toString())
//
//
//                        productDetailsViewModel.updateRatings(
//                            productId,
//                            initialRating,
//                            oldRating.text.toString().toLong() - 1,
//                            finalRating.text.toString().toLong() + 1,
//                            starPosition,
//                            averageRating.toFloat(),
//                            myRatingsIds,
//                            myRatings
//                        )

                    } else {

                        Log.d(TAG, "starPosition: $starPosition")

                        productDetailsViewModel.setRatings(
                            productId, starPosition,
                            averageRating = calculateAverageRating(starPosition + 1).toString(),
                            totalRatings = documentSnapshot["total_ratings"] as Long + 1,
                            myRatingsIds,
                            myRatings

                        )
                    }
                }
            }

        }

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



                        Toast.makeText(requireContext(), "Thanks for rating", Toast.LENGTH_SHORT)
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
                productDetailsViewModel.myCart.collect { response ->
                    when (response) {

                        is Resource.Success -> {
                            val listSize = response.data?.get("list_size") as Long
                            for (i in 0 until listSize) {

                                myCartListIds.add(response.data["product_id_$i"].toString())

                                requireActivity().invalidateMenu()

                                ALREADY_ADDED_TO_CART_LIST = myCartListIds.contains(productId)

                                FirebaseFirestore.getInstance().collection("PRODUCTS")
                                    .document(response.data["product_id_$i"].toString())
                                    .get().addOnSuccessListener {

                                        cartItemModelList.add(
                                            CartItemModel(
                                                type = CartItemModel.CART_ITEM,
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
                                                qtyIDs = null,
                                                selectedCouponId = null,
                                                discountedPrice = null,
                                                totalItems = null,
                                                totalItemsPrice = null,
                                                deliveryPrice = null,
                                                totalAmount = null,
                                                savedAmount = null
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

//                            if (cartItemModelList.size != 0) {
//                                cartItemModelList.add(
//                                    CartItemModel(
//                                        type = CartItemModel.CART_ITEM,
//                                        productId = productId,
//                                        productImage = documentSnapshot["product_image_1"].toString(),
//                                        productName = documentSnapshot["product_name"].toString(),
//                                        freeCoupons = documentSnapshot["free_coupons"] as Long,
//                                        productPrice = documentSnapshot["product_price"].toString(),
//                                        cuttedPrice = documentSnapshot["cutted_price"].toString(),
//                                        productQuantity = 1,
//                                        maxQuantity = documentSnapshot["max_quantity"] as Long,
//                                        offersApply = 0,
//                                        couponsApplied = 0,
//                                        inStock = documentSnapshot["in_stock"] as Boolean,
//                                        selectedCouponId = null,
//                                        discountedPrice = null
//                                    )
//                                )
//
//                            }

                            ALREADY_ADDED_TO_CART_LIST = true


                            myCartListIds.add(productId)
                            requireActivity().invalidateMenu()

                            Toast.makeText(
                                requireContext(),
                                "Product added to Cart list successfully",
                                Toast.LENGTH_SHORT
                            ).show()
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

    private fun calculateProductAmountDetails() {
        var totalItems = 0
        var totalItemsPrice = 0
        var deliveryPrice: String
        var totalAmount: Int
        var savedAmount = 0
        //                var i = 0
        for (i in 0 until cartItemModelList.size) {

            if (cartItemModelList[i].type == CartItemModel.CART_ITEM
                && cartItemModelList[i].inStock == true
            ) {

                val quantity = cartItemModelList[i].productQuantity

                totalItems = (totalItemsPrice + quantity!!).toInt()

                totalItemsPrice += if (cartItemModelList[i].selectedCouponId.isNullOrEmpty()) {
                    cartItemModelList[i].productPrice?.toInt()!! * quantity.toInt()
                } else {
                    cartItemModelList[i].discountedPrice?.toInt()!! * quantity.toInt()
                }

                if (cartItemModelList[i].cuttedPrice?.isNotEmpty()!!) {
                    savedAmount += (cartItemModelList[i].cuttedPrice?.toInt()!! - cartItemModelList[i].productPrice?.toInt()!!) * quantity.toInt()

                    if (!cartItemModelList[i].selectedCouponId.isNullOrEmpty()) {
                        savedAmount += (cartItemModelList[i].productPrice?.toInt()!! - cartItemModelList[i].discountedPrice?.toInt()!!) * quantity.toInt()
                    }

                } else {
                    if (cartItemModelList[i].selectedCouponId?.isNotEmpty()!!) {
                        savedAmount += (cartItemModelList[i].productPrice?.toInt()!! - cartItemModelList[i].discountedPrice?.toInt()!!) * quantity.toInt()
                    }
                }

                if (totalItemsPrice > 500) {
                    deliveryPrice = "Free"
                    totalAmount = totalItemsPrice
                } else {
                    deliveryPrice = "60"
                    totalAmount = totalItemsPrice + 60
                }
                cartItemModelList[i].totalItems = totalItems
                cartItemModelList[i].totalItemsPrice = totalItemsPrice
                cartItemModelList[i].deliveryPrice = deliveryPrice
                cartItemModelList[i].totalAmount = totalAmount
                cartItemModelList[i].savedAmount = savedAmount

            }
        }
    }

    private fun calculateAverageRating(currentUserRating: Long): Float {
        var totalStars: Long = 0
        for (i in 1..5) {
            totalStars += documentSnapshot[i.toString() + "_star"] as Long

            Log.d(TAG, "calculateAverageRating: ${documentSnapshot.get(i.toString() + "_star")}")
        }
        totalStars += currentUserRating
        return (totalStars / (documentSnapshot["total_ratings"] as Long + 1)).toFloat()
    }

    private fun calculateAverageRating2(currentUserRating: Long): Float {
        var totalStars: Long = 0
        for (i in 1..5) {

            val ratingNo =
                binding.ratingsLayout.ratingNumbersContainer.getChildAt(i - 1) as TextView

            totalStars += ratingNo.text.toString().toLong()

            Log.d(TAG, "calculateAverageRating: ${documentSnapshot.get(i.toString() + "_star")}")
        }
        totalStars += currentUserRating
        return (totalStars / (documentSnapshot["total_ratings"] as Long + 1)).toFloat()
    }


    override fun onDestroyView() {
        super.onDestroyView()
//        findNavController(binding.root).clearBackStack()
        productsImages.clear()
        wishListIds.clear()
        myCartListIds.clear()
        cartItemModelList.clear()
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

//        val badgeIcon = cartItem.actionView!!.findViewById(R.id.badge_icon) as ImageView
//        badgeIcon.setImageResource(R.drawable.baseline_shopping_cart)

        val badgeCount = cartItem.actionView!!.findViewById(R.id.cart_badge_count) as TextView

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
}