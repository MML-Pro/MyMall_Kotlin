package com.blogspot.mido_mymall.data.repo

import com.blogspot.mido_mymall.domain.repo.MyOrdersRepo
import com.blogspot.mido_mymall.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MyOrdersRepoImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : MyOrdersRepo {

    override suspend fun getOrders(): Flow<Resource<QuerySnapshot>> {

        val result = MutableStateFlow<Resource<QuerySnapshot>>(Resource.Ideal())

        result.value = Resource.Loading()

        firestore.collection("USERS").document(firebaseAuth.currentUser?.uid!!)
            .collection("USER_ORDERS")
            .get().addOnSuccessListener { querySnapshot ->

                if (querySnapshot == null || querySnapshot.isEmpty) {
                    result.value = Resource.Success(null)
                } else {
                    querySnapshot.documents.forEach { _ ->

                        firestore.collection("ORDERS")
                            .get()
                            .addOnSuccessListener {

                                result.value = Resource.Success(it)

                            }.addOnFailureListener {
                                result.value = Resource.Error(it.message.toString())
                            }

                    }
                }
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

    override suspend fun getLastOrder(): Flow<Resource<DocumentSnapshot>> {

        val result = MutableStateFlow<Resource<DocumentSnapshot>>(Resource.Ideal())


            val userOrders = firestore.collection("USERS").document(firebaseAuth.currentUser!!.uid)
                .collection("USER_ORDERS")
                .get().addOnSuccessListener { userOrdersSnapshot->
                    if(userOrdersSnapshot == null || userOrdersSnapshot.isEmpty){
                        result.value = Resource.Error("No order found")
                    }else {
                        firestore.collection("ORDERS")
                            .orderBy("ORDER DATE", Query.Direction.DESCENDING)
                            .limit(1)
                            .get().addOnSuccessListener { lastOrderQuery->
                                userOrdersSnapshot.documents.forEach {
                                    if(it.id == lastOrderQuery.documents[0].id){
                                        result.value = Resource.Success(lastOrderQuery.documents[0])
                                    }
                                }

                            }.addOnFailureListener {
                                result.value = Resource.Error(it.message.toString())
                            }
                    }
                }.addOnFailureListener {
                    result.value = Resource.Error(it.message.toString())
                }


        return result
    }
}