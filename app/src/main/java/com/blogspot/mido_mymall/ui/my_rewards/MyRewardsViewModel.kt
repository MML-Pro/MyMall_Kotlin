package com.blogspot.mido_mymall.ui.my_rewards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blogspot.mido_mymall.domain.usecase.my_rewards.GetRewardsUseCase
import com.blogspot.mido_mymall.domain.usecase.my_rewards.GetUserLastSeenUseCase
import com.blogspot.mido_mymall.util.Resource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyRewardsViewModel @Inject constructor(
    private val getRewardsUseCase: GetRewardsUseCase,
    private val getUserLastSeenUseCase: GetUserLastSeenUseCase
) :
    ViewModel() {

    private var _rewardList = MutableStateFlow<Resource<QuerySnapshot>>(Resource.Ideal())
    val rewardList: Flow<Resource<QuerySnapshot>> get() = _rewardList

    private var _userLastSeen = MutableStateFlow<Resource<DocumentSnapshot>>(Resource.Ideal())
    val userLastSeen: Flow<Resource<DocumentSnapshot>> get() = _userLastSeen

    fun getRewards() {
        viewModelScope.launch {
            getRewardsUseCase().collect {
                _rewardList.emit(it)
            }
        }
    }

    fun getUserLastSeen() {
        viewModelScope.launch {
            getUserLastSeenUseCase().collect {
                _userLastSeen.emit(it)
            }
        }
    }

}