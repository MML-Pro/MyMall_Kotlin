package com.example.mymall_kotlin.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mymall_kotlin.R
import com.example.mymall_kotlin.databinding.HorizontalScrollItemLayoutBinding
import com.example.mymall_kotlin.domain.models.HorizontalProductScrollModel
import com.example.mymall_kotlin.ui.category.CategoryFragmentDirections

class HorizontalProductScrollAdapter :
    RecyclerView.Adapter<HorizontalProductScrollAdapter.ViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<HorizontalProductScrollModel>() {
        override fun areItemsTheSame(
            oldItem: HorizontalProductScrollModel,
            newItem: HorizontalProductScrollModel
        ): Boolean {
            return oldItem.productID == newItem.productID
        }

        override fun areContentsTheSame(
            oldItem: HorizontalProductScrollModel,
            newItem: HorizontalProductScrollModel
        ): Boolean {
            return oldItem == newItem
        }

    }
    val asyncListDiffer = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: HorizontalScrollItemLayoutBinding = HorizontalScrollItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val productScrollModel: HorizontalProductScrollModel =
            asyncListDiffer.currentList[position]
        holder.bind(productScrollModel)
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

     inner class ViewHolder(private val binding: HorizontalScrollItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(productScrollModel: HorizontalProductScrollModel) {
            Glide.with(binding.root.context)
                .load(productScrollModel.productImage)
                .placeholder(R.drawable.placeholder_image)
                .into(binding.hsProductImage)

            binding.hsProductName.text = productScrollModel.productName
            binding.hsProductDescription.text = productScrollModel.productDescription
            binding.hsProductPrice.text = "Rs.${productScrollModel.productPrice}/-"
            val productID: String = productScrollModel.productID.toString()

            binding.root.setOnClickListener { view: View ->
                if (findNavController(view).currentDestination?.id
                    == R.id.homeFragment
                ) {
                    findNavController(view).navigate(
                        HomeFragmentDirections.actionHomeFragmentToProductDetailsFragment(productID)
                    )
                } else if (findNavController(view).currentDestination?.id
                    == R.id.categoryFragment) {
                    findNavController(view).navigate(
                        CategoryFragmentDirections.actionCategoryFragmentToProductDetailsFragment(
                            productID
                        )
                    )
                }
            }
        }
    }
}
