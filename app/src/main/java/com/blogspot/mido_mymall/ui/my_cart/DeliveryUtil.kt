package com.blogspot.mido_mymall.ui.my_cart

import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButton

interface DeliveryUtil {

    fun couponRedemptionButtonClick(
        productOriginalPrice: String,
        productCuttedPrice:String,
        position: Int,
        cartCouponRedemptionLayout: ConstraintLayout,
        couponRedemptionButton: MaterialButton,
        couponRedemptionTV: TextView,
        couponsApplied: TextView,
        productPriceTV: TextView,
        totalItemsPriceTV :TextView?=null ,
        totalAmountTV:TextView?=null,
        quantity:Long
    )

    fun onQuantityChanged(position: Int, newQuantity: Long) // <<< أضف هذه الدالة

}