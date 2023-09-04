package com.blogspot.mido_mymall.ui.product_details

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.blogspot.mido_mymall.domain.models.ProductSpecsModel

class ProductDetailsAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val productDescBody: String,
    private val productOtherDetails: String,
    productSpecsModelList: ArrayList<ProductSpecsModel>
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private val productSpecsModelList: ArrayList<ProductSpecsModel>

    init {
        this.productSpecsModelList = productSpecsModelList
    }


    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                val productDescriptionFragment1 = ProductDescriptionFragment()
                productDescriptionFragment1.arguments = Bundle().apply {
                    putString("productDescBody", productDescBody)
                }
                productDescriptionFragment1
            }

            1 -> {
                val productSpecificationsFragment = ProductSpecificationsFragment()
                productSpecificationsFragment.arguments = Bundle().apply {
                    putParcelableArrayList("productSpecsModelList", productSpecsModelList)
                }
                productSpecificationsFragment
            }

            2 -> {
                val productDescriptionFragment2 = ProductDescriptionFragment()

                productDescriptionFragment2.arguments = Bundle().apply {
                    putString("productOtherDetails", productOtherDetails)
                }
                productDescriptionFragment2
            }

            else -> Fragment()
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}