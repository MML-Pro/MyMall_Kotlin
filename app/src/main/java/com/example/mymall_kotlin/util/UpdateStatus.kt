package com.example.mymall_kotlin.util

sealed class UpdateStatus {
    object Updated : UpdateStatus()
    object NotUpdated : UpdateStatus()
}
