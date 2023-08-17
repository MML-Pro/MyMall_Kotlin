package com.example.mymall_kotlin.domain.usecase.address

import com.example.mymall_kotlin.domain.models.AddressesModel
import com.example.mymall_kotlin.domain.repo.AddressRepo
import javax.inject.Inject

class RemoveAddressUseCase @Inject constructor(private val addressRepo: AddressRepo) {

    suspend operator fun invoke(
        addressesModelList: ArrayList<AddressesModel>,
        position: Int
    ) = addressRepo.removeAddress(addressesModelList, position)
}