package com.example.mymall_kotlin.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class MyOrderItemModel(

    var productID: String?,

    var productName: String,
    var productImage: String,

    var orderStatus: String?,
    var address: String?,
    var couponID: String?,
    var cuttedPrice: String?,
    var orderDate: Date?,
    var packedDate: Date?,
    var shippedDate: Date?,
    var deliveredDate: Date?,
    var cancelledDate: Date?,
    var discountedPrice: String?= null,
    var freeCoupons: Long?,
    var fullName: String?,
    var orderID: String?,
    var paymentMethod: String?,
    var pinCode: String?,
    var productPrice: String?,
    var productQuantity: Long?,
    var userID: String?,
    var deliveryPrice:String?,


    var productRating: Int?
) :Parcelable {
    constructor(
        productName: String,
        productImage: String,
        productRating: Int?
    ) : this(
        null,
        productName,
        productImage,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        productRating
    )
}