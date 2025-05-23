package com.blogspot.mido_mymall.data.repo

import android.util.Log
import com.blogspot.mido_mymall.data.CartDataManager
import com.blogspot.mido_mymall.domain.models.CartItemModel
import com.blogspot.mido_mymall.domain.repo.DeliveryRepo
import com.blogspot.mido_mymall.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

private const val TAG = "DeliveryRepoImpl"

class DeliveryRepoImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : DeliveryRepo {


    override suspend fun updateCartList(
        cartListIds: ArrayList<String>,
        cartItemModelList: ArrayList<CartItemModel>
    ): Flow<Resource<Boolean>> {

        val result = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())

        result.value = Resource.Loading()

        val updateCartList = HashMap<String, Any>()

        var cartListSize: Long = 0

        val indexList = arrayListOf<Long>()

        for (i in 0 until cartListIds.size) {

            if (!cartItemModelList[i].inStock!!) {
                updateCartList["product_id_$cartListSize"] =
                    cartItemModelList[i].productId.toString()
                cartListSize++
            } else {
                indexList.add(i.toLong())
            }

        }
        updateCartList["list_size"] = cartListSize


        firestore.collection("USERS").document(firebaseAuth.currentUser?.uid!!)
            .collection("USER_DATA").document("MY_CART")
            .set(updateCartList)
            .addOnSuccessListener {


                for (i in 0 until indexList.size) {

                    if (cartListIds.isNotEmpty()) {
                        cartListIds.clear()
                    }
                    if (cartItemModelList.isNotEmpty()) {

                        val index = indexList[i].toInt()

                        if (index < cartItemModelList.size) {
                            cartItemModelList.removeAt(index)
                        } else {
                            cartItemModelList.clear()
                        }
                    }
                }
                result.value = Resource.Success(true)

                Log.d(TAG, "updateCartList: cartListIds size ${cartListIds.size}")
                Log.d(TAG, "updateCartList: cartItemModelList size ${cartItemModelList.size}")

            }.addOnFailureListener {
                result.value = Resource.Error(it.message.toString())
            }

        return result

    }


//    override suspend fun placeOrderDetails(
//        orderID: String,
//        cartItemModelList: ArrayList<CartItemModel>,
//        address: String,
//        fullName: String,
//        pinCode: String,
//        paymentMethod: String
//    ): Flow<Resource<Boolean>> {
//        val result = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())
//        result.emit(Resource.Loading())
//
//        val cartDataManager = CartDataManager()
//
//        val cartSummary =cartDataManager.calculateTotalAmount(cartItemModelList)
//
//        cartItemModelList.forEach {
//
//                val orderDetails = hashMapOf<Any, Any>()
//
//                orderDetails["ORDER ID"] = orderID
//                orderDetails["PRODUCT ID"] = it.productId.toString()
//                orderDetails["PRODUCT IMAGE"] = it.productImage.toString()
//                orderDetails["PRODUCT NAME"] = it.productName.toString()
//                orderDetails["USER ID"] = firebaseAuth.currentUser?.uid!!
//                orderDetails["PRODUCT QUANTITY"] = it.productQuantity
//                if (it.cuttedPrice.isNotEmpty()) {
//                    orderDetails["CUTTED PRICE"] = it.cuttedPrice
//                }
//
//                orderDetails["PRODUCT PRICE"] = it.productPrice
//
//                if (!it.selectedCouponId.isNullOrEmpty()) {
//                    orderDetails["COUPON ID"] = it.selectedCouponId!!
//                }
//
//                if (!it.discountedPrice.isNullOrEmpty()) {
//                    orderDetails["DISCOUNTED PRICE"] = it.discountedPrice!!
//                } else {
//                    orderDetails["DISCOUNTED PRICE"] = "0"
//                }
//                orderDetails["ORDER DATE"] = FieldValue.serverTimestamp()
//                orderDetails["PACKED DATE"] = FieldValue.serverTimestamp()
//                orderDetails["SHIPPED DATE"] = FieldValue.serverTimestamp()
//                orderDetails["DELIVERED DATE"] = FieldValue.serverTimestamp()
//                orderDetails["CANCELLED DATE"] = FieldValue.serverTimestamp()
//                orderDetails["ORDER STATUS"] = "ORDERED"
//                orderDetails["PAYMENT METHOD"] = paymentMethod
//                orderDetails["ADDRESS"] = address
//                orderDetails["FULL NAME"] = fullName
//                orderDetails["PIN CODE"] = pinCode
//                orderDetails["FREE COUPONS"] = it.freeCoupons
//
//                firestore.collection("ORDERS")
//                    .document(orderID)
//                    .set(orderDetails)
//                    .addOnSuccessListener {
////                        result.value = Resource.Success(true)
//                        val orderOtherDetails = hashMapOf<Any, Any>()
//                        orderOtherDetails["Total Items"] = cartSummary.totalItems
//                        orderOtherDetails["Total Items price"] = cartSummary.totalItemsPrice
//                        orderOtherDetails["Delivery Price"] = cartSummary.deliveryPrice
//                        orderOtherDetails["Total Amount"] = cartSummary.totalAmount
//                        orderOtherDetails["Saved Amount"] = cartSummary.savedAmount
//                        orderOtherDetails["PAYMENT STATUS"] = "not paid"
//                        orderOtherDetails["ORDER STATUS"] = "Cancelled"
//
//                        firestore.collection("ORDERS").document(orderID)
//                            .collection("OtherDetails")
//                            .document("cart_summary").set(orderOtherDetails)
//                            .addOnSuccessListener {
//                                result.value = Resource.Success(true)
//
//                            }.addOnFailureListener {
//                                result.value = Resource.Error(it.message.toString())
//
//                            }
//
//                    }.addOnFailureListener { ex ->
//                        result.value = Resource.Error(ex.message.toString())
//
//                    }
//
//            }
//        return result
//
//    }

    override suspend fun placeOrderDetails(
        orderID: String,
        cartItemModelList: ArrayList<CartItemModel>,
        address: String,
        fullName: String,
        pinCode: String,
        paymentMethod: String
    ): Flow<Resource<Boolean>> {
        val result = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())
        result.emit(Resource.Loading())

        val cartDataManager = CartDataManager()

        val cartSummary = cartDataManager.calculateTotalAmount(cartItemModelList)

        val orderDetailsList = mutableListOf<HashMap<Any, Any>>()

        for (cartItem in cartItemModelList) {
            val orderDetails = hashMapOf<Any, Any>()

            orderDetails["PRODUCT ID"] = cartItem.productId.toString()
            orderDetails["PRODUCT IMAGE"] = cartItem.productImage.toString()
            orderDetails["PRODUCT NAME"] = cartItem.productName.toString()
            orderDetails["FREE COUPONS"] = takeIf { cartItem.freeCoupons != null } ?: 0L
            orderDetails["USER ID"] = firebaseAuth.currentUser?.uid!!
            orderDetails["PRODUCT QUANTITY"] = takeIf { cartItem.productQuantity !=null } ?: 0L
            orderDetails["OFFERS APPLIED"] = takeIf { cartItem.offersApply != null } ?: 0L
            orderDetails["COUPONS APPLIED"] = takeIf { cartItem.couponsApplied != null } ?: 0L


            if (cartItem.cuttedPrice != null && cartItem.cuttedPrice.isNotEmpty()) {
                orderDetails["CUTTED PRICE"] = cartItem.cuttedPrice
            }

            orderDetails["PRODUCT PRICE"] = cartItem.productPrice.toString()

            if (!cartItem.selectedCouponId.isNullOrEmpty()) {
                orderDetails["COUPON ID"] = cartItem.selectedCouponId!!
            }

            if (!cartItem.discountedPrice.isNullOrEmpty()) {
                orderDetails["DISCOUNTED PRICE"] = cartItem.discountedPrice!!
            } else {
                orderDetails["DISCOUNTED PRICE"] = "0"
            }


            orderDetailsList.add(orderDetails)
        }

        val batch = firestore.batch()

        // Create or update_info the ORDERS document
        val ordersDocumentRef = firestore.collection("ORDERS").document(orderID)
        val ordersData = hashMapOf<String, Any>()


        ordersData["ORDER ID"] = orderID
        ordersData["ORDER DATE"] = FieldValue.serverTimestamp()
        ordersData["PACKED DATE"] = FieldValue.serverTimestamp()
        ordersData["SHIPPED DATE"] = FieldValue.serverTimestamp()
        ordersData["DELIVERED DATE"] = FieldValue.serverTimestamp()
        ordersData["CANCELLED DATE"] = FieldValue.serverTimestamp()
        ordersData["ORDER STATUS"] = "ORDERED"
        ordersData["PAYMENT METHOD"] = paymentMethod
        ordersData["ADDRESS"] = address
        ordersData["FULL NAME"] = fullName
        ordersData["PIN CODE"] = pinCode
        ordersData["USER ID"] = firebaseAuth.currentUser?.uid!!

        ordersDocumentRef.set(ordersData)

        batch.set(ordersDocumentRef, ordersData, SetOptions.merge())

        val orderSummaryRef = ordersDocumentRef.collection("OtherDetails")
            .document("order_summary")

        val orderOtherDetails = hashMapOf<String, Any>()
        orderOtherDetails["Total Items"] = cartSummary.totalItems
        orderOtherDetails["Total Items price"] = cartSummary.totalItemsPrice
        orderOtherDetails["Delivery Price"] = cartSummary.deliveryPrice
        orderOtherDetails["Total Amount"] = cartSummary.totalAmount
        orderOtherDetails["Saved Amount"] = cartSummary.savedAmount
        orderOtherDetails["PAYMENT STATUS"] = "not paid"
        orderOtherDetails["ORDER STATUS"] = "Cancelled"

        batch.set(orderSummaryRef,orderOtherDetails)

        // Create the sub-collection documents
        for (i in orderDetailsList.indices) {
            val orderDetails = orderDetailsList[i]
            val documentRef = ordersDocumentRef
                .collection("Products")
                .document("Product$i")

            batch.set(documentRef, orderDetails)
        }

        batch.commit()
            .addOnSuccessListener {
                result.value = Resource.Success(true)
            }
            .addOnFailureListener { ex ->
                result.value = Resource.Error(ex.message.toString())
            }

        return result
    }


//    override suspend fun getAvailableQuantities(cartItemModelList: ArrayList<CartItemModel>): Flow<Resource<List<DocumentSnapshot>>> {
//
//        val result = MutableStateFlow<Resource<List<DocumentSnapshot>>>(Resource.Ideal())
//
//        result.value = Resource.Loading()
//
//        for (i in 0 until cartItemModelList.size -1) {
//            firestore.collection("PRODUCTS").document(cartItemModelList[i].productId!!)
//                .collection("QUANTITY").orderBy("available", Query.Direction.DESCENDING)
//                .limit(cartItemModelList[i].productQuantity!!)
//                .get().addOnSuccessListener {
//                    result.value = Resource.Success(it.documents)
//                }.addOnFailureListener {
//                    result.value = Resource.Error(it.message.toString())
//                }
//        }
//        return result
//
//    }
//
//    override suspend fun updateAvailableQuantities(productId: String, documentId:String,
//                                                   available: Boolean
//                                                   ): Flow<Resource<Boolean>> {
//        val result = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())
//
//        result.value = Resource.Loading()
//
//        firestore.collection("PRODUCTS").document(productId)
//            .collection("QUANTITY").document(documentId)
//            .update_info("available",available)
//            .addOnSuccessListener {
//
//                result.value = Resource.Success(true)
//            }.addOnFailureListener {
//                result.value = Resource.Error(it.message.toString())
//            }
//
//        return result
//    }
//
//    override suspend fun updateAvailableQuantitiesIds(
//        productId: String,
//        documentId: String
//    ): Flow<Resource<Boolean>> {
//        val result = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())
//
//        result.value = Resource.Loading()
//
//        firestore.collection("PRODUCTS").document(productId)
//            .collection("QUANTITY").document(documentId)
//            .update_info("user_ID",firebaseAuth.currentUser?.uid!!)
//            .addOnSuccessListener {
//
//                result.value = Resource.Success(true)
//            }.addOnFailureListener {
//                result.value = Resource.Error(it.message.toString())
//            }
//
//        return result
//    }
}