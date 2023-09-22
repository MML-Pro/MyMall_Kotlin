package com.blogspot.mido_mymall.data.repo

import com.blogspot.mido_mymall.domain.models.CartItemModel
import com.blogspot.mido_mymall.domain.models.CartSummary
import com.blogspot.mido_mymall.domain.repo.MyCartRepo
import com.blogspot.mido_mymall.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MyCartRepoImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : MyCartRepo {

    override suspend fun getMyCartListIds(): Flow<Resource<DocumentSnapshot>> {

        val result = MutableStateFlow<Resource<DocumentSnapshot>>(Resource.Ideal())

        result.value = Resource.Loading()

        firestore.collection("USERS").document(firebaseAuth.currentUser?.uid!!)
            .collection("USER_DATA").document("MY_CART")
            .get().addOnSuccessListener {
                result.value = Resource.Success(it)
            }.addOnFailureListener {
                result.value = Resource.Error(it.message.toString())
            }
        return result
    }


    override suspend fun loadMyCartList(productId: String): Resource<DocumentSnapshot> {
        return try {
            val data = firestore.collection("PRODUCTS").document(productId)
                .get()
                .await()
            Resource.Success(data)
        } catch (ex: Throwable) {
            currentCoroutineContext().ensureActive()
            Resource.Error(ex.message.toString())
        }
    }

    override suspend fun removeFromCartList(
        cartListIds: ArrayList<String>,
        index: Int
    ): Flow<Resource<Boolean>> {

        val result = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())

        result.value = Resource.Loading()

        cartListIds.removeAt(index)


        val updatedWishList = HashMap<String, Any>()

        for (i in 0 until cartListIds.size) {
            updatedWishList["product_id_$i"] = cartListIds[i]
        }

        updatedWishList["list_size"] = cartListIds.size.toLong()

        firestore.collection("USERS").document(firebaseAuth.currentUser?.uid!!)
            .collection("USER_DATA").document("MY_CART")
            .set(updatedWishList).addOnSuccessListener {
                result.value = Resource.Success(true)
            }.addOnFailureListener {
                result.value = Resource.Error(it.message.toString())
            }

        return result
    }

    override suspend fun loadAddress(): Flow<Resource<DocumentSnapshot>> {
        val result = MutableStateFlow<Resource<DocumentSnapshot>>(Resource.Ideal())

        result.value = Resource.Loading()

        firestore.collection("USERS").document(firebaseAuth.currentUser?.uid!!)
            .collection("USER_DATA").document("MY_ADDRESS")
            .get().addOnSuccessListener {
                result.value = Resource.Success(it)
            }.addOnFailureListener {
                result.value = Resource.Error(it.message.toString())
            }
        return result
    }


}