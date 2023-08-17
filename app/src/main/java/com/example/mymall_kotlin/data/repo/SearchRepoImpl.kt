package com.example.mymall_kotlin.data.repo

import com.example.mymall_kotlin.domain.repo.SearchRepo
import com.example.mymall_kotlin.util.Resource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class SearchRepoImpl @Inject constructor(private val firestore: FirebaseFirestore) : SearchRepo {

    override suspend fun searchForProducts(searchQuery: String): Flow<Resource<QuerySnapshot>> {

        val result = MutableStateFlow<Resource<QuerySnapshot>>(Resource.Ideal())

        result.value = Resource.Loading()


        firestore.collection("PRODUCTS").whereArrayContains("tags", searchQuery)
            .get().addOnSuccessListener {

                result.value = Resource.Success(it)

            }.addOnFailureListener {
                result.value = Resource.Error(it.message.toString())
            }

        return result
    }


}