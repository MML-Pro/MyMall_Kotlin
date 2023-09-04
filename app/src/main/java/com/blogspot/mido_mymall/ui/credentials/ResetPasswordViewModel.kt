package com.blogspot.mido_mymall.ui.credentials

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blogspot.mido_mymall.domain.usecase.credentials.ResetPasswordUseCase
import com.blogspot.mido_mymall.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ResetPasswordViewModel"

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(private val resetPasswordUseCase: ResetPasswordUseCase) :
    ViewModel() {

    private var _resetPasswordState = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())
    val resetPasswordState: Flow<Resource<Boolean>> get() = _resetPasswordState


    fun resetPassword(email: String) {
        viewModelScope.launch {
            resetPasswordUseCase(email).collectLatest {
                Log.d(TAG, "resetPassword: ${it.data}")
                _resetPasswordState.emit(it)
            }
        }
    }

}