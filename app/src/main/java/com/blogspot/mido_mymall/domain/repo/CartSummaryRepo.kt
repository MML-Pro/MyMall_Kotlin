package com.blogspot.mido_mymall.domain.repo

import com.blogspot.mido_mymall.domain.models.CartItemModel
import com.blogspot.mido_mymall.domain.models.CartSummary

interface CartSummaryRepo {
    fun calculateTotalAmount(cartItemModelList: List<CartItemModel>): CartSummary
}