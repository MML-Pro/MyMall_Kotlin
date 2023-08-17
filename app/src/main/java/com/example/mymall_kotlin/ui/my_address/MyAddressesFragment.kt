package com.example.mymall_kotlin.ui.my_address

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymall_kotlin.databinding.FragmentMyAddressesBinding
import com.example.mymall_kotlin.domain.models.AddressesModel
import com.example.mymall_kotlin.ui.add_address.DeleteAddressUtil
import com.example.mymall_kotlin.ui.delivery.DeliveryFragment.Companion.SELECT_ADDRESS_MODE
import com.example.mymall_kotlin.util.Constants
import com.example.mymall_kotlin.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyAddressesFragment : Fragment(), MenuProvider, DeleteAddressUtil {

    companion object {
        private const val TAG = "MyAddressesFragment"
        var mode = -1

        @JvmStatic
        var SELECTED_ADDRESS = 0
    }

    private var _binding: FragmentMyAddressesBinding? = null
    private val binding get() = _binding!!

    private val myAddressesList = arrayListOf<AddressesModel>()
    private val myAddressViewModel by viewModels<MyAddressViewModel>()
    private lateinit var addressesAdapter: AddressesAdapter
    private val args by navArgs<MyAddressesFragmentArgs>()


    private var previousAddress = 0

    private var previousAddressIndex = 0

    private var loadingDialog: Dialog? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyAddressesBinding.inflate(inflater)

        loadingDialog = Constants.setProgressDialog(requireContext())

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.CREATED)

        mode = args.selectedMode

        addressesAdapter = AddressesAdapter(mode, this)


        if (mode == SELECT_ADDRESS_MODE) {
            binding.deliverHereButton.visibility = View.VISIBLE
        } else {
            binding.deliverHereButton.visibility = View.GONE
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        previousAddress = SELECTED_ADDRESS

        myAddressViewModel.getAddresses()

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                myAddressViewModel.myAddresses.collect { response ->
                    when (response) {

                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            response.data?.let {
                                val listSize = it["list_size"] as Long

                                for (i in 1 until listSize + 1) {
                                    myAddressesList.add(
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
                                addressesAdapter.asyncListDiffer.submitList(myAddressesList)
                                binding.addressesCountTextView.text =
                                    "${myAddressesList.size.toString()} addresses saved"
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

        binding.addressesRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = addressesAdapter
        }

        binding.addNewAddressBtn.setOnClickListener {
            findNavController().navigate(
                MyAddressesFragmentDirections.actionMyAddressesFragmentToAddAddressFragment(
                    intent = "null",
                    null,
                    fromCart = false,
                    null,
                    0
                )
            )
        }

        binding.deliverHereButton.setOnClickListener {
            if (SELECTED_ADDRESS != previousAddress) {

                previousAddressIndex = previousAddress

                myAddressViewModel.updateSelectedAddress(SELECTED_ADDRESS, previousAddress)
            } else {
                findNavController().navigateUp()
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                myAddressViewModel.updateSelectedAddressState.collect { response ->
                    when (response) {

                        is Resource.Loading -> {
                            loadingDialog?.show()
                        }

                        is Resource.Success -> {
                            loadingDialog?.cancel()

                            findNavController().navigateUp()
                        }

                        is Resource.Error -> {
                            previousAddress = previousAddressIndex
                            Log.e(TAG, "updateSelectedAddressState: ${response.message.toString()}")
                            loadingDialog?.cancel()
                        }

                        else -> {}
                    }
                }
            }
        }

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Log.d(TAG, "Fragment back pressed invoked")

                    if (mode == SELECT_ADDRESS_MODE) {
                        if (SELECTED_ADDRESS != previousAddress) {
                            myAddressesList[SELECTED_ADDRESS].selected = false
                            myAddressesList[previousAddress].selected = true
                            SELECTED_ADDRESS = previousAddress
                        }

                    }
                    findNavController().navigateUp()
                }
            })

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                myAddressViewModel.removeAddressState.collect { response ->

                    when (response) {
                        is Resource.Loading -> {
                            loadingDialog?.show()
                        }

                        is Resource.Success -> {


//                            addressesAdapter.asyncListDiffer.submitList(null)
//                                .also {
//                                    myAddressViewModel.getAddresses()
//                                }

                            binding.addressesCountTextView.text =
                                "${myAddressesList.size.toString()} addresses saved"


                            Log.d(TAG, "selected address value: $SELECTED_ADDRESS")
                            loadingDialog?.cancel()
                        }

                        is Resource.Error -> {
                            Log.e(TAG, "removeAddressState: ${response.message.toString()}")
                        }

                        else -> {}
                    }


                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        myAddressesList.clear()
        _binding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == android.R.id.home) {
            if (SELECTED_ADDRESS != previousAddress) {
                myAddressesList[SELECTED_ADDRESS].selected = false
                myAddressesList[previousAddress].selected = true
                SELECTED_ADDRESS = previousAddress
            }
            findNavController(binding.root).navigateUp()
            return true
        }
        return false
    }

    override fun deleteAddress(addressesModelList: ArrayList<AddressesModel>, position: Int) {
        Log.d(TAG, "selected address value before remove: $SELECTED_ADDRESS")
        myAddressViewModel.removeAddress(addressesModelList, position)
        addressesAdapter.deleteItem(position)
        myAddressesList.removeAt(position)
    }

}