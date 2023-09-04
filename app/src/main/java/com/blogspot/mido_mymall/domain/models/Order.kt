package com.blogspot.mido_mymall.domain.models


import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("notes")
    val notes: Notes?=null,
    @SerializedName("receipt")
    val receipt: String?=null
)