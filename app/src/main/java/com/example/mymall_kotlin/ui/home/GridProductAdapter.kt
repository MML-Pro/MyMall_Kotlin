package com.example.mymall_kotlin.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import com.example.mymall_kotlin.R
import com.example.mymall_kotlin.databinding.HorizontalScrollItemLayoutBinding
import com.example.mymall_kotlin.domain.models.HorizontalProductScrollModel

class GridProductAdapter(productScrollModelList: List<HorizontalProductScrollModel>) : BaseAdapter() {

    var productScrollModelList: List<HorizontalProductScrollModel>

    init {
        this.productScrollModelList = productScrollModelList
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
        var convertView: View? = convertView

        val binding: HorizontalScrollItemLayoutBinding

        if (convertView == null) {
            binding = HorizontalScrollItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context)
            )
            convertView = binding.root
            convertView.setTag(R.id.viewBinding, binding)
        } else {
            binding = convertView.getTag(R.id.viewBinding) as HorizontalScrollItemLayoutBinding
        }

//        binding.hsProductImage.setImageResource(productScrollModelList.get(position).getProductImage());
        Glide.with(binding.root.context)
            .load(productScrollModelList[position].productImage)
            .placeholder(R.drawable.placeholder_image)
            .into(binding.hsProductImage)
        binding.hsProductName.text = productScrollModelList[position].productName
        binding.hsProductDescription.text = productScrollModelList[position].productDescription
        binding.hsProductPrice.text = "Rs.${productScrollModelList[position].productPrice}/-"

//        binding.getRoot().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
        return convertView
    }
}
