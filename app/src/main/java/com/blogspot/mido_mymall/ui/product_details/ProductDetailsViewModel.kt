package com.blogspot.mido_mymall.ui.product_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blogspot.mido_mymall.domain.models.WishListModel
import com.blogspot.mido_mymall.domain.usecase.product_details.GetMyCartUseCase
import com.blogspot.mido_mymall.domain.usecase.product_details.GetProductDetailsUseCase
import com.blogspot.mido_mymall.domain.usecase.product_details.GetRatingsUseCase
import com.blogspot.mido_mymall.domain.usecase.product_details.GetWishListIdsUseCase
import com.blogspot.mido_mymall.domain.usecase.product_details.RemoveFromCartListUseCase
import com.blogspot.mido_mymall.domain.usecase.product_details.RemoveFromWishListUseCase
import com.blogspot.mido_mymall.domain.usecase.product_details.SaveCartListIdsUseCase
import com.blogspot.mido_mymall.domain.usecase.product_details.SaveWishListIdsUseCase
import com.blogspot.mido_mymall.domain.usecase.product_details.SetRatingUseCase
import com.blogspot.mido_mymall.domain.usecase.product_details.UpdateRatingUseCase
import com.blogspot.mido_mymall.util.Resource
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
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
    private val updateRatingUseCase: UpdateRatingUseCase,
    private val removeFromCartListUseCase: RemoveFromCartListUseCase
) :
    ViewModel() {

    private var _productDetails = MutableStateFlow<Resource<DocumentSnapshot>>(Resource.Ideal())
    val productDetails: Flow<Resource<DocumentSnapshot>> get() = _productDetails

    private var _wishListIds = MutableSharedFlow<Resource<DocumentSnapshot>>()
    val wishListIds get() = _wishListIds.asSharedFlow()

    private var _saveWishListIdsState = MutableSharedFlow<Resource<Boolean>>()
    val saveWishListIdsState get() = _saveWishListIdsState.asSharedFlow()

    private var _removeWishListState = MutableSharedFlow<Resource<Boolean>>()
    val removeWishListState get() = _removeWishListState.asSharedFlow()

    private var _ratingsIds = MutableStateFlow<Resource<DocumentSnapshot>>(Resource.Ideal())
    val ratingsIds: Flow<Resource<DocumentSnapshot>> get() = _ratingsIds

    private var _setRatingState = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())
    val setRatingState: Flow<Resource<Boolean>> get() = _setRatingState

    private var _myCartId = MutableSharedFlow<Resource<DocumentSnapshot>>()
    val myCartId: Flow<Resource<DocumentSnapshot>> get() = _myCartId.asSharedFlow()

    private var _saveCartListIdsState = MutableSharedFlow<Resource<Boolean>>()
    val saveCartListIdsState = _saveCartListIdsState.asSharedFlow()

    private var _updateRatingState = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())
    val updateRatingState: Flow<Resource<Boolean>> get() = _updateRatingState

    private var _removeCartListState = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())
    val removeCartListState: Flow<Resource<Boolean>> get() = _removeCartListState

    fun getProductDetails(productID: String) {
        viewModelScope.launch {
            getProductDetailsUseCase(productID).collect {
                _productDetails.emit(it)
            }
        }
    }

    fun getWishListIds() {
        viewModelScope.launch {
            _wishListIds.emit(Resource.Loading())
            _wishListIds.emit(getWishListIdsUseCase())
        }
    }

    fun saveWishListIds(productID: String, wishListSize: Int) {
        viewModelScope.launch {
            _saveWishListIdsState.emit(Resource.Loading())
            _saveWishListIdsState.emit(saveWishListIdsUseCase(productID, wishListSize))

        }
    }

    fun removeFromWishList(
        wishListIds: ArrayList<String>,
        wishListModelList: ArrayList<WishListModel>,
        index: Int
    ) {
        viewModelScope.launch {
            _removeWishListState.emit(Resource.Loading())

            _removeWishListState.emit(
                removeFromWishListUseCase(
                    wishListIds,
                    wishListModelList,
                    index
                )
            )
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
        averageRating: String,
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
            ).collect {
                _updateRatingState.emit(it)
            }
        }
    }

    fun getMyCartList() {
        viewModelScope.launch {
            _myCartId.emit(Resource.Loading())
            _myCartId.emit(getMyCartUseCase())
        }
    }

    fun saveCartListIds(productID: String, cartListSize: Int) {
        viewModelScope.launch {
            _saveCartListIdsState.emit(Resource.Loading())
            _saveCartListIdsState.emit(saveCartListIdsUseCase(productID, cartListSize))
        }
    }

    fun removeFromCartList(
        cartListIds: ArrayList<String>,
        index: Int
    ) {
        viewModelScope.launch {
            removeFromCartListUseCase(cartListIds, index).collect {
                _removeCartListState.emit(it)
            }
        }
    }
}
