package com.blogspot.mido_mymall.ui.order_details

import android.content.res.ColorStateList
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.mido_mymall.R
import com.blogspot.mido_mymall.databinding.OrderDetailsItemLayoutBinding
import com.blogspot.mido_mymall.domain.models.CartItemModel
import com.bumptech.glide.Glide

private const val TAG = "OrderDetailsAdapter"

class OrderDetailsAdapter : RecyclerView.Adapter<OrderDetailsAdapter.OrderDetailsViewHolder>() {


    private val diffCallback = object : DiffUtil.ItemCallback<CartItemModel>() {
        override fun areItemsTheSame(
            oldItem: CartItemModel,
            newItem: CartItemModel
        ): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(
            oldItem: CartItemModel,
            newItem: CartItemModel
        ): Boolean {
            return oldItem == newItem
        }

    }

    val asyncListDiffer = AsyncListDiffer(this, diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailsViewHolder {


        return OrderDetailsViewHolder(
            OrderDetailsItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: OrderDetailsViewHolder, position: Int) {
        holder.bind(asyncListDiffer.currentList[position])
    }

    inner class OrderDetailsViewHolder(private val binding: OrderDetailsItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(productItemModel: CartItemModel) {
            binding.apply {

                Glide.with(binding.root)
                    .load(productItemModel.productImage)
                    .placeholder(R.drawable.placeholder_image)
                    .into(productImage)


                productName.text = productItemModel.productName
                productPrice.text = "EGP. ${productItemModel.productPrice} /-"
                productCuttedPrice.text = "EGP. ${productItemModel.cuttedPrice} /-"
                productQty.text = "Qty: ${productItemModel.productQuantity}"

                productItemModel.productRating?.toLong()?.let { setRating(it, true) }

            }
        }


        private fun setRating(starPosition: Long, isGetRating: Boolean) {
            for (i in 0 until binding.rateNowContainer.childCount) {
                val starBtn = binding.rateNowContainer.getChildAt(i) as ImageView
                starBtn.imageTintList =
                    ColorStateList.valueOf(
                        binding.root.resources.getColor(
                            R.color.mediumGray,
                            binding.root.context.theme
                        )
                    )

                if (isGetRating) {
                    if (i < starPosition) {
                        starBtn.imageTintList =
                            ColorStateList.valueOf(
                                binding.root.resources.getColor(
                                    R.color.ratingColor,
                                    binding.root.context.theme
                                )
                            )
                    }
                } else {
                    if (i <= starPosition) {
                        starBtn.imageTintList =
                            ColorStateList.valueOf(
                                binding.root.resources.getColor(
                                    R.color.ratingColor,
                                    binding.root.context.theme
                                )
                            )
                    }
                }
            }
        }


    }
}