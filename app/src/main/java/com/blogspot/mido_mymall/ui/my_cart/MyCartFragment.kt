package com.blogspot.mido_mymall.ui.my_cart

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.blogspot.mido_mymall.R
import com.blogspot.mido_mymall.data.CartDataManager
import com.blogspot.mido_mymall.databinding.FragmentMyCartBinding
import com.blogspot.mido_mymall.domain.models.CartItemModel
import com.blogspot.mido_mymall.domain.models.CartSummary
import com.blogspot.mido_mymall.domain.models.RewardModel
import com.blogspot.mido_mymall.ui.product_details.ProductDetailsFragment
import com.blogspot.mido_mymall.util.Constants
import com.blogspot.mido_mymall.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.util.Locale

private const val TAG = "MyCartFragment"

@AndroidEntryPoint
class MyCartFragment : Fragment(), MyCartUtil {

    private var _binding: FragmentMyCartBinding? = null
    private val binding get() = _binding!!

    private var cartAdapter: CartAdapter? = null

    private var myCartListIds = arrayListOf<String>()
    private var cartItemModelList = arrayListOf<CartItemModel>()

    private val myCartViewModel by viewModels<MyCartViewModel>()

    private var listSize: Long = 0

//    private var productId: String? = null

//    private val myAddressesList = arrayListOf<AddressesModel>()

    private var loadingDialog: Dialog? = null

    private val rewardModelList = arrayListOf<RewardModel>()

//    private var fromCart = true

    private var totalAmount = 0.0

//    private lateinit var cartSummary: CartSummary

    private val cartDataManager by lazy { CartDataManager() }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyCartBinding.inflate(inflater)

        cartAdapter = CartAdapter(true, this, isDeliveryFragment = false)

        loadingDialog = Constants.setProgressDialog(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cartItemsRecyclerView.isNestedScrollingEnabled = false


//        cartItemModelList.add(CartItemModel(1,"Price (3 items)","Rs.16999","Free","Rs.5999/-"))

        if (FirebaseAuth.getInstance().currentUser != null) {
            myCartViewModel.getMyCartListIds()
        } else {
            binding.emptyCartIV.visibility = View.VISIBLE
            Constants.signInSignUpDialog(
                requireContext(),
                R.id.nav_my_cart,
                layoutInflater,
                requireView()
            )
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                myCartViewModel.myCartListIds.collect { response ->
                    when (response) {

                        is Resource.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE

                        }

                        is Resource.Success -> {
                            listSize = response.data?.get("list_size") as Long

                            if (listSize == 0L) {

                                binding.apply {
                                    cartItemsRecyclerView.visibility = View.GONE
                                    progressBar.visibility = View.GONE
                                    emptyCartIV.visibility = View.VISIBLE

                                }
                            } else {
                                binding.apply {
                                    cartItemsRecyclerView.visibility = View.VISIBLE
                                    emptyCartIV.visibility = View.GONE
                                }
                            }

                            for (i in 0 until listSize) {
                                myCartListIds.add(response.data["product_id_$i"].toString())

//                                productId = response.data["product_id_$i"].toString()
                            }

                            val deferredResults = myCartListIds.map { productId ->

                                async(Dispatchers.IO) {
//                                    myCartViewModel.loadMyCartList(productId)
                                    myCartViewModel.loadMyCartList(productId)
                                }

                            }


                            val results = deferredResults.awaitAll()

                            // ---- بداية التغيير ----
                            // متغير لتتبع ما إذا كانت العملية ناجحة لجميع العناصر
                            var allItemsLoadedSuccessfully = true

                            // قم بتجميع العناصر أولاً
                            for (result: Resource<DocumentSnapshot> in results) {
                                when (result) {
                                    is Resource.Success -> {
                                        val documentSnapshot = result.data!!
                                        cartItemModelList.add(
                                            CartItemModel(
                                                // ... بيانات المنتج ...
                                                productId = documentSnapshot.id,
                                                productImage = documentSnapshot["product_image_1"].toString(),
                                                productName = documentSnapshot["product_name"].toString(),
                                                freeCoupons = documentSnapshot["free_coupons"] as Long,
                                                productPrice = documentSnapshot["product_price"].toString(),
                                                cuttedPrice = documentSnapshot["cutted_price"].toString(),
                                                productQuantity = 1, // قيمة أولية، قد تتغير لاحقًا
                                                maxQuantity = documentSnapshot["max_quantity"] as Long,
                                                stockQuantity = documentSnapshot["stock_quantity"] as Long,
                                                offersApply = documentSnapshot["offers_applied"] as Long,
                                                couponsApplied = 0,
                                                inStock = documentSnapshot["in_stock"] as Boolean,
                                                qtyIDs = arrayListOf(),
                                                selectedCouponId = null,
                                                discountedPrice = null,
                                                productRating = 0
                                            )
                                        )
                                    }

                                    is Resource.Error -> {
                                        allItemsLoadedSuccessfully =
                                            false // حدث خطأ في تحميل أحد العناصر
                                        Log.e(
                                            TAG,
                                            "Error loading cart item details: ${result.message}"
                                        )
                                        // يمكنك إظهار رسالة خطأ للمستخدم هنا إذا أردت
                                    }

                                    else -> { /* تجاهل حالات أخرى مثل Loading هنا */
                                    }
                                }
                            }

                            // الآن، بعد انتهاء الحلقة واكتمال القائمة (إذا لم تحدث أخطاء)
                            // قم بالحساب والتحديث مرة واحدة فقط
                            if (cartItemModelList.isNotEmpty() && allItemsLoadedSuccessfully) {

                                val cartSummary =
                                    cartDataManager.calculateTotalAmount(cartItemModelList)

                                this@MyCartFragment.totalAmount = cartSummary.totalAmount

                                // تحديث الـ Adapter بالقائمة الكاملة

                                cartAdapter?.asyncListDiffer?.submitList(cartItemModelList.toList()) // مرر نسخة من القائمة


                                // تحديث واجهة المستخدم بالملخص النهائي
                                binding.apply {
                                    cartSummaryLayout.visibility = View.VISIBLE
                                    totalAmountAndContinue.visibility = View.VISIBLE
                                    submitCartSummaryData(cartSummary)
                                    progressBar.visibility = View.GONE // إخفاء مؤشر التحميل النهائي
                                }
                            } else if (cartItemModelList.isEmpty() && listSize > 0 && !allItemsLoadedSuccessfully) {
                                // حالة خاصة: فشل تحميل كل العناصر ولم تتم إضافة أي شيء للقائمة
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(
                                    requireContext(),
                                    "Failed to load cart details",
                                    Toast.LENGTH_SHORT
                                ).show()
                                // قد تحتاج لإظهار حالة خطأ أو زر إعادة محاولة هنا
                            } else if (cartItemModelList.isEmpty() && listSize == 0L) {
                                // حالة السلة فارغة أصلًا (تم التعامل معها سابقًا، لكن للتأكيد)
                                binding.apply {
                                    cartItemsRecyclerView.visibility = View.GONE
                                    progressBar.visibility = View.GONE
                                    emptyCartIV.visibility = View.VISIBLE
                                    cartSummaryLayout.visibility = View.GONE // تأكد من إخفاء الملخص
                                    totalAmountAndContinue.visibility =
                                        View.GONE // تأكد من إخفاء زر المتابعة
                                }
                            }
                            // ---- نهاية التغيير ----

// ... بقية الكود في Resource.Success للـ myCartListIds ...

                        }

                        is Resource.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Log.e(
                                TAG,
                                "onViewCreated: ${response.message.toString()}"
                            )
                        }

                        else -> {}
                    }
                }
            }
        }

//        lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                myCartViewModel.cartList.collect {
//                    if (it is Resource.Success) {
//
//                        if (it.data == null || it.data.data.isNullOrEmpty()) {
//                            binding.apply {
//                                cartItemsRecyclerView.visibility = View.GONE
//                                totalAmountAndContinue.visibility = View.GONE
//                                cartSummaryLayout.visibility = View.GONE
//                                emptyCartIV.visibility = View.VISIBLE
//                            }
//                        }
////                        var index = 0
//
//                        it.data?.let { documentSnapshot ->
//
//
////                            if (myCartListIds.size >= 2) {
////                                index = myCartListIds.size - 2
////                            }
//
////                            cartItemModelList.clear()
//
//
////                            Log.d(TAG, "onViewCreated: product id $productId")
//
//                            cartItemModelList.add(
//                                CartItemModel(
////                                    type = CartItemModel.CART_ITEM,
//                                    productId = productId!!,
//                                    productImage = documentSnapshot["product_image_1"].toString(),
//                                    productName = documentSnapshot["product_name"].toString(),
//                                    freeCoupons = documentSnapshot["free_coupons"] as Long,
//                                    productPrice = documentSnapshot["product_price"].toString(),
//                                    cuttedPrice = documentSnapshot["cutted_price"].toString(),
//                                    productQuantity = 1,
//                                    maxQuantity = documentSnapshot["max_quantity"] as Long,
//                                    stockQuantity = documentSnapshot["stock_quantity"] as Long,
//                                    offersApply = documentSnapshot["offers_applied"] as Long,
//                                    couponsApplied = 0,
//                                    inStock = documentSnapshot["in_stock"] as Boolean,
//                                    qtyIDs = arrayListOf(),
//                                    selectedCouponId = null,
//                                    discountedPrice = null
//                                )
//                            ).also {
//
//                                index++
//
////                                val totalAmountCartItem = CartItemModel(CartItemModel.TOTAL_AMOUNT)
////
////
////                                if (myCartListIds.size > 0 && !cartItemModelList.contains(
////                                        totalAmountCartItem
////                                    )
////                                ) {
////                                    cartItemModelList.add(
////                                        totalAmountCartItem
////                                    )
////                                }
//
//
//                                val cartSummary =
//                                    cartDataManager.calculateTotalAmount(cartItemModelList)
//
//                                this@MyCartFragment.totalAmount = cartSummary.totalAmount
//                                cartAdapter.asyncListDiffer.submitList(cartItemModelList)
//
//                                binding.apply {
//                                    cartSummaryLayout.visibility = View.VISIBLE
//                                    totalAmountAndContinue.visibility = View.VISIBLE
//
//                                    submitCartSummaryData(cartSummary)
////                                        val totalAmountParent =
////                                            cartTotalAmount?.parent?.parent as? LinearLayout
//
//
//                                }
//
//                            }
//                        }
//
//                    } else if (it is Resource.Error) {
//                        Log.e(TAG, "Resource.Error: ${it.message.toString()}")
//                    }
//                }
//            }
//        }


        binding.cartItemsRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = cartAdapter

        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                myCartViewModel.removeCartListState.collect {

                    if (it is Resource.Success) {
                        ProductDetailsFragment.ALREADY_ADDED_TO_CART_LIST = false


                    } else if (it is Resource.Error) {
                        Log.e(TAG, "onViewCreated: ${it.message.toString()}")
                    }

                }
            }
        }

        binding.cardContinueButton.setOnClickListener {

            myCartViewModel.getAddresses()

        }



        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                myCartViewModel.myAddresses.collect { response ->
                    when (response) {

                        is Resource.Loading -> {
//                            dialog?.show()
                        }

                        is Resource.Success -> {

                            loadingDialog?.cancel()

                            response.data?.let {
                                val listSize = it["list_size"] as Long

                                if (listSize == 0L) {
                                    findNavController().navigate(
                                        MyCartFragmentDirections
                                            .actionNavMyCartToAddAddressFragment(
                                                "add_new_address",
                                                cartListIds = myCartListIds.toTypedArray(),
                                                cartItemModelList = cartItemModelList.toTypedArray(),
                                                fromCart = true
                                            )
                                    )
                                } else {
                                    findNavController().navigate(
                                        MyCartFragmentDirections
                                            .actionNavMyCartToDeliveryFragment(
                                                cartListIds = myCartListIds.toTypedArray(),
                                                cartItemModelList = cartItemModelList.toTypedArray(),
                                                fromCart = true,
                                            )
                                    )
                                }


                            }
                        }

                        is Resource.Error -> {
                            loadingDialog?.cancel()
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

            Log.d(
                TAG,
                "submitCartSummaryData formattedTotalItemsPrice is: $formattedTotalItemsPrice"
            )

            cartSummaryTotalItemsPrice.text =
                getString(R.string.egp_price, formattedTotalItemsPrice)


            if (cartSummary.deliveryPrice == "Free") {
                cartSummaryDeliveryPrice.text = getString(R.string.free)
            } else {
                cartSummaryDeliveryPrice.text =
                    getString(R.string.egp_price, cartSummary.deliveryPrice)
            }

            Log.d(TAG, "submitCartSummaryData: total price in button ${cartSummary.totalAmount}")

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

    override fun deleteItem(position: Int) {

        if (cartItemModelList[position].selectedCouponId?.isNotEmpty() == true) {
            cartItemModelList[position].selectedCouponId = null
        }

        if (myCartListIds.isNotEmpty() && cartItemModelList.isNotEmpty()) {
            myCartViewModel.removeFromCartList(myCartListIds, position)

            val newList = cartAdapter?.asyncListDiffer?.currentList?.toMutableList()
            newList?.removeAt(position).also {

                cartAdapter?.asyncListDiffer?.submitList(newList)
                Log.d(TAG, "deleteItem: also called")

                if (newList != null) {
                    val cartSummary = cartDataManager.calculateTotalAmount(newList)
                    submitCartSummaryData(cartSummary)
                }

            }

            if (newList.isNullOrEmpty()) {
                binding.apply {
                    cartSummaryLayout.visibility = View.GONE
                    totalAmountAndContinue.visibility = View.GONE
                    emptyCartIV.visibility = View.VISIBLE
                }
            }


//            cartAdapter.notifyItemRemoved(position)

            Toast.makeText(
                requireContext(),
                getString(R.string.item_removed_from_cart_list), Toast.LENGTH_SHORT
            )
                .show()


//            binding.totalAmountAndContinue.visibility = View.GONE

//            cartAdapter.notifyDataSetChanged()


        }
    }

//    private fun recalculateTotalAmount() {
//
//        var totalItems = 0
//        var totalItemsPrice = 0
//        val deliveryPrice: String
//        val totalAmount: Int
//        var savedAmount = 0
//
//        cartItemModelList.forEach { cartItem ->
//
//            if (cartItem.type == CartItemModel.TOTAL_AMOUNT) {
//                cartItemModelList.remove(cartItem)
//            }
//
//            if (cartItem.type == CartItemModel.CART_ITEM) {
//
//                val quantity = cartItem.productQuantity
//
//                totalItems = (totalItemsPrice + quantity!!).toInt()
//
//                totalItemsPrice += if (cartItem.selectedCouponId.isNullOrEmpty()) {
//                    cartItem.productPrice?.toInt()!! * quantity.toInt()
//                } else {
//                    cartItem.discountedPrice?.toInt()!! * quantity.toInt()
//                }
//
//                if (cartItem.cuttedPrice?.isNotEmpty()!!) {
//                    savedAmount += (cartItem.cuttedPrice?.toInt()!! - cartItem.productPrice?.toInt()!!) * quantity.toInt()
//
//                    if (!cartItem.selectedCouponId.isNullOrEmpty()) {
//                        savedAmount += (cartItem.productPrice?.toInt()!! - cartItem.discountedPrice?.toInt()!!) * quantity.toInt()
//                    }
//
//                } else {
//                    if (cartItem.selectedCouponId?.isNotEmpty()!!) {
//                        savedAmount += (cartItem.productPrice?.toInt()!! - cartItem.discountedPrice?.toInt()!!) * quantity.toInt()
//                    }
//                }
//
//            }
//        }
//        if (totalItemsPrice > 500) {
//            deliveryPrice = "Free"
//            totalAmount = totalItemsPrice
//        } else {
//            deliveryPrice = "60"
//            totalAmount = totalItemsPrice + 60
//        }
//
//        cartItemModelList.add(
//            CartItemModel(
//                CartItemModel.TOTAL_AMOUNT,
//                totalItems,
//                totalItemsPrice,
//                deliveryPrice,
//                totalAmount,
//                savedAmount
//            )
//        )
//
//        cartAdapter.asyncListDiffer.submitList(cartItemModelList)
//    }


    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: cart size ${cartItemModelList.size}")
//        myCartListIds.clear()
        cartItemModelList.clear()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: called")
        Log.d(TAG, "onResume: cart size ${cartItemModelList.size}")
    }

    override fun getTotalAmount(totalAmount: Double) {
        this.totalAmount = totalAmount
        Log.d(TAG, "getTotalAmount: $totalAmount")
    }

    // داخل كلاس MyCartFragment
    override fun onQuantityChanged(position: Int, newQuantity: Long) {
        Log.d(TAG, "onQuantityChanged: Position $position, New Quantity: $newQuantity")

        // 1. الحصول على القائمة المحدثة من الـ Adapter
        //    Adapter هو المصدر الموثوق للبيانات الحالية بعد التعديل
        val currentList = cartAdapter?.asyncListDiffer?.currentList

        if (currentList != null && currentList.isNotEmpty()) {
            // 2. إعادة حساب ملخص السلة باستخدام القائمة المحدثة
            //    تأكد من أن CartItemModel في القائمة يعكس الكمية الجديدة (وهو ما تم في خطوة 2)
            val cartSummary = cartDataManager.calculateTotalAmount(currentList)

            // 3. تحديث متغير الإجمالي في الـ Fragment (إذا كنت تستخدمه لاحقًا)
            this.totalAmount = cartSummary.totalAmount

            // 4. تحديث واجهة المستخدم الخاصة بملخص السلة
            submitCartSummaryData(cartSummary)



            Log.d(TAG, "Summary updated. New Total: ${cartSummary.totalAmount}")

            Log.d(TAG, "onQuantityChanged: ${cartSummary.savedAmount}")


        } else {
            // التعامل مع حالة أن القائمة أصبحت فارغة أو الـ adapter غير موجود
            Log.d(TAG, "List is empty or adapter is null after quantity change.")
            binding.cartSummaryLayout.visibility = View.GONE
            binding.totalAmountAndContinue.visibility = View.GONE
            if (listSize > 0) { // إذا كانت القائمة الأصلية غير فارغة، قد يعني هذا خطأ ما
                // يمكنك إضافة منطق إضافي هنا إذا لزم الأمر
            } else { // إذا كانت القائمة فارغة أصلًا
                binding.emptyCartIV.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        myCartListIds.clear()
        cartItemModelList.clear()
        cartAdapter = null
        rewardModelList.clear()
        _binding = null
    }
}