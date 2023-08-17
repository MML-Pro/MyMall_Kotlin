package com.example.mymall_kotlin.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mymall_kotlin.R
import com.example.mymall_kotlin.databinding.CategoryItemBinding
import com.example.mymall_kotlin.domain.models.CategoryModel

class CategoryAdapter : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<CategoryModel>() {

        override fun areItemsTheSame(oldItem: CategoryModel, newItem: CategoryModel): Boolean {
            return oldItem.categoryIconLink == newItem.categoryIconLink
        }

        override fun areContentsTheSame(oldItem: CategoryModel, newItem: CategoryModel): Boolean {
            return oldItem == newItem
        }

    }

    val asyncListDiffer = AsyncListDiffer(this, differCallback)

    private var lastPosition = -1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val categoryItemBinding: CategoryItemBinding = CategoryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return CategoryViewHolder(categoryItemBinding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.bind(asyncListDiffer.currentList[position])

        if(lastPosition < position) {
            val animation = AnimationUtils.loadAnimation(
                holder.itemView.context,
                R.anim.fade_in
            )
            holder.itemView.animation = animation

            lastPosition = position
        }
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    inner class CategoryViewHolder(private val binding: CategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(categoryModel: CategoryModel) {
            Glide.with(binding.root)
                .load(categoryModel.categoryIconLink)
                .placeholder(R.drawable.placeholder_image)
                .into(binding.categoryIcon)
            binding.categoryName.text = categoryModel.categoryName

            binding.root.setOnClickListener { view ->
                if (layoutPosition == 0) {
                    Toast.makeText(
                        binding.root.context,
                        "you at home",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    findNavController(binding.root).navigate(
                        HomeFragmentDirections
                            .actionHomeFragmentToCategoryFragment(categoryModel.categoryName)
                    )
                }
            }
        }
    }
}