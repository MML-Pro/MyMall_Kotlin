package com.example.mymall_kotlin.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymall_kotlin.domain.usecase.home.GetCategoriesUseCase
import com.example.mymall_kotlin.domain.usecase.home.GetTopDealsUseCase
import com.example.mymall_kotlin.domain.usecase.notification.GetNotificationUseCase
import com.example.mymall_kotlin.domain.usecase.search.SearchForProductsUseCase
import com.example.mymall_kotlin.util.Resource
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
            getTopDealsUseCase.invoke().collect{
                _topDeals.emit(it)
            }
        }
    }

    fun searchForProducts(searchQuery: String) {
        viewModelScope.launch {
            searchForProductsUseCase(searchQuery).collect {
                _searchProducts.emit(it)
            }

        }
    }



}