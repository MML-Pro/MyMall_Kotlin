package com.blogspot.mido_mymall.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


// Cart Items
@Parcelize
data class CartItemModel(
//    var type: Int,
    val productId: String,
    val productImage: String,
    val productName: String,
    val freeCoupons: Long,
    val productPrice: String,
    val cuttedPrice: String,
    var productQuantity: Long,
    val maxQuantity: Long?,
    val stockQuantity: Long?,
    val offersApply: Long,
    val couponsApplied: Long,
    val inStock: Boolean?,
    val qtyIDs: ArrayList<String>?,
    var selectedCouponId: String?,
    var discountedPrice: String?,
    var productRating:Int?=0

//    var totalItems: Int?,
//    var totalItemsPrice: Int?,
//    var deliveryPrice: String?,
//    var totalAmount: Int?,
//    var savedAmount: Int?
) : Parcelable {

    //
    constructor(
        productId: String,
        productName: String,
        productImage: String,
        freeCoupons: Long,
        productPrice: String,
        cuttedPrice: String,
        discountedPrice: String?,
        productQuantity: Long,
        offersApply: Long,
        couponsApplied: Long,
        selectedCouponId: String?,
        productRating: Int?
    ) : this(
        productId = productId,
        productImage = productImage,
        productName = productName,
        freeCoupons = freeCoupons,
        productPrice = productPrice,
        cuttedPrice = cuttedPrice,
        productQuantity = productQuantity,
        maxQuantity = null,
        stockQuantity = null,
        offersApply = offersApply,
        couponsApplied = couponsApplied,
        inStock = null,
        qtyIDs = null,
        selectedCouponId = selectedCouponId,
        discountedPrice = discountedPrice,
        productRating = productRating
    )

//    var isInStock = false


    //////CART TOTAL
//
//    constructor(
//        type: Int,
//        totalItems: Int,
//        totalItemsPrice: Int,
//        deliveryPrice: String,
//        totalAmount: Int,
//        savedAmount: Int
//    ) : this(
//        type, null, null, null,
//        null, null, null,
//        null, null, null,
//        null, null, null,
//        null, null, null,
//        totalItems, totalItemsPrice, deliveryPrice, totalAmount, savedAmount
//    )


//    companion object {
//        const val CART_ITEM = 0
//        const val TOTAL_AMOUNT = 1
//    }
}