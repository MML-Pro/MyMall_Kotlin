package com.blogspot.mido_mymall.domain.models

data class User(
    val userName: String,
    val email: String,
    var imagePath: String = "") {
    constructor() : this("", "", "")
}
