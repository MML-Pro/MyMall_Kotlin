package com.blogspot.mido_mymall.ui.edit_user_info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blogspot.mido_mymall.domain.usecase.edit_user_info.UpdateUserPasswordUseCase
import com.blogspot.mido_mymall.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateUserPasswordViewModel @Inject
constructor(private val updateUserPasswordUseCase: UpdateUserPasswordUseCase) : ViewModel() {

    private var _updateUserPasswordState = MutableSharedFlow<Resource<Boolean>>()
    val updateUserPasswordState: SharedFlow<Resource<Boolean>> get() = _updateUserPasswordState.asSharedFlow()

    fun updateUserPassword(
        currentEmail: String,
        oldPassword: String,
        newPassword: String
    ) {
        viewModelScope.launch {
            updateUserPasswordUseCase(currentEmail, oldPassword, newPassword).collect {
                _updateUserPasswordState.emit(it)
            }
        }
    }
}