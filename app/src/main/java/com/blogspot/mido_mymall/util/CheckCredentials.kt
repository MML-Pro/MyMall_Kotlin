package com.blogspot.mido_mymall.util

import com.blogspot.mido_mymall.domain.models.User

fun checkValidation(
    user: User,
    password: String,
    confirmationPassword: String
): Boolean {
    val emailValidation = validateEmail(user.email)

    val passwordValidation = validatePassword(password, confirmationPassword)

    return (emailValidation is RegisterValidation.Success
            && passwordValidation is RegisterValidation.Success)
}

fun checkLoginCredentials(email: String, password: String): Boolean {
    val emailValidation = validateEmail(email)

    val passwordValidation = validatePassword(password)

    return (emailValidation is RegisterValidation.Success
            && passwordValidation is RegisterValidation.Success)
}