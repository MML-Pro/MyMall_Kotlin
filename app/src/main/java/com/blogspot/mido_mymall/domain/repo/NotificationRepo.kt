package com.blogspot.mido_mymall.domain.repo

import com.blogspot.mido_mymall.util.Resource
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.Flow

interface NotificationRepo {

    fun getNotifications(remove:Boolean) : Flow<Resource<DocumentSnapshot>>

//    suspend fun updateBeenRead(notificationsList:ArrayList<NotificationModel>) : Resource<Boolean>
}