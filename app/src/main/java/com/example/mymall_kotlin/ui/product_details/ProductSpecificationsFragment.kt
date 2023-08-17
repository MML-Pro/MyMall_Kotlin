package com.example.mymall_kotlin.ui.product_details

import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymall_kotlin.databinding.FragmentProductSpecificationsBinding
import com.example.mymall_kotlin.domain.models.ProductSpecsModel

class ProductSpecificationsFragment : Fragment() {

    private var _binding: FragmentProductSpecificationsBinding? = null
    private val binding get() = _binding!!

    private lateinit var productSpecsModelList: ArrayList<ProductSpecsModel>
    private val productSpecsAdapter = ProductSpecsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductSpecificationsBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            productSpecsModelList = it.parcelableArrayList("productSpecsModelList")!!
        }


        productSpecsAdapter.asyncListDiffer.submitList(productSpecsModelList)

        binding.productSpecsRV.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = productSpecsAdapter
        }

    }

    private inline fun <reified T : Parcelable> Bundle.parcelableArrayList(key: String): ArrayList<T>? =
        when {
            SDK_INT >= 33 -> getParcelableArrayList(key, T::class.java)
            else -> @Suppress("DEPRECATION") getParcelableArrayList(key)
        }
}