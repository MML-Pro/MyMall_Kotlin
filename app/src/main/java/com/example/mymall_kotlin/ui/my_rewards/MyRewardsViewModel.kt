package com.example.mymall_kotlin.ui.my_rewards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymall_kotlin.domain.usecase.my_rewards.GetRewardsUseCase
import com.example.mymall_kotlin.domain.usecase.my_rewards.GetUserLastSeenUseCase
import com.example.mymall_kotlin.util.Resource
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