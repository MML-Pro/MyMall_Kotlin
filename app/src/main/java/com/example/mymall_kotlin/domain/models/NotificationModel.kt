package com.example.mymall_kotlin.domain.models

data class NotificationModel(
    val image: String,
    val body: String,
    var beenRead: Boolean
)