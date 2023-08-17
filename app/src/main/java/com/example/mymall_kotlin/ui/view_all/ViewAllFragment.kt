package com.example.mymall_kotlin.ui.view_all

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymall_kotlin.R
import com.example.mymall_kotlin.databinding.FragmentViewAllBinding
import com.example.mymall_kotlin.domain.models.HorizontalProductScrollModel
import com.example.mymall_kotlin.domain.models.WishListModel
import com.example.mymall_kotlin.ui.MainActivity
import com.example.mymall_kotlin.ui.home.GridProductAdapter
import com.example.mymall_kotlin.ui.my_wish_list.WishlistAdapter

class ViewAllFragment : Fragment() {

    private val TAG = "ViewAllFragment"

    private var _binding: FragmentViewAllBinding? = null
    private val binding get() = _binding!!

    private val LAYOUT_CODE = 0
    private val horizontalProductScrollList = arrayListOf<HorizontalProductScrollModel>()
    private val wishList= arrayListOf<WishListModel>()

    private val wishlistAdapter = WishlistAdapter(false)

    private val args by navArgs<ViewAllFragmentArgs>()

    private var layoutCode:Int =0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewAllBinding.inflate(inflater)

        layoutCode = args.layoutCode
        (requireActivity() as MainActivity).fragmentTitleAndActionBar(args.title)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        if(layoutCode == 0) {

            wishList.addAll(args.viewAllProductList!!)

            wishlistAdapter.asyncListDiffer.submitList(wishList)
//            wishlistAdapter.submitList(wishList)

            binding.recyclerView.apply {
                visibility = View.VISIBLE
                layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
                adapter = wishlistAdapter
            }

        }else if(layoutCode == 1) {

            //================ Horizontal list ======================//

            binding.gridView.visibility = View.VISIBLE
            

            val gridProductAdapter = GridProductAdapter(args.horizontalProductScrollModelList.toList())

            binding.gridView.adapter = gridProductAdapter
            gridProductAdapter.notifyDataSetChanged()
        }

    }
}