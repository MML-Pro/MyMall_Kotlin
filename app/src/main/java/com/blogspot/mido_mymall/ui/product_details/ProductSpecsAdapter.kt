package com.blogspot.mido_mymall.ui.product_details

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.mido_mymall.R
import com.blogspot.mido_mymall.databinding.ProductSpecsItemLayoutBinding
import com.blogspot.mido_mymall.domain.models.ProductSpecsModel

class ProductSpecsAdapter() : RecyclerView.Adapter<ProductSpecsAdapter.ViewHolder>() {

    private var diffCallback = object : DiffUtil.ItemCallback<ProductSpecsModel>() {

        override fun areItemsTheSame(
            oldItem: ProductSpecsModel,
            newItem: ProductSpecsModel
        ): Boolean {
            return oldItem.featureValue == newItem.featureValue
        }

        override fun areContentsTheSame(
            oldItem: ProductSpecsModel,
            newItem: ProductSpecsModel
        ): Boolean {
            return oldItem == newItem
        }

    }

    val asyncListDiffer = AsyncListDiffer(this, diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            ProductSpecsModel.SPECS_TITLE -> {
                val titleTv = TextView(parent.context)
                titleTv.setTypeface(null, Typeface.BOLD)
                titleTv.setTextColor(parent.resources.getColor(R.color.spacesTitleTextColor))
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.setMargins(
                    setDP(16, parent.context), setDP(16, parent.context),
                    setDP(16, parent.context), setDP(8, parent.context)
                )
                titleTv.layoutParams = layoutParams
                ViewHolder(titleTv)
            }

            ProductSpecsModel.SPECS_BODY -> {
                val productSpecsItemLayoutBinding: ProductSpecsItemLayoutBinding =
                    ProductSpecsItemLayoutBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false)
                ViewHolder(productSpecsItemLayoutBinding)
            }

            else -> {
                val productSpecsItemLayoutBinding: ProductSpecsItemLayoutBinding =
                    ProductSpecsItemLayoutBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false)
                ViewHolder(productSpecsItemLayoutBinding)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        when (asyncListDiffer.currentList[position].type) {

            ProductSpecsModel.SPECS_TITLE -> asyncListDiffer.currentList[position].title?.let {
                holder.setTitle(
                    it
                )
            }

            ProductSpecsModel.SPECS_BODY -> {
                val productSpecsModel: ProductSpecsModel = asyncListDiffer.currentList[position]
                holder.bind(productSpecsModel)
            }

            else -> return
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (asyncListDiffer.currentList[position].type) {
            0 -> ProductSpecsModel.SPECS_TITLE
            1 -> ProductSpecsModel.SPECS_BODY
            else -> -1
        }
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    class ViewHolder : RecyclerView.ViewHolder {
        private var binding: ProductSpecsItemLayoutBinding? = null

        constructor(binding: ProductSpecsItemLayoutBinding) : super(binding.root) {
            this.binding = binding
        }

        constructor(titleTv: TextView) : super(titleTv)

        fun setTitle(title: String) {
            val titleTv = itemView as TextView
            titleTv.text = title
        }

        fun bind(productSpecsModel: ProductSpecsModel) {
            binding?.featureName?.text = productSpecsModel.featureName
            binding?.featureValue?.text = productSpecsModel.featureValue
        }
    }

    private fun setDP(dp: Int, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }
}