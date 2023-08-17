package com.example.mymall_kotlin.data.repo

import com.example.mymall_kotlin.domain.repo.MainActivityRepo
import com.example.mymall_kotlin.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class MainActivityRepoImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : MainActivityRepo {


    override suspend fun updateUserLastSeen(): Flow<Resource<Boolean>> {
        val result = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())

        result.value = Resource.Loading()

        firestore.collection("USERS").document(firebaseAuth.currentUser?.uid!!)
            .update("Last seen", FieldValue.serverTimestamp())
            .addOnSuccessListener {
                result.value = Resource.Success(true)
            }
            .addOnFailureListener {
                result.value = Resource.Error(it.message.toString())
            }

        return result
    }

    override suspend fun getUserLastSeen(): Flow<Resource<DocumentSnapshot>> {
        val result = MutableStateFlow<Resource<DocumentSnapshot>>(Resource.Ideal())

        result.value = Resource.Loading()

        firestore.collection("USERS").document(firebaseAuth.currentUser?.uid!!)
            .get().addOnSuccessListener {
                result.value = Resource.Success(it)
            }.addOnFailureListener {
                result.value = Resource.Error(it.message.toString())
            }

        return result
    }

    override suspend fun updateOrderStatus(
        orderID: String,
        paymentStatus: String,
        orderStatus: String
    ): Flow<Resource<Boolean>> {
        val result = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())

        result.value = Resource.Loading()

        val updateStatusHashMap = hashMapOf<String, Any>()

        updateStatusHashMap["PAYMENT STATUS"] = paymentStatus
        updateStatusHashMap["ORDER STATUS"] = orderStatus


        firestore.collection("ORDERS").document(orderID)
            .collection("OtherDetails")
            .document()
            .update(updateStatusHashMap)
            .addOnSuccessListener {
                result.value = Resource.Success(true)

                val userOrder = hashMapOf<String, Any>()

                userOrder["Order_ID"] = orderID

                firestore.collection("USERS").document(firebaseAuth.currentUser?.uid!!)
                    .collection("USER_ORDERS").document(orderID)
                    .set(userOrder)
                    .addOnSuccessListener {
                        result.value = Resource.Success(true)

                    }.addOnFailureListener {
                        result.value = Resource.Error(it.message.toString())
                    }

            }.addOnFailureListener {
                result.value = Resource.Error(it.message.toString())
            }

        return result

    }

    override suspend fun getUserInfo(): Flow<Resource<DocumentSnapshot>> {

        val result = MutableStateFlow<Resource<DocumentSnapshot>>(Resource.Ideal())

        result.value = Resource.Loading()

        firestore.collection("USERS").document(firebaseAuth.currentUser?.uid!!)
            .get().addOnSuccessListener {
                result.value = Resource.Success(it)
            }.addOnFailureListener {
                result.value = Resource.Error(it.message.toString())
            }

        return result
    }
}