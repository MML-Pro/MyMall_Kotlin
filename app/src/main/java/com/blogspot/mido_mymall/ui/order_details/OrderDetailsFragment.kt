package com.blogspot.mido_mymall.ui.order_details

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.blogspot.mido_mymall.R
import com.blogspot.mido_mymall.data.CartDataManager
import com.blogspot.mido_mymall.databinding.FragmentOrderDetailsBinding
import com.blogspot.mido_mymall.domain.models.CartSummary
import com.blogspot.mido_mymall.ui.my_orders.MyOrdersFragment
import com.blogspot.mido_mymall.ui.my_orders.MyOrdersViewModel
import com.blogspot.mido_mymall.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


private const val TAG = "OrderDetailsFragment"

@AndroidEntryPoint
class OrderDetailsFragment : Fragment() {

    private var _binding: FragmentOrderDetailsBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<OrderDetailsFragmentArgs>()

    private val orderDetailsAdapter = OrderDetailsAdapter()

    private val cartDataManager = CartDataManager()

    private val myOrdersViewModel by viewModels<MyOrdersViewModel>()

    private val myRatingsIds = arrayListOf<String>()
//    private val myRatings = arrayListOf<Long>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orderDetailsAdapter.asyncListDiffer.submitList(args.myOrderItem.productList).also {

            val cartSummary = cartDataManager.calculateTotalAmount(args.myOrderItem.productList)

            submitCartSummaryData(cartSummary)

            myOrdersViewModel.getRatings()


        }


        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                myOrdersViewModel.ratingsIds.collect { response ->

                    when (response) {

                        is Resource.Success -> {
                            response.data?.let { ratingDoc ->
                                val listSize = ratingDoc.get("list_size") as Long

                                val orderProductIds = arrayListOf<String>()

                                args.myOrderItem.productList.forEach { myOrderItemModel ->

                                    orderProductIds.add(myOrderItemModel.productId.toString())
                                }

                                Log.d(TAG, "orderProductIds: size ${orderProductIds.size}")

                                for (i in 0 until listSize) {

                                    myRatingsIds.add(ratingDoc.get("product_id_$i").toString())

//                                    myRatings.add(it.get("rating_$i") as Long)

                                    val ratingId = ratingDoc.get("product_id_$i").toString()

//                                    if (ratingId.equals(productId)) {
//                                        setRating(it.get("rating_$i") as Long, true)
//                                    }


                                    orderProductIds.forEachIndexed { index, productId ->

                                        val rate = (ratingDoc.get("rating_$i") as Long).toInt()

                                        if (productId.equals(ratingId)) {
                                            args.myOrderItem.productList[index].productRating = rate

                                        }

                                    }

                                }


                            }.also {
                                orderDetailsAdapter.notifyDataSetChanged()

//                                myOrderAdapter.submitRatingLists(myRatingsIds, myRatings)
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

        binding.orderDetailsLayout.apply {

            productsRV.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = orderDetailsAdapter

                val dividerItemDecoration = DividerItemDecoration(
                    requireContext(), DividerItemDecoration.VERTICAL
                )

                dividerItemDecoration.setDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.recycler_view_divider
                    )!!
                )

                addItemDecoration(dividerItemDecoration)
            }


            when (args.myOrderItem.orderStatus) {
                "ORDERED" -> {

                    val orderedDate = args.myOrderItem.orderDate!!

                    val simpleDateFormat =
                        SimpleDateFormat("EEE, dd MMM yyyy - h:mm a", Locale.ENGLISH)


                    val formattedDate = simpleDateFormat.format(orderedDate)

                    orderedIndicator.imageTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.success))
                    orderedDateTV.text = formattedDate

                    firstProgress.apply {
                        firstProgress.visibility = View.VISIBLE
                        progress = 50
                        progressTintList =
                            ColorStateList.valueOf(resources.getColor(R.color.success))
                    }

                    secondProgress.visibility = View.GONE
                    shippedDeliveredProgress.visibility = View.GONE


                    packedTV.visibility = View.GONE
                    packedIndicator.visibility = View.GONE
                    packedBodyTv.visibility = View.GONE
                    packedDateTV.visibility = View.GONE

                    shippedTv.visibility = View.GONE
                    shippedIndicator.visibility = View.GONE
                    shippedBodyTv.visibility = View.GONE
                    shippedDateTV.visibility = View.GONE

                    deliveredTv.visibility = View.GONE
                    deliveredIndicator.visibility = View.GONE
                    deliveredBodyTv.visibility = View.INVISIBLE
                    deliveredDateTV.visibility = View.GONE


                }

                "PACKED" -> {

                    val orderedDate = args.myOrderItem.orderDate!!

                    val simpleDateFormat =
                        SimpleDateFormat("EEE, dd MMM yyyy - h:mm a", Locale.ENGLISH)


                    val formattedOrderedDate = simpleDateFormat.format(orderedDate)

                    orderedIndicator.imageTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.success))
                    orderedDateTV.text = formattedOrderedDate

                    val packedDate = args.myOrderItem.packedDate!!

                    val formattedPackedDate = simpleDateFormat.format(packedDate)

                    packedIndicator.imageTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.success))
                    packedDateTV.text = formattedPackedDate

                    firstProgress.apply {
                        progress = 100
                        progressTintList =
                            ColorStateList.valueOf(resources.getColor(R.color.success))
                    }
                    secondProgress.visibility = View.GONE
                    shippedDeliveredProgress.visibility = View.GONE

                    shippedDateTV.visibility = View.GONE
                    shippedTv.visibility = View.GONE
                    shippedIndicator.visibility = View.GONE
                    shippedBodyTv.visibility = View.GONE
                    shippedTv.visibility = View.GONE

                    deliveredDateTV.visibility = View.GONE
                    deliveredTv.visibility = View.GONE
                    deliveredIndicator.visibility = View.GONE
                    deliveredBodyTv.visibility = View.INVISIBLE
                    deliveredTv.visibility = View.GONE
                }

                "SHIPPED" -> {

                    val orderedDate = args.myOrderItem.orderDate!!

                    val simpleDateFormat =
                        SimpleDateFormat("EEE, dd MMM yyyy - h:mm a", Locale.ENGLISH)


                    val formattedOrderedDate = simpleDateFormat.format(orderedDate)

                    val packedDate = args.myOrderItem.packedDate!!

                    val formattedPackedDate = simpleDateFormat.format(packedDate)

                    val shippedDate = args.myOrderItem.shippedDate!!


                    val formattedShippedDate = simpleDateFormat.format(shippedDate)



                    orderedIndicator.imageTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.success))
                    orderedDateTV.text = formattedOrderedDate

                    packedIndicator.imageTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.success))
                    packedDateTV.text = formattedPackedDate

                    shippedIndicator.imageTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.success))
                    shippedDateTV.text = formattedShippedDate

                    firstProgress.apply {
                        progress = 100
                        progressTintList =
                            ColorStateList.valueOf(resources.getColor(R.color.success))
                    }
                    secondProgress.apply {
                        progress = 100
                        progressTintList =
                            ColorStateList.valueOf(resources.getColor(R.color.success))
                    }
                    shippedDeliveredProgress.visibility = View.GONE


                    deliveredTv.visibility = View.GONE
                    deliveredDateTV.visibility = View.GONE
                    deliveredIndicator.visibility = View.GONE
                    deliveredBodyTv.visibility = View.INVISIBLE
                    deliveredTv.visibility = View.GONE
                }

                "Out for Delivery" -> {

                    val orderedDate = args.myOrderItem.orderDate!!

                    val simpleDateFormat =
                        SimpleDateFormat("EEE, dd MMM yyyy - h:mm a", Locale.ENGLISH)


                    val formattedOrderedDate = simpleDateFormat.format(orderedDate)


                    val packedDate = args.myOrderItem.packedDate!!

                    val formattedPackedDate = simpleDateFormat.format(packedDate)

                    val shippedDate = args.myOrderItem.shippedDate!!


                    val formattedShippedDate = simpleDateFormat.format(shippedDate)


                    val deliveredDate = args.myOrderItem.deliveredDate!!

                    val formattedDeliveredDate = simpleDateFormat.format(deliveredDate)


                    orderedIndicator.imageTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.success))
                    orderedDateTV.text = formattedOrderedDate

                    packedIndicator.imageTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.success))
                    packedDateTV.text = formattedPackedDate

                    shippedIndicator.imageTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.success))
                    shippedDateTV.text = formattedShippedDate

                    deliveredIndicator.imageTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.success))
                    deliveredDateTV.text = formattedDeliveredDate

                    firstProgress.progress = 100
                    secondProgress.progress = 100
                    shippedDeliveredProgress.progress = 100

                    deliveredTv.text = "Out for Delivery"
                    deliveredBodyTv.text = "Your order is out for Delivery"

                }

                "DELIVERED" -> {

                    val orderedDate = args.myOrderItem.orderDate!!

                    val simpleDateFormat =
                        SimpleDateFormat("EEE, dd MMM yyyy - h:mm a", Locale.ENGLISH)


                    val formattedOrderedDate = simpleDateFormat.format(orderedDate)


                    val packedDate = args.myOrderItem.packedDate!!

                    val formattedPackedDate = simpleDateFormat.format(packedDate)

                    val shippedDate = args.myOrderItem.shippedDate!!


                    val formattedShippedDate = simpleDateFormat.format(shippedDate)


                    val deliveredDate = args.myOrderItem.deliveredDate!!

                    val formattedDeliveredDate = simpleDateFormat.format(deliveredDate)


                    orderedIndicator.imageTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.success))
                    orderedDateTV.text = formattedOrderedDate

                    packedIndicator.imageTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.success))
                    packedDateTV.text = formattedPackedDate

                    shippedIndicator.imageTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.success))
                    shippedDateTV.text = formattedShippedDate

                    deliveredIndicator.imageTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.success))
                    deliveredDateTV.text = formattedDeliveredDate

                    firstProgress.progress = 100
                    secondProgress.progress = 100
                    shippedDeliveredProgress.progress = 100


                }

                "CANCELED" -> {

                    val orderedDate = args.myOrderItem.orderDate!!

                    val simpleDateFormat =
                        SimpleDateFormat("EEE, dd MMM yyyy - h:mm a", Locale.ENGLISH)


                    val formattedOrderedDate = simpleDateFormat.format(orderedDate)


                    val packedDate = args.myOrderItem.packedDate!!

                    val formattedPackedDate = simpleDateFormat.format(packedDate)

                    val shippedDate = args.myOrderItem.shippedDate!!


                    val formattedShippedDate = simpleDateFormat.format(shippedDate)


                    val deliveredDate = args.myOrderItem.deliveredDate!!

                    val formattedDeliveredDate = simpleDateFormat.format(deliveredDate)

                    if (args.myOrderItem.packedDate?.after(args.myOrderItem.orderDate) == true) {
                        if (args.myOrderItem.shippedDate?.after(args.myOrderItem.packedDate) == true) {


                            orderedIndicator.imageTintList =
                                ColorStateList.valueOf(resources.getColor(R.color.success))
                            orderedDateTV.text = formattedOrderedDate

                            packedIndicator.imageTintList =
                                ColorStateList.valueOf(resources.getColor(R.color.success))
                            packedDateTV.text = formattedPackedDate

                            shippedIndicator.imageTintList =
                                ColorStateList.valueOf(resources.getColor(R.color.success))
                            shippedDateTV.text = formattedShippedDate

                            deliveredIndicator.imageTintList =
                                ColorStateList.valueOf(resources.getColor(R.color.colorPrimary))
                            deliveredDateTV.text = formattedDeliveredDate

                            deliveredTv.text = "Cancelled"
                            deliveredBodyTv.text = "Your order has been cancelled"

                            firstProgress.progress = 100
                            secondProgress.progress = 100
                            shippedDeliveredProgress.progress = 100


                        } else {
                            orderedIndicator.imageTintList =
                                ColorStateList.valueOf(resources.getColor(R.color.success))
                            orderedDateTV.text = formattedOrderedDate

                            packedIndicator.imageTintList =
                                ColorStateList.valueOf(resources.getColor(R.color.success))
                            packedDateTV.text = formattedPackedDate

                            shippedIndicator.imageTintList =
                                ColorStateList.valueOf(resources.getColor(R.color.colorPrimary))
                            shippedDateTV.text = formattedShippedDate
                            shippedTv.text = getString(R.string.cancelled)
                            shippedBodyTv.text = getString(R.string.your_order_has_been_cancelled)

                            firstProgress.progress = 100
                            secondProgress.progress = 100
                            shippedDeliveredProgress.visibility = View.GONE


                            deliveredTv.visibility = View.GONE
                            deliveredIndicator.visibility = View.GONE
                            deliveredBodyTv.visibility = View.INVISIBLE
                            deliveredDateTV.visibility = View.GONE
                        }
                    } else {
                        orderedIndicator.imageTintList =
                            ColorStateList.valueOf(resources.getColor(R.color.success))
                        orderedDateTV.text = formattedOrderedDate

                        packedIndicator.imageTintList =
                            ColorStateList.valueOf(resources.getColor(R.color.colorPrimary))
                        packedDateTV.text = formattedPackedDate
                        packedTV.text = getString(R.string.cancelled)
                        packedBodyTv.text = getString(R.string.your_order_has_been_cancelled)

                        firstProgress.progress = 100
                        secondProgress.visibility = View.GONE
                        shippedDeliveredProgress.visibility = View.GONE

                        shippedTv.visibility = View.GONE
                        shippedIndicator.visibility = View.GONE
                        shippedBodyTv.visibility = View.GONE
                        shippedDateTV.visibility = View.GONE

                        deliveredTv.visibility = View.GONE
                        deliveredIndicator.visibility = View.GONE
                        deliveredBodyTv.visibility = View.INVISIBLE
                        deliveredDateTV.visibility = View.GONE
                    }
                }
            }

//            args.myOrderItem.productRating?.toLong()?.let { setRating(it,true) }


        }

        binding.shippingDetailsLayout.apply {
            fullNameTV.text = args.myOrderItem.fullName
            fullAddressTV.text = args.myOrderItem.address
            pinCodeTV.text = args.myOrderItem.pinCode
        }


//        binding.cartTotalAmountLayout.apply {
//            totalItems.text = "Price (${args.myOrderItem.productList.size} items)"
//
//            val totalItemsPriceValue: Long?
//            if (args.myOrderItem.discountedPrice != null && args.myOrderItem.discountedPrice!! != "0") {
//
//                totalItemsPriceValue =
//                    args.myOrderItem.productQuantity?.times(args.myOrderItem.discountedPrice!!.toLong())
//
//                totalItemsPrice.text = "Rs. $totalItemsPriceValue /-"
//
//            } else {
//                totalItemsPriceValue =
//                    args.myOrderItem.productQuantity?.times(args.myOrderItem.productPrice!!.toLong())
//
//                totalItemsPrice.text = "EGP. $totalItemsPriceValue /-"
//            }
//
//            if (args.myOrderItem.deliveryPrice.equals("Free")) {
//                deliveryPrice.text = args.myOrderItem.deliveryPrice
//                totalAmountTv.text = totalItemsPrice.text
//            } else {
//                deliveryPrice.text = "EGP. ${args.myOrderItem.deliveryPrice} /-"
//                totalAmountTv.text =
//                    "EGP. ${totalItemsPriceValue?.plus(args.myOrderItem.deliveryPrice?.toLong()!!)} /-"
//            }
//
//            var savedAmountValue: Long = 0
//
//            if (args.myOrderItem.cuttedPrice?.isNotEmpty()!!) {
//                savedAmountValue += (args.myOrderItem.cuttedPrice!!.toInt() - args.myOrderItem.productPrice?.toInt()!!) * args.myOrderItem.productQuantity!!.toInt()
//
//                if (args.myOrderItem.couponID.isNullOrEmpty()) {
//                    savedAmountValue += (args.myOrderItem.productPrice?.toInt()!! - args.myOrderItem.discountedPrice?.toInt()!!) * args.myOrderItem.productQuantity!!.toInt()
//                }
//
//                savedAmount.text = "You saved EGP.$savedAmountValue/- on this order"
//
//            } else {
//                if (args.myOrderItem.couponID?.isNotEmpty()!!) {
//                    savedAmountValue += (args.myOrderItem.productPrice?.toInt()!! - args.myOrderItem.discountedPrice?.toInt()!!) * args.myOrderItem.productQuantity!!.toInt()
//                }
//                savedAmount.text = "You saved EGP.$savedAmountValue/- on this order"
//            }
//
//            savedAmount.text = "You saved EGP.$savedAmountValue/- on this order"
//
//
//        }


    }

    private fun submitCartSummaryData(cartSummary: CartSummary) {

        binding.cartTotalAmountLayout.apply {


            Log.d(TAG, "submitCartSummaryData: totalItems ${cartSummary.totalItems}")
            Log.d(TAG, "submitCartSummaryData: totalItemsPrice ${cartSummary.totalItemsPrice}")
            Log.d(TAG, "submitCartSummaryData: deliveryPrice ${cartSummary.deliveryPrice}")
            Log.d(TAG, "submitCartSummaryData: totalAmount ${cartSummary.totalAmount}")
            Log.d(TAG, "submitCartSummaryData: savedAmount ${cartSummary.savedAmount}")

            val formattedTotalItemsPrice = String.format(
                    locale = Locale.ENGLISH,
                    "%.2f",
                    cartSummary.totalItemsPrice
                )


            totalItems.text = getString(R.string.price_number_items, cartSummary.totalItems)

            cartSummaryTotalItemsPrice.text =
                getString(R.string.egp_price, formattedTotalItemsPrice)

            if (cartSummary.deliveryPrice == "Free") {
                cartSummaryDeliveryPrice.text = getString(R.string.free)
            } else {
                cartSummaryDeliveryPrice.text =
                    getString(R.string.egp_price, cartSummary.deliveryPrice)

            }

            cartSummaryTotalAmountTV.text =
                getString(R.string.egp_price, cartSummary.totalAmount.toString())

            val savedAmountValue = String.format(
                locale = Locale.ENGLISH,
                "%.2f",
                cartSummary.savedAmount
            )

            savedAmount.text = getString(R.string.saved_amount, savedAmountValue)
        }
    }

//    @Suppress("DEPRECATION")
//    private fun setRating(starPosition: Long, isGetRating: Boolean) {
//        for (i in 0 until binding.orderDetailsLayout.rateNowContainer.childCount) {
//            val starBtn = binding.orderDetailsLayout.rateNowContainer.getChildAt(i) as ImageView
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                starBtn.imageTintList =
//                    ColorStateList.valueOf(
//                        binding.root.resources.getColor(
//                            R.color.mediumGray,
//                            binding.root.context.theme
//                        )
//                    )
//            } else {
//                starBtn.imageTintList =
//                    ColorStateList.valueOf(binding.root.resources.getColor(R.color.mediumGray))
//            }
//
//            if (isGetRating) {
//                if (i < starPosition) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        starBtn.imageTintList =
//                            ColorStateList.valueOf(
//                                binding.root.resources.getColor(
//                                    R.color.ratingColor,
//                                    binding.root.context.theme
//                                )
//                            )
//                    } else {
//                        starBtn.imageTintList =
//                            ColorStateList.valueOf(binding.root.resources.getColor(R.color.ratingColor))
//                    }
//                }
//            } else {
//                if (i <= starPosition) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        starBtn.imageTintList =
//                            ColorStateList.valueOf(
//                                binding.root.resources.getColor(
//                                    R.color.ratingColor,
//                                    binding.root.context.theme
//                                )
//                            )
//                    } else {
//                        starBtn.imageTintList =
//                            ColorStateList.valueOf(binding.root.resources.getColor(R.color.ratingColor))
//                    }
//                }
//            }
//        }
//    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}