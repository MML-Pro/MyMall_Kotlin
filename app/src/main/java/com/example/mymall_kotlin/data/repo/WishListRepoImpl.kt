package com.example.mymall_kotlin.data.repo

import com.example.mymall_kotlin.domain.models.WishListModel
import com.example.mymall_kotlin.domain.repo.WishListRepo
import com.example.mymall_kotlin.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class WishListRepoImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : WishListRepo {


    override suspend fun loadWishList(productId:String): Flow<Resource<DocumentSnapshot>> {

        val result = MutableStateFlow<Resource<DocumentSnapshot>>(Resource.Ideal())

        result.value = Resource.Loading()


        firestore.collection("PRODUCTS").document(productId)
            .get().addOnSuccessListener {
                result.value = Resource.Success(it)
            }.addOnFailureListener {
                result.value = Resource.Error(it.message.toString())
            }


        return result
    }

    override suspend fun removeFromWishList(
        wishListIds: ArrayList<String>,
        index: Int
    ): Flow<Resource<Boolean>> {
        val result = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())

        result.value = Resource.Loading()

        wishListIds.removeAt(index)

        val updatedWishList = HashMap<String, Any>()

        for (i in 0 until wishListIds.size) {
            updatedWishList["product_id_$i"] = wishListIds[i]
        }

        updatedWishList["list_size"] = wishListIds.size.toLong()

        firestore.collection("USERS").document(firebaseAuth.currentUser?.uid!!)
            .collection("USER_DATA").document("MY_WISHLIST")
            .set(updatedWishList).addOnSuccessListener {
                result.value = Resource.Success(true)
            }.addOnFailureListener {
                result.value = Resource.Error(it.message.toString())
            }

        return result

    }

    override suspend fun getWishListIds(): Flow<Resource<DocumentSnapshot>> {

        val result = MutableStateFlow<Resource<DocumentSnapshot>>(Resource.Ideal())

        result.value = Resource.Loading()

        firestore.collection("USERS").document(firebaseAuth.currentUser?.uid!!)
            .collection("USER_DATA").document("MY_WISHLIST")
            .get().addOnSuccessListener {
                result.value = Resource.Success(it)
            }.addOnFailureListener {
                result.value = Resource.Error(it.message.toString())
            }
        return result
    }
}