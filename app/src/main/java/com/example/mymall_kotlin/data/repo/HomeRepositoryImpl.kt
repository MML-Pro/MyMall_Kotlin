package com.example.mymall_kotlin.data.repo

import android.util.Log
import com.example.mymall_kotlin.domain.repo.HomeRepository
import com.example.mymall_kotlin.util.Resource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

private const val TAG = "HomeRepositoryImpl"

class HomeRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore) :
    HomeRepository {


    override suspend fun getCategories(): Flow<Resource<List<DocumentSnapshot>>> {

        val result = MutableStateFlow<Resource<List<DocumentSnapshot>>>(Resource.Ideal())

        result.value = Resource.Loading()
        firestore.collection("CATEGORIES").orderBy("index").get()
            .addOnSuccessListener {
                result.value = Resource.Success(it.documents)
            }
            .addOnFailureListener {
                result.value = Resource.Error(it.message.toString())
            }


        return result
    }

    override suspend fun getTopDeals(): Flow<Resource<List<DocumentSnapshot>>> {
        val result = MutableStateFlow<Resource<List<DocumentSnapshot>>>(Resource.Ideal())
        result.value = Resource.Loading()
        firestore.collection("CATEGORIES")
            .document("HOME")
            .collection("TOP_DEALS").orderBy("index").get()
            .addOnSuccessListener {
                result.value = Resource.Success(it.documents)

//                Log.d(TAG, "getTopDeals: ${it.documents[0].data.toString()}")
            }
            .addOnFailureListener {
                result.value = Resource.Error(it.message.toString())
            }


        return result
    }
}