package com.blogspot.mido_mymall.data.repo

import com.blogspot.mido_mymall.domain.models.WishListModel
import com.blogspot.mido_mymall.domain.repo.ProductDetailsRepo
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

class ProductDetailsRepoImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) :
    ProductDetailsRepo {


    override suspend fun getProductDetails(productID: String): Flow<Resource<DocumentSnapshot>> {

        val result = MutableStateFlow<Resource<DocumentSnapshot>>(Resource.Ideal())

        result.value = Resource.Loading()

        firestore.collection("PRODUCTS").document(productID)
            .get().addOnSuccessListener {

                result.value = Resource.Success(it)

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

    override suspend fun saveWishListIds(
        productId: String,
        wishListIdsSize: Int
    ): Flow<Resource<Boolean>> {

        val result = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())

        result.value = Resource.Loading()

        val addProduct: HashMap<String, Any> = HashMap()
        addProduct["product_id_$wishListIdsSize"] = productId
        addProduct["list_size"] = wishListIdsSize.toLong() + 1

        firestore.collection("USERS")
            .document(firebaseAuth.currentUser?.uid!!)
            .collection("USER_DATA")
            .document("MY_WISHLIST")
            .update(addProduct)
            .addOnSuccessListener {
                result.value = Resource.Success(true)
            }.addOnFailureListener {
                result.value = Resource.Error(it.message.toString())
            }

        return result
    }

    override suspend fun removeFromWishList(
        wishListIds: ArrayList<String>,
        wishListModelList: ArrayList<WishListModel>,
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
                if (wishListModelList.isNotEmpty()) {
                    wishListModelList.removeAt(index)
                }
                result.value = Resource.Success(true)
            }.addOnFailureListener {
                result.value = Resource.Error(it.message.toString())
            }

        return result

    }

    override suspend fun getRatings(): Flow<Resource<DocumentSnapshot>> {
        val result = MutableStateFlow<Resource<DocumentSnapshot>>(Resource.Ideal())

        firestore.collection("USERS")
            .document(firebaseAuth.currentUser?.uid!!)
            .collection("USER_DATA")
            .document("MY_RATINGS").get()
            .addOnSuccessListener {
                result.value = Resource.Success(it)
            }.addOnFailureListener {
                result.value = Resource.Error(it.message.toString())
            }

        return result
    }

    override suspend fun setRating(
        productId: String,
        starPosition: Long,
        averageRating: String,
        totalRatings: Long,
        myRatingIds: ArrayList<String>,
        myRating: ArrayList<Long>
    ): Flow<Resource<Boolean>> {

        val result = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())

        result.value = Resource.Loading()

        val productRating: HashMap<String, Any> = HashMap()

        productRating[(starPosition + 1).toString() + "_star"] = totalRatings

        productRating["average_rating"] = averageRating
        productRating["total_ratings"] = totalRatings

        firestore.collection("PRODUCTS")
            .document(productId)
            .update(productRating)
            .addOnSuccessListener {

                val rating: HashMap<String, Any> = HashMap()

                rating["list_size"] = myRatingIds.size.toLong() + 1
                rating["product_id_" + myRating.size] = productId
                rating["rating_" + myRatingIds.size] = starPosition + 1

                firestore.collection(
                    "USERS"
                ).document(firebaseAuth.currentUser?.uid!!)
                    .collection("USER_DATA")
                    .document("MY_RATINGS")
                    .update(rating).addOnSuccessListener {
                        result.value = Resource.Success(true)
                    }.addOnFailureListener {
                        result.value = Resource.Error(it.message.toString())
                    }

            }.addOnFailureListener {
                result.value = Resource.Error(it.message.toString())

            }

        return result
    }

    override suspend fun getMyCartIds(): Resource<DocumentSnapshot> {
        return try {

            val result = firestore.collection("USERS").document(firebaseAuth.currentUser?.uid!!)
                .collection("USER_DATA").document("MY_CART")
                .get().await()

            Resource.Success(result)

        } catch (ex: Exception) {
            currentCoroutineContext().ensureActive()
            return Resource.Error(ex.message.toString())
        }
    }


    override suspend fun saveCartListIds(
        productId: String,
        carListIdsSize: Int
    ): Resource<Boolean> {

        return try {

            val addProduct: HashMap<String, Any> = HashMap()
            addProduct["product_id_$carListIdsSize"] = productId
            addProduct["list_size"] = carListIdsSize.toLong() + 1

            firestore.collection("USERS")
                .document(firebaseAuth.currentUser?.uid!!)
                .collection("USER_DATA")
                .document("MY_CART")
                .update(addProduct)
                .await()

            Resource.Success(true)

        } catch (throwable: Throwable) {
            Resource.Error(throwable.message.toString())
        }

//        return result
    }

    override suspend fun updateRating(
        productId: String,
        initialRating: Long,
        oldStar: Long,
        newStar: Long,
        starPosition: Long,
        averageRating: Float,
        myRatingIds: ArrayList<String>,
        myRating: ArrayList<Long>
    ): Flow<Resource<Boolean>> {

        val result = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())

        result.value = Resource.Loading()

        val updateRating: HashMap<String, Any> = HashMap()

        //(documentSnapshot.get("${initialRating-1}_star" ) as Long)-1

        //(documentSnapshot.get("${starPosition+1}_star" ) as Long)+1


        updateRating.put("${initialRating + 1}_star", oldStar)
        updateRating.put("${starPosition + 1}_star", newStar)
        updateRating.put("average_rating", averageRating)

        firestore.collection("PRODUCTS")
            .document(productId)
            .update(updateRating)
            .addOnSuccessListener {

                val rating: HashMap<String, Any> = HashMap()

//                rating["list_size"] = myRatingIds.size.toLong() + 1
//                rating["product_id_" + myRating.size] = productId
                rating["rating_" + myRatingIds.indexOf(productId)] = starPosition + 1

                firestore.collection(
                    "USERS"
                ).document(firebaseAuth.currentUser?.uid!!)
                    .collection("USER_DATA")
                    .document("MY_RATINGS")
                    .update(rating).addOnSuccessListener {
                        result.value = Resource.Success(true)
                    }.addOnFailureListener {
                        result.value = Resource.Error(it.message.toString())
                    }

            }.addOnFailureListener {
                result.value = Resource.Error(it.message.toString())

            }

        return result
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


}