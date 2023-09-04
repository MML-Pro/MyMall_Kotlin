package com.blogspot.mido_mymall.util

sealed class UpdateStatus {
    object Updated : UpdateStatus()
    object NotUpdated : UpdateStatus()
}
