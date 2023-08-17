package com.example.mymall_kotlin.ui.credentials

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymall_kotlin.domain.models.User
import com.example.mymall_kotlin.domain.usecase.credentials.CreateUserDataUseCase
import com.example.mymall_kotlin.domain.usecase.credentials.SaveUserInfoUseCase
import com.example.mymall_kotlin.domain.usecase.credentials.SignUpUseCase
import com.example.mymall_kotlin.util.RegisterValidation
import com.example.mymall_kotlin.util.Resource
import com.example.mymall_kotlin.util.checkValidation
import com.example.mymall_kotlin.util.validateEmail
import com.example.mymall_kotlin.util.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val saveUserInfoUseCase: SaveUserInfoUseCase,
    private val createUserDataUseCase: CreateUserDataUseCase,
    val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private var _register = MutableStateFlow<Resource<FirebaseUser>>(Resource.Ideal())
    val register: Flow<Resource<FirebaseUser>> get() = _register

    private var _registerFailedStatesValidation = Channel<RegisterValidation.RegisterFailedStates>()
    val registerFailedStatesValidation get() = _registerFailedStatesValidation.receiveAsFlow()

    private var _saveUserInfoState = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())
    val saveUserInfoState : Flow<Resource<Boolean>> get() = _saveUserInfoState

    private var _createUserDataState = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())
    val createUserDataState : Flow<Resource<Boolean>> get() = _createUserDataState



    fun createAccountWithEmailAndPassword(
        user: User,
        password: String,
        confirmationPassword: String
    ) {

        _register.value = Resource.Loading()

        if(checkValidation(user, password, confirmationPassword)){

            viewModelScope.launch {
               signUpUseCase.invoke(user, password).collect{
                 _register.value = it
             }
            }

        }else {
            val registerFailedStates = RegisterValidation.RegisterFailedStates(
                validateEmail(user.email),
                validatePassword(password, confirmationPassword)
            )

            viewModelScope.launch {
                _registerFailedStatesValidation.send(registerFailedStates)
            }
        }
    }


    fun saveUserInfo(uID: String, userName: String, email: String){
        viewModelScope.launch {
            saveUserInfoUseCase(uID, userName, email).collectLatest {
                _saveUserInfoState.emit(it)
            }
        }
    }

    fun createUserData(){
        viewModelScope.launch {
            createUserDataUseCase().collect{
                _createUserDataState.emit(it)
            }
        }
    }

}