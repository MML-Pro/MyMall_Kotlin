package com.blogspot.mido_mymall.ui.my_orders

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.blogspot.mido_mymall.databinding.FragmentMyOrdersBinding
import com.blogspot.mido_mymall.domain.models.CartItemModel
import com.blogspot.mido_mymall.domain.models.MyOrderItemModel
import com.blogspot.mido_mymall.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@AndroidEntryPoint
class MyOrdersFragment : Fragment() {

    companion object {
        private const val TAG = "MyOrdersFragment"
    }

    private var _binding: FragmentMyOrdersBinding? = null
    private val binding get() = _binding!!

    private val myOrderAdapter = MyOrderAdapter()

    private val myOrderItemModelList = arrayListOf<MyOrderItemModel>()

    private val myOrdersViewModel by viewModels<MyOrdersViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyOrdersBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (FirebaseAuth.getInstance().currentUser != null) {
//            myOrdersViewModel.getMyOrders()
            myOrdersViewModel.getUserOrders(FirebaseAuth.getInstance().currentUser?.uid.toString())
        } else {
            binding.apply {
                progressBar.visibility = View.GONE
                myOrdersRecyclerView.visibility = View.GONE
                noOrdersFoundIV.visibility = View.VISIBLE
                emptyOrderTV.visibility = View.VISIBLE
            }
        }


//        lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
//                myOrdersViewModel.myOrders.collect { response ->
//                    when (response) {
//                        is Resource.Loading -> {
//                            binding.progressBar.visibility = View.VISIBLE
//                        }
//
//                        is Resource.Success -> {
//
//                            if (response.data == null) {
//
//                                binding.apply {
//                                    progressBar.visibility = View.GONE
//                                    myOrdersRecyclerView.visibility = View.GONE
//                                    noOrdersFoundIV.visibility = View.VISIBLE
//                                    emptyOrderTV.visibility = View.VISIBLE
//                                }
//                            } else {
//                                binding.apply {
//                                    myOrdersRecyclerView.visibility = View.VISIBLE
//                                    noOrdersFoundIV.visibility = View.GONE
//                                    emptyOrderTV.visibility = View.GONE
//                                }
//
//                                response.data.let {
//                                    val tempList =
//                                        mutableListOf<MyOrderItemModel>() // Create a temporary list
//
//                                    it.forEach { documentSnapshot ->
//
//
//                                        Log.d(TAG, "onViewCreated: ${documentSnapshot.id}")
//
//                                        val productList = mutableListOf<CartItemModel>()
//
//                                        val productsCollection = documentSnapshot
//                                            .reference.collection("Products")
//
//                                        productsCollection.get()
//                                            .addOnSuccessListener { productsQuerySnapshot ->
//
//
//                                                Log.d(TAG, "productsQuerySnapshot: ${productsQuerySnapshot.documents.size}")
//
//                                                productsQuerySnapshot.documents.forEach { productDocument ->
//                                                    val productName =
//                                                        productDocument["PRODUCT NAME"].toString()
//                                                    val cartItemModel = CartItemModel(
//                                                        productId = productDocument.get("PRODUCT ID")
//                                                            .toString(),
//                                                        productImage = productDocument["PRODUCT IMAGE"].toString(),
//                                                        productName = productName,
//                                                        freeCoupons = productDocument["FREE COUPONS"] as Long,
//                                                        productPrice = productDocument["PRODUCT PRICE"].toString(),
//                                                        cuttedPrice = productDocument["CUTTED PRICE"].toString(),
//                                                        productQuantity = productDocument["PRODUCT QUANTITY"] as Long,
//                                                        offersApply = productDocument["OFFERS APPLIED"] as Long,
//                                                        couponsApplied = productDocument["COUPONS APPLIED"] as Long,
//                                                        selectedCouponId = productDocument["COUPON ID"].toString(),
//                                                        discountedPrice = productDocument["DISCOUNTED PRICE"].toString(),
//                                                        productRating = 0
//                                                    )
//                                                    productList.add(cartItemModel)
//                                                }
//
//                                                val orderId = documentSnapshot.getString("ORDER ID")
//
//
//                                                val myOrderItemModel = MyOrderItemModel(
//                                                    productList = productList,
//                                                    orderStatus = documentSnapshot.getString("ORDER STATUS"),
//                                                    address = documentSnapshot.getString("ADDRESS"),
//                                                    orderDate = documentSnapshot.getDate("ORDER DATE"),
//                                                    packedDate = documentSnapshot.getDate("PACKED DATE"),
//                                                    shippedDate = documentSnapshot.getDate("SHIPPED DATE"),
//                                                    deliveredDate = documentSnapshot.getDate("DELIVERED DATE"),
//                                                    cancelledDate = documentSnapshot.getDate("CANCELLED DATE"),
//                                                    fullName = documentSnapshot.getString("FULL NAME"),
//                                                    orderID = orderId,
//                                                    paymentMethod = documentSnapshot.getString("PAYMENT METHOD"),
//                                                    pinCode = documentSnapshot.getString("PIN CODE"),
//                                                    userID = documentSnapshot.getString("USER ID")
//                                                )
//
//                                                Log.d(TAG, "order id: $orderId")
//
//
//                                                tempList.add(myOrderItemModel)
//
//                                                if (tempList.size == it.documents.size) {
//                                                    // All data is retrieved, update the list and submit
//                                                    myOrderItemModelList.clear()
//                                                    myOrderItemModelList.addAll(tempList)
//                                                    binding.progressBar.visibility = View.GONE
//                                                    myOrderAdapter.asyncListDiffer.submitList(
//                                                        myOrderItemModelList
//                                                    )
//
//                                                    myOrderAdapter.notifyDataSetChanged()
//                                                }
//                                            }
//                                            .addOnFailureListener { ex ->
//                                                binding.progressBar.visibility = View.GONE
//                                                Log.e(TAG, "Error fetching products: ${ex.message}")
//                                            }
//                                    }
//                                }
//                            }
//
//                        }
//
//                        is Resource.Error -> {
//                            binding.progressBar.visibility = View.GONE
//                            Log.e(TAG, "myOrders: ${response.message.toString()}")
//                        }
//
//                        else -> {
//                            binding.progressBar.visibility = View.GONE
//                        }
//                    }
//                }
//            }
//        }

//        lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
//                myOrdersViewModel.myOrders.collect { response ->
//                    when (response) {
//                        is Resource.Loading -> {
//                            binding.progressBar.visibility = View.VISIBLE
//                        }
//
//                        is Resource.Success -> {
//                            if (response.data == null) {
//                                // Handle empty response
//                                binding.apply {
//                                    progressBar.visibility = View.GONE
//                                    myOrdersRecyclerView.visibility = View.GONE
//                                    noOrdersFoundIV.visibility = View.VISIBLE
//                                    emptyOrderTV.visibility = View.VISIBLE
//                                }
//                            } else {
//                                binding.apply {
//                                    myOrdersRecyclerView.visibility = View.VISIBLE
//                                    noOrdersFoundIV.visibility = View.GONE
//                                    emptyOrderTV.visibility = View.GONE
//                                }
//
//                                response.data.let { orderList ->
//
//                                    val userOrdersDocs = arrayListOf<DocumentSnapshot>()
//
//                                    orderList.documents.forEach { orderDocument ->
//                                        val orderId = orderDocument.id
//
//                                        // Check if the order ID exists in the USER_ORDERS collection
//                                        val userOrderQuerySnapshot = FirebaseFirestore.getInstance()
//                                            .collection("USERS")
//                                            .document(FirebaseAuth.getInstance().currentUser?.uid!!)
//                                            .collection("USER_ORDERS")
//                                            .whereEqualTo("Order_ID", orderId)
//                                            .get()
//                                            .await()
//
//
//                                        if (userOrderQuerySnapshot.documents.isNotEmpty()) {
//                                            // Order ID exists in USER_ORDERS, add it to the tempList
//                                            userOrdersDocs.add(orderDocument)
//                                        }
//
//                                    }.also {
//                                        val tempList =
//                                            mutableListOf<MyOrderItemModel>() // Create a temporary list
//
//                                        if (userOrdersDocs.isEmpty()) {
//                                            binding.apply {
//                                                progressBar.visibility = View.GONE
//                                                myOrdersRecyclerView.visibility = View.GONE
//                                                noOrdersFoundIV.visibility = View.VISIBLE
//                                                emptyOrderTV.visibility = View.VISIBLE
//                                            }
////
//                                        } else {
//                                            userOrdersDocs.forEach { documentSnapshot ->
//
//
//                                                Log.d(TAG, "onViewCreated: ${documentSnapshot.id}")
//
//                                                val productList = mutableListOf<CartItemModel>()
//
//                                                val productsCollection = documentSnapshot
//                                                    .reference.collection("Products")
//
//                                                productsCollection.get()
//                                                    .addOnSuccessListener { productsQuerySnapshot ->
//
//
//                                                        Log.d(
//                                                            TAG,
//                                                            "productsQuerySnapshot: ${productsQuerySnapshot.documents.size}"
//                                                        )
//
//                                                        productsQuerySnapshot.documents.forEach { productDocument ->
//                                                            val productName =
//                                                                productDocument["PRODUCT NAME"].toString()
//                                                            val cartItemModel = CartItemModel(
//                                                                productId = productDocument.get("PRODUCT ID")
//                                                                    .toString(),
//                                                                productImage = productDocument["PRODUCT IMAGE"].toString(),
//                                                                productName = productName,
//                                                                freeCoupons = productDocument["FREE COUPONS"] as Long,
//                                                                productPrice = productDocument["PRODUCT PRICE"].toString(),
//                                                                cuttedPrice = productDocument["CUTTED PRICE"].toString(),
//                                                                productQuantity = productDocument["PRODUCT QUANTITY"] as Long,
//                                                                offersApply = productDocument["OFFERS APPLIED"] as Long,
//                                                                couponsApplied = productDocument["COUPONS APPLIED"] as Long,
//                                                                selectedCouponId = productDocument["COUPON ID"].toString(),
//                                                                discountedPrice = productDocument["DISCOUNTED PRICE"].toString(),
//                                                                productRating = 0
//                                                            )
//                                                            productList.add(cartItemModel)
//                                                        }
//
//                                                        val orderId =
//                                                            documentSnapshot.getString("ORDER ID")
//
//
//                                                        val myOrderItemModel = MyOrderItemModel(
//                                                            productList = productList,
//                                                            orderStatus = documentSnapshot.getString(
//                                                                "ORDER STATUS"
//                                                            ),
//                                                            address = documentSnapshot.getString("ADDRESS"),
//                                                            orderDate = documentSnapshot.getDate("ORDER DATE"),
//                                                            packedDate = documentSnapshot.getDate("PACKED DATE"),
//                                                            shippedDate = documentSnapshot.getDate("SHIPPED DATE"),
//                                                            deliveredDate = documentSnapshot.getDate(
//                                                                "DELIVERED DATE"
//                                                            ),
//                                                            cancelledDate = documentSnapshot.getDate(
//                                                                "CANCELLED DATE"
//                                                            ),
//                                                            fullName = documentSnapshot.getString("FULL NAME"),
//                                                            orderID = orderId,
//                                                            paymentMethod = documentSnapshot.getString(
//                                                                "PAYMENT METHOD"
//                                                            ),
//                                                            pinCode = documentSnapshot.getString("PIN CODE"),
//                                                            userID = documentSnapshot.getString("USER ID")
//                                                        )
//
//                                                        Log.d(TAG, "order id: $orderId")
//
//
//                                                        tempList.add(myOrderItemModel)
//
//                                                        if (tempList.size == userOrdersDocs.size) {
//                                                            // All data is retrieved, update the list and submit
//                                                            myOrderItemModelList.clear()
//                                                            myOrderItemModelList.addAll(tempList)
//                                                            binding.progressBar.visibility =
//                                                                View.GONE
//                                                            myOrderAdapter.asyncListDiffer.submitList(
//                                                                myOrderItemModelList
//                                                            )
//
//                                                            myOrderAdapter.notifyDataSetChanged()
//                                                        }
//                                                    }
//                                                    .addOnFailureListener { ex ->
//                                                        binding.progressBar.visibility = View.GONE
//                                                        Log.e(
//                                                            TAG,
//                                                            "Error fetching products: ${ex.message}"
//                                                        )
//                                                    }
//                                            }
//                                        }
//                                    }
//
//
//                                }
//                            }
//                        }
//
//                        is Resource.Error -> {
//                            binding.progressBar.visibility = View.GONE
//                            Log.e(TAG, "myOrders: ${response.message.toString()}")
//                        }
//
//                        else -> {
//                            binding.progressBar.visibility = View.GONE
//                        }
//                    }
//                }
//            }
//        }


        // افترض أن هذا الكود داخل onViewCreated في MyOrdersFragment أو ما شابه

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) { // استخدم STARTED أو CREATED حسب الحاجة
                myOrdersViewModel.userOrders.collect { response -> // افترض أن myOrders الآن يجلب طلبات المستخدم فقط مرتبة
                    when (response) {
                        is Resource.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            // إخفاء محتوى القائمة أو حالة الخطأ/الفارغ
                            binding.myOrdersRecyclerView.visibility = View.GONE
                            binding.noOrdersFoundIV.visibility = View.GONE
                            binding.emptyOrderTV.visibility = View.GONE
                        }

                        is Resource.Success -> {
                            binding.progressBar.visibility = View.GONE // إخفاء التحميل

                            // response.data هو QuerySnapshot يحتوي فقط طلبات المستخدم مرتبة
                            if (response.data == null || response.data.isEmpty) {
                                // عرض حالة عدم وجود طلبات
                                binding.myOrdersRecyclerView.visibility = View.GONE
                                binding.noOrdersFoundIV.visibility = View.VISIBLE
                                binding.emptyOrderTV.visibility = View.VISIBLE
                            } else {
                                // عرض القائمة وإخفاء حالة الفارغ
                                binding.myOrdersRecyclerView.visibility = View.VISIBLE
                                binding.noOrdersFoundIV.visibility = View.GONE
                                binding.emptyOrderTV.visibility = View.GONE

                                // --- بداية: معالجة وتحويل البيانات لعرضها (يمكن نقل هذا لـ ViewModel) ---
                                // استخدم كوروتين منفصل لتجنب حظر الـ UI أثناء جلب المنتجات لكل طلب
                                launch(Dispatchers.Main) { // ابدأ في Main ثم انتقل لـ IO عند الحاجة
                                    try {
                                        val userOrderDocuments = response.data.documents

                                        // قم بتحويل كل مستند طلب + جلب منتجاته بشكل غير متزامن
                                        val finalOrderList = userOrderDocuments.map { documentSnapshot ->
                                            // استخدم async للانتقال إلى IO لجلب المنتجات
                                            async(Dispatchers.IO) {
                                                try {
                                                    val orderId = documentSnapshot.id
                                                    // جلب المنتجات من المجموعة الفرعية Products
                                                    val productsSnapshot = documentSnapshot.reference.collection("Products").get().await()
                                                    val productList = productsSnapshot.documents.mapNotNull { productDoc ->
                                                        // تحويل آمن لبيانات المنتج إلى CartItemModel
                                                        CartItemModel(
                                                            productId = productDoc.getString("PRODUCT ID") ?: productDoc.id,
                                                            productImage = productDoc.getString("PRODUCT IMAGE") ?: "",
                                                            productName = productDoc.getString("PRODUCT NAME") ?: "",
                                                            freeCoupons = productDoc.getLong("FREE COUPONS") ?: 0L,
                                                            productPrice = productDoc.getString("PRODUCT PRICE") ?: "0.0",
                                                            cuttedPrice = productDoc.getString("CUTTED PRICE") ?: "",
                                                            productQuantity = productDoc.getLong("PRODUCT QUANTITY") ?: 1L,
                                                            // ... أكمل بقية الحقول بنفس الطريقة الآمنة ...
                                                            maxQuantity = productDoc.getLong("max_quantity"),
                                                            stockQuantity = productDoc.getLong("stock_quantity"),
                                                            offersApply = productDoc.getLong("OFFERS APPLIED") ?: 0L,
                                                            couponsApplied = productDoc.getLong("COUPONS APPLIED") ?: 0L,
                                                            inStock = productDoc.getBoolean("in_stock"),
                                                            qtyIDs = null,
                                                            selectedCouponId = productDoc.getString("COUPON ID"),
                                                            discountedPrice = productDoc.getString("DISCOUNTED PRICE"),
                                                            productRating = 0
                                                        )
                                                    }

                                                    // تحويل آمن لبيانات الطلب الرئيسية إلى MyOrderItemModel
                                                    MyOrderItemModel(
                                                        productList = ArrayList(productList),
                                                        orderStatus = documentSnapshot.getString("ORDER STATUS"),
                                                        address = documentSnapshot.getString("ADDRESS"),
                                                        orderDate = documentSnapshot.getTimestamp("ORDER DATE")?.toDate(),
                                                        packedDate = documentSnapshot.getTimestamp("PACKED DATE")?.toDate(),
                                                        shippedDate = documentSnapshot.getTimestamp("SHIPPED DATE")?.toDate(),
                                                        deliveredDate = documentSnapshot.getTimestamp("DELIVERED DATE")?.toDate(),
                                                        cancelledDate = documentSnapshot.getTimestamp("CANCELLED DATE")?.toDate(),
                                                        fullName = documentSnapshot.getString("FULL NAME"),
                                                        orderID = documentSnapshot.getString("ORDER ID") ?: orderId,
                                                        paymentMethod = documentSnapshot.getString("PAYMENT METHOD"),
                                                        pinCode = documentSnapshot.getString("PIN CODE"),
                                                        userID = documentSnapshot.getString("USER ID")
                                                        // أضف أي حقول أخرى تحتاجها MyOrderItemModel
                                                    )
                                                } catch (e: Exception) {
                                                    Log.e(TAG, "فشل في معالجة الطلب ${documentSnapshot.id}", e)
                                                    null // إرجاع null للعناصر التي تفشل
                                                }
                                            } // نهاية async
                                        }.awaitAll().filterNotNull() // انتظار كل العمليات غير المتزامنة وإزالة أي null ناتج عن خطأ

                                        Log.d(TAG, "القائمة النهائية للطلبات المراد عرضها: ${finalOrderList.size} عنصر")
                                        // تحديث الـ Adapter بالقائمة النهائية
                                        myOrderAdapter.asyncListDiffer.submitList(finalOrderList)

                                    } catch (e: Exception) {
                                        Log.e(TAG, "خطأ عام أثناء معالجة وتحويل الطلبات", e)
                                        // يمكنك عرض رسالة خطأ للمستخدم هنا
                                        Toast.makeText(context, "حدث خطأ أثناء عرض الطلبات", Toast.LENGTH_SHORT).show()
                                    }
                                } // نهاية launch
                                // --- نهاية: معالجة وتحويل البيانات ---
                            } // نهاية else (response.data ليس null)
                        } // نهاية Resource.Success

                        is Resource.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Log.e(TAG, "myOrdersViewModel Error: ${response.message.toString()}")
                            // عرض رسالة خطأ للمستخدم
                            Toast.makeText(context, "فشل تحميل الطلبات: ${response.message}", Toast.LENGTH_LONG).show()
                            // عرض حالة الخطأ أو الفارغ في الواجهة
                            binding.myOrdersRecyclerView.visibility = View.GONE
                            binding.noOrdersFoundIV.visibility = View.VISIBLE // أو صورة خطأ
                            binding.emptyOrderTV.visibility = View.VISIBLE // أو نص خطأ
                        }

                        else -> { // التعامل مع Ideal أو أي حالة أخرى
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                }
            }
        } // نهاية lifecycleScope.launch الأساسي


        binding.myOrdersRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = myOrderAdapter
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        myOrderItemModelList.clear()
        _binding = null
    }
}