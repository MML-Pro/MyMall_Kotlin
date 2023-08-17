package com.example.mymall_kotlin.ui.product_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymall_kotlin.domain.models.WishListModel
import com.example.mymall_kotlin.domain.usecase.product_details.GetMyCartUseCase
import com.example.mymall_kotlin.domain.usecase.product_details.GetProductDetailsUseCase
import com.example.mymall_kotlin.domain.usecase.product_details.GetRatingsUseCase
import com.example.mymall_kotlin.domain.usecase.product_details.GetWishListIdsUseCase
import com.example.mymall_kotlin.domain.usecase.product_details.RemoveFromWishListUseCase
import com.example.mymall_kotlin.domain.usecase.product_details.SaveCartListIdsUseCase
import com.example.mymall_kotlin.domain.usecase.product_details.SaveWishListIdsUseCase
import com.example.mymall_kotlin.domain.usecase.product_details.SetRatingUseCase
import com.example.mymall_kotlin.domain.usecase.product_details.UpdateRatingUseCase
import com.example.mymall_kotlin.util.Resource
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val getProductDetailsUseCase: GetProductDetailsUseCase,
    private val getWishListIdsUseCase: GetWishListIdsUseCase,
    private val saveWishListIdsUseCase: SaveWishListIdsUseCase,
    private val removeFromWishListUseCase: RemoveFromWishListUseCase,
    private val getRatingsUseCase: GetRatingsUseCase,
    private val setRatingUseCase: SetRatingUseCase,
    private val getMyCartUseCase: GetMyCartUseCase,
    private val saveCartListIdsUseCase: SaveCartListIdsUseCase,
    private val updateRatingUseCase: UpdateRatingUseCase
) :
    ViewModel() {

    private var _productDetails = MutableStateFlow<Resource<DocumentSnapshot>>(Resource.Ideal())
    val productDetails: Flow<Resource<DocumentSnapshot>> get() = _productDetails

    private var _wishListIds = MutableStateFlow<Resource<DocumentSnapshot>>(Resource.Ideal())
    val wishListIds: Flow<Resource<DocumentSnapshot>> get() = _wishListIds

    private var _saveWishListIdsState = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())
    val saveWishListIdsState: Flow<Resource<Boolean>> get() = _saveWishListIdsState

    private var _removeWishListState = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())
    val removeWishListState: Flow<Resource<Boolean>> get() = _removeWishListState

    private var _ratingsIds = MutableStateFlow<Resource<DocumentSnapshot>>(Resource.Ideal())
    val ratingsIds: Flow<Resource<DocumentSnapshot>> get() = _ratingsIds

    private var _setRatingState = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())
    val setRatingState: Flow<Resource<Boolean>> get() = _setRatingState

    private var _myCart = MutableStateFlow<Resource<DocumentSnapshot>>(Resource.Ideal())
    val myCart: Flow<Resource<DocumentSnapshot>> get() = _myCart

    private var _saveCartListIdsState = MutableSharedFlow<Resource<Boolean>>()
    val saveCartListIdsState = _saveCartListIdsState.asSharedFlow()

    private var _updateRatingState = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())
    val updateRatingState: Flow<Resource<Boolean>> get() = _updateRatingState

    fun getProductDetails(productID: String) {
        viewModelScope.launch {
            getProductDetailsUseCase(productID).collect {
                _productDetails.emit(it)
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

    fun saveWishListIds(productID: String, wishListSize: Int) {
        viewModelScope.launch {
            saveWishListIdsUseCase(productID, wishListSize).collect {
                _saveWishListIdsState.emit(it)
            }
        }
    }

    fun removeFromWishList(
        wishListIds: ArrayList<String>,
        wishListModelList: ArrayList<WishListModel>,
        index: Int
    ) {
        viewModelScope.launch {
            removeFromWishListUseCase(wishListIds, wishListModelList, index).collect {
                _removeWishListState.emit(it)
            }
        }
    }

    fun getRatings() {
        viewModelScope.launch {
            getRatingsUseCase().collect {
                _ratingsIds.emit(it)
            }
        }
    }

    fun setRatings(
        productId: String,
        starPosition: Long,
        averageRating: Long,
        totalRatings: Long,
        myRatingIds: ArrayList<String>,
        myRating: ArrayList<Long>
    ) {

        viewModelScope.launch {
            setRatingUseCase(
                productId,
                starPosition,
                averageRating,
                totalRatings,
                myRatingIds,
                myRating
            ).collect {
                _setRatingState.emit(it)
            }
        }
    }

    fun updateRatings(
        productId: String,
        initialRating: Long,
        oldStar: Long,
        newStar: Long,
        starPosition: Long,
        averageRating: Float,
        myRatingIds: ArrayList<String>,
        myRating: ArrayList<Long>
    ) {
        viewModelScope.launch {
            updateRatingUseCase(
                productId,
                initialRating,
                oldStar,
                newStar,
                starPosition,
                averageRating,
                myRatingIds,
                myRating
            ).collect{
                _updateRatingState.emit(it)
            }
        }
    }

    fun getMyCartList() {
        viewModelScope.launch {
            getMyCartUseCase().collect {
                _myCart.emit(it)
            }
        }
    }

    fun saveCartListIds(productID: String, cartListSize: Int) {
        viewModelScope.launch {
            _saveCartListIdsState.emit(Resource.Loading())
            _saveCartListIdsState.emit(saveCartListIdsUseCase(productID, cartListSize))
        }
    }
}
