package com.blogspot.mido_mymall.ui.my_address

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.blogspot.mido_mymall.R
import com.blogspot.mido_mymall.databinding.FragmentMyAddressesBinding
import com.blogspot.mido_mymall.domain.models.AddressesModel
import com.blogspot.mido_mymall.domain.models.CartItemModel
import com.blogspot.mido_mymall.ui.MainActivity
import com.blogspot.mido_mymall.util.Constants
import com.blogspot.mido_mymall.util.Constants.SELECT_ADDRESS_MODE
import com.blogspot.mido_mymall.util.Resource
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

//@AndroidEntryPoint
//class MyAddressesFragment : Fragment(), MenuProvider, DeleteAddressUtil {
//
//    companion object {
//        private const val TAG = "MyAddressesFragment"
//        var mode = -1
//
//        @JvmStatic
//        var SELECTED_ADDRESS = 0
//    }
//
//    private var _binding: FragmentMyAddressesBinding? = null
//    private val binding get() = _binding!!
//
//    private val myAddressesList = arrayListOf<AddressesModel>()
//    private val myAddressViewModel by viewModels<MyAddressViewModel>()
//    private lateinit var addressesAdapter: AddressesAdapter
//    private val args by navArgs<MyAddressesFragmentArgs>()
//
//
//    private var previousAddress = 0
//
//    private var previousAddressIndex = 0
//
//    private var loadingDialog: Dialog? = null
//
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentMyAddressesBinding.inflate(inflater)
//
//        loadingDialog = Constants.setProgressDialog(requireContext())
//
//        val menuHost: MenuHost = requireActivity()
//        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.CREATED)
//
//        mode = args.selectedMode
//
//        addressesAdapter = AddressesAdapter(mode, this)
//
//
//        if (mode == SELECT_ADDRESS_MODE) {
//            binding.deliverHereButton.visibility = View.VISIBLE
//        } else {
//            binding.deliverHereButton.visibility = View.GONE
//        }
//
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        previousAddress = SELECTED_ADDRESS
//
//        myAddressViewModel.getAddresses()
//
//        lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                myAddressViewModel.myAddresses.collect { response ->
//                    when (response) {
//
//                        is Resource.Loading -> {}
//                        is Resource.Success -> {
//                            response.data?.let {
//                                val listSize = it["list_size"] as Long
//
//                                for (i in 1 until listSize + 1) {
//                                    myAddressesList.add(
//                                        AddressesModel(
//                                            it.get("city_$i").toString(),
//                                            it.get("localityOrStreet_$i").toString(),
//                                            it.get("flatNumberOrBuildingName_$i").toString(),
//                                            it.get("pinCode_$i").toString(),
//                                            it.get("state_$i").toString(),
//                                            it.get("landMark_$i").toString(),
//                                            it.get("fullName_$i").toString(),
//                                            it.get("mobileNumber_$i").toString(),
//                                            it.get("alternateMobileNumber_$i").toString(),
//                                            it.get("selected_$i") as Boolean
//                                        )
//                                    )
//
//                                    if (it.get("selected_$i") as Boolean) {
//                                        SELECTED_ADDRESS = (i - 1).toString().toInt()
//                                    }
//                                }
//
//                            }.also {
//                                addressesAdapter.asyncListDiffer.submitList(myAddressesList)
//                                binding.addressesCountTextView.text =
//                                    getString(R.string.number_addresses_saved, myAddressesList.size)
//                            }
//                        }
//
//                        is Resource.Error -> {
//                            Log.e(TAG, "myAddresses: ${response.message.toString()}")
//                            Toast.makeText(
//                                requireContext(),
//                                response.message.toString(),
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//
//                        else -> {}
//                    }
//
//                }
//            }
//        }
//
//        binding.addressesRecyclerView.apply {
//            layoutManager =
//                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
//            adapter = addressesAdapter
//        }
//
//        binding.addNewAddressBtn.setOnClickListener {
//            findNavController().navigate(
//                MyAddressesFragmentDirections.actionMyAddressesFragmentToAddAddressFragment(
//                    intent = "null",
//                    null,
//                    fromCart = false,
//                    null,
//                    0.0F
//                )
//            )
//        }
//
////        binding.deliverHereButton.setOnClickListener {
////            if (SELECTED_ADDRESS != previousAddress) {
////
////                previousAddressIndex = previousAddress
////
////                myAddressViewModel.updateSelectedAddress(SELECTED_ADDRESS, previousAddress)
////            } else {
////                findNavController().navigateUp()
////            }
////        }
//
//
//        binding.deliverHereButton.setOnClickListener {
//            val currentlySelectedIndex =
//                MyAddressesFragment.SELECTED_ADDRESS // أو طريقة أفضل للحصول على الفهرس
//
//            if (currentlySelectedIndex >= 0 && currentlySelectedIndex < myAddressesList.size) {
//                val selectedModel = myAddressesList[currentlySelectedIndex] // <-- احصل على الكائن
//
//                // (اختياري) تحديث Firestore إذا تغير الاختيار
//                if (currentlySelectedIndex != previousAddress) {
//                    previousAddressIndex = previousAddress
//                    myAddressViewModel.updateSelectedAddress(
//                        currentlySelectedIndex,
//                        previousAddress
//                    )
//                    // يمكنك الانتظار هنا أو المتابعة بتفاؤل (كما في الحل السابق)
//                }
//
//                // ***** إرسال كائن العنوان المحدد كنتيجة *****
//                // استخدم مفتاح جديد ونوع AddressesModel
//                findNavController().previousBackStackEntry?.savedStateHandle?.set(
//                    "selectedAddressObject",
//                    selectedModel
//                )
//                Log.d(TAG, "Setting navigation result: selectedAddressObject = $selectedModel")
//
//                // العودة للشاشة السابقة
//                findNavController().navigateUp()
//            } else {
//                Log.e(
//                    TAG,
//                    "Invalid selected index ($currentlySelectedIndex) or address list size issue."
//                )
//                Toast.makeText(
//                    requireContext(),
//                    "Please select a valid address.",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
//
//        lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                myAddressViewModel.updateSelectedAddressState.collect { response ->
//                    when (response) {
//                        is Resource.Loading -> {
//                            Log.d(TAG, "updateSelectedAddressState: Loading...")
//                            // تأكد من أن الديالوج يُعرض فقط إذا لم يكن معروضًا بالفعل لتجنب مشاكل
//
//                            loadingDialog?.show()
//
//                        }
//
//                        is Resource.Success -> {
//                            Log.d(
//                                TAG,
//                                "updateSelectedAddressState: Success. Dismissing dialog, setting result, navigating up."
//                            )
//                            loadingDialog?.cancel() // <--- استخدم dismiss
//
//                            val indexToSend =
//                                MyAddressesFragment.SELECTED_ADDRESS // أو الفهرس الصحيح
//                            findNavController().previousBackStackEntry?.savedStateHandle?.set(
//                                "selectedAddressObject",
//                                myAddressesList.getOrNull(indexToSend)
//                            ) // <--- تأكد من إرسال الكائن هنا إذا كنت تستخدم حل الكائن
//
//                            findNavController().navigateUp()
//
//                            // ---> استدعِ دالة إعادة التعيين في ViewModel <---
//                            myAddressViewModel.resetUpdateAddressState()
//                        }
//
//                        is Resource.Error -> {
//                            Log.e(TAG, "updateSelectedAddressState: Error - ${response.message}")
//                            loadingDialog?.cancel() // <--- استخدم dismiss
//
//                            Toast.makeText(
//                                requireContext(),
//                                "Failed to update selected address: ${response.message}",
//                                Toast.LENGTH_LONG
//                            ).show()
//
//                            // (اختياري) يمكنك إعادة التحديد البصري للعنوان السابق هنا
//
//                            // ---> استدعِ دالة إعادة التعيين في ViewModel <---
//                            myAddressViewModel.resetUpdateAddressState()
//                            loadingDialog = null
//                        }
//
//                        else -> { // حالة Ideal أو غيرها
//                            // لا تظهر الديالوج، وتأكد من أنه مخفي إذا كان ظاهرًا لسبب ما
//                            loadingDialog?.cancel() // <--- استخدم dismiss هنا أيضًا للأمان
//                        }
//                    }
//                }
//            }
//        }
//
//        requireActivity()
//            .onBackPressedDispatcher
//            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    Log.d(TAG, "Fragment back pressed invoked")
//
//                    if (mode == SELECT_ADDRESS_MODE) {
//                        if (SELECTED_ADDRESS != previousAddress) {
//                            myAddressesList[SELECTED_ADDRESS].selected = false
//                            myAddressesList[previousAddress].selected = true
//                            SELECTED_ADDRESS = previousAddress
//                        }
//
//                    }
//                    findNavController().navigateUp()
//                }
//            })
//
//        lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
//                myAddressViewModel.removeAddressState.collect { response ->
//
//                    when (response) {
//                        is Resource.Loading -> {
//                            loadingDialog?.show()
//                        }
//
//                        is Resource.Success -> {
//                            loadingDialog?.cancel()
//
////                            addressesAdapter.asyncListDiffer.submitList(null)
////                                .also {
////                                    myAddressViewModel.getAddresses()
////                                }
//
//                            binding.addressesCountTextView.text =
//                                getString(R.string.number_addresses_saved, myAddressesList.size)
//
//
//
//                            Log.d(TAG, "selected address value: $SELECTED_ADDRESS")
//
//                        }
//
//                        is Resource.Error -> {
//                            loadingDialog?.cancel()
//                            loadingDialog = null
//                            Log.e(TAG, "removeAddressState: ${response.message.toString()}")
//                            myAddressViewModel.resetUpdateAddressState()
//                        }
//
//                        else -> {
//                            loadingDialog = null
//                        }
//                    }
//
//
//                }
//            }
//        }
//
//    }
//
//    override fun onPause() {
//        super.onPause()
//        loadingDialog = null
//    }
//
//    override fun onStop() {
//        super.onStop()
//        loadingDialog = null
//    }
//
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        myAddressesList.clear()
//        loadingDialog = null
//        _binding = null
//    }
//
//    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//
//    }
//
//    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
//        if (menuItem.itemId == android.R.id.home) {
//            if (SELECTED_ADDRESS != previousAddress) {
//                myAddressesList[SELECTED_ADDRESS].selected = false
//                myAddressesList[previousAddress].selected = true
//                SELECTED_ADDRESS = previousAddress
//            }
//            findNavController(binding.root).navigateUp().also {
//                if (loadingDialog?.isShowing == true) {
//                    loadingDialog?.cancel()
//                }
//            }
//            return true
//        }
//        return false
//    }
//
//    override fun deleteAddress(addressesModelList: ArrayList<AddressesModel>, position: Int) {
//        Log.d(TAG, "selected address value before remove: $SELECTED_ADDRESS")
//        myAddressViewModel.removeAddress(addressesModelList, position)
//        addressesAdapter.deleteItem(position)
//        myAddressesList.removeAt(position)
//    }
//
//}


// تطبيق الواجهة الجديدة وإزالة DeleteAddressUtil إذا لم تعد مستخدمة بشكل منفصل
@AndroidEntryPoint
class MyAddressesFragment : Fragment(), MenuProvider, AddressesAdapter.OnAddressClickListener {

    companion object {
        private const val TAG = "MyAddressesFragment"
        // --- إزالة المتغيرات الستاتيكية ---
//         var mode = -1
//         @JvmStatic
//         var SELECTED_ADDRESS = 0
    }

    private var _binding: FragmentMyAddressesBinding? = null
    private val binding get() = _binding!!

    // --- متغيرات الحالة المحلية ---
    // للاحتفاظ بالقائمة الحالية المعروضة
    private var currentAddresses = listOf<AddressesModel>()

    // لتتبع الفهرس المحدد حاليًا (-1 يعني لا يوجد تحديد)
    private var selectedAddressIndex: Int = -1

    // لتتبع الفهرس الذي كان محددًا عند تحميل البيانات أول مرة من قاعدة البيانات
    private var initialSelectedIndexFromDB: Int = -1

    private val myAddressViewModel by viewModels<MyAddressViewModel>()
    private lateinit var addressesAdapter: AddressesAdapter
    private val args by navArgs<MyAddressesFragmentArgs>()
    private val cartItemModelList = arrayListOf<CartItemModel>()

    // وضع التشغيل الحالي للـ Fragment
    private var currentMode: Int = -1

    private var loadingDialog: Dialog? = null // استخدم النوع الذي ترجعه دالة Constants

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyAddressesBinding.inflate(inflater)
        // تأكد من استخدام requireContext() بأمان هنا أو في onViewCreated
        try {
            loadingDialog = Constants.setProgressDialog(requireContext())
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Error creating loading dialog in onCreateView: ${e.message}")
            // قد يحدث هذا إذا تم استدعاء onCreateView قبل أن يكون الـ Fragment مرتبطًا بالـ Context
        }

        (requireActivity() as MainActivity).apply {
            supportActionBar?.title = getString(R.string.my_addresses)
            getToolBar().title = getString(R.string.my_addresses)
//            actionBarLogo.visibility = View.GONE
        }


        val menuHost: MenuHost = requireActivity()
        // استخدام RESUMED أفضل للمنيو لضمان أن الواجهة مرئية
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        currentMode = args.selectedMode // تعيين الوضع من الـ Arguments

        // تمرير this كمستمع للنقرات
        addressesAdapter = AddressesAdapter(currentMode, this)

        // إظهار أو إخفاء زر "التوصيل هنا"
        if (currentMode == SELECT_ADDRESS_MODE) {
            binding.deliverHereButton.visibility = View.VISIBLE
        } else {
            binding.deliverHereButton.visibility = View.GONE
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // تأكد من تهيئة الديالوج إذا لم يتم في onCreateView
        if (loadingDialog == null) {
            try {
                loadingDialog = Constants.setProgressDialog(requireContext())
            } catch (e: IllegalStateException) {
                Log.e(TAG, "Error creating loading dialog in onViewCreated: ${e.message}")
            }
        }

        if (!args.cartItemModelList.isNullOrEmpty()) {
            cartItemModelList.addAll(args.cartItemModelList!!)
        }
        // --- إزالة الاعتماد على المتغير الستاتيكي ---
        // previousAddress = SELECTED_ADDRESS

        // إعداد RecyclerView والمراقبين والمستمعين
        setupRecyclerView()
        observeAddresses() // يراقب قائمة العناوين من ViewModel
        observeUpdateState() // يراقب نتيجة تحديث العنوان المحدد في Firestore
        observeDeleteState() // يراقب نتيجة حذف العنوان من Firestore
        setupButtonClickListeners() // إعداد مستمعي النقر للأزرار
        setupBackButton() // إعداد التعامل مع زر الرجوع

        // ***** إضافة مراقب جديد للتحقق من الحاجة للتحديث *****
        val navController = findNavController()
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("addressListNeedsRefresh")
            ?.observe(viewLifecycleOwner) { needsRefresh ->
                // يتم استدعاء هذا عند العودة من AddAddressFragment إذا تم تعيين النتيجة
                if (needsRefresh == true) {
                    Log.d(TAG, "Received addressListNeedsRefresh = true. Refreshing addresses.")
                    // --- اطلب إعادة تحميل قائمة العناوين ---
                    myAddressViewModel.getAddresses()

                    // قم بإزالة النتيجة حتى لا يتم إعادة التحميل مرة أخرى تلقائيًا
                    navController.currentBackStackEntry?.savedStateHandle?.remove<Boolean>("addressListNeedsRefresh")
                }
            }

        // طلب البيانات الأولية
        myAddressViewModel.getAddresses()
    }

    /**
     * إعداد RecyclerView وتعيين الـ Adapter.
     */
    private fun setupRecyclerView() {
        binding.addressesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = addressesAdapter
            // يمكنك إضافة ItemDecoration إذا لزم الأمر
        }
    }

    /**
     * يراقب LiveData أو Flow الخاص بقائمة العناوين من الـ ViewModel.
     */
    private fun observeAddresses() {
        lifecycleScope.launch {
            // استخدام STARTED مناسب لجمع حالة الواجهة
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                myAddressViewModel.myAddresses.collect { response ->
                    when (response) {
                        is Resource.Loading -> {
                            // يمكنك إظهار مؤشر تحميل عام هنا إذا أردت، أو تجاهله
                            // loadingDialog?.show() // قد يكون مربكًا مع الديالوج الآخر
                            Log.d(TAG, "Loading addresses...")
                        }

                        is Resource.Success -> {
                            loadingDialog?.dismiss() // إخفاء الديالوج إذا كان ظاهرًا بسبب عملية سابقة
                            val tempList = mutableListOf<AddressesModel>()
                            var tempSelectedIndex = -1 // إعادة تعيين الفهرس المؤقت

                            // معالجة البيانات القادمة من Firestore بأمان
                            response.data?.let { firestoreData ->
                                val listSize = firestoreData["list_size"] as? Long ?: 0L
                                for (i in 1..listSize) { // استخدام .. لتشمل النهاية
                                    val isSelectedFromDB =
                                        firestoreData.getBoolean("selected_$i") ?: false
                                    tempList.add(
                                        AddressesModel(
                                            city = firestoreData.getString("city_$i") ?: "",
                                            localityOrStreet = firestoreData.getString("localityOrStreet_$i")
                                                ?: "",
                                            flatNumberOrBuildingName = firestoreData.getString("flatNumberOrBuildingName_$i")
                                                ?: "",
                                            pinCode = firestoreData.getString("pinCode_$i") ?: "",
                                            state = firestoreData.getString("state_$i") ?: "",
                                            landMark = firestoreData.getString("landMark_$i"), // Nullable
                                            fullName = firestoreData.getString("fullName_$i") ?: "",
                                            mobileNumber = firestoreData.getString("mobileNumber_$i")
                                                ?: "",
                                            alternateMobileNumber = firestoreData.getString("alternateMobileNumber_$i"), // Nullable
                                            selected = isSelectedFromDB // تعيين الحالة من قاعدة البيانات
                                        )
                                    )
                                    if (isSelectedFromDB) {
                                        tempSelectedIndex = (i - 1).toInt() // الفهرس المعتمد على 0
                                    }
                                }
                            }

                            Log.d(
                                TAG,
                                "Fetched ${tempList.size} addresses. DB selected index: $tempSelectedIndex"
                            )

                            // تحديث القائمة المحلية والفهرس المحدد (إذا لم يتم تحديده يدويًا بعد)
                            currentAddresses = tempList
                            if (selectedAddressIndex == -1 || selectedAddressIndex >= currentAddresses.size) {
                                // إذا كان هذا هو التحميل الأول أو الفهرس الحالي غير صالح، استخدم القيمة من DB
                                selectedAddressIndex = tempSelectedIndex
                            }
                            initialSelectedIndexFromDB =
                                tempSelectedIndex // حفظ الفهرس الأصلي من DB

                            // إنشاء قائمة جديدة للتأكد من أن حالة 'selected' صحيحة قبل الإرسال
                            val listWithCorrectSelection =
                                currentAddresses.mapIndexed { index, address ->
                                    address.copy(selected = (index == selectedAddressIndex))
                                }

                            // إرسال القائمة المحدثة إلى Adapter
                            addressesAdapter.asyncListDiffer.submitList(listWithCorrectSelection)
                            binding.addressesCountTextView.text =
                                getString(R.string.number_addresses_saved, currentAddresses.size)

                        }

                        is Resource.Error -> {
                            loadingDialog?.dismiss() // إخفاء الديالوج إذا كان ظاهرًا
                            Log.e(TAG, "myAddresses Error: ${response.message}")
                            Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                            // مسح القائمة الحالية في حالة الخطأ؟
                            currentAddresses = emptyList()
                            selectedAddressIndex = -1
                            initialSelectedIndexFromDB = -1
                            addressesAdapter.asyncListDiffer.submitList(currentAddresses)
                            binding.addressesCountTextView.text =
                                getString(R.string.number_addresses_saved, 0)
                        }

                        else -> { /* التعامل مع الحالة الأولية */
                        }
                    }
                }
            }
        }
    }

    /**
     * يتم استدعاؤها من الـ Adapter عند النقر على عنصر عنوان.
     * @param position فهرس العنصر الذي تم النقر عليه.
     */
    override fun onAddressClicked(position: Int) {
        // نفذ فقط في وضع اختيار العنوان، وإذا كان الفهرس صالحًا ومختلفًا عن الحالي
        if (currentMode == SELECT_ADDRESS_MODE && position != selectedAddressIndex && position >= 0 && position < currentAddresses.size) {
            Log.d(
                TAG,
                "Address clicked at position: $position. Previous index: $selectedAddressIndex"
            )

            // 1. إنشاء قائمة جديدة مع تحديث حالة 'selected'
            val newList = currentAddresses.mapIndexed { index, address ->
                address.copy(selected = (index == position)) // المحدد الجديد true، والباقي false
            }

            // 2. تحديث الحالة المحلية
            selectedAddressIndex = position // تحديث الفهرس المحلي
            currentAddresses = newList // تحديث القائمة المحلية

            // 3. إرسال القائمة الجديدة للـ Adapter لتحديث الواجهة
            addressesAdapter.asyncListDiffer.submitList(currentAddresses)
            Log.d(TAG, "Submitted new list via DiffUtil. New selected index: $selectedAddressIndex")
        }
    }

    /**
     * يتم استدعاؤها من الـ Adapter عند طلب تعديل عنوان.
     * @param position فهرس العنوان المطلوب تعديله.
     */
    override fun onEditAddress(position: Int) {
        if (position >= 0 && position < currentAddresses.size) {
            Log.d(TAG, "Edit address requested for position: $position")
//            val addressToEdit = currentAddresses[position] // يمكنك تمرير الكائن إذا لزم الأمر
            findNavController().navigate(
                MyAddressesFragmentDirections.actionMyAddressesFragmentToAddAddressFragment(
                    intent = "update_address",
                    // addressModel = addressToEdit, // اجعل AddressesModel Parcelable لتمرير الكائن
                    cartItemModelList = cartItemModelList.toTypedArray(),
                    cartListIds = args.cartListIds,
                    fromCart = false,
                    addressPosition = position.toLong() // أو استخدم معرفًا فريدًا إذا كان لديك
                )
            )
        } else {
            Log.e(TAG, "Invalid position for edit: $position")
        }
    }

    /**
     * يتم استدعاؤها من الـ Adapter عند طلب حذف عنوان.
     * @param position فهرس العنوان المطلوب حذفه.
     */
    override fun onDeleteAddressRequest(position: Int) {
        if (position >= 0 && position < currentAddresses.size) {
            Log.d(TAG, "Delete address requested for position: $position")
            // عرض ديالوج تأكيد قبل الحذف

            MaterialAlertDialogBuilder(requireContext(),R.style.ThemeOverlay_App_MaterialAlertDialog)
                .setTitle(getString(R.string.delete_address))
                .setMessage(getString(R.string.are_you_sure_you_want_to_delete_this_address))
                .setPositiveButton(getString(R.string.delete)) { dialog, _ ->
                    Log.d(TAG, "Deletion confirmed for position: $position")
                    // استدعاء ViewModel لحذف العنوان من Firestore
                    // مرر المعلومات اللازمة للـ ViewModel (مثل الفهرس أو الكائن أو ID)
                    // تأكد من أن دالة removeAddress في ViewModel تعالج أيضًا حالة حذف العنوان المحدد حاليًا
                    myAddressViewModel.removeAddress(
                        ArrayList(currentAddresses),
                        position,
                        selectedAddressIndex
                    )
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.cancel()
                }
                .show()

        } else {
            Log.e(TAG, "Invalid position for delete: $position")
        }
    }
    // نهاية تطبيق الواجهة

    /**
     * إعداد مستمعي النقر للأزرار الأخرى.
     */
    private fun setupButtonClickListeners() {
        binding.addNewAddressBtn.setOnClickListener {
            Log.d(TAG, "Add new address button clicked")
            // إعادة تعيين الفهرس المحدد عند الذهاب لإضافة عنوان جديد؟ قد يكون مفيدًا
            // selectedAddressIndex = -1
            findNavController().navigate(
                MyAddressesFragmentDirections.actionMyAddressesFragmentToAddAddressFragment(
                    intent = "add_new_address",
                    cartItemModelList = cartItemModelList.toTypedArray(),
                    cartListIds = args.cartListIds,
                    fromCart = false,
                    addressPosition = 0
                )
            )
        }

        binding.deliverHereButton.setOnClickListener {
            Log.d(TAG, "Deliver here button clicked. Selected index: $selectedAddressIndex")
            // استخدم الفهرس المحلي المؤكد
            if (selectedAddressIndex >= 0 && selectedAddressIndex < currentAddresses.size) {
                val selectedModel = currentAddresses[selectedAddressIndex]

                // تحديث Firestore فقط إذا تغير الاختيار عن الحالة الأصلية المحفوظة من DB
                if (selectedAddressIndex != initialSelectedIndexFromDB) {
                    Log.d(
                        TAG,
                        "Selection changed from DB state ($initialSelectedIndexFromDB). Calling updateSelectedAddress..."
                    )
                    // مرر الفهارس المعتمدة على 0
                    myAddressViewModel.updateSelectedAddress(
                        selectedAddressIndex,
                        initialSelectedIndexFromDB
                    )
                    // يجب أن يراقب observeUpdateState نتيجة هذا الاستدعاء
                } else {
                    Log.d(
                        TAG,
                        "Selection ($selectedAddressIndex) matches initial DB state ($initialSelectedIndexFromDB). No Firestore update needed."
                    )
                    // إذا لم يتغير التحديد، يمكننا العودة مباشرة وإرسال النتيجة
                    // (لأن observeUpdateState لن يتم تشغيله)
                    findNavController().previousBackStackEntry?.savedStateHandle?.set(
                        "selectedAddressObject",
                        selectedModel
                    )
                    Log.d(
                        TAG,
                        "Setting navigation result directly: selectedAddressObject = $selectedModel"
                    )
                    findNavController().navigateUp()
                }

            } else {
                Log.e(
                    TAG,
                    "DeliverHere: Invalid selected index ($selectedAddressIndex). Cannot proceed."
                )
                Toast.makeText(
                    requireContext(),
                    "Please select a valid address.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * يراقب حالة عملية تحديث العنوان في Firestore.
     */
    private fun observeUpdateState() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                myAddressViewModel.updateSelectedAddressState.collect { response ->
                    when (response) {
                        is Resource.Loading -> {
                            Log.d(TAG, "updateSelectedAddressState: Loading...")
                            loadingDialog?.show()
                        }

                        is Resource.Success -> {
                            loadingDialog?.dismiss()
                            Log.d(TAG, "updateSelectedAddressState: Success.")
                            // الآن بعد نجاح التحديث في Firestore، أرسل النتيجة وعد للخلف
                            if (selectedAddressIndex >= 0 && selectedAddressIndex < currentAddresses.size) {
                                val selectedModel = currentAddresses[selectedAddressIndex]
                                findNavController().previousBackStackEntry?.savedStateHandle?.set(
                                    "selectedAddressObject",
                                    selectedModel
                                )
                                Log.d(
                                    TAG,
                                    "Firestore update success. Setting navigation result: selectedAddressObject = $selectedModel"
                                )
                                findNavController().navigateUp()
                            } else {
                                // حالة نادرة: الفهرس أصبح غير صالح بعد التحديث؟
                                Log.e(
                                    TAG,
                                    "Firestore update success but selected index ($selectedAddressIndex) is now invalid!"
                                )
                                // عد للخلف دون نتيجة أو أظهر خطأ
                                Toast.makeText(
                                    context,
                                    "Error retrieving selected address after update.",
                                    Toast.LENGTH_LONG
                                ).show()
                                findNavController().navigateUp() // العودة على أي حال؟
                            }
                            myAddressViewModel.resetUpdateAddressState() // إعادة تعيين الحالة
                        }

                        is Resource.Error -> {
                            loadingDialog?.dismiss()
                            Log.e(TAG, "updateSelectedAddressState: Error - ${response.message}")
                            Toast.makeText(
                                context,
                                "Failed to update selection: ${response.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                            // ربما تعيد الفهرس المحلي للحالة الأصلية؟
                            // selectedAddressIndex = initialSelectedIndexFromDB
                            myAddressViewModel.resetUpdateAddressState() // إعادة تعيين الحالة
                        }

                        else -> {
                            loadingDialog?.dismiss()
                        } // إخفاء عند Ideal
                    }
                }
            }
        }
    }

    /**
     * يراقب حالة عملية حذف العنوان من Firestore.
     */
    private fun observeDeleteState() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                myAddressViewModel.removeAddressState.collect { response ->
                    when (response) {
                        is Resource.Loading -> {
                            loadingDialog?.show()
                        }

                        is Resource.Success -> {
                            loadingDialog?.dismiss()
                            Log.d(TAG, "Address deleted successfully.")
                            Toast.makeText(context, getString(R.string.address_deleted), Toast.LENGTH_SHORT).show()
                            // أعد تعيين الفهرس المحلي واطلب تحديث القائمة
                            selectedAddressIndex = -1
                            initialSelectedIndexFromDB = -1 // إعادة تعيين الفهرس الأولي أيضًا
                            myAddressViewModel.getAddresses() // أطلب تحديث القائمة
                            myAddressViewModel.resetUpdateAddressState() // إعادة تعيين حالة الحذف
                        }

                        is Resource.Error -> {
                            loadingDialog?.dismiss()
                            Log.e(TAG, "removeAddressState: Error - ${response.message}")
                            Toast.makeText(
                                context,
                                "Failed to delete address: ${response.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                            myAddressViewModel.resetUpdateAddressState()
                        }

                        else -> {
                            loadingDialog?.dismiss()
                        }
                    }
                }
            }
        }
    }

    /**
     * إعداد التعامل مع زر الرجوع الخاص بالنظام.
     */
    private fun setupBackButton() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Log.d(TAG, "System back pressed.")
                    // إذا كان المستخدم في وضع الاختيار ولم يغير شيئًا، يمكن العودة مباشرة.
                    // إذا غير شيئًا، قد ترغب في إرسال الاختيار الأخير أو لا شيء.
                    // السلوك الحالي: فقط عد للخلف.
                    if (loadingDialog?.isShowing == true) {
                        loadingDialog?.dismiss() // أغلق الديالوج إذا كان مفتوحًا
                    }
                    // السماح بالعودة الافتراضية
                    if (isEnabled) {
                        isEnabled = false
                        requireActivity().onBackPressedDispatcher.onBackPressed() // استدعاء سلوك الرجوع الافتراضي
                    }
                    // أو findNavController().navigateUp()
                }
            })
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).apply {
            supportActionBar?.title = getString(R.string.my_addresses)
            title = getString(R.string.my_addresses)
        }
    }

    override fun onDestroyView() {
        // تأكد من إخفاء الديالوج وإلغاء مرجعيته لتجنب تسريب الذاكرة
        loadingDialog?.cancel()
        loadingDialog = null
        _binding = null // مهم جدًا لمنع تسريب الذاكرة للـ Binding
        super.onDestroyView() // استدعاء الـ super في النهاية
    }

    // --- تطبيق MenuProvider ---
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        // لا توجد قائمة خيارات هنا على ما يبدو
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        // التعامل مع زر الرجوع في شريط الأدوات (السهم)
        if (menuItem.itemId == android.R.id.home) {

            if (loadingDialog?.isShowing == true) {
                loadingDialog?.dismiss()
            }

            return findNavController().navigateUp()
        }
        return false
    }
    // --- نهاية MenuProvider ---

} // نهاية MyAddressesFragment