package com.blogspot.mido_mymall.domain.models

data class CartSummary(
    val totalItems: Int,
    val totalItemsPrice: Double,
    val savedAmount: Double,
    val deliveryPrice: String,
    val totalAmount: Double
)