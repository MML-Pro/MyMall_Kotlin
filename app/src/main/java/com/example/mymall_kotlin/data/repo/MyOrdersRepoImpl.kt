package com.example.mymall_kotlin.data.repo

import com.example.mymall_kotlin.domain.repo.MyOrdersRepo
import com.example.mymall_kotlin.util.Resource
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
import java.util.Date
import javax.inject.Inject

class MyOrdersRepoImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : MyOrdersRepo {

    override suspend fun getOrders(): Flow<Resource<QuerySnapshot>> {

        val result = MutableStateFlow<Resource<QuerySnapshot>>(Resource.Ideal())

        result.emit(Resource.Loading())

        firestore.collection("USERS").document(firebaseAuth.currentUser?.uid!!)
            .collection("USER_ORDERS")
            .get().addOnSuccessListener { querySnapshot ->
                querySnapshot.documents.forEach {
                    firestore.collection("ORDERS")
                        .get()
                        .addOnSuccessListener {

                            result.value = Resource.Success(it)

                        }.addOnFailureListener {
                            result.value = Resource.Error(it.message.toString())
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

    override suspend fun getLastOrder(): Resource<DocumentSnapshot> {

        return try {
            val test = firestore.collection("ORDERS")
                .orderBy("ORDER DATE", Query.Direction.DESCENDING)
                .limit(1)
                .get().await()
            Resource.Success(test.documents[0])
        } catch (throwable: Throwable) {
            currentCoroutineContext().ensureActive()
            Resource.Error(throwable.message.toString())
        }

    }
}