package com.example.mymall_kotlin.ui.my_cart

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymall_kotlin.databinding.FragmentMyCartBinding
import com.example.mymall_kotlin.domain.models.CartItemModel
import com.example.mymall_kotlin.domain.models.RewardModel
import com.example.mymall_kotlin.ui.my_rewards.MyRewardsViewModel
import com.example.mymall_kotlin.ui.product_details.ProductDetailsFragment
import com.example.mymall_kotlin.util.Constants
import com.example.mymall_kotlin.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.internal.addHeaderLenient

private const val TAG = "MyCartFragment"

@AndroidEntryPoint
class MyCartFragment : Fragment(), MyCartUtil {

    private var _binding: FragmentMyCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var cartAdapter: CartAdapter

    private var myCartListIds = arrayListOf<String>()
    private var cartItemModelList = arrayListOf<CartItemModel>()

    private val myCartViewModel by viewModels<MyCartViewModel>()

    private var listSize: Long = 0

    private var productId: String? = null

//    private val myAddressesList = arrayListOf<AddressesModel>()

    private var dialog: AlertDialog? = null

    private val myRewardsViewModel by viewModels<MyRewardsViewModel>()

    private val rewardModelList = arrayListOf<RewardModel>()

    private var fromCart = true

    private var totalAmount = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyCartBinding.inflate(inflater)

        cartAdapter = CartAdapter(true, binding.totalPriceTV, this, isDeliveryFragment = false)

        dialog = Constants.setProgressDialog(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


//        cartItemModelList.add(CartItemModel(1,"Price (3 items)","Rs.16999","Free","Rs.5999/-"))


        myCartViewModel.getMyCartListIds()

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                myCartViewModel.myCartListIds.collect { response ->
                    when (response) {
                        is Resource.Success -> {
                            listSize = response.data?.get("list_size") as Long

                            for (i in 0 until listSize) {
                                myCartListIds.add(response.data["product_id_$i"].toString())

                                productId = response.data["product_id_$i"].toString()

                                myCartViewModel.loadMyCartList(productId!!)

                            }

                        }

                        is Resource.Error -> {
                            Log.e(
                                TAG,
                                "onViewCreated: ${response.message.toString()}"
                            )
                        }

                        else -> {}
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                myCartViewModel.cartList.collect {
                    if (it is Resource.Success) {

                        it.data?.let { documentSnapshot ->

                            var index = 0
                            if (myCartListIds.size >= 2) {
                                index = myCartListIds.size - 2
                            }


                            cartItemModelList.add(
                                index,
                                CartItemModel(
                                    type = CartItemModel.CART_ITEM,
                                    productId = productId,
                                    productImage = documentSnapshot["product_image_1"].toString(),
                                    productName = documentSnapshot["product_name"].toString(),
                                    freeCoupons = documentSnapshot["free_coupons"] as Long,
                                    productPrice = documentSnapshot["product_price"].toString(),
                                    cuttedPrice = documentSnapshot["cutted_price"].toString(),
                                    productQuantity = 1,
                                    maxQuantity = documentSnapshot["max_quantity"] as Long,
                                    stockQuantity = documentSnapshot["stock_quantity"] as Long,
                                    offersApply = documentSnapshot["offers_applied"] as Long,
                                    couponsApplied = 0,
                                    inStock = documentSnapshot["in_stock"] as Boolean,
                                    qtyIDs = arrayListOf(),
                                    selectedCouponId = null,
                                    discountedPrice = null,
                                    totalItems = null,
                                    totalItemsPrice = null,
                                    deliveryPrice = null,
                                    totalAmount = null,
                                    savedAmount = null
                                )
                            ).also {
                                if (myCartListIds.size > 0) {
                                    cartItemModelList.add(
                                        CartItemModel(CartItemModel.TOTAL_AMOUNT)
                                    )
                                }

                                cartAdapter.asyncListDiffer.submitList(cartItemModelList)
                                binding.totalAmountAndContinue.visibility = View.VISIBLE
                            }


                        }

                    } else if (it is Resource.Error) {
                        Log.e(TAG, "Resource.Error: ${it.message.toString()}")
                    }
                }
            }
        }


        binding.cartItemsRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = cartAdapter

        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                myCartViewModel.removeCartListState.collect {

                    if (it is Resource.Success) {
                        ProductDetailsFragment.ALREADY_ADDED_TO_CART_LIST = false


                    } else if (it is Resource.Error) {
                        Log.e(TAG, "onViewCreated: ${it.message.toString()}")
                    }

                }
            }
        }

        binding.cardContinueButton.setOnClickListener {

            myCartViewModel.getAddresses()

        }



        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                myCartViewModel.myAddresses.collect { response ->
                    when (response) {

                        is Resource.Loading -> {
//                            dialog?.show()
                        }

                        is Resource.Success -> {

                            dialog?.cancel()

                            response.data?.let {
                                val listSize = it["list_size"] as Long

                                if (listSize == 0L) {
                                    findNavController().navigate(
                                        MyCartFragmentDirections
                                            .actionNavMyCartToAddAddressFragment(
                                                "deliveryIntent",
                                                cartListIds = myCartListIds.toTypedArray(),
                                                cartItemModelList = cartItemModelList.toTypedArray(),
                                                fromCart = fromCart,
                                                totalAmount = totalAmount
                                            )
                                    )
                                } else {
                                    findNavController().navigate(
                                        MyCartFragmentDirections
                                            .actionNavMyCartToDeliveryFragment(
                                                cartListIds = myCartListIds.toTypedArray(),
                                                cartItemModelList = cartItemModelList.toTypedArray(),
                                                fromCart = fromCart,
                                                totalAmount = totalAmount
                                            )
                                    )
                                }


                            }
                        }

                        is Resource.Error -> {
                            dialog?.cancel()
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

    }

    override fun deleteItem(position: Int) {

        if (cartItemModelList[position].selectedCouponId?.isNotEmpty() == true) {
            cartItemModelList[position].selectedCouponId = null
        }

        if (myCartListIds.isNotEmpty() && cartItemModelList.isNotEmpty()) {
            myCartViewModel.removeFromCartList(myCartListIds, position)
            cartAdapter.deleteItem(position)
//            cartAdapter.notifyItemRemoved(position)

            try {
                if (myCartListIds.isEmpty()) {
                    cartItemModelList.clear()
                }
            } catch (ex: Exception) {
                Log.e(TAG, "deleteItem: ${ex.cause.toString()}")
            } finally {
                binding.totalAmountAndContinue.visibility = View.GONE
            }

//            binding.totalAmountAndContinue.visibility = View.GONE

//            cartAdapter.notifyDataSetChanged()

            Toast.makeText(requireContext(), "Item deleted from cart list", Toast.LENGTH_SHORT)
                .show()

        }
    }

    override fun getTotalAmount(totalAmount: Int) {
        this.totalAmount = totalAmount
    }

    override fun onDestroyView() {
        super.onDestroyView()
        myCartListIds.clear()
        cartItemModelList.clear()
        rewardModelList.clear()
        _binding = null
    }
}