package com.blogspot.mido_mymall.util

sealed class RegisterValidation(){

    object Success : RegisterValidation()

    data class Failed(val message:String) : RegisterValidation()

    data class RegisterFailedStates(
        val email: RegisterValidation,
        val password : RegisterValidation
    )

}
