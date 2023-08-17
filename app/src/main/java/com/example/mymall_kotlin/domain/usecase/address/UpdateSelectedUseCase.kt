package com.example.mymall_kotlin.domain.usecase.address

import com.example.mymall_kotlin.domain.repo.AddressRepo
import javax.inject.Inject

class UpdateSelectedUseCase @Inject constructor(private val addressRepo: AddressRepo) {

    suspend operator fun invoke(selectedAddress: Int, previousAddress: Int) =
        addressRepo.updateSelectedAddress(selectedAddress, previousAddress)
}