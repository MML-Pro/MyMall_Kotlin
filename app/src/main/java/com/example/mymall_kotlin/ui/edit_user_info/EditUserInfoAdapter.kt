package com.example.mymall_kotlin.ui.edit_user_info

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class EditUserInfoAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
) : FragmentStateAdapter(fragmentManager, lifecycle) {


    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        if (position == 0) {

            return UpdateInfoFragment()

        } else if (position == 1) {
            return UpdatePasswordFragment()
        } else {
            return UpdateInfoFragment()
        }

    }
}