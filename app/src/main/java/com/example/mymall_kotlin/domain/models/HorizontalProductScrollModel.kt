package com.example.mymall_kotlin.domain.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@kotlinx.parcelize.Parcelize
data class HorizontalProductScrollModel(
    var productID: String?,
    var productImage: String?,
    var productName: String?,
    var productDescription: String?,
    var productPrice: String?,
) : Parcelable