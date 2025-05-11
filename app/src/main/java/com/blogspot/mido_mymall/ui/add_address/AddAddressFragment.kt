package com.blogspot.mido_mymall.ui.add_address

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.blogspot.mido_mymall.R
import com.blogspot.mido_mymall.databinding.FragmentAddAddressBinding
import com.blogspot.mido_mymall.domain.models.AddressesModel
import com.blogspot.mido_mymall.ui.MainActivity
import com.blogspot.mido_mymall.util.Constants
import com.blogspot.mido_mymall.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddAddressFragment : Fragment() {

    companion object {
        private const val TAG = "AddAddressFragment"

    }

    private var _binding: FragmentAddAddressBinding? = null
    private val binding get() = _binding!!

    private lateinit var stateList: Array<String>
    private var selectedState: String? = null

    private val addAddressViewModel by viewModels<AddAddressViewModel>()

    private var loadingDialog: Dialog?=null

    private val args by navArgs<AddAddressFragmentArgs>()

    private val myAddressList = arrayListOf<AddressesModel>()

//    private lateinit var addressesAdapter: AddressesAdapter

//    private var updateAddress = false

    private lateinit var addressesModel: AddressesModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddAddressBinding.inflate(inflater)

        loadingDialog = Constants.setProgressDialog(requireContext())

        if(args.intent == "add_new_address"){

            (requireActivity() as MainActivity).apply {
                supportActionBar?.title = getString(R.string.add_address)
                getToolBar().title = getString(R.string.add_address)
                actionBarLogo.visibility = View.GONE
            }
        }else {
            (requireActivity() as MainActivity).apply {
                supportActionBar?.title = getString(R.string.update_address)
                getToolBar().title = getString(R.string.update_address)
                actionBarLogo.visibility = View.GONE
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addAddressViewModel.getAddress()

//        addressesAdapter = AddressesAdapter(MyAddressesFragment.mode)

//        stateList = resources.getStringArray(R.array.egypt_states)
        stateList = arrayOf(getString(R.string.state), *resources.getStringArray(R.array.egypt_states))

        val spinnerAdapter: ArrayAdapter<*> = ArrayAdapter<Any?>(
            requireContext(),
            android.R.layout.simple_spinner_item, stateList
        )

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.stateSpinner.adapter = spinnerAdapter
        binding.stateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                selectedState = stateList[position]
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }


        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                addAddressViewModel.myAddresses.collect { response ->
                    when (response) {

                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            response.data?.let {
                                val listSize = it["list_size"] as Long

                                for (i in 1 until listSize + 1) {
                                    myAddressList.add(
                                        AddressesModel(
                                            city = it.get("city_$i").toString(),
                                            localityOrStreet = it.get("localityOrStreet_$i").toString(),
                                            flatNumberOrBuildingName = it.get("flatNumberOrBuildingName_$i").toString(),
                                            pinCode = it.get("pinCode_$i").toString(),
                                            state = it.get("state_$i").toString(),
                                            landMark = it.get("landMark_$i").toString(),
                                            fullName = it.get("fullName_$i").toString(),
                                            mobileNumber = it.get("mobileNumber_$i").toString(),
                                            alternateMobileNumber = it.get("alternateMobileNumber_$i").toString(),
                                            selected = it.getBoolean("selected_$i") ?: false // احتفظ بالحالة من قاعدة البيانات
                                        )
                                    )

//                                    if (it.get("selected_$i") as Boolean) {
//                                        SELECTED_ADDRESS = (i - 1).toString().toInt()
//                                    }
                                }

                            }.also {

//                                addressesAdapter.asyncListDiffer.submitList(myAddressList)

                                if (args.intent.equals("update_address")) {

                                    Log.d(TAG, "onViewCreated: intent is ${args.intent}")

                                    addressesModel = myAddressList[args.addressPosition.toInt()]

                                    binding.apply {
                                        cityEditText.setText(addressesModel.city)
                                        localityOrStreet.setText(addressesModel.localityOrStreet)
                                        flatBuildEditText.setText(addressesModel.flatNumberOrBuildingName)
                                        pinCodeEditText.setText(addressesModel.pinCode)
//                                                stateList.
                                        for (i in stateList.indices) {
                                            if (stateList[i].equals(addressesModel.state)) {
                                                stateSpinner.setSelection(i)
                                            }
                                        }


                                        landmarkEditText.setText(addressesModel.landMark)
                                        nameEditText.setText(addressesModel.fullName)
                                        mobileNoEditText.setText(addressesModel.mobileNumber)
                                        alternateMobileNoEditText.setText(addressesModel.alternateMobileNumber)

                                        saveButton.text = getString(R.string.update_address)

                                        saveButton.setOnClickListener {
                                            loadingDialog?.show()
                                            addAddressViewModel.updateAddressInfo(
                                                args.addressPosition,
                                                binding.cityEditText.text.toString(),
                                                binding.localityOrStreet.text.toString(),
                                                binding.flatBuildEditText.text.toString(),
                                                binding.pinCodeEditText.text.toString(),
                                                binding.stateSpinner.selectedItem.toString(),
                                                binding.landmarkEditText.text.toString(),
                                                binding.nameEditText.text.toString(),
                                                binding.mobileNoEditText.text.toString(),
                                                binding.alternateMobileNoEditText.text.toString(),
                                            )
                                        }

                                    }

                                } else {

                                    Log.d(TAG, "onViewCreated: intent is ${args.intent}")

                                    binding.saveButton.text = getString(R.string.save_address)

                                    binding.saveButton.setOnClickListener { view ->

                                        if (TextUtils.isEmpty(binding.cityEditText.text)) {
                                            binding.cityEditText.error =
                                                getString(R.string.city_required)
                                        } else if (TextUtils.isEmpty(binding.localityOrStreet.text)) {
                                            binding.localityOrStreet.error =
                                                getString(R.string.locality_area_or_street_required)
                                        } else if (TextUtils.isEmpty(binding.flatBuildEditText.text)) {
                                            binding.flatBuildEditText.error =
                                                getString(R.string.flat_no_building_name_required)
                                        } else if (TextUtils.isEmpty(binding.pinCodeEditText.text)) {
                                            binding.pinCodeEditText.error =
                                                getString(R.string.pin_code_required)
                                        } else if (TextUtils.isEmpty(binding.nameEditText.text)) {
                                            binding.nameEditText.error =
                                                getString(R.string.name_required)
                                        } else if (TextUtils.isEmpty(binding.mobileNoEditText.text)) {
                                            binding.mobileNoEditText.error =
                                                getString(R.string.mobile_number_required)
                                        } else {
                                            loadingDialog?.show()



                                            addAddressViewModel.addNewAddress(
                                                binding.cityEditText.text.toString(),
                                                binding.localityOrStreet.text.toString(),
                                                binding.flatBuildEditText.text.toString(),
                                                binding.pinCodeEditText.text.toString(),
                                                binding.stateSpinner.selectedItem.toString(),
                                                binding.landmarkEditText.text.toString(),
                                                binding.nameEditText.text.toString(),
                                                binding.mobileNoEditText.text.toString(),
                                                binding.alternateMobileNoEditText.text.toString(),
                                            )

                                        }
                                    }
                                }
                            }
                        }

                        is Resource.Error -> {
                            Log.e(TAG, "myAddresses: ${response.message.toString()}")
                            Toast.makeText(
                                requireContext(),
                                response.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {}
                    }

                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                addAddressViewModel.addAddressState.collect { response ->

                    if (response is Resource.Success) {
                        loadingDialog?.dismiss()

                        if (args.intent.equals("add_new_address")) {
                            Log.d(TAG, "onViewCreated: intent is ${args.intent}")

                            findNavController().navigate(
                                AddAddressFragmentDirections.actionAddAddressFragmentToDeliveryFragment(
                                    cartItemModelList = args.cartItemModelList,
                                    cartListIds = args.cartListIds,
                                    fromCart = args.fromCart
                                )
                            )
                        } else {
//                            MyAddressesFragment.refreshItem(
//                                FirebaseDbQueries.selectedAddress,
//                                FirebaseDbQueries.addressesModelList.size() - 1
//                            )

//                            addressesAdapter.refreshItem(SELECTED_ADDRESS, myAddressList.size - 1)

                            Log.d(TAG, "onViewCreated: intent is ${args.intent}")

//                            findNavController(binding.root).popBackStack()
//                            findNavController().navigateUp()
                        }

//                        Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()

                    } else if (response is Resource.Error) {
                        loadingDialog?.dismiss()
                        Log.e(TAG, "addAddressState: ${response.message.toString()}")
                    }


                }

            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                addAddressViewModel.updateAddressState.collect { response ->
                    when (response) {
                        is Resource.Loading -> {
                            loadingDialog?.show()
                        }

                        is Resource.Success -> {
                            loadingDialog?.cancel()
                            Toast.makeText(requireContext(),
                                getString(R.string.address_updated), Toast.LENGTH_SHORT)
                                .show()
                            findNavController().navigateUp()
                        }

                        is Resource.Error -> {
                            loadingDialog?.cancel()
                            Log.e(TAG, "updateAddressState: ${response.message.toString()}")
                        }

                        else -> {}
                    }
                }
            }
        }


    }

    override fun onStop() {
        super.onStop()
        loadingDialog = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as MainActivity).apply {
            supportActionBar?.title = ""
            getToolBar().title = ""
//            actionBarLogo.visibility = View.VISIBLE
        }
        loadingDialog = null
        _binding = null
    }

}


// الكود الجديد


//@AndroidEntryPoint
//class AddAddressFragment : Fragment() {
//
//    companion object {
//        private const val TAG = "AddAddressFragment"
//        // لا حاجة لمتغيرات ستاتيكية هنا
//    }
//
//    private var _binding: FragmentAddAddressBinding? = null
//    private val binding get() = _binding!!
//
//    private lateinit var stateList: Array<String>
//    private var selectedState: String? = null
//
//    private val addAddressViewModel by viewModels<AddAddressViewModel>()
//
//    private lateinit var loadingDialog: AlertDialog // أو Dialog
//
//    private val args by navArgs<AddAddressFragmentArgs>()
//
//    // --- إزالة المتغيرات غير المستخدمة ---
//    // private val myAddressList = arrayListOf<AddressesModel>() // لا نستخدم القائمة هنا
//    // private lateinit var addressesAdapter: AddressesAdapter // لا نستخدم الأدابتر هنا
//
//    private var updateAddress = false // لتحديد هل نحن في وضع إضافة أم تعديل
//    private lateinit var addressesModel: AddressesModel // لتخزين بيانات العنوان عند التعديل
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentAddAddressBinding.inflate(inflater)
//        loadingDialog = Constants.setProgressDialog(requireContext()) // تهيئة الديالوج
//
//        // تحديث عنوان شريط الأدوات بناءً على النية (إضافة أو تعديل)
//        val intentAction = args.intent
//        if (intentAction == "update_address" || intentAction == "deliveryIntent") {
//            val titleResId =
//                if (intentAction == "update_address") R.string.update_address else R.string.add_address
//            (requireActivity() as? MainActivity)?.supportActionBar?.title = getString(titleResId)
//        }
//
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        setupSpinner() // إعداد قائمة المحافظات
//        observeAddAddressState() // مراقبة حالة إضافة عنوان جديد
//        observeUpdateAddressState() // مراقبة حالة تعديل عنوان
//
//        // التحقق إذا كنا في وضع التعديل
//        if (args.intent == "update_address") {
//            updateAddress = true
//            // نحتاج لجلب بيانات العنوان المحدد للتعديل
//            // الطريقة الحالية بجلب كل العناوين ليست مثالية، الأفضل تمرير العنوان أو ID الخاص به عبر Safe Args
//            // ولكن سنبقي على جلب الكل مؤقتًا ونفلتر
//            observeMyAddressesForUpdate() // ابدأ بمراقبة قائمة العناوين
//            addAddressViewModel.getAddress() // اطلب القائمة
//        } else {
//            // وضع الإضافة: إعداد زر الحفظ مباشرة
//            updateAddress = false
//            setupSaveButtonListener()
//        }
//    }
//
//    /**
//     * إعداد Spinner الخاص بالمحافظات.
//     */
//    private fun setupSpinner() {
//        stateList =
//            arrayOf(getString(R.string.state), *resources.getStringArray(R.array.egypt_states))
//        val spinnerAdapter = ArrayAdapter(
//            requireContext(),
//            android.R.layout.simple_spinner_item, stateList
//        )
//        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.stateSpinner.adapter = spinnerAdapter
//        binding.stateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                // لا تختر العنصر الأول (النص الافتراضي)
//                selectedState = if (position > 0) stateList[position] else null
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                selectedState = null
//            }
//        }
//    }
//
//    /**
//     * يراقب قائمة العناوين فقط عند الحاجة إليها في وضع التعديل.
//     */
//    private fun observeMyAddressesForUpdate() {
//        lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                addAddressViewModel.myAddresses.collect { response ->
//                    // نفذ فقط إذا كنا في وضع التعديل
//                    if (!updateAddress) return@collect
//
//                    when (response) {
//                        is Resource.Loading -> {} // يمكن عرض مؤشر تحميل إذا أردت
//                        is Resource.Success -> {
//                            val tempList = mutableListOf<AddressesModel>()
//                            response.data?.let { it ->
//                                val listSize = it["list_size"] as? Long ?: 0L
//                                for (i in 1..listSize) {
//                                    tempList.add(
//                                        AddressesModel( // تأكد من استخدام الحقول الصحيحة
//                                            city = it.getString("city_$i") ?: "",
//                                            localityOrStreet = it.getString("localityOrStreet_$i")
//                                                ?: "",
//                                            flatNumberOrBuildingName = it.getString("flatNumberOrBuildingName_$i")
//                                                ?: "",
//                                            pinCode = it.getString("pinCode_$i") ?: "",
//                                            state = it.getString("state_$i") ?: "",
//                                            landMark = it.getString("landMark_$i"),
//                                            fullName = it.getString("fullName_$i") ?: "",
//                                            mobileNumber = it.getString("mobileNumber_$i")
//                                                ?: "",
//                                            alternateMobileNumber = it.getString("alternateMobileNumber_$i"),
//                                            selected = it.getBoolean("selected_$i")
//                                                ?: false
//                                        )
//                                    )
//                                }
//                                // البحث عن العنوان المراد تحديثه
//                                val positionToUpdate = args.addressPosition.toInt()
//                                if (positionToUpdate >= 0 && positionToUpdate < tempList.size) {
//                                    addressesModel =
//                                        tempList[positionToUpdate] // الحصول على بيانات العنوان
//                                    populateFieldsForUpdate() // ملء الحقول بالبيانات
//                                    setupSaveButtonListener() // إعداد زر الحفظ (الآن سيقوم بالتحديث)
//                                } else {
//                                    // خطأ: الفهرس غير صالح
//                                    Log.e(
//                                        TAG,
//                                        "Invalid address position for update: $positionToUpdate"
//                                    )
//                                    Toast.makeText(
//                                        context,
//                                        "Error loading address for update.",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
////                                    findNavController().popBackStack() // العودة للخلف
//                                }
//                            } ?: run {
//                                Log.e(TAG, "Firestore data is null when fetching for update.")
//                                Toast.makeText(
//                                    context,
//                                    "Error loading address data.",
//                                    Toast.LENGTH_SHORT
//                                ).show()
////                                findNavController().popBackStack()
//                            }
//                        }
//
//                        is Resource.Error -> {
//                            Log.e(TAG, "Error fetching myAddresses for update: ${response.message}")
//                            Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
////                            findNavController().popBackStack() // العودة للخلف عند الخطأ
//                        }
//
//                        else -> {}
//                    }
//                }
//            }
//        }
//    }
//
//    /**
//     * ملء حقول الإدخال ببيانات العنوان الحالي عند التعديل.
//     */
//    private fun populateFieldsForUpdate() {
//        binding.apply {
//            cityEditText.setText(addressesModel.city)
//            localityOrStreet.setText(addressesModel.localityOrStreet)
//            flatBuildEditText.setText(addressesModel.flatNumberOrBuildingName)
//            pinCodeEditText.setText(addressesModel.pinCode)
//            val statePosition = stateList.indexOf(addressesModel.state)
//            if (statePosition >= 0) {
//                stateSpinner.setSelection(statePosition)
//            } else {
//                stateSpinner.setSelection(0) // الحالة الافتراضية
//            }
//            landmarkEditText.setText(addressesModel.landMark ?: "") // التعامل مع القيمة Nullable
//            nameEditText.setText(addressesModel.fullName)
//            mobileNoEditText.setText(addressesModel.mobileNumber)
//            alternateMobileNoEditText.setText(
//                addressesModel.alternateMobileNumber ?: ""
//            ) // التعامل مع القيمة Nullable
//            saveButton.text = getString(R.string.update_address) // تغيير نص الزر
//        }
//    }
//
//    /**
//     * إعداد مستمع النقر لزر الحفظ (إما إضافة أو تحديث).
//     */
//    private fun setupSaveButtonListener() {
//        binding.saveButton.setOnClickListener {
//            // --- التحقق الأساسي من الحقول ---
//            val city = binding.cityEditText.text.toString()
//            val locality = binding.localityOrStreet.text.toString()
//            val flatBuilding = binding.flatBuildEditText.text.toString()
//            val pinCode = binding.pinCodeEditText.text.toString()
//            val state = selectedState // تم تحديثه من Spinner listener
//            val fullName = binding.nameEditText.text.toString()
//            val mobile = binding.mobileNoEditText.text.toString()
//            val landmark = binding.landmarkEditText.text.toString().takeIf { it.isNotBlank() }
//            val altMobile =
//                binding.alternateMobileNoEditText.text.toString().takeIf { it.isNotBlank() }
//
//            var isValid = true
//            if (city.isEmpty()) {
//                binding.cityEditText.error = "Required"; isValid = false
//            }
//            if (locality.isEmpty()) {
//                binding.localityOrStreet.error = "Required"; isValid = false
//            }
//            if (flatBuilding.isEmpty()) {
//                binding.flatBuildEditText.error = "Required"; isValid = false
//            }
//            if (pinCode.isEmpty()) {
//                binding.pinCodeEditText.error = "Required"; isValid = false
//            }
//            if (state == null || state == getString(R.string.state)) {
//                Toast.makeText(context, "Please select state", Toast.LENGTH_SHORT).show(); isValid =
//                    false
//            }
//            if (fullName.isEmpty()) {
//                binding.nameEditText.error = "Required"; isValid = false
//            }
//            if (mobile.isEmpty()) {
//                binding.mobileNoEditText.error = "Required"; isValid = false
//            }
//            // يمكنك إضافة تحقق من طول رقم الموبايل أو الـ Pin code
//
//            if (!isValid) return@setOnClickListener // لا تتابع إذا كانت البيانات غير صالحة
//
//            // --- إظهار الديالوج وبدء العملية ---
//            loadingDialog.show()
//
//            if (updateAddress) {
//                // ** تحديث عنوان موجود **
//                addAddressViewModel.updateAddressInfo(
//                    position = args.addressPosition, // الفهرس المطلوب تحديثه
//                    city = city,
//                    localityOrStreet = locality,
//                    flatNumberOrBuildingName = flatBuilding,
//                    pinCode = pinCode,
//                    state = state!!, // state لن تكون null هنا بسبب التحقق أعلاه
//                    landMark = landmark,
//                    fullName = fullName,
//                    mobileNumber = mobile,
//                    alternateMobileNumber = altMobile
//                    // لا نمرر حالة selected هنا، يجب أن يعالجها الـ ViewModel/Repo
//                )
//            } else {
//                // ** إضافة عنوان جديد **
//                // تأكد من أن دالة addNewAddress في ViewModel لا تتوقع المعامل الأخير (SELECTED_ADDRESS)
//                addAddressViewModel.addNewAddress(
//                    city = city,
//                    localityOrStreet = locality,
//                    flatNumberOrBuildingName = flatBuilding,
//                    pinCode = pinCode,
//                    state = state!!,
//                    landMark = landmark,
//                    fullName = fullName,
//                    mobileNumber = mobile,
//                    alternateMobileNumber = altMobile
//                    // --- لا تمرر myAddressList أو SELECTED_ADDRESS ---
//                )
//            }
//        }
//    }
//
//    /**
//     * يراقب حالة عملية إضافة عنوان جديد.
//     */
//    private fun observeAddAddressState() {
////        lifecycleScope.launch {
////            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
////                addAddressViewModel.addAddressState.collect { response ->
////                    when (response) {
////                        is Resource.Loading -> { /* الديالوج ظاهر بالفعل */
////                        }
////
////                        is Resource.Success -> {
////                            loadingDialog.dismiss()
////                            Toast.makeText(context, "Address Saved", Toast.LENGTH_SHORT).show()
////                            // العودة دائمًا إلى الشاشة السابقة (MyAddressesFragment)
////                            // ***** إرسال إشارة لتحديث القائمة في الشاشة السابقة *****
////                            findNavController().previousBackStackEntry?.savedStateHandle?.set("addressListNeedsRefresh", true)
////                            Log.d(TAG, "Setting addressListNeedsRefresh = true")
////
////                            try {
////                                val navController = findNavController()
////                                val currentDestId = navController.currentDestination?.id
////                                val previousDestId = navController.previousBackStackEntry?.destination?.id
////
////                                // الحصول على اسم المورد (قد يفشل إذا لم يتم العثور على ID)
////                                val currentDestName = try { currentDestId?.let { resources.getResourceEntryName(it) } ?: "Unknown Current ID" } catch (e: Exception) { "ID $currentDestId" }
////                                val previousDestName = try { previousDestId?.let { resources.getResourceEntryName(it) } ?: "No Previous ID" } catch (e: Exception) { "ID $previousDestId" }
////
////                                // الحصول على الـ Label (قد يكون فارغًا)
////                                val currentLabel = navController.currentDestination?.label ?: ""
////                                val previousLabel = navController.previousBackStackEntry?.destination?.label ?: ""
////
////
////                                Log.d(TAG, "Nav Back Stack Info:")
////                                Log.d(TAG, " - Current Destination: ID=$currentDestName, Label='$currentLabel'")
////                                Log.d(TAG, " - Previous Destination: ID=$previousDestName, Label='$previousLabel'")
////
////                                // التحقق بشكل صريح باستخدام ID الخاص بـ MyAddressesFragment
////                                val previousIsMyAddresses = (previousDestId == R.id.myAddressesFragment)
////                                Log.d(TAG, " - Is Previous MyAddressesFragment? $previousIsMyAddresses") // <-- ركز على هذه القيمة
////
////                            } catch (e: Exception) {
////                                Log.e(TAG, "Error logging back stack info: $e")
////                            }
////
////
////
////                            if (args.intent == "deliveryIntent") {
////                            findNavController().navigate(
////                                AddAddressFragmentDirections.actionAddAddressFragmentToDeliveryFragment(
////                                    cartItemModelList = args.cartItemModelList,
////                                    cartListIds = args.cartListIds,
////                                    fromCart = args.fromCart,
////                                    totalAmount = args.totalAmount
////                                )
////                            )
////                        } else {
//////                            MyAddressesFragment.refreshItem(
//////                                FirebaseDbQueries.selectedAddress,
//////                                FirebaseDbQueries.addressesModelList.size() - 1
//////                            )
////
//////                            addressesAdapter.refreshItem(SELECTED_ADDRESS, myAddressList.size - 1)
////
////
////                                // العودة إلى MyAddressesFragment
////                                findNavController().popBackStack()
////                                addAddressViewModel.resetAddAddressState() // إعادة تعيين الحالة
////                        }
//////
////
////
////
////
////                        }
////
////                        is Resource.Error -> {
////                            loadingDialog.dismiss()
////                            Log.e(TAG, "addAddressState Error: ${response.message}")
////                            Toast.makeText(
////                                context,
////                                "Error: ${response.message}",
////                                Toast.LENGTH_SHORT
////                            ).show()
//////                            addAddressViewModel.resetAddAddressState() // إعادة تعيين الحالة
////                        }
////
////                        else -> { /* الحالة الأولية */
////                        }
////                    }
////                }
////            }
////        }
//
//        lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                addAddressViewModel.addAddressState.collect { response ->
//
//                    if (response is Resource.Success) {
//                        loadingDialog.dismiss()
//
//                        if (args.intent == "deliveryIntent") {
//                            findNavController(binding.root).navigate(
//                                AddAddressFragmentDirections.actionAddAddressFragmentToDeliveryFragment(
//                                    cartItemModelList = args.cartItemModelList,
//                                    cartListIds = args.cartListIds,
//                                    fromCart = args.fromCart,
//                                    totalAmount = args.totalAmount
//                                )
//                            )
//                        } else {
////                            MyAddressesFragment.refreshItem(
////                                FirebaseDbQueries.selectedAddress,
////                                FirebaseDbQueries.addressesModelList.size() - 1
////                            )
//
////                            addressesAdapter.refreshItem(SELECTED_ADDRESS, myAddressList.size - 1)
//
//
//                            findNavController(binding.root).popBackStack()
//                        }
//
////                        Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
//
//                    } else if (response is Resource.Error) {
//                        loadingDialog.dismiss()
//                        Log.e(TAG, "addAddressState: ${response.message.toString()}")
//                    }
//
//
//                }
//
//            }
//        }
//    }
//
//    /**
//     * يراقب حالة عملية تحديث عنوان موجود.
//     */
//    private fun observeUpdateAddressState() {
//        lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                addAddressViewModel.updateAddressState.collect { response ->
//                    when (response) {
//                        is Resource.Loading -> { /* الديالوج ظاهر بالفعل */
//                        }
//
//                        is Resource.Success -> {
//                            loadingDialog.dismiss()
//                            Toast.makeText(
//                                requireContext(),
//                                getString(R.string.address_updated),
//                                Toast.LENGTH_SHORT
//                            ).show()
//
//                            // ***** إرسال إشارة لتحديث القائمة في الشاشة السابقة *****
////                            findNavController().previousBackStackEntry?.savedStateHandle?.set("addressListNeedsRefresh", true)
//                            Log.d(TAG, "Setting addressListNeedsRefresh = true after update")
//
////                            findNavController().navigateUp() // العودة للشاشة السابقة
//                            addAddressViewModel.resetUpdateAddressState() // إعادة تعيين الحالة
//
//                        }
//
//                        is Resource.Error -> {
//                            loadingDialog.dismiss()
//                            Log.e(TAG, "updateAddressState Error: ${response.message}")
//                            Toast.makeText(
//                                context,
//                                "Error: ${response.message}",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                            addAddressViewModel.resetUpdateAddressState() // إعادة تعيين الحالة
//                        }
//
//                        else -> { /* الحالة الأولية */
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null // لمنع تسريب الذاكرة
//    }
//}