package com.blogspot.mido_mymall.ui.my_account

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.blogspot.mido_mymall.R
import com.blogspot.mido_mymall.databinding.FragmentMyAccountBinding
import com.blogspot.mido_mymall.ui.MainActivity
import com.blogspot.mido_mymall.ui.credentials.SignOutViewModel
import com.blogspot.mido_mymall.ui.my_address.MyAddressViewModel
import com.blogspot.mido_mymall.util.Constants
import com.blogspot.mido_mymall.util.Constants.MANAGE_ADDRESS
import com.blogspot.mido_mymall.util.Resource
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch



@AndroidEntryPoint
class MyAccountFragment : Fragment() {

    private var _binding: FragmentMyAccountBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val TAG = "MyAccountFragment"
    }

//    private val myAccountViewModel by viewModels<MyAccountViewModel>()
//    private val myOrdersViewModel by viewModels<MyOrdersViewModel>()

    private val myAddressViewModel by viewModels<MyAddressViewModel>()

    private val signOutViewModel by viewModels<SignOutViewModel>()

    private lateinit var loadingDialog: AlertDialog



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

        if (FirebaseAuth.getInstance().currentUser != null) {

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

//            myOrdersViewModel.getMyOrders()
//            myAccountViewModel.getLastOrder()
            myAddressViewModel.getAddresses()

        } else {
//            for (x in 0 until 4) {
//                binding.yourRecentOrdersLayout.recentOrdersContainer.getChildAt(
//                    x
//                ).visibility = View.GONE
//            }

            binding.apply {

                profileDataLayout.userNameTV.text = getString(R.string.not_signed_in)

                myAddressLayout.apply {
                    viewAllButton.visibility = View.GONE
                    fullAddressTV.text = getString(R.string.no_addresses)
                }

                signOutButton.visibility = View.GONE
            }

            Constants.signInSignUpDialog(
                requireContext(),
                R.id.nav_my_account,
                layoutInflater,
                requireView()
            )
        }

//        lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                myAccountViewModel.lastOrder.collect { response ->
//
//                    when (response) {
//                        is Resource.Loading -> {
////                            loadingDialog.create()
//                            loadingDialog.show()
//                        }
//
//                        is Resource.Success -> {
//                            loadingDialog.cancel()
//
//                            response.data?.let { documentSnapshot ->
//
//                                Log.d(TAG, "documentSnapshot id: ${documentSnapshot.id}")
//
////                                val orderSummary = documentSnapshot
////                                    .reference.collection("OtherDetails")
////                                    .document("order_summary")
////                                    .get().await()
//
//                                val orderStatus = documentSnapshot.getString("ORDER STATUS")
//
//                                Log.d(TAG, "last order: $orderStatus")
//
//                                if (!orderStatus.equals("DELIVERED") && !orderStatus.equals("CANCELLED")) {
//                                    Glide.with(this@MyAccountFragment)
//                                        .load(
//                                            documentSnapshot.getString("PRODUCT IMAGE")
//                                                .toString()
//                                        )
//                                        .placeholder(R.drawable.placeholder_image)
//                                        .into(binding.orderStatusLayout.currentOrderImage)
//                                }
//
//                                binding.orderStatusLayout.currentOrderStatus.text =
//                                    orderStatus
//
//                                when (orderStatus) {
//                                    "ORDERED" -> {
//
//                                        binding.orderStatusLayout.currentOrderStatus.text =
//                                            getString(R.string.ordered)
//
////                                        binding.orderStatusLayout.orderedIndicator.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.success))
//
//                                        ImageViewCompat.setImageTintList(
//                                            binding.orderStatusLayout.orderedIndicator,
//                                            ColorStateList.valueOf(resources.getColor(R.color.success))
//                                        )
//                                    }
//
//                                    "PACKED" -> {
//
//                                        binding.orderStatusLayout.currentOrderStatus.text =
//                                            getString(R.string.packed)
//
////                                                binding.orderStatusLayout.orderedIndicator.backgroundTintList =
////                                                    ColorStateList.valueOf(resources.getColor(R.color.success))
//
//                                        ImageViewCompat.setImageTintList(
//                                            binding.orderStatusLayout.orderedIndicator,
//                                            ColorStateList.valueOf(resources.getColor(R.color.success))
//                                        )
//
//                                        ImageViewCompat.setImageTintList(
//                                            binding.orderStatusLayout.packedIndicator,
//                                            ColorStateList.valueOf(resources.getColor(R.color.success))
//                                        )
//
//
//                                        binding.orderStatusLayout.orderedPackedProgress
//                                            .progressTintList =
//                                            ColorStateList.valueOf(resources.getColor(R.color.success))
//
//                                        binding.orderStatusLayout.orderedPackedProgress.progress =
//                                            100
//                                    }
//
//                                    "SHIPPED" -> {
//
//                                        binding.orderStatusLayout.currentOrderStatus.text =
//                                            getString(R.string.shipped)
//
//                                        ImageViewCompat.setImageTintList(
//                                            binding.orderStatusLayout.orderedIndicator,
//                                            ColorStateList.valueOf(resources.getColor(R.color.success))
//                                        )
//
//                                        ImageViewCompat.setImageTintList(
//                                            binding.orderStatusLayout.packedIndicator,
//                                            ColorStateList.valueOf(resources.getColor(R.color.success))
//                                        )
//
//                                        ImageViewCompat.setImageTintList(
//                                            binding.orderStatusLayout.shippedIndicator,
//                                            ColorStateList.valueOf(resources.getColor(R.color.success))
//                                        )
//
//                                        binding.orderStatusLayout.orderedPackedProgress
//                                            .progressTintList =
//                                            ColorStateList.valueOf(resources.getColor(R.color.success))
//
//                                        binding.orderStatusLayout.packedShippedProgress
//                                            .progressTintList =
//                                            ColorStateList.valueOf(resources.getColor(R.color.success))
//
//                                        binding.orderStatusLayout.orderedPackedProgress.progress =
//                                            100
//                                        binding.orderStatusLayout.packedShippedProgress.progress =
//                                            100
//                                    }
//
//                                    "Out for Delivery" -> {
//
//                                        binding.orderStatusLayout.currentOrderStatus.text =
//                                            getString(R.string.out_for_delivery)
//
//                                        ImageViewCompat.setImageTintList(
//                                            binding.orderStatusLayout.orderedIndicator,
//                                            ColorStateList.valueOf(resources.getColor(R.color.success))
//                                        )
//
//                                        ImageViewCompat.setImageTintList(
//                                            binding.orderStatusLayout.packedIndicator,
//                                            ColorStateList.valueOf(resources.getColor(R.color.success))
//                                        )
//
//                                        ImageViewCompat.setImageTintList(
//                                            binding.orderStatusLayout.shippedIndicator,
//                                            ColorStateList.valueOf(resources.getColor(R.color.success))
//                                        )
//
//                                        ImageViewCompat.setImageTintList(
//                                            binding.orderStatusLayout.deliveredIndicator,
//                                            ColorStateList.valueOf(resources.getColor(R.color.success))
//                                        )
//
//                                        binding.orderStatusLayout.orderedPackedProgress
//                                            .progressTintList =
//                                            ColorStateList.valueOf(resources.getColor(R.color.success))
//
//                                        binding.orderStatusLayout.packedShippedProgress
//                                            .progressTintList =
//                                            ColorStateList.valueOf(resources.getColor(R.color.success))
//
//
//                                        binding.orderStatusLayout.shippedDeliveredProgress
//                                            .progressTintList =
//                                            ColorStateList.valueOf(resources.getColor(R.color.success))
//
//
//                                        binding.orderStatusLayout.orderedPackedProgress.progress =
//                                            100
//                                        binding.orderStatusLayout.packedShippedProgress.progress =
//                                            100
//                                        binding.orderStatusLayout.shippedDeliveredProgress.progress =
//                                            100
//                                    }
//
//                                }
//
//
//                            }
//
//                        }
//
//                        is Resource.Error -> {
//
//                            for (x in 0 until 4) {
//                                binding.yourRecentOrdersLayout.recentOrdersContainer.getChildAt(
//                                    x
//                                ).visibility = View.GONE
//                            }
//                            binding.yourRecentOrdersLayout.yourRecentOrdersTV.text =
//                                getString(R.string.no_recent_orders)
//
//                            loadingDialog.cancel()
//
//                            Log.e(TAG, "myOrders: ${response.message.toString()}")
//                        }
//
//                        else -> {
//                            loadingDialog.cancel()
//                        }
//                    }
//
//
//                }
//            }
//        }
//
//
//        lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                myOrdersViewModel.myOrders.collect { response ->
//
//                    if (response is Resource.Success) {
//
//                        response.data?.let { ordersDocsList ->
//                            var i = 0
//                            val userOrdersDocs = arrayListOf<DocumentSnapshot>()
//
//
//                            ordersDocsList.documents.forEach { orderDocument ->
//                                val orderId = orderDocument.id
//
//                                // Check if the order ID exists in the USER_ORDERS collection
//                                val userOrderQuerySnapshot = FirebaseFirestore.getInstance()
//                                    .collection("USERS")
//                                    .document(FirebaseAuth.getInstance().currentUser?.uid!!)
//                                    .collection("USER_ORDERS")
//                                    .whereEqualTo("Order_ID", orderId)
//                                    .get()
//                                    .await()
//
//
//                                if (userOrderQuerySnapshot.documents.isNotEmpty()) {
//                                    // Order ID exists in USER_ORDERS, add it to the tempList
//                                    userOrdersDocs.add(orderDocument)
//                                }
//                            }.also {
//
//                                if (userOrdersDocs.isEmpty()) {
//                                    binding.apply {
////                                        progressBar.visibility = View.GONE
////                                        myOrdersRecyclerView.visibility = View.GONE
////                                        noOrdersFoundIV.visibility = View.VISIBLE
////                                        emptyOrderTV.visibility = View.VISIBLE
//                                    }
////
//                                } else {
//
//                                    userOrdersDocs.forEachIndexed { index, documentSnapshot ->
//
//                                        val orderSummary = documentSnapshot
//                                            .reference.collection("OtherDetails")
//                                            .document("order_summary")
//                                            .get().await()
//
//                                        if (i < 4) {
//
//                                            val orderStatus = orderSummary.getString("ORDER STATUS")
//
//
//                                            Log.d(TAG, "orderStatus: $orderStatus")
//
//                                            if (orderStatus.equals("DELIVERED")) {
//
//                                                Glide.with(this@MyAccountFragment)
//                                                    .load(
//                                                        documentSnapshot.getString("PRODUCT IMAGE")
//                                                            .toString()
//                                                    )
//                                                    .placeholder(R.drawable.profile_placeholder)
//                                                    .into(
//                                                        binding.yourRecentOrdersLayout.recentOrdersContainer.getChildAt(
//                                                            index
//                                                        ) as CircleImageView
//                                                    )
//
//                                                i++
//
//                                            }
//                                        } else {
//                                            return@forEachIndexed
//                                        }
//                                    }
//
//                                    if (i == 0) {
//                                        binding.yourRecentOrdersLayout.yourRecentOrdersTV.text =
//                                            getString(R.string.no_recent_orders)
//                                    }
//
//                                    if (i < 3) {
//                                        for (x in 0 until 4) {
//                                            binding.yourRecentOrdersLayout.recentOrdersContainer.getChildAt(
//                                                x
//                                            ).visibility = View.GONE
//                                        }
//                                    }
//                                }
//
//                            }
//                        }
//
//                    } else if (response is Resource.Error) {
//                        Log.e(TAG, "myOrders: ${response.message.toString()}")
//                    }
//
//                }
//            }
//        }


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

                                        fullAddressTV.text = getString(R.string.no_addresses)

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
            (requireActivity()as MainActivity).destroyAdAfterLogOut()
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