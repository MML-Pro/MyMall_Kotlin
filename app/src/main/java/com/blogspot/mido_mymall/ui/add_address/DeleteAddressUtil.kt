package com.blogspot.mido_mymall.ui.add_address

import com.blogspot.mido_mymall.domain.models.AddressesModel

interface DeleteAddressUtil {

    fun deleteAddress(
        addressesModelList: ArrayList<AddressesModel>,
        position: Int
    )
}