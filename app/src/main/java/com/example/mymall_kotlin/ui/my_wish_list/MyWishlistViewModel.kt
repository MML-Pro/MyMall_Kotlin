package com.example.mymall_kotlin.ui.my_wish_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymall_kotlin.domain.models.WishListModel
import com.example.mymall_kotlin.domain.usecase.my_wishlist.GetWishListIdsUseCase
import com.example.mymall_kotlin.domain.usecase.my_wishlist.GetWishListUseCase
import com.example.mymall_kotlin.domain.usecase.my_wishlist.RemoveFromWishListUseCase
import com.example.mymall_kotlin.util.Resource
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyWishlistViewModel @Inject constructor(
    private val getWishListUseCase: GetWishListUseCase,
    private val getWishListIdsUseCase: GetWishListIdsUseCase,
    private val removeFromWishListUseCase: RemoveFromWishListUseCase
) : ViewModel() {

    private var _wishList = MutableStateFlow<Resource<DocumentSnapshot>>(Resource.Ideal())
    val wishList: Flow<Resource<DocumentSnapshot>> get() = _wishList

    private var _wishListIds = MutableStateFlow<Resource<DocumentSnapshot>>(Resource.Ideal())
    val wishListIds: Flow<Resource<DocumentSnapshot>> get() = _wishListIds

    private var _removeWishListState = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())
    val removeWishListState: Flow<Resource<Boolean>> get() = _removeWishListState

    fun loadWishList(productId: String) {
        viewModelScope.launch {
            getWishListUseCase(productId).collect {
                _wishList.emit(it)
            }
        }
    }


    fun getWishListIds() {
        viewModelScope.launch {
            getWishListIdsUseCase().collect {
                _wishListIds.emit(it)
            }
        }
    }

    fun removeFromWishList(
        wishListIds: ArrayList<String>,
        index: Int
    ) {
        viewModelScope.launch {
            removeFromWishListUseCase(wishListIds, index).collect {
                _removeWishListState.emit(it)
            }
        }
    }


}