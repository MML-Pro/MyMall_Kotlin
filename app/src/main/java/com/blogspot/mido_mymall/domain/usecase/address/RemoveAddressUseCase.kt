package com.blogspot.mido_mymall.domain.usecase.address

import com.blogspot.mido_mymall.domain.models.AddressesModel
import com.blogspot.mido_mymall.domain.repo.AddressRepo
import javax.inject.Inject

class RemoveAddressUseCase @Inject constructor(private val addressRepo: AddressRepo) {

    suspend operator fun invoke(
        addressesModelList: ArrayList<AddressesModel>,
        position: Int
    ) = addressRepo.removeAddress(addressesModelList, position)
}