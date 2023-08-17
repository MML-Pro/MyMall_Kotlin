package com.example.mymall_kotlin.ui.add_address

import com.example.mymall_kotlin.domain.models.AddressesModel

interface DeleteAddressUtil {

    fun deleteAddress(
        addressesModelList: ArrayList<AddressesModel>,
        position: Int
    )
}