package com.blogspot.mido_mymall.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

// داخل كلاس AddressesModel
@Parcelize
data class AddressesModel(
    val id: String = UUID.randomUUID().toString(), // ID فريد
    var city: String,
    var localityOrStreet: String,
    var flatNumberOrBuildingName: String,
    var pinCode: String,
    var state: String,
    var landMark: String?,
    var fullName: String,
    var mobileNumber: String,
    var alternateMobileNumber: String?,
    var selected: Boolean
) : Parcelable {

    // Constructor ثانوي مُعدّل (إذا كنت تحتاجه)
    constructor(
        city: String,
        localityOrStreet: String,
        flatNumberOrBuildingName: String,
        pinCode: String,
        state: String,
        landMark: String?,
        fullName: String,
        mobileNumber: String,
        selected: Boolean
        // يمكنك إضافة alternateMobileNumber هنا إذا احتجت لتمريره
    ) : this( // <-- استدعاء للـ Primary Constructor باستخدام Named Arguments
        // id = يتم استخدام القيمة الافتراضية من Primary Constructor
        city = city,
        localityOrStreet = localityOrStreet,
        flatNumberOrBuildingName = flatNumberOrBuildingName,
        pinCode = pinCode,
        state = state,
        landMark = landMark,
        fullName = fullName,
        mobileNumber = mobileNumber,
        alternateMobileNumber = null, // تعيين قيمة افتراضية لـ altMobile لهذا الكونستركتور
        selected = selected
    ) {
        // لا حاجة لإعادة تعيين الحقول هنا لأن this() قامت بذلك
    }
}
