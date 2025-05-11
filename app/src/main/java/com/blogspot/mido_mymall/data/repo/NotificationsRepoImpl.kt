package com.blogspot.mido_mymall.data.repo

import android.util.Log
import com.blogspot.mido_mymall.domain.repo.NotificationRepo
import com.blogspot.mido_mymall.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

private const val TAG = "NotificationsRepoImpl"

class NotificationsRepoImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : NotificationRepo {


    override fun getNotifications(remove: Boolean): Flow<Resource<DocumentSnapshot>> {

        val result = MutableStateFlow<Resource<DocumentSnapshot>>(Resource.Ideal())

        result.value = Resource.Loading()

        val listenerRegistration =
            firestore.collection("USERS").document(firebaseAuth.currentUser?.uid!!)
                .collection("USER_DATA")
                .document("MY_NOTIFICATIONS")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(TAG, "Listen failed.", error)

                        result.value = Resource.Error(error.message.toString())
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Log.d(TAG, "Current data: ${snapshot.data}")
                        result.value = Resource.Success(snapshot)
                    } else {
                        result.value = Resource.Success(null)
                        Log.d(TAG, "Current data: null")
                    }

                }

        if (remove) {
            listenerRegistration.remove()
        }

        return result
    }

//    override suspend fun updateBeenRead(notificationsList: ArrayList<NotificationModel>): Resource<Boolean> {
//
//        return try {
//
//
//
//            Resource.Success(true)
//
//        } catch (throwable: Throwable) {
//            return Resource.Error(throwable.message.toString())
//        }
//
//    }
}