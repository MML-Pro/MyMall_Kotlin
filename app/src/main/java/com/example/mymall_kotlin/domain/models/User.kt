package com.example.mymall_kotlin.domain.models

data class User(
    val userName: String,
    val email: String,
    var imagePath: String = "") {
    constructor() : this("", "", "")
}
