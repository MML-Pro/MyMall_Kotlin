package com.example.mymall_kotlin.ui.edit_user_info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymall_kotlin.domain.usecase.edit_user_info.UpdateUserPasswordUseCase
import com.example.mymall_kotlin.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
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