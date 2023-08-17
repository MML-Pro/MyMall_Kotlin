package com.example.mymall_kotlin.util

sealed class RegisterValidation(){

    object Success : RegisterValidation()

    data class Failed(val message:String) : RegisterValidation()

    data class RegisterFailedStates(
        val email: RegisterValidation,
        val password : RegisterValidation
    )

}
