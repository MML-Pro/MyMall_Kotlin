package com.example.mymall_kotlin.ui.credentials

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymall_kotlin.domain.usecase.credentials.SignOutUseCase
import com.example.mymall_kotlin.util.Resource
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignOutViewModel @Inject constructor(
    private val signOutUseCase: SignOutUseCase,
    val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _authResult = MutableSharedFlow<Resource<Boolean>>()
    val authResult get() = _authResult.asSharedFlow()

    fun signOut(googleSignInClient: GoogleSignInClient?=null) {
        viewModelScope.launch {
            signOutUseCase(googleSignInClient).collectLatest {
                _authResult.emit(it)
            }
        }
    }
}