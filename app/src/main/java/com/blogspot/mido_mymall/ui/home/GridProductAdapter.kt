package com.blogspot.mido_mymall.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.blogspot.mido_mymall.R
import com.blogspot.mido_mymall.databinding.HorizontalScrollItemLayoutBinding
import com.blogspot.mido_mymall.domain.models.HorizontalProductScrollModel

private const val TAG = "GridProductAdapter"

class GridProductAdapter( val productScrollModelList: ArrayList<HorizontalProductScrollModel> = arrayListOf<HorizontalProductScrollModel>()) :
    BaseAdapter() {


//    init {
//
//    }

    fun submitList(productScrollModelList: List<HorizontalProductScrollModel>) {

        if (productScrollModelList.isNotEmpty()) {
            this.productScrollModelList.clear()
            this.productScrollModelList.addAll(productScrollModelList)
        }
        notifyDataSetChanged()

        Log.d(TAG, "submitList: size ${productScrollModelList.size}")
    }
//
//    fun clearList() {
//        this.productScrollModelList.clear()
//        notifyDataSetChanged()
//    }


    override fun getCount(): Int {
        return productScrollModelList.size
    }

    override fun getItem(i: Int): Any? {
        return null
    }


    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {


        var localConvertView: View? = convertView

        val binding: HorizontalScrollItemLayoutBinding

        if (localConvertView == null) {
            binding = HorizontalScrollItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context)
            )
            localConvertView = binding.root
            localConvertView.setTag(R.id.viewBinding, binding)
        } else {
            binding = localConvertView.getTag(R.id.viewBinding) as HorizontalScrollItemLayoutBinding
        }

        val size = convertView?.resources?.getDimension(R.dimen.test_size)

        val testSize = size ?: 150

//        binding.hsProductImage.setImageResource(productScrollModelList.get(position).getProductImage());
        Glide.with(binding.root.context)
            .load(productScrollModelList[position].productImage)
            .fitCenter()
            .placeholder(R.drawable.placeholder_image)
            .into(binding.hsProductImage)
        binding.hsProductName.text = productScrollModelList[position].productName
        binding.hsProductDescription.text = productScrollModelList[position].productSubtitle
        binding.hsProductPrice.text = binding.root.resources.getString(
            R.string.egp_price,
            productScrollModelList[position].productPrice
        )

        return localConvertView
    }

}


//class GridProductAdapter() :
//    ListAdapter<HorizontalProductScrollModel, GridProductAdapter.ProductViewHolder>(
//        ProductDiffCallback()
//    ) {
//
//    // DiffUtil لمقارنة العناصر
//    class ProductDiffCallback : DiffUtil.ItemCallback<HorizontalProductScrollModel>() {
//        override fun areItemsTheSame(
//            oldItem: HorizontalProductScrollModel,
//            newItem: HorizontalProductScrollModel
//        ): Boolean {
//            // مقارنة العناصر بناءً على productID
//            return oldItem.productID == newItem.productID
//        }
//
//        override fun areContentsTheSame(
//            oldItem: HorizontalProductScrollModel,
//            newItem: HorizontalProductScrollModel
//        ): Boolean {
//            // مقارنة محتويات العنصر (جميع الحقول)
//            return oldItem.productImage == newItem.productImage &&
//                    oldItem.productName == newItem.productName &&
//                    oldItem.productSubtitle == newItem.productSubtitle &&
//                    oldItem.productPrice == newItem.productPrice
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
//        val binding = HorizontalScrollItemLayoutBinding.inflate(
//            LayoutInflater.from(parent.context), parent, false
//        )
//        return ProductViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
//        val product = getItem(position)
//        holder.bind(product)
//    }
//
////    fun clearList() {
////        submitList(emptyList()) // استبدال clearList باستخدام submitList مع قائمة فارغة
////    }
//
//    inner class ProductViewHolder(
//        private val binding: HorizontalScrollItemLayoutBinding
//    ) : RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(product: HorizontalProductScrollModel) {
//            // تحميل الصورة باستخدام Glide
//            Glide.with(binding.root.context)
//                .load(product.productImage)
//                .placeholder(R.drawable.placeholder_image)
//                .into(binding.hsProductImage)
//
//            // تعيين النصوص
//            binding.hsProductName.text = product.productName
//            binding.hsProductDescription.text = product.productSubtitle
//            binding.hsProductPrice.text = binding.root.resources.getString(
//                R.string.egp_price,
//                product.productPrice
//            )
//
//
//        }
//    }
//}
