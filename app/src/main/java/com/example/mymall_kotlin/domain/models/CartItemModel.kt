package com.example.mymall_kotlin.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


// Cart Items
@Parcelize
data class CartItemModel(
    var type: Int?,
    var productId: String?,
    var productImage: String?,
    var productName: String?,
    var freeCoupons: Long?,
    var productPrice: String?,
    var cuttedPrice: String?,
    var productQuantity: Long?,
    var maxQuantity: Long?,
    var stockQuantity: Long?,
    var offersApply: Long?,
    var couponsApplied: Long?,
    var inStock: Boolean?,
    var qtyIDs: ArrayList<String>?,
    var selectedCouponId: String?,
    var discountedPrice: String?,

    var totalItems: Int?,
    var totalItemsPrice: Int?,
    var deliveryPrice: String?,
    var totalAmount: Int?,
    var savedAmount: Int?
) : Parcelable {

    //
    constructor(type: Int) : this(
        type,
        productId = null,
        productImage = null,
        productName = null,
        freeCoupons = null,
        productPrice = null,
        cuttedPrice = null,
        productQuantity = null,
        maxQuantity = null,
        stockQuantity = null,
        offersApply = null,
        couponsApplied = null,
        inStock = null,
        qtyIDs = null,
        selectedCouponId = null,
        discountedPrice = null,
        totalItems = null,
        totalItemsPrice = null,
        deliveryPrice = null,
        totalAmount = null,
        savedAmount = null
    ) {
        this.type = type
    }

//    var isInStock = false


    ////////CART TOTAL

    constructor(
        type: Int,
        totalItems: Int,
        totalItemsPrice: Int,
        deliveryPrice: String,
        totalAmount: Int,
        savedAmount: Int
    ) : this(
        type, null, null, null,
        null, null, null,
        null, null, null,
        null, null, null,
        null, null, null,
        totalItems, totalItemsPrice, deliveryPrice, totalAmount, savedAmount
    )


    companion object {
        const val CART_ITEM = 0
        const val TOTAL_AMOUNT = 1
    }
}