package com.blogspot.mido_mymall.data.repo

import android.util.Log
import com.blogspot.mido_mymall.domain.repo.MyOrdersRepo
import com.blogspot.mido_mymall.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val TAG = "MyOrdersRepoImpl"

class MyOrdersRepoImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : MyOrdersRepo {


    // افترض أن اسم حقل الطابع الزمني لديك هو "orderDate" أو "order_timestamp"
// استبدل "order_timestamp" بالاسم الفعلي للحقل لديك
    companion object{
        const val ORDER_TIMESTAMP_FIELD = "ORDER DATE"
        private const val TAG = "MyOrdersRepoImpl"
    }

//    override suspend fun getAllOrders(): Resource<QuerySnapshot> {
//        return try {
//            Log.d(TAG, "جاري جلب الطلبات مرتبة تنازليًا...")
//            val result = firestore.collection("ORDERS")
//                // --- >> الإضافة: الترتيب حسب الطابع الزمني تنازليًا << ---
//                .orderBy(ORDER_TIMESTAMP_FIELD, Query.Direction.DESCENDING)
//                .get()
//                .await()
//            Log.d(TAG, "تم جلب ${result.size()} طلب بنجاح.")
//            Resource.Success(result)
//
//        } catch (throwable: Throwable) {
//            Log.e(TAG, "خطأ في جلب الطلبات: ${throwable.message}", throwable)
//            currentCoroutineContext().ensureActive()
//            Resource.Error(throwable.message ?: "خطأ غير معروف في جلب الطلبات")
//        }
//    }

    override suspend fun getRatings(): Flow<Resource<DocumentSnapshot>> {
        val result = MutableStateFlow<Resource<DocumentSnapshot>>(Resource.Ideal())

        firestore.collection("USERS")
            .document(firebaseAuth.currentUser?.uid!!)
            .collection("USER_DATA")
            .document("MY_RATINGS").get()
            .addOnSuccessListener {
                result.value = Resource.Success(it)
            }.addOnFailureListener {
                result.value = Resource.Error(it.message.toString())
            }

        return result
    }

//    override suspend fun getLastOrder(): Flow<Resource<DocumentSnapshot>> = flow {
//        emit(Resource.Loading())
//
//        val currentUserID = firebaseAuth.currentUser?.uid
//
//        if (currentUserID.isNullOrEmpty()) {
//            emit(Resource.Error("User ID is null or empty"))
//            return@flow
//        }
//
//        val lastOrderQuery = firestore.collection("ORDERS")
//            .whereEqualTo("USER ID", currentUserID)
//            .orderBy("ORDER DATE", Query.Direction.DESCENDING)
//            .limit(1)
//            .get()
//            .await()
//
//        val lastOrderDocument = lastOrderQuery.documents.firstOrNull()
//
//        Log.d(TAG, "getLastOrder: id ${lastOrderDocument?.id}")
//
//        if (lastOrderDocument != null) {
//            emit(Resource.Success(lastOrderDocument))
//        } else {
//            emit(Resource.Error("No order found for the current user"))
//        }
//    }

    // داخل دالة الـ Repository أو UseCase (مثل getUserOrders)

    override suspend fun getUserOrders(userId: String): Resource<QuerySnapshot> { // تأكد من تمرير userId
        return try {
            Log.d(TAG, "جاري جلب طلبات المستخدم $userId مرتبة...")
            val result = firestore.collection("ORDERS")
                // --- >> الفلترة حسب المستخدم الحالي << ---
                .whereEqualTo("USER ID", userId) // استخدم اسم الحقل كما هو في مثالك
                // --- >> الترتيب حسب تاريخ الطلب (الأحدث أولاً) << ---
                .orderBy("ORDER DATE", Query.Direction.DESCENDING) // استخدم اسم الحقل كما هو في مثالك
                .get()
                .await()
            Log.d(TAG, "تم جلب ${result.size()} طلب للمستخدم $userId.")
            Resource.Success(result)
        } catch (throwable: Throwable) {
            // ... (معالجة الخطأ) ...
            Resource.Error(throwable.message ?: "Unknown error")
        }
    }
}