package com.blogspot.mido_mymall.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import com.blogspot.mido_mymall.R
import com.blogspot.mido_mymall.databinding.HorizontalScrollItemLayoutBinding
import com.blogspot.mido_mymall.domain.models.HorizontalProductScrollModel

private const val TAG = "GridProductAdapter"

class GridProductAdapter : BaseAdapter() {

    private var productScrollModelList = arrayListOf<HorizontalProductScrollModel>()

//    init {
//
//    }

    fun submitList(productScrollModelList: List<HorizontalProductScrollModel>){
        this.productScrollModelList.clear()
        this.productScrollModelList.addAll(productScrollModelList)
        notifyDataSetChanged()

        Log.d(TAG, "submitList: size ${productScrollModelList.size}")
    }

    fun clearList() {
        this.productScrollModelList.clear()
        notifyDataSetChanged()
    }


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

//        binding.hsProductImage.setImageResource(productScrollModelList.get(position).getProductImage());
        Glide.with(binding.root.context)
            .load(productScrollModelList[position].productImage)
            .placeholder(R.drawable.placeholder_image)
            .into(binding.hsProductImage)
        binding.hsProductName.text = productScrollModelList[position].productName
        binding.hsProductDescription.text = productScrollModelList[position].productSubtitle
        binding.hsProductPrice.text = "EGP.${productScrollModelList[position].productPrice}/-"

//        binding.getRoot().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
        return localConvertView
    }

}
