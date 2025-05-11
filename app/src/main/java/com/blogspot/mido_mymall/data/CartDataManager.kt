package com.blogspot.mido_mymall.data

import android.util.Log
import com.blogspot.mido_mymall.domain.models.CartItemModel
import com.blogspot.mido_mymall.domain.models.CartSummary
import com.blogspot.mido_mymall.domain.repo.CartSummaryRepo
import com.blogspot.mido_mymall.util.safeParseDouble
import kotlin.math.round

private const val TAG = "CartDataManager"

class CartDataManager {

//    fun calculateTotalAmount(cartItemModelList: List<CartItemModel>): CartSummary {
//        var totalItems = 0
//        var totalItemsPrice = 0.0
//        val deliveryPrice: String
//        val totalAmount: Double
//        var savedAmount = 0.0
//
//        cartItemModelList.forEach { cartItem ->
//
//            val quantity = cartItem.productQuantity
//
//            Log.d(TAG, "calculateTotalAmount: product price ${cartItem.productPrice}")
//
//            totalItems++
//
//            totalItemsPrice += if (cartItem.selectedCouponId.isNullOrEmpty() &&
//                cartItem.discountedPrice.isNullOrEmpty() || cartItem.discountedPrice!!.toInt() == 0
//            ) {
//                cartItem.productPrice.toDouble() * quantity.toInt()
//
//            } else {
//                cartItem.discountedPrice?.toDouble()!! * quantity.toInt()
//            }
//
//            Log.d(TAG, "discountedPrice: ${cartItem.discountedPrice}")
//
////            totalItemsPrice += cartItem.productPrice.toDouble() * quantity.toInt()
//
////            val test1 = cartItem.productPrice.toDouble() * quantity.toInt()
////
////            val test2 = cartItem.discountedPrice?.toDouble()?.times(quantity.toInt())
////
////            Log.d(TAG, "calculateTotalAmount: test1 $test1 test2 $test2")
//
//            if (cartItem.cuttedPrice.isNotEmpty()) {
//                savedAmount += (cartItem.cuttedPrice.toDouble() - cartItem.productPrice.toDouble()) * quantity.toInt()
//
//                if (!cartItem.selectedCouponId.isNullOrEmpty()) {
//                    savedAmount += (cartItem.productPrice.toDouble() - cartItem.discountedPrice?.toDouble()!!) * quantity.toInt()
//                }
//
//            } else {
//                if (cartItem.selectedCouponId?.isNotEmpty()!!) {
//                    savedAmount += (cartItem.productPrice.toDouble() - cartItem.discountedPrice?.toDouble()!!) * quantity.toInt()
//                }
//            }
//
//        }
//
//        val roundedSavedAmount = round(savedAmount * 100) / 100
//
//        Log.d(TAG, "calculateTotalAmount: roundedSavedAmount $roundedSavedAmount")
//
//        if (totalItemsPrice > 500) {
//            deliveryPrice = "Free"
//            totalAmount = totalItemsPrice
//        } else {
//            deliveryPrice = "60"
//            totalAmount = totalItemsPrice + 60
//        }
//
//        Log.d(TAG, "cartSummaryTotalItemsPrice: $totalItemsPrice")
//
//
//        return CartSummary(
//            totalItems,
//            totalItemsPrice,
//            roundedSavedAmount,
//            deliveryPrice,
//            totalAmount
//        )
//    }



  companion object{
      const val TAG = "CartDataManager" // تعريف TAG إذا لم يكن معرفًا
  }

// ... (باقي الكلاس أو الملف) آخر تحديث 14/4/2025

// fun calculateTotalAmount(cartItemModelList: List<CartItemModel>): CartSummary {
//    var totalItems = 0
//    var totalItemsPrice = 0.0
//    val deliveryPrice: String
//    val totalAmount: Double
//    var savedAmount = 0.0
//
//    cartItemModelList.forEach { cartItem ->
//        val quantity = cartItem.productQuantity.toInt()
//        Log.d(TAG, "calculateTotalAmount: product price string ${cartItem.productPrice}")
//
//        totalItems++
//
//        val productPriceDouble = safeParseDouble(cartItem.productPrice)
//        val discountedPriceDouble = safeParseDouble(cartItem.discountedPrice)
//        val cuttedPriceDouble = safeParseDouble(cartItem.cuttedPrice)
//
//        // تسجيل القيم بعد التحويل
//        Log.d(TAG, "Product: ${cartItem.productName}, Parsed Product Price: $productPriceDouble, Parsed Discounted Price: $discountedPriceDouble")
//
//        totalItemsPrice += if (cartItem.selectedCouponId.isNullOrEmpty() || discountedPriceDouble == 0.0) {
//            val amount = productPriceDouble * quantity
//            Log.d(TAG, "Using productPrice: $productPriceDouble * $quantity = $amount")
//            amount
//        } else {
//            val amount = discountedPriceDouble * quantity
//            Log.d(TAG, "Using discountedPrice: $discountedPriceDouble * $quantity = $amount")
//            amount
//        }
//
//        if (cuttedPriceDouble > 0.0) {
//            savedAmount += (cuttedPriceDouble - productPriceDouble) * quantity
//            if (!cartItem.selectedCouponId.isNullOrEmpty() && discountedPriceDouble > 0.0) {
//                savedAmount += (productPriceDouble - discountedPriceDouble) * quantity
//            }
//        } else {
//            if (!cartItem.selectedCouponId.isNullOrEmpty() && discountedPriceDouble > 0.0) {
//                savedAmount += (productPriceDouble - discountedPriceDouble) * quantity
//            }
//        }
//    }
//
//    val roundedSavedAmount = round(savedAmount * 100) / 100.0
//    Log.d(TAG, "calculateTotalAmount: roundedSavedAmount $roundedSavedAmount")
//
//    if (totalItemsPrice > 500) {
//        deliveryPrice = "Free"
//        totalAmount = totalItemsPrice
//    } else {
//        deliveryPrice = "60"
//        totalAmount = totalItemsPrice + 60.0
//    }
//
//    val roundedTotalItemsPrice = round(totalItemsPrice * 100) / 100.0
//    val roundedTotalAmount = round(totalAmount * 100) / 100.0
//
//    Log.d(TAG, "cartSummaryTotalItemsPrice: $roundedTotalItemsPrice")
//    Log.d(TAG, "cartSummaryTotalAmount: $roundedTotalAmount")
//
//    return CartSummary(
//        totalItems,
//        roundedTotalItemsPrice,
//        roundedSavedAmount,
//        deliveryPrice,
//        roundedTotalAmount
//    )
//}


    fun calculateTotalAmount(cartItemModelList: List<CartItemModel>): CartSummary {
        // --- >> التعديل 1: تغيير النوع إلى Long والبدء بـ 0 << ---
        var totalItems: Long = 0L
        var totalItemsPrice = 0.0
        var savedAmount = 0.0
        // ... (بقيمة متغيرات deliveryPrice و totalAmount) ...

        cartItemModelList.forEach { cartItem ->
            // --- >> التعديل 2: استخدام الكمية كـ Long مباشرة << ---
            val quantity = cartItem.productQuantity // quantity هو Long

            // --- >> التعديل 3: جمع الكميات بدلاً من عد العناصر << ---
            totalItems += quantity!! // <--- هذا هو التغيير الأساسي لحساب العدد الصحيح

            // تأكد من أن بقية الحسابات تستخدم quantity (الذي هو Long) بشكل صحيح
            val productPriceDouble = safeParseDouble(cartItem.productPrice)
            val discountedPriceDouble = safeParseDouble(cartItem.discountedPrice)
            val cuttedPriceDouble = safeParseDouble(cartItem.cuttedPrice)

            totalItemsPrice += if (cartItem.selectedCouponId.isNullOrEmpty() || discountedPriceDouble == 0.0) {
                productPriceDouble * quantity?.toLong()!! // الضرب في Long quantity
            } else {
                discountedPriceDouble * quantity?.toLong()!! // الضرب في Long quantity
            }

            // تأكد أن حساب savedAmount يستخدم quantity (Long)
            if (cuttedPriceDouble > 0.0 && cuttedPriceDouble > productPriceDouble) {
                savedAmount += (cuttedPriceDouble - productPriceDouble) * quantity
                if (!cartItem.selectedCouponId.isNullOrEmpty() && discountedPriceDouble > 0.0 && discountedPriceDouble < productPriceDouble) {
                    savedAmount += (productPriceDouble - discountedPriceDouble) * quantity
                }
            } else {
                if (!cartItem.selectedCouponId.isNullOrEmpty() && discountedPriceDouble > 0.0 && discountedPriceDouble < productPriceDouble) {
                    savedAmount += (productPriceDouble - discountedPriceDouble) * quantity
                }
            }
        } // نهاية الحلقة

        // ... (حساب deliveryPrice و totalAmount وتقريب القيم) ...
        val roundedSavedAmount = round(savedAmount * 100) / 100.0
        val deliveryPrice: String
        val totalAmount: Double
        if (totalItemsPrice > 500 || totalItemsPrice == 0.0) { // تم تعديل شرط التوصيل ليشمل السعر 0
            deliveryPrice = if(totalItemsPrice == 0.0) "N/A" else "Free"
            totalAmount = totalItemsPrice
        } else {
            deliveryPrice = "60"
            totalAmount = totalItemsPrice + 60.0
        }
        val roundedTotalItemsPrice = round(totalItemsPrice * 100) / 100.0
        val roundedTotalAmount = round(totalAmount * 100) / 100.0


        // --- >> التعديل 4: تأكد أن CartSummary يقبل Int أو Long وأن النص المصدر يستخدم %d << ---
        // تأكد من تعريف CartSummary: data class CartSummary(val totalItems: Int, ...)
        return CartSummary(
            totalItems = totalItems.toInt(), // <--- تحويل إلى Int إذا كان CartSummary يتوقع Int
            totalItemsPrice = roundedTotalItemsPrice,
            savedAmount = roundedSavedAmount,
            deliveryPrice = deliveryPrice,
            totalAmount = roundedTotalAmount
        )
    }

// تأكد أن النص المصدر price_number_items يستخدم %d
// مثال: <string name="price_number_items">السعر (%1$d منتجات)</string>

}