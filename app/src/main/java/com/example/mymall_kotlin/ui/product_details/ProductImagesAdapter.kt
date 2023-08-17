package com.example.mymall_kotlin.ui.product_details

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.example.mymall_kotlin.R
import com.example.mymall_kotlin.databinding.ProductImageViewBinding

class ProductImagesAdapter(private var productImages: ArrayList<String>) : RecyclerView.Adapter<ProductImagesAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ProductImageViewBinding = ProductImageViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(binding)
    }

    //    @Override
    //    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
    //        super.onViewDetachedFromWindow(holder);
    //        holder.binding.getRoot().removeAllViews();
    //    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        holder.bind(productImages[position])

//        holder.binding.productImageView.setImageResource(productImages.get(position));

        //        productImage
    }

    override fun getItemCount(): Int {
        return productImages.size
    }

    class ViewHolder(private val binding: ProductImageViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(image:String) {
            Glide.with(binding.root.context)
                .load(image)
                .placeholder(R.drawable.placeholder_image)
                .into(binding.productImageView)
        }
    }
}