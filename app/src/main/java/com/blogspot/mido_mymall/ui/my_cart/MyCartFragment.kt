package com.blogspot.mido_mymall.ui.my_cart

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
import com.blogspot.mido_mymall.databinding.FragmentMyCartBinding
import com.blogspot.mido_mymall.domain.models.CartItemModel
import com.blogspot.mido_mymall.domain.models.RewardModel
import com.blogspot.mido_mymall.ui.my_rewards.MyRewardsViewModel
import com.blogspot.mido_mymall.ui.product_details.ProductDetailsFragment
import com.blogspot.mido_mymall.util.Constants
import com.blogspot.mido_mymall.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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

                            if (listSize == 0L) {

                                binding.apply {
                                    cartItemsRecyclerView.visibility = View.GONE
                                    emptyCartIV.visibility = View.VISIBLE

                                }
                            } else {
                                binding.apply {
                                    cartItemsRecyclerView.visibility = View.VISIBLE
                                    emptyCartIV.visibility = View.GONE
                                }
                            }

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

                        if (it.data == null || it.data.data.isNullOrEmpty()) {
                            binding.apply {
                                cartItemsRecyclerView.visibility = View.GONE
                                emptyCartIV.visibility = View.VISIBLE
                            }
                        }

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

                                val totalAmountCartItem = CartItemModel(CartItemModel.TOTAL_AMOUNT)


                                if (myCartListIds.size > 0 && !cartItemModelList.contains(
                                        totalAmountCartItem
                                    )
                                ) {
                                    cartItemModelList.add(
                                        totalAmountCartItem
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

            Toast.makeText(requireContext(), "Item deleted from cart list", Toast.LENGTH_SHORT)
                .show()


//            binding.totalAmountAndContinue.visibility = View.GONE

//            cartAdapter.notifyDataSetChanged()


        }
    }

//    private fun recalculateTotalAmount() {
//
//        var totalItems = 0
//        var totalItemsPrice = 0
//        val deliveryPrice: String
//        val totalAmount: Int
//        var savedAmount = 0
//
//        cartItemModelList.forEach { cartItem ->
//
//            if (cartItem.type == CartItemModel.TOTAL_AMOUNT) {
//                cartItemModelList.remove(cartItem)
//            }
//
//            if (cartItem.type == CartItemModel.CART_ITEM) {
//
//                val quantity = cartItem.productQuantity
//
//                totalItems = (totalItemsPrice + quantity!!).toInt()
//
//                totalItemsPrice += if (cartItem.selectedCouponId.isNullOrEmpty()) {
//                    cartItem.productPrice?.toInt()!! * quantity.toInt()
//                } else {
//                    cartItem.discountedPrice?.toInt()!! * quantity.toInt()
//                }
//
//                if (cartItem.cuttedPrice?.isNotEmpty()!!) {
//                    savedAmount += (cartItem.cuttedPrice?.toInt()!! - cartItem.productPrice?.toInt()!!) * quantity.toInt()
//
//                    if (!cartItem.selectedCouponId.isNullOrEmpty()) {
//                        savedAmount += (cartItem.productPrice?.toInt()!! - cartItem.discountedPrice?.toInt()!!) * quantity.toInt()
//                    }
//
//                } else {
//                    if (cartItem.selectedCouponId?.isNotEmpty()!!) {
//                        savedAmount += (cartItem.productPrice?.toInt()!! - cartItem.discountedPrice?.toInt()!!) * quantity.toInt()
//                    }
//                }
//
//            }
//        }
//        if (totalItemsPrice > 500) {
//            deliveryPrice = "Free"
//            totalAmount = totalItemsPrice
//        } else {
//            deliveryPrice = "60"
//            totalAmount = totalItemsPrice + 60
//        }
//
//        cartItemModelList.add(
//            CartItemModel(
//                CartItemModel.TOTAL_AMOUNT,
//                totalItems,
//                totalItemsPrice,
//                deliveryPrice,
//                totalAmount,
//                savedAmount
//            )
//        )
//
//        cartAdapter.asyncListDiffer.submitList(cartItemModelList)
//    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: cart size ${cartItemModelList.size}")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: cart size ${cartItemModelList.size}")
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