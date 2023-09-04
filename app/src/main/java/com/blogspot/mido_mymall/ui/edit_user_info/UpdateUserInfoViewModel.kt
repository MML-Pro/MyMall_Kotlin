package com.blogspot.mido_mymall.ui.edit_user_info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blogspot.mido_mymall.domain.usecase.edit_user_info.UpdateUserEmailUseCase
import com.blogspot.mido_mymall.domain.usecase.edit_user_info.UpdateUserNameUseCase
import com.blogspot.mido_mymall.domain.usecase.edit_user_info.UploadUserProfileImageUseCase
import com.blogspot.mido_mymall.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateUserInfoViewModel @Inject constructor(
    private val updateUserNameUseCase: UpdateUserNameUseCase,
    private val updateUserEmailUseCase: UpdateUserEmailUseCase,
    private val uploadUserProfileImageUseCase: UploadUserProfileImageUseCase,

    ) : ViewModel() {


    private var _updateUserNameState = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())
    val updateUserNameState = _updateUserNameState.asSharedFlow()

    private var _updateUserEmailState = MutableSharedFlow<Resource<Boolean>>()
    val updateUserEmailState = _updateUserEmailState.asSharedFlow()

    private var _updateUserProfileImageState = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())
    val updateUserProfileImageState = _updateUserProfileImageState.asSharedFlow()



    fun updateUserName(userName: String) {
        viewModelScope.launch {
            _updateUserNameState.emit(Resource.Loading())
            updateUserNameUseCase(userName).let {
                _updateUserNameState.emit(it)
            }
        }
    }

    fun updateUserEmail(
        oldEmail: String?,
        newEmail: String, password: String?,
        idToken: String? = null
    ) {
        viewModelScope.launch {
            updateUserEmailUseCase(oldEmail, newEmail, password, idToken).collect {
                _updateUserEmailState.emit(it)
            }
        }
    }

    fun updateUserProfileImage(imageDataByteArray: ByteArray) {
        viewModelScope.launch {
            uploadUserProfileImageUseCase(imageDataByteArray).collect {
                _updateUserProfileImageState.emit(it)
            }
        }
    }


}