package com.blogspot.mido_mymall.ui.delivery

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.blogspot.mido_mymall.R
import com.blogspot.mido_mymall.data.CartDataManager
import com.blogspot.mido_mymall.databinding.CouponRedeemDialogBinding
import com.blogspot.mido_mymall.databinding.FragmentDeliveryBinding
import com.blogspot.mido_mymall.domain.models.AddressesModel
import com.blogspot.mido_mymall.domain.models.CartItemModel
import com.blogspot.mido_mymall.domain.models.CartSummary
import com.blogspot.mido_mymall.domain.models.Order
import com.blogspot.mido_mymall.domain.models.RewardModel
import com.blogspot.mido_mymall.ui.MainActivity
import com.blogspot.mido_mymall.ui.MainActivityViewModel
import com.blogspot.mido_mymall.ui.my_cart.CartAdapter
import com.blogspot.mido_mymall.ui.my_cart.DeliveryUtil
import com.blogspot.mido_mymall.ui.my_rewards.MyRewardsAdapter
import com.blogspot.mido_mymall.ui.my_rewards.MyRewardsViewModel
import com.blogspot.mido_mymall.ui.product_details.ProductDetailsFragment
import com.blogspot.mido_mymall.util.Constants
import com.blogspot.mido_mymall.util.Constants.SELECT_ADDRESS_MODE
import com.blogspot.mido_mymall.util.Resource
import com.blogspot.mido_mymall.util.safeParseDouble
import com.google.android.material.button.MaterialButton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Locale
import kotlin.math.roundToLong

@AndroidEntryPoint
class DeliveryFragment : Fragment(), MenuProvider, DeliveryUtil {

    companion object {

        private const val TAG = "DeliveryFragment"

        // الحالة الساكنة ليست مثالية، الأفضل استخدام ViewModel
        @JvmStatic
        var getQtyIds = true
    }

    private var _binding: FragmentDeliveryBinding? = null
    private val binding get() = _binding!!

    private var cartAdapter: CartAdapter?=null

    // قوائم محلية لتخزين البيانات من الـ Arguments
    private val cartItemModelList = arrayListOf<CartItemModel>()
    private var myCartListIds = arrayListOf<String>()

    private val args by navArgs<DeliveryFragmentArgs>()

    private val deliveryViewModel by viewModels<DeliveryViewModel>()

    private val myAddress = arrayListOf<AddressesModel>()

    private val mainActivityViewModel by activityViewModels<MainActivityViewModel>()

//    private var allProductsAvailable = true

    private var paymentSuccessResponse = false

    private lateinit var firestore: FirebaseFirestore

    private val myRewardsViewModel by viewModels<MyRewardsViewModel>()

    private var fullAddrress: String = ""
    private var fullName: String = ""
    private var pinCode: String = ""


    private var rewardModelList = arrayListOf<RewardModel>()

    private var orderId = ""

    private var currentTotalAmount: Double = 0.0 // استخدم Double للتوافق مع cartSummary

    private val cartDataManager by lazy { CartDataManager() }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeliveryBinding.inflate(inflater)

        cartAdapter = CartAdapter(false, deliveryUtil = this, isDeliveryFragment = true)
        firestore = FirebaseFirestore.getInstance()

        getQtyIds = true

        // تهيئة الإجمالي الحالي من الـ Arguments
//        currentTotalAmount = args.totalAmount.toDouble() // حول إلى Double هنا

        Log.d(TAG, "onCreateView: ${binding.orderConfirmationLayout.isVisible}")
        if (binding.orderConfirmationLayout.isVisible) {
            binding.orderConfirmationLayout.visibility = View.GONE
        }

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.CREATED)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ***** أضف هذا الكود للطباعة في البداية *****
//        Log.d(TAG, "onViewCreated: START")
//        Log.d(TAG, "onViewCreated: args.cartItemModelList is null? ${args.cartItemModelList == null}")
//        Log.d(TAG, "onViewCreated: args.cartItemModelList size: ${args.cartItemModelList?.size}")
//        Log.d(TAG, "onViewCreated: args.cartListIds size: ${args.cartListIds?.size}")
//        Log.d(TAG, "onViewCreated: args.totalAmount: ${args.totalAmount}")
//        Log.d(TAG, "onViewCreated: args.fromCart: ${args.fromCart}")

        // ***** إعداد مستمع لنتيجة التنقل *****

        // ***** تعديل مستمع نتيجة التنقل لاستقبال AddressesModel *****
        val navController = findNavController()
        // استمع للمفتاح الجديد وتوقع نوع AddressesModel
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<AddressesModel>("selectedAddressObject") // <-- تغيير النوع والمفتاح
            ?.observe(viewLifecycleOwner) { selectedAddress ->
                // selectedAddress هو الكائن الذي تم إرساله

                if (selectedAddress != null) {
                    Log.d(
                        TAG,
                        "Received navigation result: selectedAddressObject = $selectedAddress"
                    )

                    // ***** تحديث الواجهة والمتغيرات مباشرة من الكائن المستلم *****
                    binding.shippingDetailsLayout.fullNameTV.text = selectedAddress.fullName
//                    fullName = selectedAddress.fullName // تحديث المتغير المحلي
//                    fullAddrress = (selectedAddress.flatNumberOrBuildingName + " " +
//                            selectedAddress.localityOrStreet + " " +
//                            selectedAddress.landMark.toString() + " " +
//                            selectedAddress.city + " " + selectedAddress.state)

                    fullAddrress = formatAddressString(selectedAddress)


                    binding.shippingDetailsLayout.fullAddressTV.text = fullAddrress
                    binding.shippingDetailsLayout.pinCodeTV.text = selectedAddress.pinCode
                    pinCode = selectedAddress.pinCode // تحديث المتغير المحلي

                    // مسح النتيجة
                    navController.currentBackStackEntry?.savedStateHandle?.remove<AddressesModel>("selectedAddressObject") // <-- تغيير النوع والمفتاح
                } else {
                    Log.w(TAG, "Received null selectedAddressObject.")
                }
            }
        // ------------------------------------
        // ------------------------------------


        // --- جلب وعرض العنوان (الأولي أو الافتراضي) ---
        deliveryViewModel.getAddress() // اطلب تحميل العناوين
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                deliveryViewModel.myAddress.collect { response ->
                    when (response) {
                        is Resource.Loading -> { /* يمكنك إظهار مؤشر تحميل هنا */
                        }

                        is Resource.Success -> {
                            myAddress.clear() // امسح القائمة القديمة قبل إضافة الجديدة
                            var initialSelectedIndex: Long =
                                -1 // لتتبع الفهرس المحدد من قاعدة البيانات
                            response.data?.let { firestoreData ->
                                val listSize = firestoreData["list_size"] as Long
                                for (i in 1 until listSize + 1) {
                                    val isSelectedFromDB =
                                        firestoreData.get("selected_$i") as Boolean
                                    myAddress.add(
                                        AddressesModel(
                                            city = firestoreData.get("city_$i").toString(),
                                            localityOrStreet = firestoreData.get("localityOrStreet_$i")
                                                .toString(),
                                            flatNumberOrBuildingName = firestoreData.get("flatNumberOrBuildingName_$i")
                                                .toString(),
                                            pinCode = firestoreData.get("pinCode_$i").toString(),
                                            state = firestoreData.get("state_$i").toString(),
                                            landMark = firestoreData.get("landMark_$i").toString(),
                                            fullName = firestoreData.get("fullName_$i").toString(),
                                            mobileNumber = firestoreData.get("mobileNumber_$i")
                                                .toString(),
                                            alternateMobileNumber = firestoreData.get("alternateMobileNumber_$i")
                                                .toString(),
                                            selected = isSelectedFromDB // احتفظ بالحالة من قاعدة البيانات
                                        )
                                    )
                                    if (isSelectedFromDB) {
                                        initialSelectedIndex = (i - 1)
                                    }
                                }

                                // ***** 3. تعديل منطق العرض الأولي *****
                                // تحقق مما إذا كنا قد استقبلنا نتيجة للتو (لتجنب الكتابة فوقها)
                                val justReceivedResult =
                                    navController.currentBackStackEntry?.savedStateHandle?.contains(
                                        "selectedAddressIndex"
                                    ) == true

                                if (!justReceivedResult && myAddress.isNotEmpty()) {
                                    // لم نستقبل نتيجة، والقائمة ليست فارغة: اعرض العنوان المحدد من قاعدة البيانات أو الأول
                                    val indexToDisplay =
                                        if (initialSelectedIndex.toInt() != -1) initialSelectedIndex else 0 // اعرض المحدد أو الأول كافتراضي

                                    Log.d(
                                        TAG,
                                        "Displaying initial address from DB/Default at index: $indexToDisplay"
                                    )
                                    val addrToDisplay = myAddress[indexToDisplay.toInt()]

                                    // تحديث الواجهة والمتغيرات المحلية
                                    binding.shippingDetailsLayout.fullNameTV.text =
                                        addrToDisplay.fullName
                                    fullName = addrToDisplay.fullName
//                                    fullAddrress =
//                                        (addrToDisplay.flatNumberOrBuildingName + " " + // ... إلخ
//                                                addrToDisplay.localityOrStreet + " " +
//                                                addrToDisplay.landMark.toString() + " " +
//                                                addrToDisplay.city + " " + addrToDisplay.state)

                                    fullAddrress = formatAddressString(addrToDisplay)


                                    binding.shippingDetailsLayout.fullAddressTV.text = fullAddrress
                                    binding.shippingDetailsLayout.pinCodeTV.text =
                                        addrToDisplay.pinCode
                                    pinCode = addrToDisplay.pinCode

                                    // (اختياري) تحديث الستاتيكي
                                    // MyAddressesFragment.SELECTED_ADDRESS = indexToDisplay

                                } else if (justReceivedResult) {
                                    Log.d(
                                        TAG,
                                        "Skipping initial address display, NavResult observer will handle it."
                                    )
                                    // لا تفعل شيئًا هنا، المستمع للنتيجة سيتولى التحديث
                                } else { // myAddress is empty
                                    Log.w(TAG, "Address list fetched from DB is empty.")
                                    // مسح الحقول أو عرض نص بديل
//                                    binding.shippingDetailsLayout.fullNameTV.text = ""
//                                    binding.shippingDetailsLayout.fullAddressTV.text = ""
//                                    binding.shippingDetailsLayout.pinCodeTV.text = ""
//                                    fullName = ""
//                                    fullAddrress = ""
//                                    pinCode = ""

                                    displayEmptyAddress()
                                }

                            } ?: run { // في حالة كانت بيانات Firestore فارغة (null)
                                Log.w(TAG, "Firestore address data is null.")
                                // مسح الحقول
                                binding.shippingDetailsLayout.fullNameTV.text = ""
                                // ... مسح باقي الحقول والمتغيرات ...
//                                fullName = ""; fullAddrress = ""; pinCode = ""
                                displayEmptyAddress()
                            }
                            // يمكنك إخفاء مؤشر التحميل هنا إذا أظهرته في Loading

                        }

                        is Resource.Error -> {
                            Log.e(TAG, "myAddresses Error: ${response.message.toString()}")
                            Toast.makeText(
                                requireContext(),
                                response.message.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                            // مسح الحقول عند حدوث خطأ
                            binding.shippingDetailsLayout.fullNameTV.text = ""
                            // ... مسح باقي الحقول والمتغيرات ...
//                            fullName = ""; fullAddrress = ""; pinCode = ""
                            displayEmptyAddress()
                            // يمكنك إخفاء مؤشر التحميل هنا
                        }

                        else -> {}
                    }
                }
            }
        }


        // ------------------------------------


        if (!args.cartItemModelList.isNullOrEmpty()) {
            cartItemModelList.addAll(args.cartItemModelList!!)

            cartAdapter?.asyncListDiffer?.submitList(cartItemModelList)
            val cartSummary = cartDataManager.calculateTotalAmount(cartItemModelList)
            submitCartSummaryData(cartSummary)
            binding.cartSummaryLayout.visibility = View.VISIBLE

            // تحديث واجهة المستخدم بالملخص النهائي


        }

        if (!args.cartListIds.isNullOrEmpty()) {
            myCartListIds.addAll(args.cartListIds!!)
        }

        Log.d(TAG, "onViewCreated: ${cartItemModelList.size}")
        Log.d(TAG, "args cart size: ${args.cartItemModelList?.size}")


        binding.deliveryRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = cartAdapter

        }

//        binding.totalPriceTV.text = getString(R.string.egp_price, args.totalAmount.toString())

//
//        deliveryViewModel.getAddress()
//
//        lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                deliveryViewModel.myAddress.collect { response ->
//                    when (response) {
//
//                        is Resource.Loading -> {}
//                        is Resource.Success -> {
//                            response.data?.let {
//                                val listSize = it["list_size"] as Long
//
//                                for (i in 1 until listSize + 1) {
//
////                                    val altMobileNum = it.get("alternateMobileNumber_$i") as? String
//
//                                    myAddress.add(
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
//                                            selected = it.get("selected_$i") as Boolean
//                                        )
//                                    )
//
//                                    if (it.get("selected_$i") as Boolean) {
//                                        SELECTED_ADDRESS = (i - 1).toString().toInt()
//                                    }
//                                }
//
//                            }.also {
//
//                                if (myAddress.isNotEmpty()) {
//
//
//                                    binding.shippingDetailsLayout.fullNameTV.text =
//                                        myAddress[SELECTED_ADDRESS].fullName
//
//                                    fullName = myAddress[SELECTED_ADDRESS].fullName
//
//                                    fullAddrress =
//                                        (myAddress[SELECTED_ADDRESS].flatNumberOrBuildingName
//                                                + " " + myAddress[SELECTED_ADDRESS].localityOrStreet
//                                                ) + " " + myAddress[SELECTED_ADDRESS].landMark.toString() + " " +
//                                                myAddress[SELECTED_ADDRESS].city + " " + myAddress[SELECTED_ADDRESS].state
//
//                                    binding.shippingDetailsLayout.fullAddressTV.text =
//                                        fullAddrress
//
//
//
//                                    binding.shippingDetailsLayout.pinCodeTV.text =
//                                        myAddress[SELECTED_ADDRESS].pinCode
//
//                                    pinCode = myAddress[SELECTED_ADDRESS].pinCode
//                                }
//
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


        binding.shippingDetailsLayout.changeOrAddAddressBtn.setOnClickListener {
            getQtyIds = false
            findNavController().navigate(
                DeliveryFragmentDirections
                    .actionDeliveryFragmentToMyAddressesFragment(
                        SELECT_ADDRESS_MODE,
                        cartItemModelList.toTypedArray(),
                        args.cartListIds!!,
                    )
            )
        }

        binding.cardContinueButton.setOnClickListener {

//            val amount =  binding.totalPriceTV.text.toString().substring(4,
//                binding.totalPriceTV.text.toString().length - 2
//            )

            if (cartItemModelList.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_add_the_product_to_the_cart),
                    Toast.LENGTH_SHORT
                ).show()
            }

//            val amount = (currentTotalAmount * 100).toDouble()
            val amountInPiastres: Long = (currentTotalAmount * 100).roundToLong()

            val order = Order(amount = amountInPiastres, "EGP")
            Log.d(TAG, "إنشاء طلب Razorpay بالمبلغ: $amountInPiastres (بالقروش)")


            val userName = "rzp_test_itLLgXvQdarfeC"
            val password = Constants.RAZORPAY_API_PASSWORD

            val base = "$userName:$password"

            val authHeader = "Basic " + Base64.encodeToString(base.toByteArray(), Base64.NO_WRAP)


            deliveryViewModel.createOrder(authHeader, order)


        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                deliveryViewModel.orderResponse.collect { response ->
                    when (response) {
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            response.data?.let {
                                Log.d(TAG, "orderResponse: ID ${it.id}")

                                paymentSuccessResponse = true

                                getQtyIds = false
                                (requireActivity() as MainActivity)
                                    .startPayment(it.id, currentTotalAmount.toString())
                            }

                            deliveryViewModel.resetCreateOrderState()


                        }

                        is Resource.Error -> {
                            paymentSuccessResponse = false
                            Log.e(TAG, "orderResponse: ${response.message.toString()}")
                            Toast.makeText(
                                requireContext(),
                                response.message.toString(),
                                Toast.LENGTH_SHORT
                            )
                        }

                        else -> {}
                    }
                }
            }
        }



        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainActivityViewModel.paymentState.collect {
                    if (it) {

//
//                        for (i in 0 until cartItemModelList.size - 1) {
//                            cartItemModelList[i].qtyIDs?.forEach { qtyID ->
//
//
//                                deliveryViewModel.updateAvailableQuantitiesIds(
//                                    cartItemModelList[i].productId!!,
//                                    qtyID
//                                )
//
//                            }
//                        }

                        mainActivityViewModel.orderId.collect { orderId ->
                            binding.orderConfirmationLayout.visibility = View.VISIBLE
                            binding.orderIdTV.text = getString(R.string.order_id, orderId)

                            binding.continueShoppingButton.setOnClickListener {
                                findNavController().navigate(
                                    DeliveryFragmentDirections
                                        .actionDeliveryFragmentToHomeFragment()
                                )
                            }

                            if (orderId.isNotEmpty()) {

                                this@DeliveryFragment.orderId = orderId

                                deliveryViewModel.placeOrder(
                                    orderID = orderId,
                                    cartItemModelList = cartItemModelList,
                                    address = fullAddrress,
                                    fullName = fullName,
                                    pinCode = pinCode,
                                    paymentMethod = "RazorPay"
                                )
                            }

                            if (args.fromCart) {
                                deliveryViewModel.updateCartListAfterRemove(
                                    myCartListIds,
                                    cartItemModelList
                                )
                            }
                            mainActivityViewModel.resetPaymentStatus()

//                            binding.continueShoppingButton.setOnClickListener {
//                                findNavController().navigate(DeliveryFragmentDirections.actionDeliveryFragmentToHomeFragment())
//                            }
                        }

                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                deliveryViewModel.updateCartListAfterRemove.collect { response ->
                    if (response is Resource.Success) {

//                        Toast.makeText(requireContext(), "updated success", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "updateCartListAfterRemove: ${response.data}")
                        ProductDetailsFragment.ALREADY_ADDED_TO_CART_LIST = false

                    } else if (response is Resource.Error) {
                        Log.e(TAG, "updateCartListAfterRemove: ${response.message.toString()}")
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                deliveryViewModel.placeOrderStatus.collect { response ->
//                    Log.d(TAG, "placeOrderStatus: ${it.message.toString()}")

                    when (response) {

                        is Resource.Loading -> {
                            Log.d(TAG, "placeOrderStatus: response ${response.data}")

                        }

                        is Resource.Success -> {

                            Log.d(TAG, "placeOrderStatus: ${response.data}")
                            deliveryViewModel.resetPlaceOrderState()

                        }

                        is Resource.Error -> {
                            Log.e(TAG, "placeOrderStatus: ${response.message.toString()}")
                        }

                        else -> {}
                    }


                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainActivityViewModel.updateOrderStatus.collect { response ->
                    if (response is Resource.Success) {
                        Log.d(TAG, "updateOrderStatus: ${response.data}")
                    } else if (response is Resource.Error) {

                        Log.e(TAG, "updateOrderStatus: ${response.message.toString()}")
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.order_canceled), Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }
        }


    }


    override fun onStart() {
        super.onStart()
//        if (getQtyIds && cartItemModelList.isNotEmpty()) {
//            for (x in 0 until cartItemModelList.size - 1) {
//                for (y in 0 until cartItemModelList[x].productQuantity!!) {
//                    val quantityDocumentName = UUID.randomUUID().toString().substring(0, 20)
//
//                    val timestamp = HashMap<String, Any>()
//
//                    timestamp["time"] = FieldValue.serverTimestamp()
//
//                    firestore.collection("PRODUCTS").document(cartItemModelList[x].productId!!)
//                        .collection("QUANTITY").document(quantityDocumentName)
//                        .set(timestamp)
//                        .addOnSuccessListener {
//
//
//                            if (cartItemModelList[x].qtyIDs == null) {
//
//                                cartItemModelList[x].qtyIDs = arrayListOf<String>()
//
//                            }
//                            cartItemModelList[x].qtyIDs?.add(quantityDocumentName)
//
//                            Log.d(TAG, "onStart: qtyIds ${cartItemModelList.get(x).qtyIDs?.get(0)}")
//
//                            if (y + 1 == cartItemModelList[x].productQuantity) {
//                                firestore.collection("PRODUCTS")
//                                    .document(cartItemModelList[x].productId!!)
//                                    .collection("QUANTITY")
//                                    .orderBy("time", Query.Direction.ASCENDING)
//                                    .limit(cartItemModelList[x].stockQuantity!!)
//                                    .get()
//                                    .addOnSuccessListener { querySnapshot ->
//                                        val serverQuantities = arrayListOf<String>()
//
//                                        querySnapshot.documents.forEach {
//                                            serverQuantities.add(it.id)
//                                        }
//
//                                        cartItemModelList[x].qtyIDs?.forEach { qtyId ->
//                                            if (!serverQuantities.contains(qtyId)) {
//                                                Toast.makeText(
//                                                    requireContext(),
//                                                    "all products may not be available in required quantity",
//                                                    Toast.LENGTH_SHORT
//                                                ).show()
//                                                allProductsAvailable = false
//                                            }
//                                            if (serverQuantities.size >= cartItemModelList[x].stockQuantity!!) {
//                                                firestore.collection("PRODUCTS")
//                                                    .document(cartItemModelList[x].productId!!)
//                                                    .update_info("in_stock", false)
//
//
//                                            }
//                                        }
//
//                                    }.addOnFailureListener {
//                                        Log.e(TAG, "onStart: ${it.message.toString()}")
//                                    }
//                            }
//
//                        }.addOnFailureListener {
//                            Log.e(TAG, "onStart: ${it.message.toString()}")
//
//                        }
//
//                }
//            }
//        } else {
//            getQtyIds = true
//
//        }

    }

    override fun onStop() {
        super.onStop()
        if (getQtyIds) {
            for (i in 0 until cartItemModelList.size - 1) {
                if (!paymentSuccessResponse) {
                    cartItemModelList.get(i).qtyIDs!!.forEach { qtyId ->


                        try {
                            FirebaseFirestore.getInstance().collection("PRODUCTS")
                                .document(cartItemModelList[i].productId.toString())
                                .collection("QUANTITY")
                                .document(qtyId).delete().addOnSuccessListener {

                                    Log.d(TAG, "onStop: success")
                                    //                                if (cartItemModelList[i].qtyIDs != null) {
                                    //
                                    //                                    if (cartItemModelList[i].qtyIDs!!.isNotEmpty()) {
                                    //
                                    //                                        if (qtyId.equals(
                                    //                                                cartItemModelList.get(i).qtyIDs?.get(
                                    //                                                    cartItemModelList.get(i).qtyIDs!!.size - 1
                                    //                                                )
                                    //                                            )
                                    //                                        )
                                    //                                            cartItemModelList[i].qtyIDs?.clear()
                                    //                                    }
                                    //                                }

                                    Log.d(
                                        TAG,
                                        "onStop: cartItemModelList ${cartItemModelList.size}"
                                    )
                                    Log.d(
                                        TAG,
                                        "onStop: cartItemModelList is empty ${cartItemModelList.isEmpty()}"
                                    )


                                    //                                    firestore.collection("PRODUCTS")
                                    //                                        .document(cartItemModelList[i].productId!!)
                                    //                                        .collection("QUANTITY")
                                    //                                        .orderBy("time", Query.Direction.ASCENDING)
                                    //                                        .get()
                                    //                                        .addOnSuccessListener {
                                    //                                            if (it.documents.size < cartItemModelList[i].stockQuantity!!) {
                                    //                                                firestore.collection("PRODUCTS")
                                    //                                                    .document(cartItemModelList[i].productId!!)
                                    //                                                    .update_info("in_stock", true)
                                    //                                            }
                                    //
                                    //                                        }.addOnFailureListener {
                                    //
                                    //                                        }


                                    Log.d(TAG, "onStop: qtyId deleted")
                                }.addOnFailureListener {
                                    Log.e(TAG, "onStop: ${it.message.toString()}")
                                }
                        } catch (ex: Exception) {
                            Log.e(TAG, "onStop: ${ex.message.toString()}")
                            Log.e(TAG, "onStop: ${ex.cause.toString()}")
                        }
                    }
                } else {
                    cartItemModelList[i].qtyIDs!!.clear()
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        myCartListIds.clear()
        cartItemModelList.clear()
        rewardModelList.clear()
        cartAdapter = null
        _binding = null
    }

    @SuppressLint("UseCompatLoadingForDrawables", "StringFormatMatches")
    override fun couponRedemptionButtonClick(
        productOriginalPrice: String,
        productCuttedPrice: String,
        position: Int,
        cartCouponRedemptionLayout: ConstraintLayout,
        couponRedemptionButton: MaterialButton,
        couponRedemptionTV: TextView,
        couponsApplied: TextView,
        productPriceTV: TextView,
        totalItemsPriceTV: TextView?,
        totalAmountTV: TextView?,
        quantity: Long
    ) {


        val couponRedeemDialogBinding: CouponRedeemDialogBinding =
            CouponRedeemDialogBinding.inflate(layoutInflater, null, false)

        val checkCouponPriceDialog = Dialog(requireActivity())
        checkCouponPriceDialog.setContentView(couponRedeemDialogBinding.root)
        checkCouponPriceDialog.setCancelable(true)
        checkCouponPriceDialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        var lastSeen: Date? = null


        val rewardsAdapter = MyRewardsAdapter(
            cartItemModelList,
            position,
            true,
            couponRedeemDialogBinding.couponRecyclerView,
            couponRedeemDialogBinding.selectedCoupon,
            productOriginalPrice,
            couponRedeemDialogBinding.couponContainer.couponTitle,
            couponRedeemDialogBinding.couponContainer.couponValidity,
            couponRedeemDialogBinding.couponContainer.couponBody,
            couponRedeemDialogBinding.discountedPriceTV

        )

        myRewardsViewModel.getUserLastSeen()

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                myRewardsViewModel.userLastSeen.collect { response ->

                    when (response) {

                        is Resource.Loading -> {
//                                binding.progressBar.visibility = View.VISIBLE
                        }

                        is Resource.Success -> {
                            response.data?.let {
                                lastSeen = it.getDate("Last seen")
                            }.also {
                                myRewardsViewModel.getRewards()
                            }
                        }

                        is Resource.Error -> {
//                                binding.progressBar.visibility = View.GONE
                            Log.e(TAG, "userLastSeen: ${response.message.toString()}")
                        }

                        else -> {}
                    }


                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                myRewardsViewModel.rewardList.collect { response ->
                    when (response) {

                        is Resource.Loading -> {
//                                binding.progressBar.visibility = View.VISIBLE
                        }

                        is Resource.Success -> {

                            rewardModelList.clear().also {
                                response.data?.let { querySnapshot ->
                                    querySnapshot.documents.forEach {

                                        if (it["type"].toString().equals("Discount")
                                            && lastSeen?.before((it.getDate("validity") as Date)) == true
                                        ) {
                                            rewardModelList.add(
                                                RewardModel(
                                                    it.id,
                                                    it["type"].toString(),
                                                    it["lower_limit"].toString(),
                                                    it["upper_limit"].toString(),
                                                    it["percentage"].toString(),
                                                    it["body"].toString(),
                                                    it["validity"] as Timestamp,
                                                    it["already_used"] as Boolean

                                                )
                                            )
                                        } else if (it["type"].toString().equals("Flat EGP. * OFF")
                                            && lastSeen?.before((it.getDate("validity") as Date)) == true
                                        ) {
                                            rewardModelList.add(
                                                RewardModel(
                                                    it.id,
                                                    it["type"].toString(),
                                                    it["lower_limit"].toString(),
                                                    it["upper_limit"].toString(),
                                                    it["amount"].toString(),
                                                    it["body"].toString(),
                                                    it["validity"] as Timestamp,
                                                    it["already_used"] as Boolean

                                                )
                                            )
                                        }


                                    }
                                }.also {
//                                    binding.progressBar.visibility = View.GONE
//                                    myRewardsAdapter.asyncListDiffer.submitList(rewardModelList)
                                    rewardsAdapter.asyncListDiffer.submitList(rewardModelList)
                                    cartAdapter?.submitRewardList(rewardModelList)
                                }
                            }

                        }

                        is Resource.Error -> {
//                                binding.progressBar.visibility = View.GONE
                            Log.e(TAG, "rewardList: ${response.message.toString()}")
                        }

                        else -> {}
                    }
                }
            }
        }

        couponRedeemDialogBinding.applyOrRemoveContainer.visibility = View.VISIBLE
        couponRedeemDialogBinding.footerText.visibility = View.GONE

        couponRedeemDialogBinding.applyButton.setOnClickListener {

            var offerDiscountedAmount: Long = 0

            if (!cartItemModelList[position].selectedCouponId.isNullOrEmpty()) {
                rewardModelList.forEach {
                    if (it.couponId.equals(cartItemModelList[position].selectedCouponId)) {
                        it.alreadyUsed = true
                        cartCouponRedemptionLayout.background = resources.getDrawable(
                            R.drawable.reward_gradient_background,
                            requireContext().theme
                        )
                        couponRedemptionTV.text = rewardModelList[position].couponBody
                        couponRedemptionButton.text = getString(R.string.coupon)
                    }

                }.also {

                    offerDiscountedAmount =
                        productOriginalPrice.toLong() * rewardModelList[position].discountOrAmount.toLong() / 100
                    couponsApplied.visibility = View.VISIBLE



                    cartItemModelList[position].discountedPrice = offerDiscountedAmount.toString()

                    couponsApplied.text = "Coupon applied -${offerDiscountedAmount}"
                    productPriceTV.text =
                        "EGP. ${((productOriginalPrice.toLong() * quantity) - offerDiscountedAmount)} /-"
                    totalItemsPriceTV?.text =
                        "EGP. ${((productOriginalPrice.toLong() * quantity) - offerDiscountedAmount)} /-"
                    totalAmountTV?.text =
                        "EGP. ${((productOriginalPrice.toLong() * quantity) - offerDiscountedAmount)} /-"
                    binding.totalPriceTV.text =
                        "EGP. ${((productOriginalPrice.toLong() * quantity) - offerDiscountedAmount)} /-"
                    checkCouponPriceDialog.dismiss()
                }

            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.there_is_no_coupon_for_this_product), Toast.LENGTH_SHORT
                ).show()
            }
        }

        couponRedeemDialogBinding.removeButton.setOnClickListener {
            rewardModelList.forEach {
                if (it.couponId.equals(cartItemModelList[position].selectedCouponId)) {
                    it.alreadyUsed = false
                }
            }
            couponsApplied.visibility = View.INVISIBLE
            cartCouponRedemptionLayout.setBackgroundColor(
                resources.getColor(
                    R.color.couponRed,
                    requireContext().theme
                )
            )
            couponRedemptionTV.text = getString(R.string.apply_your_coupon_here)
            couponRedemptionButton.text = getString(R.string.redeem)

            cartItemModelList[position].selectedCouponId = null


//            productPriceTV.text = getString(R.string.egp_price,(productOriginalPrice.toDouble() * quantity))
//            totalItemsPriceTV?.text = getString(R.string.egp_price,(productOriginalPrice.toLong() * quantity))
//            totalAmountTV?.text = getString(R.string.egp_price,(productOriginalPrice.toLong() * quantity))
//            binding.totalPriceTV.text =  getString(R.string.egp_price,args.totalAmount.toString())

            // تأكد من أن دالة safeParseDouble معرفة ومتاحة في هذا الملف أو تم استيرادها

            productPriceTV.text = getString(
                R.string.egp_price,
                (safeParseDouble(productOriginalPrice) * quantity).toString()
            )

            totalItemsPriceTV?.text = getString(
                R.string.egp_price,
                (safeParseDouble(productOriginalPrice) * quantity).toString()
            )

            totalAmountTV?.text = getString(
                R.string.egp_price,
                (safeParseDouble(productOriginalPrice) * quantity).toString()
            )

            binding.totalPriceTV.text = getString(R.string.egp_price,currentTotalAmount.toString())



            checkCouponPriceDialog.dismiss()


        }



        couponRedeemDialogBinding.couponRecyclerView.layoutManager = linearLayoutManager
        couponRedeemDialogBinding.couponRecyclerView.adapter = rewardsAdapter

        couponRedeemDialogBinding.originalPriceTV.text =
            getString(R.string.egp_price, productOriginalPrice)



        couponRedeemDialogBinding.couponRedeemBtn.setOnClickListener { view3 ->
            if (couponRedeemDialogBinding.couponRecyclerView.visibility == View.GONE) {
                couponRedeemDialogBinding.couponRecyclerView.visibility = View.VISIBLE
                couponRedeemDialogBinding.selectedCoupon.visibility = View.GONE
            } else {
                couponRedeemDialogBinding.couponRecyclerView.visibility = View.GONE
                couponRedeemDialogBinding.selectedCoupon.visibility = View.VISIBLE
            }
        }
        checkCouponPriceDialog.show()

        checkCouponPriceDialog.setOnDismissListener {
            rewardModelList.clear()
        }
    }


// ... بقية كود DeliveryFragment ...

    // تطبيق دالة الـ Callback من DeliveryUtil
      override fun onQuantityChanged(position: Int, newQuantity: Long) {
        Log.d(TAG, "onQuantityChanged: Position $position, New Quantity: $newQuantity")

        // 1. الحصول على القائمة المحدثة من الـ Adapter
        //    Adapter هو المصدر الموثوق للبيانات الحالية بعد التعديل
        val currentList = cartAdapter?.asyncListDiffer?.currentList

        if (currentList.isNullOrEmpty() == false) {
            // 2. إعادة حساب ملخص السلة باستخدام القائمة المحدثة
            //    تأكد من أن CartItemModel في القائمة يعكس الكمية الجديدة (وهو ما تم في خطوة 2)
            val cartSummary = cartDataManager.calculateTotalAmount(currentList)

            // 3. تحديث متغير الإجمالي في الـ Fragment (إذا كنت تستخدمه لاحقًا)
//            this.totalAmount = cartSummary.totalAmount

            // 4. تحديث واجهة المستخدم الخاصة بملخص السلة
            submitCartSummaryData(cartSummary)

            Log.d(TAG, "Summary updated. New Total: ${cartSummary.totalAmount}")
            Log.d(TAG, "onQuantityChanged: ${cartSummary.savedAmount}")

        } else {
            // التعامل مع حالة أن القائمة أصبحت فارغة أو الـ adapter غير موجود
            Log.d(TAG, "List is empty or adapter is null after quantity change.")
            binding.cartSummaryLayout.visibility = View.GONE
            binding.totalAmountAndContinue.visibility = View.GONE
        }
    }

    private fun submitCartSummaryData(cartSummary: CartSummary) {

        binding.apply {


//            Log.d(TAG, "submitCartSummaryData: totalItems ${cartSummary.totalItems}")
//            Log.d(TAG, "submitCartSummaryData: totalItemsPrice ${cartSummary.totalItemsPrice}")
//            Log.d(TAG, "submitCartSummaryData: deliveryPrice ${cartSummary.deliveryPrice}")
//            Log.d(TAG, "submitCartSummaryData: totalAmount ${cartSummary.totalAmount}")
//            Log.d(TAG, "submitCartSummaryData: savedAmount ${cartSummary.savedAmount}")

            val formattedTotalItemsPrice = String.format(
                locale = Locale.ENGLISH,
                "%.2f",
                cartSummary.totalItemsPrice
            )


            totalItems.text = getString(R.string.price_number_items, cartSummary.totalItems)

            Log.d(TAG, "submitCartSummaryData formattedTotalItemsPrice is: $formattedTotalItemsPrice")
            cartSummaryTotalItemsPrice.text = getString(R.string.egp_price, formattedTotalItemsPrice)


            if (cartSummary.deliveryPrice == "Free") {
                cartSummaryDeliveryPrice.text = getString(R.string.free)
            } else {
                cartSummaryDeliveryPrice.text =
                    getString(R.string.egp_price, cartSummary.deliveryPrice)
            }

            Log.d(TAG, "submitCartSummaryData: total price in button ${cartSummary.totalAmount}")

            currentTotalAmount = cartSummary.totalAmount
            totalPriceTV.text = getString(R.string.egp_price, cartSummary.totalAmount.toString())

            cartSummaryTotalAmountTV.text =
                getString(R.string.egp_price, cartSummary.totalAmount.toString())

            val savedAmountValue = String.format(
                locale = Locale.ENGLISH,
                "%.2f",
                cartSummary.savedAmount
            )

            savedAmount.text = getString(R.string.saved_amount, savedAmountValue)
        }
    }

    /**
     * تنسيق كائن العنوان إلى سلسلة نصية قابلة للعرض مع معالجة الحقول الاختيارية واختيار الفاصلة.
     */
    private fun formatAddressString(address: AddressesModel): String {
        val components = mutableListOf<String>()

        // إضافة المكونات غير الفارغة فقط بالترتيب المرغوب
        address.flatNumberOrBuildingName.takeIf { it.isNotBlank() }?.let { components.add(it.trim()) }
        address.localityOrStreet.takeIf { it.isNotBlank() }?.let { components.add(it.trim()) }
        address.landMark?.takeIf { it.isNotBlank() }?.let { components.add(it.trim()) } // تجاهل العلامة الفارغة
        address.city.takeIf { it.isNotBlank() }?.let { components.add(it.trim()) }
        address.state.takeIf { it.isNotBlank() }?.let { components.add(it.trim()) }

        // تحديد الفاصلة (تخمين بسيط للغة بناءً على أول مكون)
        val separator = if (components.firstOrNull()?.firstOrNull()?.let { Character.UnicodeBlock.of(it) == Character.UnicodeBlock.ARABIC } == true) {
            "، " // فاصلة عربية
        } else {
            ", " // فاصلة إنجليزية (افتراضي)
        }

        // دمج المكونات مع الفاصل المحدد
        return components.joinToString(separator)
    }

    // دالة مساعدة لمسح حقول عرض العنوان
    private fun displayEmptyAddress() {
        binding.shippingDetailsLayout.fullNameTV.text = "" // أو نص مناسب مثل "لا يوجد عنوان"
        binding.shippingDetailsLayout.fullAddressTV.text = ""
        binding.shippingDetailsLayout.pinCodeTV.text = ""
        fullName = ""
        fullAddrress = ""
        pinCode = ""
    }

    override fun onResume() {
        super.onResume()
//        Log.d(TAG, "onResume: called")
//        Log.d(TAG, "onResume: cartItemModelList size ${cartItemModelList.size}")
//        Log.d(TAG, "onResume: asyncListDiffer size ${cartAdapter.asyncListDiffer.currentList.size}")
//        if(cartAdapter.asyncListDiffer.currentList.isEmpty()){
//            cartAdapter.asyncListDiffer.submitList(args.cartItemModelList?.toList())
//        }
        (requireActivity() as MainActivity).apply {
            supportActionBar?.title = getString(R.string.delivery)
            getToolBar().title = getString(R.string.delivery)
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        (requireActivity() as MainActivity).apply {
            supportActionBar?.title = getString(R.string.delivery)
            getToolBar().title = getString(R.string.delivery)
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == android.R.id.home) { // زر الرجوع في الشريط العلوي
            Log.d(TAG, "Up button pressed")
            return findNavController().navigateUp() // navigateUp() هو الأفضل هنا
        }

        return false // لم يتم التعامل مع الحدث
    }
    // --- نهاية MenuProvider ---

// ... بقية كود DeliveryFragment ...

}