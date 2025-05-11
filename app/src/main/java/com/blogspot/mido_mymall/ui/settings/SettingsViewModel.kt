package com.blogspot.mido_mymall.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blogspot.mido_mymall.data.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val dataStoreRepository: DataStoreRepository) :
    ViewModel() {

    val getSelectedDayNightMode = dataStoreRepository.getSelectedDayNightMode


    fun saveSelectedDayNightMode(nightModeChecked: Int) {
        viewModelScope.launch {
            dataStoreRepository.saveSelectedDayNightMode(nightModeChecked)
        }
    }

}