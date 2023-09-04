package com.blogspot.mido_mymall.domain.models

import android.os.Parcelable

@kotlinx.parcelize.Parcelize
data class HorizontalProductScrollModel(
    var productID: String?,
    var productImage: String?,
    var productName: String?,
    var productSubtitle: String?,
    var productPrice: String?,
) : Parcelable