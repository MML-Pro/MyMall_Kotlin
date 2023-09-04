package com.blogspot.mido_mymall.ui.home

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.blogspot.mido_mymall.R
import com.blogspot.mido_mymall.databinding.SliderLayoutBinding
import com.blogspot.mido_mymall.domain.models.SliderModel

class SliderAdapter() : RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {


    private val differCallback = object : DiffUtil.ItemCallback<SliderModel>() {

        override fun areItemsTheSame(oldItem: SliderModel, newItem: SliderModel): Boolean {
            return oldItem.banner == newItem.banner
        }

        override fun areContentsTheSame(oldItem: SliderModel, newItem: SliderModel): Boolean {
            return oldItem == newItem
        }

    }

    val asyncListDiffer = AsyncListDiffer(this, differCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val sliderLayoutBinding: SliderLayoutBinding = SliderLayoutBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return SliderViewHolder(sliderLayoutBinding)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.bind(asyncListDiffer.currentList[position])
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    class SliderViewHolder(private val binding: SliderLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(sliderModel: SliderModel) {
            Glide.with(binding.root.context)
                .load(sliderModel.banner)
                .placeholder(R.drawable.placeholder_image)
                .into(binding.bannerIV)

            binding.bannerContainer.backgroundTintList = ColorStateList.valueOf(
                Color.parseColor(
                    sliderModel.backGroundColor
                )
            )
        }
    }
}