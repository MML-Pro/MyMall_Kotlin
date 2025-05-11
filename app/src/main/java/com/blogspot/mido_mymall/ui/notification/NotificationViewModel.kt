package com.blogspot.mido_mymall.ui.notification

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blogspot.mido_mymall.domain.usecase.notification.GetNotificationUseCase
import com.blogspot.mido_mymall.util.Resource
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(private val notificationUseCase: GetNotificationUseCase) :
    ViewModel() {

        companion object{
            private const val TAG = "NotificationViewModel"
        }


    private var _notification = MutableStateFlow<Resource<DocumentSnapshot>>(Resource.Ideal())
    val notification: Flow<Resource<DocumentSnapshot>> get() = _notification

    fun getNotifications(remove: Boolean) {
        viewModelScope.launch {
            notificationUseCase(remove).collect { data->

                Log.d(TAG, "getNotifications: ${data}")
                _notification.emit(data)
            }
        }
    }
}