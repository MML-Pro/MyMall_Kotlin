package com.example.mymall_kotlin.ui.edit_user_info

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.mymall_kotlin.R
import com.example.mymall_kotlin.databinding.FragmentEditUserInfoBinding
import com.example.mymall_kotlin.ui.product_details.ProductDetailsFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

private const val TAG = "EditUserInfoFragment"

class EditUserInfoFragment : Fragment() {

    private var _binding: FragmentEditUserInfoBinding? = null
    private val binding get() = _binding!!

    private lateinit var tabLayoutMediator: TabLayoutMediator


    private val editUserInfoAdapter by lazy {
        EditUserInfoAdapter(
            childFragmentManager,
            requireActivity().lifecycle
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEditUserInfoBinding.inflate(inflater, container, false)

        tabLayoutMediator = TabLayoutMediator(
            binding.tabLayout,
            binding.editUserInfoViewPager
        ) { tab: TabLayout.Tab?, position: Int ->

//            container?.clipChildren = false
            if(position == 0){
                tab?.text = "User Info"
            }else if(position == 1){
                tab?.text = "Password"
            }

        }

        binding.editUserInfoViewPager.adapter = editUserInfoAdapter

        if (!tabLayoutMediator.isAttached) {
            tabLayoutMediator.attach()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tabLayout.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {

                tab?.let {
                    Log.d(TAG, "onTabSelected: ${tab.position}")
                    binding.editUserInfoViewPager.currentItem =
                        tab.position
                }


            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}