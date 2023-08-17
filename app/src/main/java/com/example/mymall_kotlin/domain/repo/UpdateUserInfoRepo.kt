package com.example.mymall_kotlin.domain.repo

import com.example.mymall_kotlin.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

interface UpdateUserInfoRepo {

    suspend fun updateUserName(userName: String): Resource<Boolean>

    suspend fun updateUserProfileImage(imageDataByteArray: ByteArray): MutableSharedFlow<Resource<Boolean>>

    suspend fun updateEmail(
        oldEmail: String?,
        newEmail: String,
        password: String?,
        idToken: String?
    ): Flow<Resource<Boolean>>

    suspend fun updatePassword(
        currentEmail: String,
        oldPassword: String,
        newPassword: String
    ): MutableSharedFlow<Resource<Boolean>>

}