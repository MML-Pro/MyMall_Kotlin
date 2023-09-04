package com.blogspot.mido_mymall.domain.models

data class NotificationModel(
    val image: String,
    val body: String,
    var beenRead: Boolean
)