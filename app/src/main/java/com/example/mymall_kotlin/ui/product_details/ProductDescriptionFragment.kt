package com.example.mymall_kotlin.ui.product_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mymall_kotlin.databinding.FragmentProductDescriptionBinding

class ProductDescriptionFragment : Fragment() {

    companion object {
        private const val TAG = "ProductDescriptionFragment"
    }

    private var _binding:FragmentProductDescriptionBinding?=null
    private val binding get() = _binding!!

    private var productDescBody: String? = null
    private var productOtherDetails:String?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDescriptionBinding.inflate(inflater,container,false)

        arguments?.let {
            productDescBody = it.getString("productDescBody")
            productOtherDetails = it.getString("productOtherDetails")
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(productOtherDetails.isNullOrEmpty()){
            binding.productDescriptionTV.text = productDescBody
        }else if(productDescBody.isNullOrEmpty()){
            binding.productDescriptionTV.text = productOtherDetails
        }


    }
}