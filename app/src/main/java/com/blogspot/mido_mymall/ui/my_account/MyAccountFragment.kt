package com.blogspot.mido_mymall.ui.my_account

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.blogspot.mido_mymall.R
import com.blogspot.mido_mymall.databinding.FragmentMyAccountBinding
import com.blogspot.mido_mymall.ui.MainActivity
import com.blogspot.mido_mymall.ui.credentials.SignOutViewModel
import com.blogspot.mido_mymall.ui.my_address.MyAddressViewModel
import com.blogspot.mido_mymall.ui.my_orders.MyOrdersViewModel
import com.blogspot.mido_mymall.util.Constants
import com.blogspot.mido_mymall.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch

private const val TAG = "MyAccountFragment"

@AndroidEntryPoint
class MyAccountFragment : Fragment() {

    private var _binding: FragmentMyAccountBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val MANAGE_ADDRESS = 1
    }

    private val myOrdersViewModel by viewModels<MyOrdersViewModel>()

    private val myAddressViewModel by viewModels<MyAddressViewModel>()

    private val signOutViewModel by viewModels<SignOutViewModel>()

    private lateinit var loadingDialog: AlertDialog

    private val myAccountViewModel by viewModels<MyAccountViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyAccountBinding.inflate(inflater)

        loadingDialog = Constants.setProgressDialog(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.profileDataLayout.apply {
            Glide.with(this@MyAccountFragment)
                .load((requireActivity() as MainActivity).userImage)
                .placeholder(R.drawable.account)
                .into(userImageView)

            userNameTV.text = (requireActivity() as MainActivity).userName
            userEmailTV.text = (requireActivity() as MainActivity).userEmail

            profileSettingsButton.setOnClickListener {
                findNavController()
                    .navigate(
                        MyAccountFragmentDirections
                            .actionNavMyAccountToEditUserInfoFragment(

                            )
                    )
            }
        }

        myOrdersViewModel.getMyOrders()
        myAccountViewModel.getLastOrder()

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                myAccountViewModel.lastOrder.collect { response ->

                    when (response) {
                        is Resource.Loading -> {
//                            loadingDialog.create()
                            loadingDialog.show()
                        }

                        is Resource.Success -> {
                            loadingDialog.cancel()

                            response.data?.let { documentSnapshot ->

                                    val orderStatus = documentSnapshot.getString("ORDER STATUS")

                                    if (!orderStatus.equals("DELIVERED") && !orderStatus.equals("CANCELLED")) {
                                        Glide.with(this@MyAccountFragment)
                                            .load(
                                                documentSnapshot.getString("PRODUCT IMAGE")
                                                    .toString()
                                            )
                                            .placeholder(R.drawable.account)
                                            .into(binding.orderStatusLayout.currentOrderImage)
                                    }

                                        binding.orderStatusLayout.currentOrderStatus.text =
                                            orderStatus

                                        when (orderStatus) {
                                            "ORDERED" -> {
                                                ImageViewCompat.setImageTintList(
                                                    binding.orderStatusLayout.orderedIndicator,
                                                    ColorStateList.valueOf(resources.getColor(R.color.success))
                                                )
                                            }

                                            "PACKED" -> {
//                                                binding.orderStatusLayout.orderedIndicator.backgroundTintList =
//                                                    ColorStateList.valueOf(resources.getColor(R.color.success))

                                                ImageViewCompat.setImageTintList(
                                                    binding.orderStatusLayout.orderedIndicator,
                                                    ColorStateList.valueOf(resources.getColor(R.color.success))
                                                )

                                                ImageViewCompat.setImageTintList(
                                                    binding.orderStatusLayout.packedIndicator,
                                                    ColorStateList.valueOf(resources.getColor(R.color.success))
                                                )


                                                binding.orderStatusLayout.orderedPackedProgress
                                                    .progressTintList =
                                                    ColorStateList.valueOf(resources.getColor(R.color.success))

                                                binding.orderStatusLayout.orderedPackedProgress.progress =
                                                    100
                                            }

                                            "SHIPPED" -> {
                                                ImageViewCompat.setImageTintList(
                                                    binding.orderStatusLayout.orderedIndicator,
                                                    ColorStateList.valueOf(resources.getColor(R.color.success))
                                                )

                                                ImageViewCompat.setImageTintList(
                                                    binding.orderStatusLayout.packedIndicator,
                                                    ColorStateList.valueOf(resources.getColor(R.color.success))
                                                )

                                                ImageViewCompat.setImageTintList(
                                                    binding.orderStatusLayout.shippedIndicator,
                                                    ColorStateList.valueOf(resources.getColor(R.color.success))
                                                )

                                                binding.orderStatusLayout.orderedPackedProgress
                                                    .progressTintList =
                                                    ColorStateList.valueOf(resources.getColor(R.color.success))

                                                binding.orderStatusLayout.packedShippedProgress
                                                    .progressTintList =
                                                    ColorStateList.valueOf(resources.getColor(R.color.success))

                                                binding.orderStatusLayout.orderedPackedProgress.progress =
                                                    100
                                                binding.orderStatusLayout.packedShippedProgress.progress =
                                                    100
                                            }

                                            "Out for Delivery" -> {
                                                ImageViewCompat.setImageTintList(
                                                    binding.orderStatusLayout.orderedIndicator,
                                                    ColorStateList.valueOf(resources.getColor(R.color.success))
                                                )

                                                ImageViewCompat.setImageTintList(
                                                    binding.orderStatusLayout.packedIndicator,
                                                    ColorStateList.valueOf(resources.getColor(R.color.success))
                                                )

                                                ImageViewCompat.setImageTintList(
                                                    binding.orderStatusLayout.shippedIndicator,
                                                    ColorStateList.valueOf(resources.getColor(R.color.success))
                                                )

                                                ImageViewCompat.setImageTintList(
                                                    binding.orderStatusLayout.deliveredIndicator,
                                                    ColorStateList.valueOf(resources.getColor(R.color.success))
                                                )

                                                binding.orderStatusLayout.orderedPackedProgress
                                                    .progressTintList =
                                                    ColorStateList.valueOf(resources.getColor(R.color.success))

                                                binding.orderStatusLayout.packedShippedProgress
                                                    .progressTintList =
                                                    ColorStateList.valueOf(resources.getColor(R.color.success))


                                                binding.orderStatusLayout.shippedDeliveredProgress
                                                    .progressTintList =
                                                    ColorStateList.valueOf(resources.getColor(R.color.success))


                                                binding.orderStatusLayout.orderedPackedProgress.progress =
                                                    100
                                                binding.orderStatusLayout.packedShippedProgress.progress =
                                                    100
                                                binding.orderStatusLayout.shippedDeliveredProgress.progress =
                                                    100
                                            }

                                        }


                                }

                        }

                        is Resource.Error -> {

                            for (x in 0 until 4) {
                                binding.yourRecentOrdersLayout.recentOrdersContainer.getChildAt(
                                    x
                                ).visibility = View.GONE
                            }
                            binding.yourRecentOrdersLayout.yourRecentOrdersTV.text =
                                "No recent orders."

                            loadingDialog.cancel()

                            Log.e(TAG, "myOrders: ${response.message.toString()}")
                        }

                        else -> {loadingDialog.cancel()}
                    }


                }
            }
        }


        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                myOrdersViewModel.myOrders.collect{ response->

                    if(response is Resource.Success){

                        response.data?.let {querySnapshot->
                            var i = 0
                            querySnapshot.documents.forEachIndexed { index, documentSnapshot ->

                                if (i < 4) {

                                    val orderStatus = documentSnapshot.getString("ORDER STATUS")
                                    if (orderStatus.equals("DELIVERED")) {

                                        Glide.with(this@MyAccountFragment)
                                            .load(
                                                documentSnapshot.getString("PRODUCT IMAGE")
                                                    .toString()
                                            )
                                            .placeholder(R.drawable.profile_placeholder)
                                            .into(
                                                binding.yourRecentOrdersLayout.recentOrdersContainer.getChildAt(
                                                    index
                                                ) as CircleImageView
                                            )

                                        i++

                                    }
                                } else {
                                    return@forEachIndexed
                                }
                            }

                            if (i == 0) {
                                binding.yourRecentOrdersLayout.yourRecentOrdersTV.text =
                                    "No recent orders."
                            }

                            if (i < 3) {
                                for (x in 0 until 4) {
                                    binding.yourRecentOrdersLayout.recentOrdersContainer.getChildAt(
                                        x
                                    ).visibility = View.GONE
                                }
                            }
                        }

                    }else if(response is Resource.Error){
                        Log.e(TAG, "myOrders: ${response.message.toString()}" )
                    }

                }
            }
        }

        myAddressViewModel.getAddresses()

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                myAddressViewModel.myAddresses.collect { response ->
                    when (response) {

                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            response.data?.let {
                                val listSize = it["list_size"] as Long

                                if (listSize == 0L) {

                                    binding.myAddressLayout.apply {
                                        fullNameTV.text = "N/A"

                                        fullAddressTV.text = "No addresses"

                                        pinCode.text = "N/A"

                                    }
                                } else {
                                    for (i in 1 until listSize + 1) {

                                        if (it.get("selected_$i") as Boolean) {
                                            binding.myAddressLayout.apply {
                                                fullNameTV.text =
                                                    it.get("fullName_$i").toString()

                                                val address = (it.get("flatNumberOrBuildingName_$i")
                                                    .toString()
                                                        + " " + it.get("localityOrStreet_$i")
                                                    .toString()
                                                        ) + " " + it.get("landMark_$i")
                                                    .toString() + " " +
                                                        it.get("city_$i").toString() + " " + it.get(
                                                    "state_$i"
                                                )

                                                fullAddressTV.text = address

                                                pinCode.text = it.get("pinCode_$i").toString()

                                            }

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


        binding.myAddressLayout.viewAllButton.setOnClickListener {
            findNavController()
                .navigate(
                    MyAccountFragmentDirections.actionNavMyAccountToMyAddressesFragment(
                        MANAGE_ADDRESS
                    )
                )
        }

        binding.signOutButton.setOnClickListener {
            signOutViewModel.signOut((requireActivity() as MainActivity).googleSignInClient)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                signOutViewModel.authResult.collect {
                    if (it is Resource.Success) {
                        findNavController().navigate(MyAccountFragmentDirections.actionGlobalLoginFragment())
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}