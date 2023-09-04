package com.blogspot.mido_mymall.data.repo

import android.util.Log
import com.blogspot.mido_mymall.domain.models.CartItemModel
import com.blogspot.mido_mymall.domain.repo.DeliveryRepo
import com.blogspot.mido_mymall.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
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
                        cartListIds.removeAt(indexList[i].toInt())
                    }
                    if (cartItemModelList.isNotEmpty()) {
                        cartItemModelList.removeAt(indexList[i].toInt())
                        cartItemModelList.removeAt(cartItemModelList.size - 1)
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

        cartItemModelList.forEach {
            if (it.type == CartItemModel.CART_ITEM) {

                val orderDetails = hashMapOf<Any, Any>()

                orderDetails["ORDER ID"] = orderID
                orderDetails["PRODUCT ID"] = it.productId.toString()
                orderDetails["PRODUCT IMAGE"] = it.productImage.toString()
                orderDetails["PRODUCT NAME"] = it.productName.toString()
                orderDetails["USER ID"] = firebaseAuth.currentUser?.uid!!
                orderDetails["PRODUCT QUANTITY"] = it.productQuantity!!
                if (!it.cuttedPrice.isNullOrEmpty()) {
                    orderDetails["CUTTED PRICE"] = it.cuttedPrice!!
                }

                orderDetails["PRODUCT PRICE"] = it.productPrice!!

                if (!it.selectedCouponId.isNullOrEmpty()) {
                    orderDetails["COUPON ID"] = it.selectedCouponId!!
                }

                if (!it.discountedPrice.isNullOrEmpty()) {
                    orderDetails["DISCOUNTED PRICE"] = it.discountedPrice!!
                } else {
                    orderDetails["DISCOUNTED PRICE"] = ""
                }
                orderDetails["ORDER DATE"] = FieldValue.serverTimestamp()
                orderDetails["PACKED DATE"] = FieldValue.serverTimestamp()
                orderDetails["SHIPPED DATE"] = FieldValue.serverTimestamp()
                orderDetails["DELIVERED DATE"] = FieldValue.serverTimestamp()
                orderDetails["CANCELLED DATE"] = FieldValue.serverTimestamp()
                orderDetails["ORDER STATUS"] = "ORDERED"
                orderDetails["PAYMENT METHOD"] = paymentMethod
                orderDetails["ADDRESS"] = address
                orderDetails["FULL NAME"] = fullName
                orderDetails["PIN CODE"] = pinCode
                orderDetails["FREE COUPONS"] = it.freeCoupons!!
                if (it.deliveryPrice != null) {
                    orderDetails["DELIVERY PRICE"] = it.deliveryPrice!!

                    Log.d(TAG, "placeOrderDetails: DELIVERY PRICE ${it.deliveryPrice}")
                }else {
                    orderDetails["DELIVERY PRICE"] = "Free"
                }

                firestore.collection("ORDERS")
                    .document(orderID)
                    .set(orderDetails)
                    .addOnSuccessListener {
                        result.value = Resource.Success(true)
                    }.addOnFailureListener { ex ->
                        result.value = Resource.Error(ex.message.toString())

                    }

            } else {
                val orderDetails = hashMapOf<Any, Any>()

                orderDetails["Total Items"] = it.totalItems!!
                orderDetails["Total Items price"] = it.totalItemsPrice!!
                orderDetails["Delivery Price"] = it.deliveryPrice!!
                orderDetails["Total Amount"] = it.totalAmount!!
                orderDetails["Saved Amount"] = it.savedAmount!!
                orderDetails["PAYMENT STATUS"] = "not paid"
                orderDetails["ORDER STATUS"] = "Cancelled"

                firestore.collection("ORDERS").document(orderID)
                    .collection("OtherDetails")
                    .document().set(orderDetails)
                    .addOnSuccessListener {
                        result.value = Resource.Success(true)

                    }.addOnFailureListener {
                        result.value = Resource.Error(it.message.toString())

                    }
            }
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
//            .update("available",available)
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
//            .update("user_ID",firebaseAuth.currentUser?.uid!!)
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