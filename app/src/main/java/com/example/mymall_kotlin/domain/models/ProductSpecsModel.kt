package com.example.mymall_kotlin.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class ProductSpecsModel(
    var type: Int = 0,
    var title: String? = null,
    var featureName: String? = null,
    var featureValue: String? = null
) : Parcelable {

    constructor(type: Int, title: String?) : this(type = type, title = title, featureName = null, featureValue = null)

    constructor(title: String?) : this(type = 0, title = title, featureName = null, featureValue = null)

    constructor(type: Int, featureName: String?, featureValue: String?) : this(
        type = type,
        title = null,
        featureName = featureName,
        featureValue = featureValue
    )

    companion object {
        const val SPECS_TITLE = 0
        const val SPECS_BODY = 1
    }
}

