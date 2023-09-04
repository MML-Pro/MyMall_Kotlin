package com.blogspot.mido_mymall.domain.usecase.address

import com.blogspot.mido_mymall.domain.repo.AddressRepo
import javax.inject.Inject

class UpdateSelectedUseCase @Inject constructor(private val addressRepo: AddressRepo) {

    suspend operator fun invoke(selectedAddress: Int, previousAddress: Int) =
        addressRepo.updateSelectedAddress(selectedAddress, previousAddress)
}