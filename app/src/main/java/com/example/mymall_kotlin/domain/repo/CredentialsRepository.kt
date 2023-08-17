package com.example.mymall_kotlin.domain.repo

import android.content.Intent
import com.example.mymall_kotlin.domain.models.User
import com.example.mymall_kotlin.util.Resource
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface CredentialsRepository {

    fun createUserWithEmailAndPassword(
        user: User,
        password: String
    ): Flow<Resource<FirebaseUser>>

    fun saveUserInfo(userUID: String, userName: String, email: String): Flow<Resource<Boolean>>


    fun signInWithEmailAndPassword(email: String, password: String): Flow<Resource<FirebaseUser>>

    fun signInWithGoogle(): Flow<Resource<Intent>>

    fun signInWithCredential(credential: AuthCredential): Flow<Resource<AuthResult>>

    fun signOut(googleSignInClient: GoogleSignInClient?): Flow<Resource<Boolean>>

    fun resetPassword(email: String): Flow<Resource<Boolean>>

    suspend fun createUserData() : Flow<Resource<Boolean>>

}
