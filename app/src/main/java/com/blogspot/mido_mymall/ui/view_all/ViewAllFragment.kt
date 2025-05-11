package com.blogspot.mido_mymall.ui.view_all

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.blogspot.mido_mymall.databinding.FragmentViewAllBinding
import com.blogspot.mido_mymall.domain.models.HorizontalProductScrollModel
import com.blogspot.mido_mymall.domain.models.WishListModel
import com.blogspot.mido_mymall.ui.MainActivity
import com.blogspot.mido_mymall.ui.home.GridProductAdapter
import com.blogspot.mido_mymall.ui.my_wish_list.WishlistAdapter
import com.blogspot.mido_mymall.util.onItemClick


class ViewAllFragment : Fragment() {

    private val TAG = "ViewAllFragment"

    private var _binding: FragmentViewAllBinding? = null
    private val binding get() = _binding!!

//    private val LAYOUT_CODE = 0

    private val horizontalProductScrollList = arrayListOf<HorizontalProductScrollModel>()
    private val wishList = arrayListOf<WishListModel>()

    private val wishlistAdapter = WishlistAdapter(false)

    private val args by navArgs<ViewAllFragmentArgs>()

    private var layoutCode: Int = 0

    private var gridProductAdapter: GridProductAdapter? = null

//    private var horizontalScrollLayoutTitle:TextView?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewAllBinding.inflate(inflater)

        gridProductAdapter = GridProductAdapter()

        layoutCode = args.layoutCode
        (requireActivity() as MainActivity).fragmentTitleAndActionBar(args.title)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        Log.d(TAG, "onViewCreated: title ${args.title}")

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

                onItemClick { _, position, v ->

                    findNavController().navigate(
                        ViewAllFragmentDirections
                            .actionViewAllFragmentToProductDetailsFragment(wishList.get(position).productID!!)
                    )

                }
            }

        } else if (layoutCode == 1) {

            //================ Horizontal list ======================//


            if (horizontalProductScrollList.isEmpty()) {
                horizontalProductScrollList.addAll(args.horizontalProductScrollModelList)
            }

//            gridProductAdapter?.clearList()
//            val spacingInPixels = resources.getDimensionPixelSize(R.dimen.grid_item_spacing) // عرف هذا البعد في dimens.xml (مثلاً 8dp أو 10dp)

            binding.gridView.apply {
                visibility = View.VISIBLE
                adapter = gridProductAdapter
            }

            Log.d(
                TAG,
                "onViewCreated: horizontalProductScrollModelList size ${args.horizontalProductScrollModelList.size}"
            )



            gridProductAdapter?.submitList(horizontalProductScrollList)

            if(gridProductAdapter?.productScrollModelList?.size != horizontalProductScrollList.size ){
                gridProductAdapter?.notifyDataSetChanged()
            }

            binding.gridView.setOnItemClickListener { _, _, position, _ ->
                // Handle the item click event here
                val productID = horizontalProductScrollList[position].productID
                findNavController().navigate(
                    ViewAllFragmentDirections.actionViewAllFragmentToProductDetailsFragment(
                        productID!!
                    )
                )
            }


        }

    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: called")
        Log.d(TAG, "onPause: list size ${horizontalProductScrollList.size}")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: called")
        Log.d(TAG, "onPause: list size ${horizontalProductScrollList.size}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        wishList.clear()
        horizontalProductScrollList.clear()
        gridProductAdapter = null
        _binding = null
    }


}