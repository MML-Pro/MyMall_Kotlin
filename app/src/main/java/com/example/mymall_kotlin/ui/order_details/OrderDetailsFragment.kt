package com.example.mymall_kotlin.ui.order_details

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.mymall_kotlin.R
import com.example.mymall_kotlin.databinding.FragmentOrderDetailsBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "OrderDetailsFragment"

class OrderDetailsFragment : Fragment() {

    private var _binding: FragmentOrderDetailsBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<OrderDetailsFragmentArgs>()

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

        binding.orderDetailsLayout.apply {

            Glide.with(this@OrderDetailsFragment)
                .load(args.myOrderItem.productImage)
                .placeholder(R.drawable.placeholder_image)
                .into(productImage)

            productName.text = args.myOrderItem.productName

            if (args.myOrderItem.productPrice?.isNotEmpty() == true) {
                productPrice.text = "Rs.${args.myOrderItem.productPrice} /-"
            } else {
                productPrice.text = "Rs.${args.myOrderItem.discountedPrice} /-"
            }


//            productPrice.text = "Rs.${args.myOrderItem.productPrice} /-"

            Log.d(TAG, "onViewCreated: ${args.myOrderItem.productPrice}")

            productQty.text = "Qty: ${args.myOrderItem.productQuantity}"



            when (args.myOrderItem.orderStatus) {
                "ORDERED" -> {

                    val orderedDate  = args.myOrderItem.orderDate!!

                    val simpleDateFormat = SimpleDateFormat("EEE, dd MMM yyyy - h:mm a", Locale.ENGLISH)



                    val formattedDate = simpleDateFormat.format(orderedDate)

                    orderedIndicator.imageTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.success))
                    orderedDateTV.text = formattedDate

                    firstProgress.visibility = View.GONE
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

                    val orderedDate  = args.myOrderItem.orderDate!!

                    val simpleDateFormat = SimpleDateFormat("EEE, dd MMM yyyy - h:mm a", Locale.ENGLISH)



                    val formattedOrderedDate = simpleDateFormat.format(orderedDate)

                    orderedIndicator.imageTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.success))
                    orderedDateTV.text = formattedOrderedDate

                    val packedDate  = args.myOrderItem.packedDate!!

                    val formattedPackedDate = simpleDateFormat.format(packedDate)

                    packedIndicator.imageTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.success))
                    packedDateTV.text = formattedPackedDate

                    firstProgress.apply {
                        progress = 100
                        progressTintList = ColorStateList.valueOf(resources.getColor(R.color.success))
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

                    val orderedDate  = args.myOrderItem.orderDate!!

                    val simpleDateFormat = SimpleDateFormat("EEE, dd MMM yyyy - h:mm a", Locale.ENGLISH)



                    val formattedOrderedDate = simpleDateFormat.format(orderedDate)

                    val packedDate  = args.myOrderItem.packedDate!!

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
                        progressTintList = ColorStateList.valueOf(resources.getColor(R.color.success))
                    }
                    secondProgress.apply {
                        progress = 100
                        progressTintList = ColorStateList.valueOf(resources.getColor(R.color.success))
                    }
                    shippedDeliveredProgress.visibility = View.GONE


                    deliveredTv.visibility = View.GONE
                    deliveredDateTV.visibility = View.GONE
                    deliveredIndicator.visibility = View.GONE
                    deliveredBodyTv.visibility = View.INVISIBLE
                    deliveredTv.visibility = View.GONE
                }

                "Out for Delivery" ->{

                    val orderedDate  = args.myOrderItem.orderDate!!

                    val simpleDateFormat = SimpleDateFormat("EEE, dd MMM yyyy - h:mm a", Locale.ENGLISH)



                    val formattedOrderedDate = simpleDateFormat.format(orderedDate)


                    val packedDate  = args.myOrderItem.packedDate!!

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

                    val orderedDate  = args.myOrderItem.orderDate!!

                    val simpleDateFormat = SimpleDateFormat("EEE, dd MMM yyyy - h:mm a", Locale.ENGLISH)



                    val formattedOrderedDate = simpleDateFormat.format(orderedDate)


                    val packedDate  = args.myOrderItem.packedDate!!

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

                "CANCELLED" -> {

                    val orderedDate  = args.myOrderItem.orderDate!!

                    val simpleDateFormat = SimpleDateFormat("EEE, dd MMM yyyy - h:mm a", Locale.ENGLISH)



                    val formattedOrderedDate = simpleDateFormat.format(orderedDate)


                    val packedDate  = args.myOrderItem.packedDate!!

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
                            shippedTv.text = "Cancelled"
                            shippedBodyTv.text = "Your order has been cancelled"

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
                        packedTV.text = "Cancelled"
                        packedBodyTv.text = "Your order has been cancelled"

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

            args.myOrderItem.productRating?.toLong()?.let { setRating(it,true) }


        }

        binding.shippingDetailsLayout.apply {
            fullNameTV.text = args.myOrderItem.fullName
            fullAddressTV.text = args.myOrderItem.address
            pinCodeTV.text = args.myOrderItem.pinCode
        }

        binding.cartTotalAmountLayout.apply {
            totalItems.text = "Price (${args.myOrderItem.productQuantity} items)"

            val totalItemsPriceValue: Long?
            if (args.myOrderItem.discountedPrice != null && args.myOrderItem.discountedPrice!! != "0") {

                totalItemsPriceValue =
                    args.myOrderItem.productQuantity?.times(args.myOrderItem.discountedPrice!!.toLong())

                totalItemsPrice.text = "Rs. $totalItemsPriceValue /-"

            } else {
                totalItemsPriceValue =
                    args.myOrderItem.productQuantity?.times(args.myOrderItem.productPrice!!.toLong())

                totalItemsPrice.text = "Rs. $totalItemsPriceValue /-"
            }

            if (args.myOrderItem.deliveryPrice.equals("Free")) {
                deliveryPrice.text = args.myOrderItem.deliveryPrice
                totalAmountTv.text = totalItemsPrice.text
            } else {
                deliveryPrice.text = "Rs. ${args.myOrderItem.deliveryPrice} /-"
                totalAmountTv.text =
                    "Rs. ${totalItemsPriceValue?.plus(args.myOrderItem.deliveryPrice?.toLong()!!)} /-"
            }

            var savedAmountValue: Long = 0

            if (args.myOrderItem.cuttedPrice?.isNotEmpty()!!) {
                savedAmountValue += (args.myOrderItem.cuttedPrice!!.toInt() - args.myOrderItem.productPrice?.toInt()!!) * args.myOrderItem.productQuantity!!.toInt()

                if (args.myOrderItem.couponID.isNullOrEmpty()) {
                    savedAmountValue += (args.myOrderItem.productPrice?.toInt()!! - args.myOrderItem.discountedPrice?.toInt()!!) * args.myOrderItem.productQuantity!!.toInt()
                }

                savedAmount.text = "You saved Rs.$savedAmountValue/- on this order"

            } else {
                if (args.myOrderItem.couponID?.isNotEmpty()!!) {
                    savedAmountValue += (args.myOrderItem.productPrice?.toInt()!! - args.myOrderItem.discountedPrice?.toInt()!!) * args.myOrderItem.productQuantity!!.toInt()
                }
                savedAmount.text = "You saved Rs.$savedAmountValue/- on this order"
            }

            savedAmount.text = "You saved Rs.$savedAmountValue/- on this order"


        }



    }

    @Suppress("DEPRECATION")
    private fun setRating(starPosition: Long, isGetRating: Boolean) {
        for (i in 0 until binding.orderDetailsLayout.rateNowContainer.childCount) {
            val starBtn = binding.orderDetailsLayout.rateNowContainer.getChildAt(i) as ImageView
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                starBtn.imageTintList =
                    ColorStateList.valueOf(
                        binding.root.resources.getColor(
                            R.color.mediumGray,
                            binding.root.context.theme
                        )
                    )
            } else {
                starBtn.imageTintList =
                    ColorStateList.valueOf(binding.root.resources.getColor(R.color.mediumGray))
            }

            if (isGetRating) {
                if (i < starPosition) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        starBtn.imageTintList =
                            ColorStateList.valueOf(
                                binding.root.resources.getColor(
                                    R.color.ratingColor,
                                    binding.root.context.theme
                                )
                            )
                    } else {
                        starBtn.imageTintList =
                            ColorStateList.valueOf(binding.root.resources.getColor(R.color.ratingColor))
                    }
                }
            } else {
                if (i <= starPosition) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        starBtn.imageTintList =
                            ColorStateList.valueOf(
                                binding.root.resources.getColor(
                                    R.color.ratingColor,
                                    binding.root.context.theme
                                )
                            )
                    } else {
                        starBtn.imageTintList =
                            ColorStateList.valueOf(binding.root.resources.getColor(R.color.ratingColor))
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}