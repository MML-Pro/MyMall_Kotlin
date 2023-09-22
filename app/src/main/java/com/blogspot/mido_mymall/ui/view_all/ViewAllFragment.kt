package com.blogspot.mido_mymall.ui.view_all

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.blogspot.mido_mymall.databinding.FragmentViewAllBinding
import com.blogspot.mido_mymall.domain.models.HorizontalProductScrollModel
import com.blogspot.mido_mymall.domain.models.WishListModel
import com.blogspot.mido_mymall.ui.MainActivity
import com.blogspot.mido_mymall.ui.home.GridProductAdapter
import com.blogspot.mido_mymall.ui.my_wish_list.WishlistAdapter

class ViewAllFragment : Fragment() {

    private val TAG = "ViewAllFragment"

    private var _binding: FragmentViewAllBinding? = null
    private val binding get() = _binding!!

    private val LAYOUT_CODE = 0

    private val horizontalProductScrollList = arrayListOf<HorizontalProductScrollModel>()
    private val wishList = arrayListOf<WishListModel>()

    private val wishlistAdapter = WishlistAdapter(false)

    private val args by navArgs<ViewAllFragmentArgs>()

    private var layoutCode: Int = 0

    private val gridProductAdapter by lazy { GridProductAdapter() }

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



        if (layoutCode == 0) {


            wishList.addAll(args.viewAllProductList!!)

            Log.d(TAG, "wishList size: ${wishList.size}")

            wishlistAdapter.asyncListDiffer.submitList(wishList)
//            wishlistAdapter.submitList(wishList)

            binding.recyclerView.apply {
                visibility = View.VISIBLE
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = wishlistAdapter
            }

        } else if (layoutCode == 1) {

            //================ Horizontal list ======================//

            horizontalProductScrollList.addAll(args.horizontalProductScrollModelList)

            gridProductAdapter.clearList()

            binding.gridView.visibility = View.VISIBLE



            binding.gridView.adapter = gridProductAdapter

            Log.d(
                TAG,
                "onViewCreated: horizontalProductScrollModelList size ${args.horizontalProductScrollModelList.size}"
            )


            gridProductAdapter.submitList(horizontalProductScrollList)

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        wishList.clear()
        horizontalProductScrollList.clear()
        gridProductAdapter.clearList()
        _binding = null
    }


}