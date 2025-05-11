package com.blogspot.mido_mymall.ui.my_address

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.mido_mymall.R
import com.blogspot.mido_mymall.databinding.MyAddressesItemLayoutBinding
import com.blogspot.mido_mymall.domain.models.AddressesModel
import com.blogspot.mido_mymall.util.Constants.MANAGE_ADDRESS
import com.blogspot.mido_mymall.util.Constants.SELECT_ADDRESS_MODE

//class AddressesAdapter(MODE: Int, private val deleteAddressUtil: DeleteAddressUtil? = null) :
//    RecyclerView.Adapter<AddressesAdapter.AddressesHolder>() {
//
//    private val diffCallback = object : DiffUtil.ItemCallback<AddressesModel>() {
//        override fun areItemsTheSame(oldItem: AddressesModel, newItem: AddressesModel): Boolean {
//            return oldItem.pinCode == newItem.pinCode
//        }
//
//        override fun areContentsTheSame(oldItem: AddressesModel, newItem: AddressesModel): Boolean {
//            return oldItem == newItem
//        }
//
//    }
//
//    val asyncListDiffer = AsyncListDiffer(this, diffCallback)
//
//    private val MODE: Int
//    private var preSelectedPosition: Int = 0
//
//    private var refresh = false
//
//    fun deleteItem(position: Int) {
//        val newList = asyncListDiffer.currentList.toMutableList()
//        newList.removeAt(position)
//
//        if (newList.isEmpty()) {
//            asyncListDiffer.submitList(null)
//        } else {
//            asyncListDiffer.submitList(newList)
//
//        }
//
//    }
//
//    init {
////        this.addressesList = addressesList
//        this.MODE = MODE
//        preSelectedPosition = SELECTED_ADDRESS
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressesHolder {
//        val binding: MyAddressesItemLayoutBinding = MyAddressesItemLayoutBinding
//            .inflate(LayoutInflater.from(parent.context), parent, false)
//        return AddressesHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: AddressesHolder, position: Int) {
//        val addressesModel: AddressesModel = asyncListDiffer.currentList[position]
//        holder.bind(addressesModel, position)
//    }
//
//    override fun getItemCount(): Int {
//        return asyncListDiffer.currentList.size
//    }
//
//    fun refreshItem(deselectPos: Int, selectPos: Int) {
//        notifyItemChanged(deselectPos)
//        notifyItemChanged(selectPos)
//    }
//
//    inner class AddressesHolder(private val binding: MyAddressesItemLayoutBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(addressesModel: AddressesModel, position: Int) {
//
//
//            val address = (addressesModel.flatNumberOrBuildingName
//                    + " " + addressesModel.localityOrStreet
//                    ) + " " + addressesModel.landMark.toString() + " " +
//                    addressesModel.city + " " + addressesModel.state
//
//
//            binding.fullNameTV.text = addressesModel.fullName
//            binding.fullAddressTV.text = address
//            binding.pinCodeTV.text = addressesModel.pinCode
//
//            if (MODE == SELECT_ADDRESS_MODE) {
//
//                binding.checkSelectButton.setImageResource(R.drawable.ic_check_24)
//                val selected: Boolean = addressesModel.selected
//                if (selected) {
//                    binding.checkSelectButton.visibility = View.VISIBLE
//                    preSelectedPosition = position
//
//                } else {
//                    binding.checkSelectButton.visibility = View.GONE
//
//                }
//                binding.root.setOnClickListener { view ->
//                    if (preSelectedPosition != position) {
//                        asyncListDiffer.currentList[position].selected = true
//                        asyncListDiffer.currentList[preSelectedPosition].selected = false
//                        refreshItem(preSelectedPosition, position)
//                        preSelectedPosition = position
//                        SELECTED_ADDRESS = position
//                    }
//                }
//
//
//
//
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
//
////            if (MODE == SELECT_ADDRESS_MODE) {
////                binding.checkSelectButton.setImageResource(R.drawable.ic_check_24)
////                val selected: Boolean = addressesModel.selected
////                if (selected) {
////                    preSelectedPosition = position
////                    binding.checkSelectButton.visibility = View.VISIBLE
////                } else {
////                    binding.checkSelectButton.visibility = View.GONE
////                }
////                binding.root.setOnClickListener { view ->
//////                    if (preSelectedPosition != position) {
//////                        addressesList[position].setSelected(true)
//////                        addressesList[preSelectedPosition].setSelected(false)
//////                        refreshItem(preSelectedPosition, position)
//////                        preSelectedPosition = position
//////                        FirebaseDbQueries.selectedAddress = position
//////                    }
////                }
////            } else if (MODE == MANAGE_ADDRESS) {
////                binding.checkSelectButton.setImageResource(R.drawable.plus)
////                binding.checkSelectButton.setOnClickListener { view ->
////                    showMenu(
////                        binding.checkSelectButton,
////                        R.menu.edit_or_delete_addresses_menu,
////                        position
////                    )
////                }
////            }
//        }
//
//
//        private fun showMenu(view: View, @MenuRes menuRes: Int, position: Int) {
//            val popup = PopupMenu(view.context, view)
//            popup.menuInflater.inflate(menuRes, popup.menu)
//            popup.setOnMenuItemClickListener { menuItem: MenuItem? ->
//                // Respond to menu item click.
////                Toast.makeText(view.getContext(), "Pop Menu clicked", Toast.LENGTH_SHORT).show();
//
//                if (menuItem?.itemId?.equals(R.id.editAddress) == true) {
//
//                    findNavController(view).navigate(
//                        MyAddressesFragmentDirections.actionMyAddressesFragmentToAddAddressFragment(
//                            intent = "update_address",
//                            null,
//                            fromCart = false,
//                            null,
//                            0.0F,
//                            addressPosition = position.toLong()
//                        )
//                    )
//                    refresh = false
//
//                } else {
//                    refresh = false
//
//                    deleteAddressUtil?.deleteAddress(
//                        asyncListDiffer.currentList.toCollection(
//                            arrayListOf()
//                        ), position
//                    ).also {
//                        Toast.makeText(
//                            view.context,
//                            view.context.getString(R.string.address_deleted), Toast.LENGTH_SHORT
//                        ).show()
//                    }
//
//                }
//
//                if (refresh) {
//                    refreshItem(preSelectedPosition, preSelectedPosition)
//                } else {
//                    refresh = true
//                }
//                preSelectedPosition = position
//                true
//            }
//            popup.setOnDismissListener { menu: PopupMenu? -> }
//            // Show the popup menu.
//            popup.show()
//        }
//    }
//}


// إزالة import DeleteAddressUtil إذا لم تعد تستخدمه مباشرة

/**
 * واجهة رد النداء (Callback Interface) للإبلاغ عن الأحداث من الـ Adapter إلى الـ Fragment.
 */


class AddressesAdapter(
    private val mode: Int, // وضع التشغيل (اختيار أو إدارة)
    val clickListener: OnAddressClickListener // مستمع النقرات والأفعال
    // لم نعد بحاجة لـ deleteAddressUtil مباشرة هنا
) : RecyclerView.Adapter<AddressesAdapter.AddressesHolder>() {



    interface OnAddressClickListener {
        /**
         * تُستدعى عند النقر على عنصر العنوان في وضع الاختيار.
         * @param position فهرس العنصر الذي تم النقر عليه.
         */
        fun onAddressClicked(position: Int)

        /**
         * تُستدعى عند اختيار "تعديل" من القائمة.
         * @param position فهرس العنصر المراد تعديله.
         */
        fun onEditAddress(position: Int)

        /**
         * تُستدعى عند اختيار "حذف" من القائمة.
         * @param position فهرس العنصر المراد حذفه.
         */
        fun onDeleteAddressRequest(position: Int)
    }

    // DiffUtil لحساب الفروقات وتحديث القائمة بكفاءة
    private val diffCallback = object : DiffUtil.ItemCallback<AddressesModel>() {
        override fun areItemsTheSame(oldItem: AddressesModel, newItem: AddressesModel): Boolean {
            // استخدم معرفًا فريدًا إذا كان متاحًا (مثل addressId)
            // إذا لم يكن متاحًا، اعتمد على محتوى الكائن أو توليفة حقول فريدة
            // استخدام pinCode وحده قد لا يكون كافيًا
            return oldItem.id == newItem.id // الاعتماد على hashcode لكلاس البيانات قد يكون حلاً جيدًا إذا كانت الحقول ثابتة
            // أو return oldItem.uniqueIdentifier == newItem.uniqueIdentifier // إذا أضفت حقلاً للمعرف الفريد
        }

        override fun areContentsTheSame(oldItem: AddressesModel, newItem: AddressesModel): Boolean {
            // التحقق من تساوي محتوى الكائن باستخدام data class equality
            return oldItem == newItem
        }
    }

    // AsyncListDiffer لإدارة القائمة وتحديثها في الخلفية
    val asyncListDiffer = AsyncListDiffer(this, diffCallback)

    // لا يوجد حاجة لـ init أو preSelectedPosition هنا

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressesHolder {
        val binding: MyAddressesItemLayoutBinding = MyAddressesItemLayoutBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressesHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressesHolder, position: Int) {
        // الحصول على العنصر الحالي بأمان
        val addressesModel = asyncListDiffer.currentList.getOrNull(position)
        // ربط البيانات فقط إذا كان العنصر موجودًا
        addressesModel?.let {
            holder.bind(it) // لم نعد بحاجة لتمرير position هنا إذا لم تستخدمه bind مباشرة
        }
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    // Holder الداخلي
    inner class AddressesHolder(private val binding: MyAddressesItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // تم تحديث bind لتبسيط المنطق واستخدام adapterPosition
        fun bind(addressesModel: AddressesModel) {

            // 1. ربط البيانات النصية (لا تغيير هنا)
            val address = StringBuilder()
            address.append(addressesModel.flatNumberOrBuildingName).append(" ")
            address.append(addressesModel.localityOrStreet).append(" ")
            addressesModel.landMark?.let { if (it.isNotBlank()) address.append(it).append(" ") } // إضافة العلامة المميزة إن وجدت
            address.append(addressesModel.city).append(" ")
            address.append(addressesModel.state)

            binding.fullNameTV.text = addressesModel.fullName
            binding.fullAddressTV.text = address.toString()
            binding.pinCodeTV.text = addressesModel.pinCode

            // 2. التعامل مع الواجهة بناءً على الوضع (MODE)
            if (mode == SELECT_ADDRESS_MODE) {
                // --- وضع اختيار العنوان ---
                binding.checkSelectButton.setImageResource(R.drawable.ic_check_24)

                // أظهر/أخفِ علامة الصح بناءً على حالة العنصر فقط
                binding.checkSelectButton.visibility = if (addressesModel.selected) View.VISIBLE else View.GONE

                // أضف مستمع النقر للعنصر بأكمله
                binding.root.setOnClickListener {
                    // تحقق من أن الموضع لا يزال صالحًا قبل استدعاء المستمع
                    val currentPosition = adapterPosition
                    if (currentPosition != RecyclerView.NO_POSITION) {
                        clickListener.onAddressClicked(currentPosition)
                    }
                }
                // تأكد من أن زر الاختيار لا يستهلك النقرات الموجهة للصف بأكمله
                binding.checkSelectButton.isFocusable = false
                binding.checkSelectButton.isClickable = false

            } else if (mode == MANAGE_ADDRESS) {
                // --- وضع إدارة العناوين ---
                binding.checkSelectButton.visibility = View.VISIBLE // افترض أنه دائمًا مرئي
                binding.checkSelectButton.setImageResource(R.drawable.plus) // أو أي أيقونة مناسبة

                // مستمع النقر لزر "الخيارات" (الأيقونة) لإظهار القائمة
                binding.checkSelectButton.setOnClickListener { view ->
                    val currentPosition = adapterPosition
                    if (currentPosition != RecyclerView.NO_POSITION) {
                        showMenu(view, R.menu.edit_or_delete_addresses_menu, currentPosition)
                    }
                }
                // أزل مستمع النقر من الصف بأكمله إذا لم يكن له وظيفة في هذا الوضع
                binding.root.setOnClickListener(null)
            }
        } // نهاية bind

        /**
         * يعرض قائمة الخيارات (تعديل/حذف).
         */
        private fun showMenu(view: View, @MenuRes menuRes: Int, position: Int) {
            val popup = PopupMenu(view.context, view)
            popup.menuInflater.inflate(menuRes, popup.menu)

            popup.setOnMenuItemClickListener { menuItem: MenuItem ->
                // استخدم position التي تم تمريرها للدالة showMenu لأن adapterPosition قد تتغير
                when (menuItem.itemId) {
                    R.id.editAddress -> {
                        // أبلغ الـ Fragment بطلب التعديل
                        clickListener.onEditAddress(position)
                        true
                    }
                    R.id.deleteAddress -> { // تأكد من أن ID عنصر الحذف هو deleteAddress
                        // أبلغ الـ Fragment بطلب الحذف
                        clickListener.onDeleteAddressRequest(position)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        } // نهاية showMenu
    } // نهاية AddressesHolder
} // نهاية AddressesAdapter
