package com.blogspot.mido_mymall.ui.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.blogspot.mido_mymall.R
import com.blogspot.mido_mymall.databinding.NotificationItemBinding
import com.blogspot.mido_mymall.domain.models.NotificationModel

class NotificationAdapter : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<NotificationModel>() {
        override fun areItemsTheSame(
            oldItem: NotificationModel,
            newItem: NotificationModel
        ): Boolean {
            return oldItem.body == newItem.body
        }

        override fun areContentsTheSame(
            oldItem: NotificationModel,
            newItem: NotificationModel
        ): Boolean {
            return oldItem == newItem
        }


    }

    val asyncListDiffer = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(
            NotificationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }


    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(asyncListDiffer.currentList[position])
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }


    inner class NotificationViewHolder(private val binding: NotificationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(notificationModel: NotificationModel) {
            binding.apply {
                Glide.with(binding.root).load(notificationModel.image)
                    .placeholder(R.drawable.account)
                    .into(notificationIV)

                if(notificationModel.beenRead){
                    notificationTV.apply {
                        setTextColor(binding.root.resources.getColor(android.R.color.tertiary_text_light))
                        alpha = 0.5F
                    }

                }else {
                    notificationTV.alpha = 1F
                }

                notificationTV.text = notificationModel.body
            }
        }

    }
}