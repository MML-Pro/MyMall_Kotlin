package com.blogspot.mido_mymall.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blogspot.mido_mymall.domain.usecase.home.GetCategoriesUseCase
import com.blogspot.mido_mymall.domain.usecase.home.GetTopDealsUseCase
import com.blogspot.mido_mymall.domain.usecase.search.SearchForProductsUseCase
import com.blogspot.mido_mymall.util.Resource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getTopDealsUseCase: GetTopDealsUseCase,
    private val searchForProductsUseCase: SearchForProductsUseCase
) :
    ViewModel() {

    private var _categories = MutableStateFlow<Resource<List<DocumentSnapshot>>>(Resource.Ideal())
    val categories: Flow<Resource<List<DocumentSnapshot>>> get() = _categories

    private var _topDeals = MutableStateFlow<Resource<List<DocumentSnapshot>>>(Resource.Ideal())
    val topDeals: Flow<Resource<List<DocumentSnapshot>>> get() = _topDeals

    private var _searchProducts = MutableStateFlow<Resource<QuerySnapshot>>(Resource.Ideal())
    val searchProducts: Flow<Resource<QuerySnapshot>> get() = _searchProducts


    fun getCategories() = viewModelScope.launch {
        getCategoriesUseCase().collect {
            _categories.emit(it)
        }
    }

    fun getTopDeals() {
        viewModelScope.launch {
            Log.d("HomeViewModel", "getTopDeals: Fetching top deals")
            getTopDealsUseCase.invoke().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        Log.d("HomeViewModel", "getTopDeals: Loading state")
                        _topDeals.emit(resource)
                    }
                    is Resource.Success -> {
                        Log.d("HomeViewModel", "getTopDeals: Success - Documents: ${resource.data?.size}")
                        resource.data?.forEachIndexed { index, doc ->
                            Log.d("HomeViewModel", "getTopDeals: Document $index - view_type: ${doc.get("view_type")}")
                        }
                        _topDeals.emit(resource)
                    }
                    is Resource.Error -> {
                        Log.e("HomeViewModel", "getTopDeals: Error - ${resource.message}")
                        _topDeals.emit(resource)
                    }
                    else -> {
                        Log.d("HomeViewModel", "getTopDeals: Idle state")
                        _topDeals.emit(resource)
                    }
                }
            }
        }
    }

    fun searchForProducts(searchTerms: List<String>) {
        viewModelScope.launch {
            // 1. أصدر حالة التحميل فوراً قبل بدء العملية الطويلة
            _searchProducts.emit(Resource.Loading())
            Log.d("HomeViewModel", "Emitting Loading state for search terms: $searchTerms")

            // 2. استدعِ دالة الـ UseCase/Repository (التي هي الآن suspend وتعيد Resource مباشرة)
            // سيتوقف الـ coroutine هنا مؤقتًا حتى تعود النتيجة من الـ Repository
            val result = searchForProductsUseCase(searchTerms) // افترض أن UseCase يستدعي Repo

            // 3. أصدر النتيجة النهائية (Success أو Error) التي تم الحصول عليها
            Log.d("HomeViewModel", "Emitting result state: ${result::class.simpleName}")
            _searchProducts.emit(result)
        }

    }

    fun resetSearchState() {
        // أعد الحالة إلى الوضع الافتراضي/الأصلي
        _searchProducts.value = Resource.Ideal() // أو الحالة التي تمثل عدم وجود بحث نشط
    }




}