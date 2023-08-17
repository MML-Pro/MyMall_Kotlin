package com.example.mymall_kotlin.ui.my_wish_list

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mymall_kotlin.R
import com.example.mymall_kotlin.databinding.WishlistItemLayoutBinding
import com.example.mymall_kotlin.domain.models.WishListModel
import com.example.mymall_kotlin.ui.home.HomeFragmentDirections
import com.example.mymall_kotlin.ui.view_all.ViewAllFragmentDirections

private const val TAG = "WishlistAdapter"

class WishlistAdapter(IS_THIS_WISH_LIST: Boolean, private val wishlistUtil: WishlistUtil? = null) :
    RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<WishListModel>() {
        override fun areItemsTheSame(oldItem: WishListModel, newItem: WishListModel): Boolean {
            return oldItem.productID == newItem.productID
        }

        override fun areContentsTheSame(oldItem: WishListModel, newItem: WishListModel): Boolean {

            Log.d(TAG, "areContentsTheSame: ${oldItem == newItem}")
            return oldItem == newItem
        }

    }

    val asyncListDiffer = AsyncListDiffer(this, diffCallback)

    companion object {
        private var IS_THIS_WISH_LIST = false
    }

    private var lastPosition = -1

    init {
        Companion.IS_THIS_WISH_LIST = IS_THIS_WISH_LIST
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishlistViewHolder {
        // Inflate the layout for a single wishlist item and return a new viewholder
        val binding: WishlistItemLayoutBinding = WishlistItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return WishlistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WishlistViewHolder, position: Int) {
        // Bind the data for the wishlist item at the given position
        val wishListModel: WishListModel = asyncListDiffer.currentList[position]
        holder.bind(wishListModel, position)
    }

    override fun getItemCount(): Int {
        // Return the number of items in the wishlist
        return asyncListDiffer.currentList.size
    }

    inner class WishlistViewHolder(private val binding: WishlistItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(wishListModel: WishListModel, position: Int) {
            binding.productName.text = wishListModel.productName

            Glide.with(binding.root)
                .load(wishListModel.productImage)
                .placeholder(R.drawable.placeholder_image)
                .into(binding.productImage)

            val freeCouponsNo: Long = wishListModel.freeCoupons
            if (freeCouponsNo != 0L && wishListModel.isInStock == true) {

                binding.freeCouponIcon.visibility = View.VISIBLE
                if (freeCouponsNo == 1L) {
                    binding.freeCouponTV.text = "free $freeCouponsNo coupon"
                } else {
                    binding.freeCouponTV.text = "free $freeCouponsNo coupons"
                }
            } else {
                binding.freeCouponIcon.visibility = View.INVISIBLE
                binding.freeCouponTV.visibility = View.INVISIBLE
            }
            if (wishListModel.isInStock == true) {

//                binding.averageRatingMiniViewTV.setVisibility(View.VISIBLE);
                binding.linearLayout2.visibility = View.VISIBLE
                binding.totalRatingsTv.visibility = View.VISIBLE
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    binding.productPrice.setTextColor(
                        binding.root.resources.getColor(R.color.black, binding.root.context.theme)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    binding.productPrice.setTextColor(
                        binding.root.resources.getColor(R.color.black)
                    )
                }
                binding.cuttedPrice.visibility = View.VISIBLE
                binding.cashOnDeliveryTV.visibility = View.VISIBLE
                binding.divider7.visibility = View.VISIBLE
                binding.averageRatingMiniViewTV.text = wishListModel.averageRating
                val totalRatingsNo: Long = wishListModel.totalRatings
                binding.totalRatingsTv.text = "($totalRatingsNo) Ratings"
                binding.productPrice.text = "RS.${wishListModel.productPrice}/-"
                binding.cuttedPrice.text = wishListModel.cuttedPrice
                if (wishListModel.isCOD) {
                    binding.cashOnDeliveryTV.visibility = View.VISIBLE
                } else {
                    binding.cashOnDeliveryTV.visibility = View.GONE
                }
            } else {
                binding.linearLayout2.visibility = View.INVISIBLE
                binding.totalRatingsTv.visibility = View.INVISIBLE
                binding.productPrice.text = "Out of Stock"
                binding.productPrice.setTextColor(
                    binding.root.resources.getColor(R.color.colorPrimary)
                )
                binding.cuttedPrice.visibility = View.INVISIBLE
                binding.cashOnDeliveryTV.visibility = View.INVISIBLE
                binding.divider7.visibility = View.INVISIBLE
            }
            if (IS_THIS_WISH_LIST) {
                binding.deleteButtonIv.visibility = View.VISIBLE
            } else {
                binding.deleteButtonIv.visibility = View.GONE
            }
            binding.deleteButtonIv.setOnClickListener { view ->
//                FirebaseDbQueries.removeFromWishList(
//                    position,
//                    binding.getRoot().getContext()
//                )

                wishlistUtil?.deleteItem(position)

            }
            binding.root.setOnClickListener { view ->

//                 String productID = wishListModel.getProductImage()
                when (findNavController(binding.root).currentDestination?.id) {
                    R.id.nav_my_wishlist -> {
                        findNavController(binding.root)
                            .navigate(
                                MyWishlistFragmentDirections
                                    .actionNavMyWishlistToProductDetailsFragment(wishListModel.productID!!)
                            )
                    }
                    R.id.homeFragment -> {

                        val navOption = NavOptions.Builder().setPopUpTo(R.id.homeFragment, true).build()


                        findNavController(binding.root).navigate(HomeFragmentDirections
                            .actionHomeFragmentToProductDetailsFragment(wishListModel.productID!!),navOption)
                    }
                    else -> {
                        findNavController(binding.root)
                            .navigate(
                                ViewAllFragmentDirections
                                    .actionViewAllFragmentToProductDetailsFragment(wishListModel.productID!!)
                            )

                    }
                }
            }
        }
    }


}