package com.example.mymall_kotlin.domain.models

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import kotlinx.parcelize.Parcelize

@Parcelize
data class WishListModel(
    var productID: String? = null,
    var productImage: String?,
    var productName: String?,
    var freeCoupons: Long,
    var averageRating: String?,
    var totalRatings: Long,
    var productPrice: String?,
    var cuttedPrice: String?,
    var isCOD: Boolean,
    var isInStock: Boolean?
) : Parcelable
