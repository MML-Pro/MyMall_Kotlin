package com.blogspot.mido_mymall.ui.my_orders

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.blogspot.mido_mymall.databinding.FragmentMyOrdersBinding
import com.blogspot.mido_mymall.domain.models.CartItemModel
import com.blogspot.mido_mymall.domain.models.MyOrderItemModel
import com.blogspot.mido_mymall.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyOrdersFragment : Fragment() {

    companion object {
        private const val TAG = "MyOrdersFragment"
    }

    private var _binding: FragmentMyOrdersBinding? = null
    private val binding get() = _binding!!

    private val myOrderAdapter = MyOrderAdapter()

    private val myOrderItemModelList = arrayListOf<MyOrderItemModel>()

    private val myOrdersViewModel by viewModels<MyOrdersViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyOrdersBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        myOrdersViewModel.getMyOrders()

//        lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                myOrdersViewModel.myOrders.collect { response ->
//
//                    when (response) {
//
//                        is Resource.Loading -> {
//                            binding.progressBar.visibility = View.VISIBLE
//                        }
//
//                        is Resource.Success -> {
//
////                            Log.d(TAG, "myOrders: called")
//
//                            response.data?.let {
//
////                                myOrderItemModelList.clear()
//
//
//
//                                it.documents.forEach { documentSnapshot ->
//
//                                    val productList = mutableListOf<CartItemModel>()
//
//                                    val productsCollection =
//                                        documentSnapshot.reference.collection("Products")
//
//                                    productsCollection.get()
//                                        .addOnSuccessListener { productsQuerySnapshot ->
//                                            productsQuerySnapshot.documents.forEach { productDocument ->
//
//                                                val productName = productDocument["PRODUCT NAME"].toString()
//
//                                                val cartItemModel = CartItemModel(
//                                                    productId = productDocument.get("PRODUCT ID")
//                                                        .toString(),
//                                                    productImage = productDocument["PRODUCT IMAGE"].toString(),
//                                                    productName = productName,
//                                                    freeCoupons = productDocument["FREE COUPONS"] as Long,
//                                                    productPrice = productDocument["PRODUCT PRICE"].toString(),
//                                                    cuttedPrice = productDocument["CUTTED PRICE"].toString(),
//                                                    productQuantity = productDocument["PRODUCT QUANTITY"] as Long,
//                                                    offersApply = productDocument["OFFERS APPLIED"] as Long,
//                                                    couponsApplied = productDocument["COUPONS APPLIED"] as Long,
//                                                    selectedCouponId = productDocument["COUPON ID"].toString(),
//                                                    discountedPrice = productDocument["DISCOUNTED PRICE"].toString(),
//                                                    productRating = 0
//                                                )
//                                                productList.add(cartItemModel)
//
////                                                Log.d(TAG, "productsQuerySnapshot: $productName")
//                                            }
//
//
//
//                                        }.also {
//                                            val myOrderItemModel = MyOrderItemModel(
//                                                productList = productList,
//                                                orderStatus = documentSnapshot.getString("ORDER STATUS"),
//                                                address = documentSnapshot.getString("ADDRESS"),
//                                                orderDate = documentSnapshot.getDate("ORDER DATE"),
//                                                packedDate = documentSnapshot.getDate("PACKED DATE"),
//                                                shippedDate = documentSnapshot.getDate("SHIPPED DATE"),
//                                                deliveredDate = documentSnapshot.getDate("DELIVERED DATE"),
//                                                cancelledDate = documentSnapshot.getDate("CANCELLED DATE"),
//                                                fullName = documentSnapshot.getString("FULL NAME"),
//                                                orderID = documentSnapshot.getString("ORDER ID"),
//                                                paymentMethod = documentSnapshot.getString("PAYMENT METHOD"),
//                                                pinCode = documentSnapshot.getString("PIN CODE"),
//                                                userID = documentSnapshot.getString("USER ID")
//                                            )
//
//                                            myOrderItemModelList.add(myOrderItemModel).also {
//                                                binding.progressBar.visibility = View.GONE
//                                                myOrderAdapter.asyncListDiffer.submitList(myOrderItemModelList)
//                                            }
//                                        }
//
//
//                                }
//
//                            }
//
//                        }
//
//
//                        is Resource.Error -> {
//                            binding.progressBar.visibility = View.GONE
//                            Log.e(TAG, "myOrders: ${response.message.toString()}")
//                        }
//
//                        else -> {
//                            binding.progressBar.visibility = View.GONE
//                        }
//                    }
//
//
//                }
//            }
//        }


        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                myOrdersViewModel.myOrders.collect { response ->
                    when (response) {
                        is Resource.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is Resource.Success -> {

                            if(response.data == null){

                                binding.apply {
                                    progressBar.visibility = View.GONE
                                    myOrdersRecyclerView.visibility = View.GONE
                                    noOrdersFoundIV.visibility = View.VISIBLE
                                    emptyOrderTV.visibility = View.VISIBLE
                                }
                            }else {
                                binding.apply {
                                    myOrdersRecyclerView.visibility = View.VISIBLE
                                    noOrdersFoundIV.visibility = View.GONE
                                    emptyOrderTV.visibility = View.GONE
                                }

                                response.data.let {
                                    val tempList =
                                        mutableListOf<MyOrderItemModel>() // Create a temporary list

                                    it.documents.forEach { documentSnapshot ->

                                        val productList = mutableListOf<CartItemModel>()
                                        val productsCollection =
                                            documentSnapshot.reference.collection("Products")

                                        productsCollection.get()
                                            .addOnSuccessListener { productsQuerySnapshot ->
                                                productsQuerySnapshot.documents.forEach { productDocument ->
                                                    val productName =
                                                        productDocument["PRODUCT NAME"].toString()
                                                    val cartItemModel = CartItemModel(
                                                        productId = productDocument.get("PRODUCT ID")
                                                            .toString(),
                                                        productImage = productDocument["PRODUCT IMAGE"].toString(),
                                                        productName = productName,
                                                        freeCoupons = productDocument["FREE COUPONS"] as Long,
                                                        productPrice = productDocument["PRODUCT PRICE"].toString(),
                                                        cuttedPrice = productDocument["CUTTED PRICE"].toString(),
                                                        productQuantity = productDocument["PRODUCT QUANTITY"] as Long,
                                                        offersApply = productDocument["OFFERS APPLIED"] as Long,
                                                        couponsApplied = productDocument["COUPONS APPLIED"] as Long,
                                                        selectedCouponId = productDocument["COUPON ID"].toString(),
                                                        discountedPrice = productDocument["DISCOUNTED PRICE"].toString(),
                                                        productRating = 0
                                                    )
                                                    productList.add(cartItemModel)
                                                }

                                                val orderId = documentSnapshot.getString("ORDER ID")


                                                val myOrderItemModel = MyOrderItemModel(
                                                    productList = productList,
                                                    orderStatus = documentSnapshot.getString("ORDER STATUS"),
                                                    address = documentSnapshot.getString("ADDRESS"),
                                                    orderDate = documentSnapshot.getDate("ORDER DATE"),
                                                    packedDate = documentSnapshot.getDate("PACKED DATE"),
                                                    shippedDate = documentSnapshot.getDate("SHIPPED DATE"),
                                                    deliveredDate = documentSnapshot.getDate("DELIVERED DATE"),
                                                    cancelledDate = documentSnapshot.getDate("CANCELLED DATE"),
                                                    fullName = documentSnapshot.getString("FULL NAME"),
                                                    orderID = orderId,
                                                    paymentMethod = documentSnapshot.getString("PAYMENT METHOD"),
                                                    pinCode = documentSnapshot.getString("PIN CODE"),
                                                    userID = documentSnapshot.getString("USER ID")
                                                )

                                                Log.d(TAG, "order id: $orderId")


                                                tempList.add(myOrderItemModel)

                                                if (tempList.size == it.documents.size) {
                                                    // All data is retrieved, update the list and submit
                                                    myOrderItemModelList.clear()
                                                    myOrderItemModelList.addAll(tempList)
                                                    binding.progressBar.visibility = View.GONE
                                                    myOrderAdapter.asyncListDiffer.submitList(
                                                        myOrderItemModelList
                                                    )
                                                }
                                            }
                                            .addOnFailureListener { ex ->
                                                binding.progressBar.visibility = View.GONE
                                                Log.e(TAG, "Error fetching products: ${ex.message}")
                                            }
                                    }
                                }
                            }

                        }

                        is Resource.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Log.e(TAG, "myOrders: ${response.message.toString()}")
                        }

                        else -> {
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                }
            }
        }


        binding.myOrdersRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = myOrderAdapter
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        myOrderItemModelList.clear()
        _binding = null
    }
}