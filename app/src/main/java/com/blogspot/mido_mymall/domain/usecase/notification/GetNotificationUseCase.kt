package com.blogspot.mido_mymall.domain.usecase.notification

import com.blogspot.mido_mymall.domain.repo.NotificationRepo
import javax.inject.Inject

class GetNotificationUseCase @Inject constructor(private val notificationRepo: NotificationRepo) {

    operator fun invoke(remove:Boolean) = notificationRepo.getNotifications(remove)
}