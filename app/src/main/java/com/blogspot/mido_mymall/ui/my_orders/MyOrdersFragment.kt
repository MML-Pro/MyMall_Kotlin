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
import com.blogspot.mido_mymall.domain.models.MyOrderItemModel
import com.blogspot.mido_mymall.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Date

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

    private val myRatingsIds = arrayListOf<String>()
    private val myRatings = arrayListOf<Long>()


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

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                myOrdersViewModel.myOrders.collect { response ->

                    when (response) {

                        is Resource.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is Resource.Success -> {

                            Log.d(TAG, "myOrders: called")

                            response.data?.let {

                                myOrderItemModelList.clear()

                                it.documents.forEach { documentSnapshot ->

                                    myOrderItemModelList.add(
                                        MyOrderItemModel(
                                            productID = documentSnapshot.getString("PRODUCT ID")
                                                .toString(),
                                            productName = documentSnapshot.getString("PRODUCT NAME")
                                                .toString(),
                                            productImage = documentSnapshot.getString("PRODUCT IMAGE")
                                                .toString(),
                                            orderStatus = documentSnapshot.getString("ORDER STATUS")
                                                .toString(),
                                            address = documentSnapshot.getString("ADDRESS")
                                                .toString(),
                                            couponID = documentSnapshot.getString("COUPON ID")
                                                .toString(),
                                            cuttedPrice = documentSnapshot.getString("CUTTED PRICE")
                                                .toString(),
                                            orderDate = documentSnapshot.getDate("ORDER DATE") as Date,
                                            packedDate = documentSnapshot.getDate("PACKED DATE") as Date,
                                            shippedDate = documentSnapshot.getDate("SHIPPED DATE") as Date,
                                            deliveredDate = documentSnapshot.getDate("DELIVERED DATE") as Date,
                                            cancelledDate = documentSnapshot.getDate("CANCELLED DATE") as Date,
                                            discountedPrice = documentSnapshot.getString("DISCOUNTED PRICE")
                                                .toString(),
                                            freeCoupons = documentSnapshot.getLong("FREE COUPONS") as Long,
                                            fullName = documentSnapshot.getString("FULL NAME")
                                                .toString(),
                                            orderID = documentSnapshot.getString("ORDER ID")
                                                .toString(),
                                            paymentMethod = documentSnapshot.getString("PAYMENT METHOD")
                                                .toString(),
                                            pinCode = documentSnapshot.getString("PIN CODE")
                                                .toString(),
                                            productPrice = documentSnapshot.getString("PRODUCT PRICE")
                                                .toString(),
                                            productQuantity = documentSnapshot.getLong("PRODUCT QUANTITY") as Long,
                                            userID = documentSnapshot.getString("USER ID")
                                                .toString(),
                                            deliveryPrice = documentSnapshot.getString("DELIVERY PRICE"),
                                            productRating = null,

                                            )
                                    )

                                }.also {

//                                        Log.d(TAG, "onViewCreated: product price ${myOrderItemModelList[0].productPrice}")


                                    binding.progressBar.visibility = View.GONE
                                    myOrderAdapter.asyncListDiffer.submitList(myOrderItemModelList)
                                    myOrdersViewModel.getRatings()
                                }

                            }

                        }


                        is Resource.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Log.e(TAG, "myOrders: ${response.message.toString()}")
                        }

                        else -> {binding.progressBar.visibility = View.GONE}
                    }


                }
            }
        }


        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                myOrdersViewModel.ratingsIds.collect { response ->

                    when (response) {

                        is Resource.Success -> {
                            response.data?.let {
                                val listSize = it.get("list_size") as Long

                                val orderProductIds = arrayListOf<String>()

                                myOrderItemModelList.forEach { myOrderItem ->
                                    orderProductIds.add(myOrderItem.productID!!)
                                }

                                Log.d(TAG, "orderProductIds: size ${orderProductIds.size}")

                                for (i in 0 until listSize) {
                                    myRatingsIds.add(it.get("product_id_$i").toString())
                                    myRatings.add(it.get("rating_$i") as Long)

                                    val ratingId = it.get("product_id_$i").toString()

//                                    if (ratingId.equals(productId)) {
//                                        setRating(it.get("rating_$i") as Long, true)
//                                    }


                                    orderProductIds.forEachIndexed { index, productId ->

                                        if (productId.equals(ratingId)) {
                                            myOrderItemModelList[index].productRating =
                                                (it.get("rating_$i") as Long).toInt()
                                        }

                                    }

                                }


                            }.also {
                                myOrderAdapter.notifyDataSetChanged()
                                binding.progressBar.visibility = View.GONE
//                                myOrderAdapter.submitRatingLists(myRatingsIds, myRatings)
                            }
                        }

                        is Resource.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Log.e(TAG, "onViewCreated: ${response.message.toString()}")
                        }

                        else -> {}
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