package com.blogspot.mido_mymall.data.repo

import android.util.Log
import com.blogspot.mido_mymall.domain.repo.SearchRepo
import com.blogspot.mido_mymall.util.Resource
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val TAG = "SearchRepoImpl"

class SearchRepoImpl @Inject constructor(private val firestore: FirebaseFirestore) : SearchRepo {

//    override suspend fun searchForProducts(searchQuery: String): Flow<Resource<QuerySnapshot>> {
//
//        val result = MutableStateFlow<Resource<QuerySnapshot>>(Resource.Ideal())
//
//        result.value = Resource.Loading()
//
//
//        firestore.collection("PRODUCTS").whereArrayContains("tags", searchQuery)
//            .get().addOnSuccessListener {
//
//                result.value = Resource.Success(it)
//
//            }.addOnFailureListener {
//                result.value = Resource.Error(it.message.toString())
//            }
//
//        return result
//    }


    override suspend fun searchForProducts(searchTerms: List<String>): Resource<QuerySnapshot> {

        // 1. تحقق مبدئي من المدخلات
        if (searchTerms.isEmpty()) {
            return Resource.Error("No search terms provided")
        }

        // بناء قائمة الفلاتر
        val filters = searchTerms.map { term ->
            Filter.arrayContains("tags", term.lowercase()) // تأكد أن term صغير
        }


        // ---> التعديل الرئيسي هنا <---
        val finalFilter: Filter // سنحدد الفلتر النهائي هنا

        if (filters.size == 1) {
            // ***** إذا كان هناك فلتر واحد فقط، استخدمه مباشرة *****
            Log.d(TAG, "Single search term detected, using direct arrayContains.")
            finalFilter = filters.first()
        }
        // تحقق من حد OR query
        else if (filters.size > 30) {
            Log.w(
                "SearchRepoImpl",
                "Search query has ${filters.size} terms, exceeding Firestore OR limit of 30."
            )
            return Resource.Error("Search query too broad (more than 30 terms).")
        } else if (filters.isEmpty()) {
            return Resource.Error("Internal error: No valid search terms found.") // حالة نادرة
        } else {
            // ***** إذا كان هناك أكثر من فلتر، استخدم OR *****
            Log.d(TAG, "Multiple search terms detected, using Filter.or.")
            // لا تنس التحقق من filters.size > 30 هنا إذا لم تكن قد فعلت ذلك بالفعل
            finalFilter = Filter.or(*filters.toTypedArray())
        }
        // -----------------------------

        // تنفيذ الاستعلام باستخدام الفلتر المحدد (finalFilter)
        return try {
            Log.d(
                TAG,
                "Executing Firestore query with filter type: ${finalFilter::class.simpleName}"
            )
            val querySnapshot = firestore.collection("PRODUCTS")
                .where(finalFilter) // <-- استخدم الفلتر النهائي هنا
                .get()
                .await()

            Log.d(
                TAG,
                "Firestore query successful, found ${querySnapshot.size()} documents."
            )
            Resource.Success(querySnapshot)

        } catch (e: Exception) {
            Log.e(TAG, "Error performing Firestore search: ${e.message}", e)
            Resource.Error(e.message ?: "Unknown Firestore error during search")
        }
    }

    // ... ( الدوال الأخرى في الـ Repo إن وجدت ) ...


}