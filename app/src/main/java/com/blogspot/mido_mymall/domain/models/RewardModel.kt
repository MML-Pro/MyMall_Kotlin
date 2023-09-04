package com.blogspot.mido_mymall.domain.models

import com.google.firebase.Timestamp

data class RewardModel(
    var couponId:String,
    var type: String,
    var lowerLimit: String?,
    var upperLimit: String?,
    var discountOrAmount: String,
    var couponBody: String,
    var timestamp: Timestamp,
    var alreadyUsed:Boolean
)