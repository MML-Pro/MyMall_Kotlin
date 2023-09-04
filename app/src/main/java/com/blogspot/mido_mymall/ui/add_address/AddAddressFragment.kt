package com.blogspot.mido_mymall.ui.add_address

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.blogspot.mido_mymall.R
import com.blogspot.mido_mymall.databinding.FragmentAddAddressBinding
import com.blogspot.mido_mymall.domain.models.AddressesModel
import com.blogspot.mido_mymall.ui.my_address.AddressesAdapter
import com.blogspot.mido_mymall.ui.my_address.MyAddressesFragment
import com.blogspot.mido_mymall.ui.my_address.MyAddressesFragment.Companion.SELECTED_ADDRESS
import com.blogspot.mido_mymall.util.Constants
import com.blogspot.mido_mymall.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddAddressFragment : Fragment() {

    companion object {
        private const val TAG = "AddAddressFragment"

    }

    private var _binding: FragmentAddAddressBinding? = null
    private val binding get() = _binding!!

    private lateinit var stateList: Array<String>
    private var selectedState: String? = null

    private val addAddressViewModel by viewModels<AddAddressViewModel>()

    private lateinit var loadingDialog: AlertDialog

    private val args by navArgs<AddAddressFragmentArgs>()

    private val myAddressList = arrayListOf<AddressesModel>()

    private lateinit var addressesAdapter: AddressesAdapter

    private var updateAddress = false

    private lateinit var addressesModel: AddressesModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddAddressBinding.inflate(inflater)

        loadingDialog = Constants.setProgressDialog(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addAddressViewModel.getAddress()

        addressesAdapter = AddressesAdapter(MyAddressesFragment.mode)

        stateList = resources.getStringArray(R.array.us_states)
        val spinnerAdapter: ArrayAdapter<*> = ArrayAdapter<Any?>(
            requireContext(),
            android.R.layout.simple_spinner_item, stateList
        )

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.stateSpinner.adapter = spinnerAdapter

        binding.stateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                selectedState = stateList[position]
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }


        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                addAddressViewModel.myAddresses.collect { response ->
                    when (response) {

                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            response.data?.let {
                                val listSize = it["list_size"] as Long

                                for (i in 1 until listSize + 1) {
                                    myAddressList.add(
                                        AddressesModel(
                                            it.get("city_$i").toString(),
                                            it.get("localityOrStreet_$i").toString(),
                                            it.get("flatNumberOrBuildingName_$i").toString(),
                                            it.get("pinCode_$i").toString(),
                                            it.get("state_$i").toString(),
                                            it.get("landMark_$i").toString(),
                                            it.get("fullName_$i").toString(),
                                            it.get("mobileNumber_$i").toString(),
                                            it.get("alternateMobileNumber_$i").toString(),
                                            it.get("selected_$i") as Boolean
                                        )
                                    )

                                    if (it.get("selected_$i") as Boolean) {
                                        SELECTED_ADDRESS = (i - 1).toString().toInt()
                                    }
                                }

                            }.also {

                                addressesAdapter.asyncListDiffer.submitList(myAddressList)

                                if (args.intent.equals("update_address")) {
                                    updateAddress = true
                                    addressesModel = myAddressList[args.addressPosition.toInt()]

                                    binding.apply {
                                        cityEditText.setText(addressesModel.city)
                                        localityOrStreet.setText(addressesModel.localityOrStreet)
                                        flatBuildEditText.setText(addressesModel.flatNumberOrBuildingName)
                                        pinCodeEditText.setText(addressesModel.pinCode)
//                                                stateList.
                                        for (i in stateList.indices) {
                                            if (stateList[i].equals(addressesModel.state)) {
                                                stateSpinner.setSelection(i)
                                            }
                                        }


                                        landmarkEditText.setText(addressesModel.landMark)
                                        nameEditText.setText(addressesModel.fullName)
                                        mobileNoEditText.setText(addressesModel.mobileNumber)
                                        alternateMobileNoEditText.setText(addressesModel.alternateMobileNumber)

                                        saveButton.text = "Update Address"

                                        saveButton.setOnClickListener {
                                            loadingDialog.show()
                                            addAddressViewModel.updateAddressInfo(
                                                args.addressPosition,
                                                binding.cityEditText.text.toString(),
                                                binding.localityOrStreet.text.toString(),
                                                binding.flatBuildEditText.text.toString(),
                                                binding.pinCodeEditText.text.toString(),
                                                binding.stateSpinner.selectedItem.toString(),
                                                binding.landmarkEditText.text.toString(),
                                                binding.nameEditText.text.toString(),
                                                binding.mobileNoEditText.text.toString(),
                                                binding.alternateMobileNoEditText.text.toString(),
                                            )
                                        }

                                    }

                                } else {

                                    binding.saveButton.text = "Save Address"

                                    binding.saveButton.setOnClickListener { view ->

                                        if (TextUtils.isEmpty(binding.cityEditText.text)) {
                                            binding.cityEditText.error = "cannot be empty"
                                        } else if (TextUtils.isEmpty(binding.localityOrStreet.text)) {
                                            binding.localityOrStreet.error = "cannot be empty"
                                        } else if (TextUtils.isEmpty(binding.flatBuildEditText.text)) {
                                            binding.flatBuildEditText.error = "cannot be empty"
                                        } else if (TextUtils.isEmpty(binding.pinCodeEditText.text)) {
                                            binding.pinCodeEditText.error = "cannot be empty"
                                        } else if (TextUtils.isEmpty(binding.nameEditText.text)) {
                                            binding.nameEditText.error = "cannot be empty"
                                        } else if (TextUtils.isEmpty(binding.mobileNoEditText.text)) {
                                            binding.mobileNoEditText.error = "cannot be empty"
                                        } else {
                                            loadingDialog.show()



                                            addAddressViewModel.addNewAddress(
                                                myAddressList,
                                                binding.cityEditText.text.toString(),
                                                binding.localityOrStreet.text.toString(),
                                                binding.flatBuildEditText.text.toString(),
                                                binding.pinCodeEditText.text.toString(),
                                                binding.stateSpinner.selectedItem.toString(),
                                                binding.landmarkEditText.text.toString(),
                                                binding.nameEditText.text.toString(),
                                                binding.mobileNoEditText.text.toString(),
                                                binding.alternateMobileNoEditText.text.toString(),
                                                SELECTED_ADDRESS
                                            )

                                        }
                                    }
                                }
                            }
                        }

                        is Resource.Error -> {
                            Log.e(TAG, "myAddresses: ${response.message.toString()}")
                            Toast.makeText(
                                requireContext(),
                                response.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {}
                    }

                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                addAddressViewModel.addAddressState.collect { response ->

                    if (response is Resource.Success) {
                        loadingDialog.dismiss()

                        if (args.intent == "deliveryIntent") {
                            findNavController(binding.root).navigate(
                                AddAddressFragmentDirections.actionAddAddressFragmentToDeliveryFragment(
                                    cartItemModelList = args.cartItemModelList,
                                    cartListIds = args.cartListIds,
                                    fromCart = args.fromCart,
                                    totalAmount = args.totalAmount
                                )
                            )
                        } else {
//                            MyAddressesFragment.refreshItem(
//                                FirebaseDbQueries.selectedAddress,
//                                FirebaseDbQueries.addressesModelList.size() - 1
//                            )

                            addressesAdapter.refreshItem(SELECTED_ADDRESS, myAddressList.size - 1)


                            findNavController(binding.root).popBackStack()
                        }

//                        Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()

                    } else if (response is Resource.Error) {
                        loadingDialog.dismiss()
                        Log.e(TAG, "addAddressState: ${response.message.toString()}")
                    }


                }

            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                addAddressViewModel.updateAddressState.collect { response ->
                    when (response) {
                        is Resource.Loading -> {
                            loadingDialog.show()
                        }

                        is Resource.Success -> {
                            loadingDialog.cancel()
                            Toast.makeText(requireContext(), "Address updated", Toast.LENGTH_SHORT)
                                .show()
                            findNavController().navigateUp()
                        }

                        is Resource.Error -> {
                            loadingDialog.cancel()
                            Log.e(TAG, "updateAddressState: ${response.message.toString()}")
                        }

                        else -> {}
                    }
                }
            }
        }


    }


}