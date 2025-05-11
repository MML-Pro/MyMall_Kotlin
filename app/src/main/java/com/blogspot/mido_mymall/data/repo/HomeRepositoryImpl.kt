package com.blogspot.mido_mymall.data.repo

import android.util.Log
import com.blogspot.mido_mymall.domain.repo.HomeRepository
import com.blogspot.mido_mymall.util.Resource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
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
    Log.d(TAG, "getTopDeals: Starting to fetch top deals")
    result.value = Resource.Loading()

    firestore.collection("CATEGORIES")
        .document("HOME")
        .collection("TOP_DEALS")
        .orderBy("index")
        .get()
        .addOnSuccessListener { querySnapshot ->
            val documents = querySnapshot.documents
            Log.d(TAG, "getTopDeals: Success - Documents fetched: ${documents.size}")
            documents.forEachIndexed { index, doc ->
                Log.d(TAG, "getTopDeals: Document $index - Data: ${doc.data}")
            }
            result.value = Resource.Success(documents)
        }
        .addOnFailureListener { exception ->
            Log.e(TAG, "getTopDeals: Failed - Error: ${exception.message}")
            result.value = Resource.Error(exception.message.toString())
        }

    return result
}
}