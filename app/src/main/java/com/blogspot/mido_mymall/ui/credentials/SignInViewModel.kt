package com.blogspot.mido_mymall.ui.credentials

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blogspot.mido_mymall.domain.usecase.credentials.SignInCredentialUseCase
import com.blogspot.mido_mymall.domain.usecase.credentials.SignInEmailAndPwUseCase
import com.blogspot.mido_mymall.domain.usecase.credentials.SignInGoogleUseCase
import com.blogspot.mido_mymall.util.RegisterValidation
import com.blogspot.mido_mymall.util.Resource
import com.blogspot.mido_mymall.util.checkLoginCredentials
import com.blogspot.mido_mymall.util.validateEmail
import com.blogspot.mido_mymall.util.validatePassword
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInWithEmailAndPwUseCase: SignInEmailAndPwUseCase,
    private val signInGoogleUseCase: SignInGoogleUseCase,
    private val signInCredentialUseCase: SignInCredentialUseCase
) :
    ViewModel() {

    private var _validation = Channel<RegisterValidation.RegisterFailedStates>()
    val validation get() = _validation.receiveAsFlow()

    private val _login = MutableSharedFlow<Resource<FirebaseUser>>()
    val login get() = _login.asSharedFlow()

    private val _googleIntent = MutableSharedFlow<Resource<Intent>>()
    val googleIntent get() = _googleIntent.asSharedFlow()

    private val _credentialSignInResult = MutableSharedFlow<Resource<AuthResult>>()
    val credentialSignInResult
        get() = _credentialSignInResult.asSharedFlow()

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _login.emit(Resource.Loading())
        }

        if (checkLoginCredentials(email, password)) {
            viewModelScope.launch {
                signInWithEmailAndPwUseCase(email, password).collectLatest {
                    _login.emit(it)
                }
            }
        } else {
            val registerFailedStates = RegisterValidation.RegisterFailedStates(
                validateEmail(email),
                validatePassword(password)
            )

            viewModelScope.launch {
                _validation.send(registerFailedStates)
            }
        }
    }

    fun signInGoogle() {
        viewModelScope.launch {
            signInGoogleUseCase().collect {
                _googleIntent.emit(it)
            }
        }
    }

    fun signInWithCredential(credential: AuthCredential) = viewModelScope.launch {
        signInCredentialUseCase(credential).collect {
            _credentialSignInResult.emit(it)
        }
    }
}