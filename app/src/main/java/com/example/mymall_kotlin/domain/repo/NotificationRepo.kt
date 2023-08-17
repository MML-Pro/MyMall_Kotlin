package com.example.mymall_kotlin.domain.repo

import com.example.mymall_kotlin.domain.models.NotificationModel
import com.example.mymall_kotlin.util.Resource
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.Flow

interface NotificationRepo {

    fun getNotifications(remove:Boolean) : Flow<Resource<DocumentSnapshot>>

//    suspend fun updateBeenRead(notificationsList:ArrayList<NotificationModel>) : Resource<Boolean>
}