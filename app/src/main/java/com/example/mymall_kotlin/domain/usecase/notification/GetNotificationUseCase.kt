package com.example.mymall_kotlin.domain.usecase.notification

import com.example.mymall_kotlin.domain.repo.NotificationRepo
import javax.inject.Inject

class GetNotificationUseCase @Inject constructor(private val notificationRepo: NotificationRepo) {

    operator fun invoke(remove:Boolean) = notificationRepo.getNotifications(remove)
}