package com.blogspot.mido_mymall.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class MyOrderItemModel(

//    var productID: String?,
//
//    var productName: String,
//    var productImage: String,

    val productList: List<CartItemModel>,
    var orderStatus: String?,
    var address: String?,
    var orderDate: Date?,
    var packedDate: Date?,
    var shippedDate: Date?,
    var deliveredDate: Date?,
    var cancelledDate: Date?,
    var fullName: String?,
    var orderID: String?,
    var paymentMethod: String?,
    var pinCode: String?,
    var userID: String?
) :Parcelable