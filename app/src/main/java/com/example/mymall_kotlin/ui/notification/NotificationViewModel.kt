package com.example.mymall_kotlin.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymall_kotlin.domain.usecase.notification.GetNotificationUseCase
import com.example.mymall_kotlin.util.Resource
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(private val notificationUseCase: GetNotificationUseCase) :
    ViewModel() {


    private var _notification = MutableStateFlow<Resource<DocumentSnapshot>>(Resource.Ideal())
    val notification: Flow<Resource<DocumentSnapshot>> get() = _notification

    fun getNotifications(remove: Boolean) {
        viewModelScope.launch {
            notificationUseCase(remove).collect {
                _notification.emit(it)
            }
        }
    }
}