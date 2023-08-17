package com.example.mymall_kotlin.domain.models

data class AddressesModel(
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

) {
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
    ) : this(
        city,
        localityOrStreet,
        flatNumberOrBuildingName,
        pinCode,
        state,
        landMark,
        fullName,
        mobileNumber,
        null,
        selected
    ) {

        this.fullName = fullName
        this.pinCode = pinCode
        this.mobileNumber = mobileNumber
        this.selected = selected
    }

}
