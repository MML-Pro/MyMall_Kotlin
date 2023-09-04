package com.blogspot.mido_mymall.ui.my_address

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.mido_mymall.R
import com.blogspot.mido_mymall.databinding.MyAddressesItemLayoutBinding
import com.blogspot.mido_mymall.domain.models.AddressesModel
import com.blogspot.mido_mymall.ui.add_address.DeleteAddressUtil
import com.blogspot.mido_mymall.ui.delivery.DeliveryFragment.Companion.SELECT_ADDRESS_MODE
import com.blogspot.mido_mymall.ui.my_account.MyAccountFragment.Companion.MANAGE_ADDRESS
import com.blogspot.mido_mymall.ui.my_address.MyAddressesFragment.Companion.SELECTED_ADDRESS

class AddressesAdapter(MODE: Int, private val deleteAddressUtil: DeleteAddressUtil? = null) :
    RecyclerView.Adapter<AddressesAdapter.AddressesHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<AddressesModel>() {
        override fun areItemsTheSame(oldItem: AddressesModel, newItem: AddressesModel): Boolean {
            return oldItem.pinCode == newItem.pinCode
        }

        override fun areContentsTheSame(oldItem: AddressesModel, newItem: AddressesModel): Boolean {
            return oldItem == newItem
        }

    }

    val asyncListDiffer = AsyncListDiffer(this, diffCallback)

    private val MODE: Int
    private var preSelectedPosition: Int = 0

    private var refresh = false

    fun deleteItem(position: Int) {
        val newList = asyncListDiffer.currentList.toMutableList()
        newList.removeAt(position)

        if (newList.isEmpty()) {
            asyncListDiffer.submitList(null)
        } else {
            asyncListDiffer.submitList(newList)

        }

    }

    init {
//        this.addressesList = addressesList
        this.MODE = MODE
        preSelectedPosition = SELECTED_ADDRESS
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressesHolder {
        val binding: MyAddressesItemLayoutBinding = MyAddressesItemLayoutBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressesHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressesHolder, position: Int) {
        val addressesModel: AddressesModel = asyncListDiffer.currentList[position]
        holder.bind(addressesModel, position)
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    fun refreshItem(deselectPos: Int, selectPos: Int) {
        notifyItemChanged(deselectPos)
        notifyItemChanged(selectPos)
    }

    inner class AddressesHolder(private val binding: MyAddressesItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(addressesModel: AddressesModel, position: Int) {


            val address = (addressesModel.flatNumberOrBuildingName
                    + " " + addressesModel.localityOrStreet
                    ) + " " + addressesModel.landMark.toString() + " " +
                    addressesModel.city + " " + addressesModel.state


            binding.fullNameTV.text = addressesModel.fullName
            binding.fullAddressTV.text = address
            binding.pinCodeTV.text = addressesModel.pinCode

            if (MODE == SELECT_ADDRESS_MODE) {

                binding.checkSelectButton.setImageResource(R.drawable.ic_check_24)
                val selected: Boolean = addressesModel.selected
                if (selected) {
                    binding.checkSelectButton.visibility = View.VISIBLE
                    preSelectedPosition = position

                } else {
                    binding.checkSelectButton.visibility = View.GONE

                }
                binding.root.setOnClickListener { view ->
                    if (preSelectedPosition != position) {
                        asyncListDiffer.currentList[position].selected = true
                        asyncListDiffer.currentList[preSelectedPosition].selected = false
                        refreshItem(preSelectedPosition, position)
                        preSelectedPosition = position
                        SELECTED_ADDRESS = position
                    }
                }

            } else if (MODE == MANAGE_ADDRESS) {
                binding.checkSelectButton.setImageResource(R.drawable.plus)
                binding.checkSelectButton.setOnClickListener { view ->
                    showMenu(
                        binding.checkSelectButton,
                        R.menu.edit_or_delete_addresses_menu,
                        position
                    )
                }
            }

//            if (MODE == SELECT_ADDRESS_MODE) {
//                binding.checkSelectButton.setImageResource(R.drawable.ic_check_24)
//                val selected: Boolean = addressesModel.selected
//                if (selected) {
//                    preSelectedPosition = position
//                    binding.checkSelectButton.visibility = View.VISIBLE
//                } else {
//                    binding.checkSelectButton.visibility = View.GONE
//                }
//                binding.root.setOnClickListener { view ->
////                    if (preSelectedPosition != position) {
////                        addressesList[position].setSelected(true)
////                        addressesList[preSelectedPosition].setSelected(false)
////                        refreshItem(preSelectedPosition, position)
////                        preSelectedPosition = position
////                        FirebaseDbQueries.selectedAddress = position
////                    }
//                }
//            } else if (MODE == MANAGE_ADDRESS) {
//                binding.checkSelectButton.setImageResource(R.drawable.plus)
//                binding.checkSelectButton.setOnClickListener { view ->
//                    showMenu(
//                        binding.checkSelectButton,
//                        R.menu.edit_or_delete_addresses_menu,
//                        position
//                    )
//                }
//            }
        }


        private fun showMenu(view: View, @MenuRes menuRes: Int, position: Int) {
            val popup = PopupMenu(view.context, view)
            popup.menuInflater.inflate(menuRes, popup.menu)
            popup.setOnMenuItemClickListener { menuItem: MenuItem? ->
                // Respond to menu item click.
//                Toast.makeText(view.getContext(), "Pop Menu clicked", Toast.LENGTH_SHORT).show();

                if (menuItem?.itemId?.equals(R.id.editAddress) == true) {

                    findNavController(view).navigate(
                        MyAddressesFragmentDirections.actionMyAddressesFragmentToAddAddressFragment(
                            intent = "update_address",
                            null,
                            fromCart = false,
                            null,
                            0,
                            addressPosition = position.toLong()
                        )
                    )
                    refresh = false

                } else {
                    refresh = false

                    deleteAddressUtil?.deleteAddress(
                        asyncListDiffer.currentList.toCollection(
                            arrayListOf()
                        ), position
                    )

                }

                if (refresh) {
                    refreshItem(preSelectedPosition, preSelectedPosition)
                } else {
                    refresh = true
                }
                preSelectedPosition = position
                true
            }
            popup.setOnDismissListener { menu: PopupMenu? -> }
            // Show the popup menu.
            popup.show()
        }
    }
}
