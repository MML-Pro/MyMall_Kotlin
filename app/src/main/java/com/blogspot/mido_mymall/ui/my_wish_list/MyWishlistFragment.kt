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
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.blogspot.mido_mymall.R
import com.blogspot.mido_mymall.databinding.FragmentMyWishlistBinding
import com.blogspot.mido_mymall.domain.models.WishListModel
import com.blogspot.mido_mymall.ui.MainActivity
import com.blogspot.mido_mymall.ui.home.HomeFragmentDirections
import com.blogspot.mido_mymall.ui.product_details.ProductDetailsFragment
import com.blogspot.mido_mymall.ui.view_all.ViewAllFragmentDirections
import com.blogspot.mido_mymall.util.Resource
import com.blogspot.mido_mymall.util.addItemClickSupport
import com.blogspot.mido_mymall.util.onItemClick
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.collectLatest
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate: called")

    }
//
//
//    fun observWishlist(){
//
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (FirebaseAuth.getInstance().currentUser != null) {
            myWishlistViewModel.getWishListIds()
            binding.myWishListRV.visibility = View.VISIBLE
        } else {
            binding.apply {
                emptyWishlistIV.visibility = View.VISIBLE
                emptyWishlistTV.visibility = View.VISIBLE
                myWishListRV.visibility = View.GONE
            }
        }


        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                myWishlistViewModel.wishListIds.collect { response ->


                    when (response) {

                        is Resource.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            Log.d(TAG, "loading called")
                        }


                        is Resource.Success -> {
                            listSize = response.data?.get("list_size") as Long

                            if (listSize == 0L) {
                                binding.apply {
                                    emptyWishlistIV.visibility = View.VISIBLE
                                    emptyWishlistTV.visibility = View.VISIBLE
                                    myWishListRV.visibility = View.GONE
                                }
                            } else {
                                binding.apply {
                                    emptyWishlistIV.visibility = View.GONE
                                    emptyWishlistTV.visibility = View.GONE
                                    myWishListRV.visibility = View.VISIBLE
                                }
                            }

                            for (i in 0 until listSize) {
                                wishListIds.add(response.data["product_id_$i"].toString())
                            }

                            val deferredResults = wishListIds.map { productId ->

                                async(Dispatchers.IO) {
                                    myWishlistViewModel.loadWishList(productId)
                                }

                            }

                            val results = deferredResults.awaitAll()

                            val newWishListItems =
                                mutableListOf<WishListModel>() // Create a separate list for new items

                            for (result: Resource<DocumentSnapshot> in results) {
                                when (result) {
                                    is Resource.Success -> {
                                        result.data?.let { documentSnapshot ->
                                            newWishListItems.add(
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
                                            )
                                        }
                                    }

                                    is Resource.Error -> {
                                        // Handle error case
                                        Log.e(TAG, "onViewCreated: ${response.message.toString()}")
                                    }

                                    else -> {}
                                }
                            }


                            wishListModelList.addAll(newWishListItems) // Add all new items to the wishListModelList
                            wishlistAdapter.asyncListDiffer.submitList(wishListModelList).also {
                                Log.d(TAG, "wishListModelList size: ${wishListModelList.size}")
                                Log.d(
                                    TAG,
                                    "asyncListDiffer size: ${wishlistAdapter.asyncListDiffer.currentList.size}"
                                )

                                binding.progressBar.visibility = View.GONE

                                wishlistAdapter.notifyDataSetChanged()

                            }
                        }

                        is Resource.Error -> {
                            binding.progressBar.visibility = View.GONE
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
                myWishlistViewModel.removeWishListState.collect { response ->

                    if (response is Resource.Success) {


                        Toast.makeText(
                            requireContext(),
                            getString(R.string.product_removed_from_wishlist),
                            Toast.LENGTH_SHORT
                        )
                            .show()

                    } else if (response is Resource.Error) {
                        Log.e(TAG, "onViewCreated: ${response.message.toString()}")
                    }

                }
            }
        }


//        observWishlist()


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

            onItemClick { _, position, _ ->

                when (findNavController().currentDestination?.id) {


                    R.id.nav_my_wishlist -> {


                        Navigation.findNavController(binding.root)
                            .navigate(
                                MyWishlistFragmentDirections
                                    .actionNavMyWishlistToProductDetailsFragment(wishListModelList[position].productID!!)
                            )
                    }


//                    R.id.homeFragment -> {
//
//                        val navOption =
//                            NavOptions.Builder().setPopUpTo(R.id.homeFragment, true).build()
//
//
//                        Navigation.findNavController(binding.root).navigate(
//                            HomeFragmentDirections
//                                .actionHomeFragmentToProductDetailsFragment(wishListModel.productID!!),
//                            navOption
//                        )
//                    }
//
//                    else -> {
//                        Navigation.findNavController(binding.root)
//                            .navigate(
//                                ViewAllFragmentDirections
//                                    .actionViewAllFragmentToProductDetailsFragment(wishListModel.productID!!)
//                            )
//
//                    }
                }

            }
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

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).apply {
            supportActionBar?.title = getString(R.string.my_wishlist)
            title = getString(R.string.my_wishlist)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
//        wishlistAdapter.asyncListDiffer.submitList(null)

        Log.d(TAG, "onDestroyView: called")

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
//            Toast.makeText(requireContext(), "Item deleted from wishlist", Toast.LENGTH_SHORT)
//                .show()
        }
    }
}