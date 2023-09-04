package com.blogspot.mido_mymall.domain.usecase.address

import com.blogspot.mido_mymall.domain.repo.AddressRepo
import javax.inject.Inject

class GetAddressUseCase @Inject constructor(private val addressRepo: AddressRepo) {

    suspend operator fun invoke() = addressRepo.getAddress()
}