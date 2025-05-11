package com.blogspot.mido_mymall.ui.add_address

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blogspot.mido_mymall.domain.models.AddressesModel
import com.blogspot.mido_mymall.domain.usecase.address.AddAddressUseCase
import com.blogspot.mido_mymall.domain.usecase.address.GetAddressUseCase
import com.blogspot.mido_mymall.domain.usecase.address.UpdateAddressInfoUseCase
import com.blogspot.mido_mymall.util.Resource
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddAddressViewModel @Inject constructor(
    private val getAddressUseCase: GetAddressUseCase,
    private val addAddressUseCase: AddAddressUseCase,
    private val updateAddressInfoUseCase: UpdateAddressInfoUseCase
) : ViewModel() {

    // StateFlow لحالة إضافة عنوان جديد
    private var _addAddressState = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())
    val addAddressState: StateFlow<Resource<Boolean>> get() = _addAddressState // استخدام StateFlow هنا أفضل

    // StateFlow لجلب العناوين (يستخدم لجلب العنوان عند التعديل)
    private var _myAddresses = MutableStateFlow<Resource<DocumentSnapshot>>(Resource.Ideal())
    val myAddresses: StateFlow<Resource<DocumentSnapshot>> get() = _myAddresses // استخدام StateFlow

    // StateFlow لحالة تحديث عنوان موجود
    private var _updateAddressState = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())
    val updateAddressState: StateFlow<Resource<Boolean>> get() = _updateAddressState // استخدام StateFlow

    /**
     * جلب بيانات العناوين من Firestore (يستخدم بشكل أساسي لتحميل بيانات العنوان عند التعديل).
     */
    fun getAddress() {
        viewModelScope.launch {
            // يمكنك إضافة إصدار حالة Loading هنا إذا أردت
            // _myAddresses.emit(Resource.Loading())
            getAddressUseCase().collect {
                _myAddresses.emit(it)
            }
        }
    }

    /**
     * إضافة عنوان جديد إلى Firestore.
     * تم تعديل التعريف لإزالة المعاملات غير المستخدمة.
     */
    fun addNewAddress(
        // لا نحتاج لتمرير القائمة أو الفهرس المحدد السابق هنا
        city: String,
        localityOrStreet: String,
        flatNumberOrBuildingName: String,
        pinCode: String,
        state: String,
        landMark: String? = null,
        fullName: String,
        mobileNumber: String,
        alternateMobileNumber: String? = null
    ) {
        viewModelScope.launch {
            _addAddressState.emit(Resource.Loading()) // إصدار حالة التحميل
            // تأكد من أن AddAddressUseCase تم تعديله أيضًا ليقبل المعاملات الصحيحة
            addAddressUseCase(
                // لا تمرر addressesModelList أو selectedAddress هنا
                city = city,
                localityOrStreet = localityOrStreet,
                flatNumberOrBuildingName = flatNumberOrBuildingName,
                pinCode = pinCode,
                state = state,
                landMark = landMark,
                fullName = fullName,
                mobileNumber = mobileNumber,
                alternateMobileNumber = alternateMobileNumber
            ).collect {
                _addAddressState.emit(it) // إصدار النتيجة (Success أو Error)
            }
        }
    }

    /**
     * تحديث معلومات عنوان موجود في Firestore.
     */
    fun updateAddressInfo(
        position: Long, // هذا هو الفهرس أو المعرف الذي يستخدمه UseCase/Repo لتحديد أي عنوان يتم تحديثه
        city: String,
        localityOrStreet: String,
        flatNumberOrBuildingName: String,
        pinCode: String,
        state: String,
        landMark: String?,
        fullName: String,
        mobileNumber: String,
        alternateMobileNumber: String?
    ) {
        viewModelScope.launch {
            _updateAddressState.emit(Resource.Loading()) // إصدار حالة التحميل
            updateAddressInfoUseCase(
                position, // تأكد من أن هذا هو المعرف الصحيح (قد يكون فهرس 0-based أو ID فريد)
                city,
                localityOrStreet,
                flatNumberOrBuildingName,
                pinCode,
                state,
                landMark,
                fullName,
                mobileNumber,
                alternateMobileNumber
            ).collect {
                _updateAddressState.emit(it) // إصدار النتيجة (Success أو Error)
            }
        }
    }

    // --- دوال إعادة التعيين المضافة ---

    /**
     * إعادة تعيين حالة إضافة العنوان إلى الحالة الأولية (Ideal).
     */
    fun resetAddAddressState() {
        _addAddressState.value = Resource.Ideal()
        Log.d("AddAddressViewModel", "addAddressState reset to Ideal")
    }

    /**
     * إعادة تعيين حالة تحديث العنوان إلى الحالة الأولية (Ideal).
     */
    fun resetUpdateAddressState() {
        _updateAddressState.value = Resource.Ideal()
        Log.d("AddAddressViewModel", "updateAddressState reset to Ideal")
    }

     /**
      * (اختياري) دالة لإعادة تعيين حالة جلب العناوين إذا احتجت إليها.
      */
    // fun resetMyAddressesState() {
    //     _myAddresses.value = Resource.Ideal()
    // }

} // نهاية AddAddressViewModel