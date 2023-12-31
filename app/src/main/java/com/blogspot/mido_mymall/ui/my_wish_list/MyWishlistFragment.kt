package com.blogspot.mido_mymall.ui.my_wish_list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.blogspot.mido_mymall.databinding.FragmentMyWishlistBinding
import com.blogspot.mido_mymall.domain.models.WishListModel
import com.blogspot.mido_mymall.ui.product_details.ProductDetailsFragment
import com.blogspot.mido_mymall.util.Resource
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

private const val TAG = "MyWishlistFragment"

@AndroidEntryPoint
class MyWishlistFragment : Fragment(), WishlistUtil {

    private var _binding: FragmentMyWishlistBinding? = null
    private val binding get() = _binding!!

    private val wishlistAdapter = WishlistAdapter(true, this)

    private val wishListIds = arrayListOf<String>()
    private val wishListModelList = arrayListOf<WishListModel>()

    private val myWishlistViewModel by viewModels<MyWishlistViewModel>()

    private var listSize: Long = 0

//    private var productId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyWishlistBinding.inflate(inflater)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myWishlistViewModel.getWishListIds()


        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                myWishlistViewModel.wishListIds.collect { response ->
                    when (response) {
                        is Resource.Success -> {
                            listSize = response.data?.get("list_size") as Long

                            for (i in 0 until listSize) {
                                wishListIds.add(response.data["product_id_$i"].toString())
                            }

                            val deferredResults = wishListIds.map {productId->

                                async(Dispatchers.IO) {
                                    myWishlistViewModel.loadWishList(productId)
                                }

                            }

                            val results = deferredResults.awaitAll()

                            for (result: Resource<DocumentSnapshot> in results) {

                                when (result) {
                                    is Resource.Success -> {
//                                        val documentSnapshot = result.data!!

                                        result.data?.let { documentSnapshot ->

                                            wishListModelList.add(
                                                WishListModel(
                                                    productID = documentSnapshot.id,
                                                    productImage = documentSnapshot["product_image_1"].toString(),
                                                    productName = documentSnapshot["product_name"].toString(),
                                                    freeCoupons = documentSnapshot["free_coupons"] as Long,
                                                    averageRating = documentSnapshot["average_rating"].toString(),
                                                    totalRatings = documentSnapshot["total_ratings"] as Long,
                                                    productPrice = documentSnapshot["product_price"].toString(),
                                                    cuttedPrice = documentSnapshot["cutted_price"].toString(),
                                                    isCOD = documentSnapshot["COD"] as Boolean,
                                                    isInStock = documentSnapshot["in_stock"] as Boolean
                                                )
                                            ).also {
                                                wishlistAdapter.asyncListDiffer.submitList(
                                                    wishListModelList
                                                )
                                            }
                                        }


                                    }

                                    is Resource.Error -> {
                                        // Handle error case
//                                        binding.progressBar.visibility = View.GONE

                                        Log.e(TAG, "onViewCreated: ${response.message.toString()}")
                                    }

                                    else -> {}
                                }
                            }


                            // Remove the initial wishlist items from the list
//                            if (wishlistSize > 0) {
//                                wishListModelList.removeAll(wishListModelList.subList(0, wishlistSize)
//                                    .toSet())
//                            }
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

//        lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                myWishlistViewModel.wishList.collect {
//                    if (it is Resource.Success) {
//
//                        it.data?.let { documentSnapshot ->
//
//                        }
//
//                    } else if (it is Resource.Error) {
//                        Log.e(TAG, "Resource.Error: ${it.message.toString()}")
//                    }
//                }
//            }
//        }

        binding.myWishListRV.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = wishlistAdapter
        }


        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                myWishlistViewModel.removeWishListState.collect {

                    if (it is Resource.Success) {
                        ProductDetailsFragment.ALREADY_ADDED_TO_WISH_LIST = false


                    } else if (it is Resource.Error) {
                        Log.e(TAG, "onViewCreated: ${it.message.toString()}")
                    }

                }
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
//        wishlistAdapter.asyncListDiffer.submitList(null)
        wishListIds.clear()
        wishListModelList.clear()
        _binding = null
    }

    override fun deleteItem(position: Int) {

        if (wishListIds.isNotEmpty() && wishListModelList.isNotEmpty()) {

            if (position != -1) {
                wishListModelList.removeAt(position)
            }
            myWishlistViewModel.removeFromWishList(wishListIds, position)
            wishlistAdapter.notifyDataSetChanged()
            Toast.makeText(requireContext(), "Item deleted from wishlist", Toast.LENGTH_SHORT)
                .show()
        }
    }
}