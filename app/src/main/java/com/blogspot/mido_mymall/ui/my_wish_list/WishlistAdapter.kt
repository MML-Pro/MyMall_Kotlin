package com.blogspot.mido_mymall.ui.my_wish_list

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
import com.blogspot.mido_mymall.R
import com.blogspot.mido_mymall.databinding.WishlistItemLayoutBinding
import com.blogspot.mido_mymall.domain.models.WishListModel
import com.blogspot.mido_mymall.ui.home.HomeFragmentDirections
import com.blogspot.mido_mymall.ui.view_all.ViewAllFragmentDirections

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

//    fun deleteItem(position: Int) {
//        val newList = asyncListDiffer.currentList.toMutableList()
//        newList.removeAt(position)
//        asyncListDiffer.submitList(newList)
//
//    }

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
                    binding.freeCouponTV.text =
                        binding.root.resources.getString(R.string.free_coupon, freeCouponsNo)
                } else {
                    binding.freeCouponTV.text =
                        binding.root.resources.getString(R.string.free_coupons, freeCouponsNo)

                }
            } else {
                binding.freeCouponIcon.visibility = View.INVISIBLE
                binding.freeCouponTV.visibility = View.INVISIBLE
            }
            if (wishListModel.isInStock == true) {

//                binding.averageRatingMiniViewTV.setVisibility(View.VISIBLE);
                binding.linearLayout2.visibility = View.VISIBLE
                binding.totalRatingsTv.visibility = View.VISIBLE

//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    binding.productPrice.setTextColor(
//                        binding.root.resources.getColor(R.color.black, binding.root.context.theme)
//                    )
//                } else {
//                    @Suppress("DEPRECATION")
//                    binding.productPrice.setTextColor(
//                        binding.root.resources.getColor(R.color.black)
//                    )
//                }
                binding.cuttedPrice.visibility = View.VISIBLE
                binding.cashOnDeliveryTV.visibility = View.VISIBLE
                binding.divider7.visibility = View.VISIBLE
                binding.averageRatingMiniViewTV.text = wishListModel.averageRating
                val totalRatingsNo: Long = wishListModel.totalRatings
                binding.totalRatingsTv.text =
                    binding.root.resources.getString(R.string.number_ratings, totalRatingsNo)
                binding.productPrice.text =
                    binding.root.resources.getString(R.string.egp_price, wishListModel.productPrice)
                binding.cuttedPrice.text = wishListModel.cuttedPrice
                if (wishListModel.isCOD) {
                    binding.cashOnDeliveryTV.visibility = View.VISIBLE
                } else {
                    binding.cashOnDeliveryTV.visibility = View.GONE
                }
            } else {
                binding.linearLayout2.visibility = View.INVISIBLE
                binding.totalRatingsTv.visibility = View.INVISIBLE
                binding.productPrice.text = binding.root.resources.getString(R.string.out_of_stock)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    binding.productPrice.setTextColor(
                        binding.root.resources.getColor(R.color.colorPrimary,binding.root.context.theme)
                    )
                }else {
                    @Suppress("DEPRECATION")
                    binding.productPrice.setTextColor(
                        binding.root.resources.getColor(R.color.colorPrimary))
                }
                binding.cuttedPrice.visibility = View.INVISIBLE
                binding.cashOnDeliveryTV.visibility = View.INVISIBLE
                binding.divider7.visibility = View.INVISIBLE
            }
            if (IS_THIS_WISH_LIST) {
                binding.deleteButtonIv.visibility = View.VISIBLE
            } else {
                binding.deleteButtonIv.visibility = View.GONE
            }
            binding.deleteButtonIv.setOnClickListener {
//                FirebaseDbQueries.removeFromWishList(
//                    position,
//                    binding.getRoot().getContext()
//                )

                wishlistUtil?.deleteItem(position)

            }
            binding.root.setOnClickListener {

//                 String productID = wishListModel.getProductImage()

            }
        }
    }


}