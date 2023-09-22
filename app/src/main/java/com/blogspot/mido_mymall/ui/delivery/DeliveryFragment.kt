package com.blogspot.mido_mymall.ui.delivery

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.blogspot.mido_mymall.R
import com.blogspot.mido_mymall.databinding.CouponRedeemDialogBinding
import com.blogspot.mido_mymall.databinding.FragmentDeliveryBinding
import com.blogspot.mido_mymall.domain.models.AddressesModel
import com.blogspot.mido_mymall.domain.models.CartItemModel
import com.blogspot.mido_mymall.domain.models.Order
import com.blogspot.mido_mymall.domain.models.RewardModel
import com.blogspot.mido_mymall.ui.MainActivity
import com.blogspot.mido_mymall.ui.MainActivityViewModel
import com.blogspot.mido_mymall.ui.my_address.MyAddressesFragment.Companion.SELECTED_ADDRESS
import com.blogspot.mido_mymall.ui.my_cart.CartAdapter
import com.blogspot.mido_mymall.ui.my_cart.DeliveryUtil
import com.blogspot.mido_mymall.ui.my_rewards.MyRewardsAdapter
import com.blogspot.mido_mymall.ui.my_rewards.MyRewardsViewModel
import com.blogspot.mido_mymall.ui.product_details.ProductDetailsFragment
import com.blogspot.mido_mymall.util.Constants
import com.blogspot.mido_mymall.util.Resource
import com.google.android.material.button.MaterialButton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Date

@AndroidEntryPoint
class DeliveryFragment : Fragment(), DeliveryUtil {

    companion object {
        @JvmStatic
        var SELECT_ADDRESS_MODE = 0
        private const val TAG = "DeliveryFragment"

        @JvmStatic
        var getQtyIds = true
    }

    private var _binding: FragmentDeliveryBinding? = null
    private val binding get() = _binding!!

    private lateinit var cartAdapter: CartAdapter

    private val cartItemModelList = arrayListOf<CartItemModel>()
    private var myCartListIds = arrayListOf<String>()

    private val args by navArgs<DeliveryFragmentArgs>()

    private val deliveryViewModel by viewModels<DeliveryViewModel>()

    private val myAddress = arrayListOf<AddressesModel>()

    private val mainActivityViewModel by activityViewModels<MainActivityViewModel>()

//    private var allProductsAvailable = true

    private var paymentSuccessResponse = false

    private lateinit var firestore: FirebaseFirestore

    private val myRewardsViewModel by viewModels<MyRewardsViewModel>()

    private var fullAddrress: String = ""
    private var fullName: String = ""
    private var pinCode: String = ""


    private var rewardModelList = arrayListOf<RewardModel>()

    private var orderId = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeliveryBinding.inflate(inflater)

        firestore = FirebaseFirestore.getInstance()

        getQtyIds = true



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (!args.cartItemModelList.isNullOrEmpty()) {
            cartItemModelList.addAll(args.cartItemModelList!!)
        }

        if (!args.cartListIds.isNullOrEmpty()) {
            myCartListIds.addAll(args.cartListIds!!)
        }

        cartAdapter =
            CartAdapter(false, deliveryUtil = this, isDeliveryFragment = true)

        Log.d(TAG, "onViewCreated: ${cartItemModelList.size}")
        Log.d(TAG, "args cart size: ${args.cartItemModelList?.size}")

        cartAdapter.asyncListDiffer.submitList(args.cartItemModelList?.toList())

        binding.deliveryRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = cartAdapter

        }

        binding.totalPriceTV.text = "EGP. ${args.totalAmount}/-"


        deliveryViewModel.getAddress()

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                deliveryViewModel.myAddress.collect { response ->
                    when (response) {

                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            response.data?.let {
                                val listSize = it["list_size"] as Long

                                for (i in 1 until listSize + 1) {

//                                    val altMobileNum = it.get("alternateMobileNumber_$i") as? String

                                    myAddress.add(
                                        AddressesModel(
                                            it.get("city_$i").toString(),
                                            it.get("localityOrStreet_$i").toString(),
                                            it.get("flatNumberOrBuildingName_$i").toString(),
                                            it.get("pinCode_$i").toString(),
                                            it.get("state_$i").toString(),
                                            it.get("landMark_$i").toString(),
                                            it.get("fullName_$i").toString(),
                                            it.get("mobileNumber_$i").toString(),
                                            it.get("alternateMobileNumber_$i").toString(),
                                            selected = it.get("selected_$i") as Boolean
                                        )
                                    )

                                    if (it.get("selected_$i") as Boolean) {
                                        SELECTED_ADDRESS = (i - 1).toString().toInt()
                                    }
                                }

                            }.also {

                                if (myAddress.isNotEmpty()) {


                                    binding.shippingDetailsLayout.fullNameTV.text =
                                        myAddress[SELECTED_ADDRESS].fullName

                                    fullName = myAddress[SELECTED_ADDRESS].fullName

                                    fullAddrress =
                                        (myAddress[SELECTED_ADDRESS].flatNumberOrBuildingName
                                                + " " + myAddress[SELECTED_ADDRESS].localityOrStreet
                                                ) + " " + myAddress[SELECTED_ADDRESS].landMark.toString() + " " +
                                                myAddress[SELECTED_ADDRESS].city + " " + myAddress[SELECTED_ADDRESS].state

                                    binding.shippingDetailsLayout.fullAddressTV.text =
                                        fullAddrress



                                    binding.shippingDetailsLayout.pinCodeTV.text =
                                        myAddress[SELECTED_ADDRESS].pinCode

                                    pinCode = myAddress[SELECTED_ADDRESS].pinCode
                                }

                            }
                        }

                        is Resource.Error -> {
                            Log.e(TAG, "myAddresses: ${response.message.toString()}")
                            Toast.makeText(
                                requireContext(),
                                response.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {}
                    }

                }
            }
        }


        binding.shippingDetailsLayout.changeOrAddAddressBtn.setOnClickListener {
            getQtyIds = false
            findNavController().navigate(
                DeliveryFragmentDirections
                    .actionDeliveryFragmentToMyAddressesFragment(
                        SELECT_ADDRESS_MODE
                    )
            )
        }

        binding.cardContinueButton.setOnClickListener {

//            val amount =  binding.totalPriceTV.text.toString().substring(4,
//                binding.totalPriceTV.text.toString().length - 2
//            )

            if (cartItemModelList.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Please add the product to the cart",
                    Toast.LENGTH_SHORT
                ).show()
            }

            val order = Order(amount = (args.totalAmount * 100).toDouble(), "INR")

            val userName = "rzp_test_itLLgXvQdarfeC"
            val password = Constants.RAZORPAY_API_PASSWORD

            val base = "$userName:$password"

            val authHeader = "Basic " + Base64.encodeToString(base.toByteArray(), Base64.NO_WRAP)


            deliveryViewModel.createOrder(authHeader, order)


        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                deliveryViewModel.orderResponse.collect { response ->
                    when (response) {
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            response.data?.let {
//                                Log.d(TAG, "orderResponse: ID ${it.id}")

                                paymentSuccessResponse = true

                                getQtyIds = false
                                (requireActivity() as MainActivity)
                                    .startPayment(it.id, args.totalAmount.toString())
                            }


                        }

                        is Resource.Error -> {
                            paymentSuccessResponse = false
                            Log.e(TAG, "orderResponse: ${response.message.toString()}")
                        }

                        else -> {}
                    }
                }
            }
        }



        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainActivityViewModel.paymentState.collect {
                    if (it) {

//
//                        for (i in 0 until cartItemModelList.size - 1) {
//                            cartItemModelList[i].qtyIDs?.forEach { qtyID ->
//
//
//                                deliveryViewModel.updateAvailableQuantitiesIds(
//                                    cartItemModelList[i].productId!!,
//                                    qtyID
//                                )
//
//                            }
//                        }

                        mainActivityViewModel.orderId.collect { orderId ->
                            binding.orderConfirmationLayout.visibility = View.VISIBLE
                            binding.orderIdTV.text = "Order ID: $orderId"

                            binding.continueShoppingButton.setOnClickListener {
                                findNavController().navigate(DeliveryFragmentDirections.actionDeliveryFragmentToHomeFragment())
                            }

                            if (orderId.isNotEmpty()) {

                                this@DeliveryFragment.orderId = orderId

                                deliveryViewModel.placeOrder(
                                    orderID = orderId,
                                    cartItemModelList = cartItemModelList,
                                    address = fullAddrress,
                                    fullName = fullName,
                                    pinCode = pinCode,
                                    paymentMethod = "RazorPay"
                                )
                            }

                            if (args.fromCart) {
                                deliveryViewModel.updateCartListAfterRemove(
                                    myCartListIds,
                                    cartItemModelList
                                )
                            }

//                            binding.continueShoppingButton.setOnClickListener {
//                                findNavController().navigate(DeliveryFragmentDirections.actionDeliveryFragmentToHomeFragment())
//                            }
                        }

                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                deliveryViewModel.updateCartListAfterRemove.collect { response ->
                    if (response is Resource.Success) {

//                        Toast.makeText(requireContext(), "updated success", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "updateCartListAfterRemove: ${response.data}")
                        ProductDetailsFragment.ALREADY_ADDED_TO_CART_LIST = false

                    } else if (response is Resource.Error) {
                        Log.e(TAG, "updateCartListAfterRemove: ${response.message.toString()}")
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                deliveryViewModel.placeOrderStatus.collect { response->
//                    Log.d(TAG, "placeOrderStatus: ${it.message.toString()}")

                    when(response){

                        is Resource.Loading-> {
                            Log.d(TAG, "placeOrderStatus: response ${response.data}")

                        }

                        is Resource.Success ->{

                            Log.d(TAG, "placeOrderStatus: ${response.data}")

                        }

                        is Resource.Error->{
                            Log.e(TAG, "placeOrderStatus: ${response.message.toString()}", )
                        }

                        else -> {}
                    }



                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainActivityViewModel.updateOrderStatus.collect { response ->
                    if (response is Resource.Success) {
                        Log.d(TAG, "updateOrderStatus: ${response.data}")
                    } else if (response is Resource.Error) {

                        Log.e(TAG, "updateOrderStatus: ${response.message.toString()}" )
                        Toast.makeText(requireContext(), "Order Canceled", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }


    }


    override fun onStart() {
        super.onStart()
//        if (getQtyIds && cartItemModelList.isNotEmpty()) {
//            for (x in 0 until cartItemModelList.size - 1) {
//                for (y in 0 until cartItemModelList[x].productQuantity!!) {
//                    val quantityDocumentName = UUID.randomUUID().toString().substring(0, 20)
//
//                    val timestamp = HashMap<String, Any>()
//
//                    timestamp["time"] = FieldValue.serverTimestamp()
//
//                    firestore.collection("PRODUCTS").document(cartItemModelList[x].productId!!)
//                        .collection("QUANTITY").document(quantityDocumentName)
//                        .set(timestamp)
//                        .addOnSuccessListener {
//
//
//                            if (cartItemModelList[x].qtyIDs == null) {
//
//                                cartItemModelList[x].qtyIDs = arrayListOf<String>()
//
//                            }
//                            cartItemModelList[x].qtyIDs?.add(quantityDocumentName)
//
//                            Log.d(TAG, "onStart: qtyIds ${cartItemModelList.get(x).qtyIDs?.get(0)}")
//
//                            if (y + 1 == cartItemModelList[x].productQuantity) {
//                                firestore.collection("PRODUCTS")
//                                    .document(cartItemModelList[x].productId!!)
//                                    .collection("QUANTITY")
//                                    .orderBy("time", Query.Direction.ASCENDING)
//                                    .limit(cartItemModelList[x].stockQuantity!!)
//                                    .get()
//                                    .addOnSuccessListener { querySnapshot ->
//                                        val serverQuantities = arrayListOf<String>()
//
//                                        querySnapshot.documents.forEach {
//                                            serverQuantities.add(it.id)
//                                        }
//
//                                        cartItemModelList[x].qtyIDs?.forEach { qtyId ->
//                                            if (!serverQuantities.contains(qtyId)) {
//                                                Toast.makeText(
//                                                    requireContext(),
//                                                    "all products may not be available in required quantity",
//                                                    Toast.LENGTH_SHORT
//                                                ).show()
//                                                allProductsAvailable = false
//                                            }
//                                            if (serverQuantities.size >= cartItemModelList[x].stockQuantity!!) {
//                                                firestore.collection("PRODUCTS")
//                                                    .document(cartItemModelList[x].productId!!)
//                                                    .update("in_stock", false)
//
//
//                                            }
//                                        }
//
//                                    }.addOnFailureListener {
//                                        Log.e(TAG, "onStart: ${it.message.toString()}")
//                                    }
//                            }
//
//                        }.addOnFailureListener {
//                            Log.e(TAG, "onStart: ${it.message.toString()}")
//
//                        }
//
//                }
//            }
//        } else {
//            getQtyIds = true
//
//        }

    }

    override fun onStop() {
        super.onStop()
        if (getQtyIds) {
            for (i in 0 until cartItemModelList.size - 1) {
                if (!paymentSuccessResponse) {
                    cartItemModelList.get(i).qtyIDs!!.forEach { qtyId ->


                        try {
                            FirebaseFirestore.getInstance().collection("PRODUCTS")
                                .document(cartItemModelList[i].productId)
                                .collection("QUANTITY")
                                .document(qtyId).delete().addOnSuccessListener {

                                    Log.d(TAG, "onStop: success")
                //                                if (cartItemModelList[i].qtyIDs != null) {
                //
                //                                    if (cartItemModelList[i].qtyIDs!!.isNotEmpty()) {
                //
                //                                        if (qtyId.equals(
                //                                                cartItemModelList.get(i).qtyIDs?.get(
                //                                                    cartItemModelList.get(i).qtyIDs!!.size - 1
                //                                                )
                //                                            )
                //                                        )
                //                                            cartItemModelList[i].qtyIDs?.clear()
                //                                    }
                //                                }

                                    Log.d(TAG, "onStop: cartItemModelList ${cartItemModelList.size}")
                                    Log.d(TAG, "onStop: cartItemModelList is empty ${cartItemModelList.isEmpty()}")


                //                                    firestore.collection("PRODUCTS")
                //                                        .document(cartItemModelList[i].productId!!)
                //                                        .collection("QUANTITY")
                //                                        .orderBy("time", Query.Direction.ASCENDING)
                //                                        .get()
                //                                        .addOnSuccessListener {
                //                                            if (it.documents.size < cartItemModelList[i].stockQuantity!!) {
                //                                                firestore.collection("PRODUCTS")
                //                                                    .document(cartItemModelList[i].productId!!)
                //                                                    .update("in_stock", true)
                //                                            }
                //
                //                                        }.addOnFailureListener {
                //
                //                                        }


                                    Log.d(TAG, "onStop: qtyId deleted")
                                }.addOnFailureListener {
                                    Log.e(TAG, "onStop: ${it.message.toString()}")
                                }
                        }catch (ex:Exception){
                            Log.e(TAG, "onStop: ${ex.message.toString()}" )
                            Log.e(TAG, "onStop: ${ex.cause.toString()}" )
                        }
                    }
                } else {
                    cartItemModelList[i].qtyIDs!!.clear()
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()

        myCartListIds.clear()
        cartItemModelList.clear()
        _binding = null
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun couponRedemptionButtonClick(
        productOriginalPrice: String,
        productCuttedPrice: String,
        position: Int,
        cartCouponRedemptionLayout: ConstraintLayout,
        couponRedemptionButton: MaterialButton,
        couponRedemptionTV: TextView,
        couponsApplied: TextView,
        productPriceTV: TextView,
        totalItemsPriceTV: TextView?,
        totalAmountTV: TextView?,
        quantity: Long
    ) {


        val couponRedeemDialogBinding: CouponRedeemDialogBinding =
            CouponRedeemDialogBinding.inflate(layoutInflater, null, false)

        val checkCouponPriceDialog = Dialog(requireActivity())
        checkCouponPriceDialog.setContentView(couponRedeemDialogBinding.root)
        checkCouponPriceDialog.setCancelable(true)
        checkCouponPriceDialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        var lastSeen: Date? = null


        val rewardsAdapter = MyRewardsAdapter(
            cartItemModelList,
            position,
            true,
            couponRedeemDialogBinding.couponRecyclerView,
            couponRedeemDialogBinding.selectedCoupon,
            productOriginalPrice,
            couponRedeemDialogBinding.couponContainer.couponTitle,
            couponRedeemDialogBinding.couponContainer.couponValidity,
            couponRedeemDialogBinding.couponContainer.couponBody,
            couponRedeemDialogBinding.discountedPriceTV

        )

        myRewardsViewModel.getUserLastSeen()

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                myRewardsViewModel.userLastSeen.collect { response ->

                    when (response) {

                        is Resource.Loading -> {
//                                binding.progressBar.visibility = View.VISIBLE
                        }

                        is Resource.Success -> {
                            response.data?.let {
                                lastSeen = it.getDate("Last seen")
                            }.also {
                                myRewardsViewModel.getRewards()
                            }
                        }

                        is Resource.Error -> {
//                                binding.progressBar.visibility = View.GONE
                            Log.e(TAG, "userLastSeen: ${response.message.toString()}")
                        }

                        else -> {}
                    }


                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                myRewardsViewModel.rewardList.collect { response ->
                    when (response) {

                        is Resource.Loading -> {
//                                binding.progressBar.visibility = View.VISIBLE
                        }

                        is Resource.Success -> {

                            rewardModelList.clear().also {
                                response.data?.let { querySnapshot ->
                                    querySnapshot.documents.forEach {

                                        if (it["type"].toString().equals("Discount")
                                            && lastSeen?.before((it.getDate("validity") as Date)) == true
                                        ) {
                                            rewardModelList.add(
                                                RewardModel(
                                                    it.id,
                                                    it["type"].toString(),
                                                    it["lower_limit"].toString(),
                                                    it["upper_limit"].toString(),
                                                    it["percentage"].toString(),
                                                    it["body"].toString(),
                                                    it["validity"] as Timestamp,
                                                    it["already_used"] as Boolean

                                                )
                                            )
                                        } else if (it["type"].toString().equals("Flat EGP. * OFF")
                                            && lastSeen?.before((it.getDate("validity") as Date)) == true
                                        ) {
                                            rewardModelList.add(
                                                RewardModel(
                                                    it.id,
                                                    it["type"].toString(),
                                                    it["lower_limit"].toString(),
                                                    it["upper_limit"].toString(),
                                                    it["amount"].toString(),
                                                    it["body"].toString(),
                                                    it["validity"] as Timestamp,
                                                    it["already_used"] as Boolean

                                                )
                                            )
                                        }


                                    }
                                }.also {
//                                    binding.progressBar.visibility = View.GONE
//                                    myRewardsAdapter.asyncListDiffer.submitList(rewardModelList)
                                    rewardsAdapter.asyncListDiffer.submitList(rewardModelList)
                                    cartAdapter.submitRewardList(rewardModelList)
                                }
                            }

                        }

                        is Resource.Error -> {
//                                binding.progressBar.visibility = View.GONE
                            Log.e(TAG, "rewardList: ${response.message.toString()}")
                        }

                        else -> {}
                    }
                }
            }
        }

        couponRedeemDialogBinding.applyOrRemoveContainer.visibility = View.VISIBLE
        couponRedeemDialogBinding.footerText.visibility = View.GONE

        couponRedeemDialogBinding.applyButton.setOnClickListener {

            var offerDiscountedAmount: Long = 0

            if (!cartItemModelList[position].selectedCouponId.isNullOrEmpty()) {
                rewardModelList.forEach {
                    if (it.couponId.equals(cartItemModelList[position].selectedCouponId)) {
                        it.alreadyUsed = true
                        cartCouponRedemptionLayout.background = resources.getDrawable(
                            R.drawable.reward_gradient_background,
                            requireContext().theme
                        )
                        couponRedemptionTV.text = rewardModelList[position].couponBody
                        couponRedemptionButton.text = "Coupon"
                    }

                }.also {

                    offerDiscountedAmount =
                        productOriginalPrice.toLong() * rewardModelList[position].discountOrAmount.toLong() / 100
                    couponsApplied.visibility = View.VISIBLE



                    cartItemModelList[position].discountedPrice = offerDiscountedAmount.toString()

                    couponsApplied.text = "Coupon applied -${offerDiscountedAmount}"
                    productPriceTV.text =
                        "EGP. ${((productOriginalPrice.toLong() * quantity) - offerDiscountedAmount)} /-"
                    totalItemsPriceTV?.text =
                        "EGP. ${((productOriginalPrice.toLong() * quantity) - offerDiscountedAmount)} /-"
                    totalAmountTV?.text =
                        "EGP. ${((productOriginalPrice.toLong() * quantity) - offerDiscountedAmount)} /-"
                    binding.totalPriceTV.text =
                        "EGP. ${((productOriginalPrice.toLong() * quantity) - offerDiscountedAmount)} /-"
                    checkCouponPriceDialog.dismiss()
                }

            }
        }

        couponRedeemDialogBinding.removeButton.setOnClickListener {
            rewardModelList.forEach {
                if (it.couponId.equals(cartItemModelList[position].selectedCouponId)) {
                    it.alreadyUsed = false
                }
            }
            couponsApplied.visibility = View.INVISIBLE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cartCouponRedemptionLayout.setBackgroundColor(
                    resources.getColor(
                        R.color.couponRed,
                        requireContext().theme
                    )
                )
            } else {
                cartCouponRedemptionLayout.setBackgroundColor(resources.getColor(R.color.couponRed))

            }
            couponRedemptionTV.text = "Apply your coupon here"
            couponRedemptionButton.text = "Redeem"

            cartItemModelList[position].selectedCouponId = null


            productPriceTV.text = "Rs. ${(productOriginalPrice.toLong() * quantity)}/-"
            totalItemsPriceTV?.text = "Rs. ${(productOriginalPrice.toLong() * quantity)}/-"
            totalAmountTV?.text = "Rs. ${(productOriginalPrice.toLong() * quantity)}/-"
            binding.totalPriceTV.text = "Rs. ${((productOriginalPrice.toLong() * quantity))} /-"



            checkCouponPriceDialog.dismiss()


        }



        couponRedeemDialogBinding.couponRecyclerView.layoutManager = linearLayoutManager
        couponRedeemDialogBinding.couponRecyclerView.adapter = rewardsAdapter

        couponRedeemDialogBinding.originalPriceTV.text = "Rs. $productOriginalPrice/-"



        couponRedeemDialogBinding.couponRedeemBtn.setOnClickListener { view3 ->
            if (couponRedeemDialogBinding.couponRecyclerView.visibility == View.GONE) {
                couponRedeemDialogBinding.couponRecyclerView.visibility = View.VISIBLE
                couponRedeemDialogBinding.selectedCoupon.visibility = View.GONE
            } else {
                couponRedeemDialogBinding.couponRecyclerView.visibility = View.GONE
                couponRedeemDialogBinding.selectedCoupon.visibility = View.VISIBLE
            }
        }
        checkCouponPriceDialog.show()

        checkCouponPriceDialog.setOnDismissListener {
            rewardModelList.clear()
        }
    }

}