package com.example.mymall_kotlin.data.repo

import com.example.mymall_kotlin.domain.repo.RewardRepo
import com.example.mymall_kotlin.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class RewardRepoImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : RewardRepo {

    override suspend fun getRewards(): Flow<Resource<QuerySnapshot>> {

        val result = MutableStateFlow<Resource<QuerySnapshot>>(Resource.Ideal())

        result.value = Resource.Loading()

        firestore.collection("USERS").document(firebaseAuth.currentUser?.uid!!)
            .collection("USER_REWARDS").get()
            .addOnSuccessListener {
                result.value = Resource.Success(it)
            }.addOnFailureListener {
                result.value = Resource.Error(it.message.toString())
            }

        return result
    }
}