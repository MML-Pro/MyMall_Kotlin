package com.example.mymall_kotlin.data.repo

import com.example.mymall_kotlin.domain.models.AddressesModel
import com.example.mymall_kotlin.domain.repo.AddressRepo
import com.example.mymall_kotlin.ui.my_address.MyAddressesFragment.Companion.SELECTED_ADDRESS
import com.example.mymall_kotlin.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class AddressRepoImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
) : AddressRepo {


    override suspend fun getAddress(): Flow<Resource<DocumentSnapshot>> {
        val result = MutableStateFlow<Resource<DocumentSnapshot>>(Resource.Ideal())

        result.value = Resource.Loading()

        firebaseFirestore.collection("USERS")
            .document(firebaseAuth.currentUser?.uid!!)
            .collection("USER_DATA")
            .document("MY_ADDRESS")
            .get()
            .addOnSuccessListener {
                result.value = Resource.Success(it)
            }.addOnFailureListener {
                result.value = Resource.Error(it.message.toString())
            }

        return result
    }

    override suspend fun addAddress(
        addressesModelList: ArrayList<AddressesModel>,
        city: String,
        localityOrStreet: String,
        flatNumberOrBuildingName: String,
        pinCode: String,
        state: String,
        landMark: String?,
        fullName: String,
        mobileNumber: String,
        alternateMobileNumber: String?,
        selectedAddress: Int
    ): Flow<Resource<Boolean>> {
        val addAddressMap: MutableMap<String, Any> = HashMap()

        val result = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())

        result.value = Resource.Loading()

        addAddressMap["list_size"] = addressesModelList.size.toLong() + 1
        addAddressMap["city_" + (addressesModelList.size.toLong() + 1).toString()] = city
        addAddressMap["localityOrStreet_" + (addressesModelList.size.toLong() + 1).toString()] =
            localityOrStreet
        addAddressMap["flatNumberOrBuildingName_" + (addressesModelList.size.toLong() + 1).toString()] =
            flatNumberOrBuildingName
        addAddressMap["pinCode_" + (addressesModelList.size.toLong() + 1).toString()] = pinCode
        addAddressMap["state_" + (addressesModelList.size.toLong() + 1).toString()] = state
        addAddressMap["landMark_" + (addressesModelList.size.toLong() + 1).toString()] =
            landMark.toString()
        addAddressMap["fullName_" + (addressesModelList.size.toLong() + 1).toString()] = fullName
        addAddressMap["mobileNumber_" + (addressesModelList.size.toLong() + 1).toString()] =
            mobileNumber
        addAddressMap["alternateMobileNumber_" + (addressesModelList.size.toLong() + 1).toString()] =
            alternateMobileNumber.toString()


        addAddressMap["selected_" + (addressesModelList.size.toLong() + 1).toString()] = true

        if (addressesModelList.size > 0) {
            addAddressMap["selected_" + (selectedAddress + 1)] = false
        }

        firebaseFirestore.collection("USERS")
            .document(firebaseAuth.currentUser?.uid!!)
            .collection("USER_DATA").document("MY_ADDRESS")
            .update(addAddressMap).addOnSuccessListener {

                if (addressesModelList.size > 0) {
                    addressesModelList[selectedAddress].selected = false
                }

                addressesModelList.add(
                    AddressesModel(
                        city = city,
                        localityOrStreet = localityOrStreet,
                        flatNumberOrBuildingName = flatNumberOrBuildingName,
                        pinCode = pinCode,
                        state = state,
                        landMark = landMark,
                        fullName = fullName,
                        mobileNumber = mobileNumber,
                        alternateMobileNumber = alternateMobileNumber,
                        selected = true
                    )
                )

//                } else {
//
//                    addressesModelList.add(
//                        AddressesModel(
//                            city = city,
//                            localityOrStreet = localityOrStreet,
//                            flatNumberOrBuildingName = flatNumberOrBuildingName,
//                            pinCode = pinCode,
//                            state = state,
//                            landMark = landMark,
//                            fullName = fullName,
//                            mobileNumber = mobileNumber,
//                            selected = true
//                        )
//                    )
//                }

                result.value = Resource.Success(true)


            }
            .addOnFailureListener {
                result.value = Resource.Error(it.message.toString())

            }


        return result
    }

    override suspend fun updateSelectedAddress(
        selectedAddress: Int,
        previousAddress: Int
    ): Flow<Resource<Boolean>> {
        val result = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())

        result.value = Resource.Loading()

        val updateSelection: HashMap<String, Any> = HashMap()
        updateSelection["selected_" + (previousAddress + 1).toString()] = false
        updateSelection["selected_" + java.lang.String.valueOf(selectedAddress + 1)] =
            true

//        previousAddress = selectedAddress

        firebaseFirestore.collection("USERS")
            .document(firebaseAuth.currentUser?.uid!!)
            .collection("USER_DATA").document("MY_ADDRESS")
            .update(updateSelection)
            .addOnSuccessListener {
                result.value = Resource.Success(true)
            }.addOnFailureListener {
                result.value = Resource.Error(it.message.toString())
            }

        return result
    }

    override suspend fun updateAddressInfo(

        position: Long,
        city: String,
        localityOrStreet: String,
        flatNumberOrBuildingName: String,
        pinCode: String,
        state: String,
        landMark: String?,
        fullName: String,
        mobileNumber: String,
        alternateMobileNumber: String?
    ): Flow<Resource<Boolean>> {
        val result = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())

        result.value = Resource.Loading()

        val updateAddressMap: HashMap<String, Any> = HashMap()

        updateAddressMap["list_size"] = (position + 1)
        updateAddressMap["city_" + ((position + 1)).toString()] = city
        updateAddressMap["localityOrStreet_" + (position + 1).toString()] =
            localityOrStreet
        updateAddressMap["flatNumberOrBuildingName_" + (position + 1).toString()] =
            flatNumberOrBuildingName
        updateAddressMap["pinCode_" + (position + 1).toString()] = pinCode
        updateAddressMap["state_" + (position + 1).toString()] = state
        updateAddressMap["landMark_" + (position + 1).toString()] =
            landMark.toString()
        updateAddressMap["fullName_" + (position + 1).toString()] = fullName
        updateAddressMap["mobileNumber_" + (position + 1).toString()] =
            mobileNumber
        updateAddressMap["alternateMobileNumber_" + (position + 1).toString()] =
            alternateMobileNumber.toString()

        firebaseFirestore.collection("USERS")
            .document(firebaseAuth.currentUser?.uid!!)
            .collection("USER_DATA").document("MY_ADDRESS")
            .update(updateAddressMap).addOnSuccessListener {
                result.value = Resource.Success(true)
            }.addOnFailureListener {
                result.value = Resource.Error(it.message.toString())
            }

        return result


    }

    override suspend fun removeAddress(
        addressesModelList: ArrayList<AddressesModel>,
        position: Int
    ): Flow<Resource<Boolean>> {

        val result = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())

        result.value = Resource.Loading()

        val updateAddressMap: HashMap<String, Any> = HashMap()
        var x = 0
        var selected = -1

        for (i in 0 until addressesModelList.size) {

            if (i != position) {
                x++

                updateAddressMap["city_" + (x).toString()] = addressesModelList[i].city
                updateAddressMap["localityOrStreet_" + (x).toString()] =
                    addressesModelList[i].localityOrStreet

                updateAddressMap["flatNumberOrBuildingName_" + (x).toString()] =
                    addressesModelList[i].flatNumberOrBuildingName
                updateAddressMap["pinCode_" + (x).toString()] = addressesModelList[i].pinCode
                updateAddressMap["state_" + (x).toString()] = addressesModelList[i].state
                updateAddressMap["landMark_" + (x).toString()] =
                    addressesModelList[i].landMark.toString()
                updateAddressMap["fullName_" + (x).toString()] = addressesModelList[i].fullName
                updateAddressMap["mobileNumber_" + (x).toString()] =
                    addressesModelList[i].mobileNumber

                updateAddressMap["alternateMobileNumber_" + (x).toString()] =
                    addressesModelList[i].alternateMobileNumber.toString()

                if (addressesModelList[position].selected) {
                    if (position - 1 >= 0) {
                        if (x == position - 1) {
                            updateAddressMap["selected_$x"] = true
                            selected = x
                        }else {
                            updateAddressMap["selected_" + (x).toString()] = addressesModelList[i].selected
                        }
                    } else {
                        if (x == 1) {
                            updateAddressMap["selected_$x"] = true
                            selected = x
                        }else {
                            updateAddressMap["selected_" + (x).toString()] = addressesModelList[i].selected
                        }
                    }
                } else {
                    updateAddressMap["selected_" + (x).toString()] = addressesModelList[i].selected

                }
            }

        }
        updateAddressMap["list_size"] = x

        firebaseFirestore.collection("USERS")
            .document(firebaseAuth.currentUser?.uid!!)
            .collection("USER_DATA").document("MY_ADDRESS")
            .set(updateAddressMap)
            .addOnSuccessListener {

                addressesModelList.removeAt(position)

                if (selected != -1) {
                    SELECTED_ADDRESS = selected - 1
                    addressesModelList[selected - 1].selected = true
                }

                result.value = Resource.Success(true)


            }.addOnFailureListener {
                result.value = Resource.Error(it.message.toString())
            }

        return result

    }


}