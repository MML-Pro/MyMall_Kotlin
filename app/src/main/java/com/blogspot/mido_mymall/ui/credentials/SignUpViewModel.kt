package com.blogspot.mido_mymall.ui.credentials

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blogspot.mido_mymall.domain.models.User
import com.blogspot.mido_mymall.domain.usecase.credentials.CreateUserDataUseCase
import com.blogspot.mido_mymall.domain.usecase.credentials.SaveUserInfoUseCase
import com.blogspot.mido_mymall.domain.usecase.credentials.SignUpUseCase
import com.blogspot.mido_mymall.util.RegisterValidation
import com.blogspot.mido_mymall.util.Resource
import com.blogspot.mido_mymall.util.checkValidation
import com.blogspot.mido_mymall.util.validateEmail
import com.blogspot.mido_mymall.util.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val saveUserInfoUseCase: SaveUserInfoUseCase,
    private val createUserDataUseCase: CreateUserDataUseCase,
    val firebaseAuth: FirebaseAuth
) : ViewModel() {

    companion object{
        private const val TAG = "SignUpViewModel"
    }

    private var _register = MutableStateFlow<Resource<FirebaseUser>>(Resource.Ideal())
    val register: Flow<Resource<FirebaseUser>> get() = _register

    private var _registerFailedStatesValidation = Channel<RegisterValidation.RegisterFailedStates>()
    val registerFailedStatesValidation get() = _registerFailedStatesValidation.receiveAsFlow()

    private var _saveUserInfoState = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())
    val saveUserInfoState : Flow<Resource<Boolean>> get() = _saveUserInfoState

    private var _createUserDataState = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())
    val createUserDataState : Flow<Resource<Boolean>> get() = _createUserDataState



  fun createAccountWithEmailAndPassword(
    user: User,
    password: String,
    confirmationPassword: String
) {
      _register.value = Resource.Loading()

    // 1. تحقق من المدخلات أولاً قبل تغيير الحالة إلى Loading
    if (!checkValidation(user, password, confirmationPassword)) {
        // فشل التحقق *قبل* محاولة التسجيل
        Log.e(TAG, "Validation failed.")
        val registerFailedStates = RegisterValidation.RegisterFailedStates(
            validateEmail(user.email),
            validatePassword(password, confirmationPassword)
        )
        viewModelScope.launch {
            Log.e(TAG, "Sending failed states: ${registerFailedStates.toString()}")
            _registerFailedStatesValidation.send(registerFailedStates)

            // 2. أعد الحالة الرئيسية إلى وضع غير Loading
            //    (مهم لإخفاء مؤشر التقدم في الواجهة)
            _register.value = Resource.Ideal()// أو Resource.Error("Validation Failed") إذا أردت
        }
        return // 3. أوقف التنفيذ هنا لأن التحقق فشل
    }

    // 4. التحقق نجح، الآن ابدأ عملية التسجيل وغير الحالة إلى Loading

    Log.d(TAG, "Validation passed. Attempting registration.")

    viewModelScope.launch {
        signUpUseCase.invoke(user, password).collect { registrationResult ->
             Log.d(TAG, "Registration result received: $registrationResult")
            // 5. حدث الحالة الرئيسية بنتيجة التسجيل الفعلية (Success أو Error)
            _register.value = registrationResult
        }
    }
}


    fun saveUserInfo(uID: String, userName: String, email: String){
        viewModelScope.launch {
            saveUserInfoUseCase(uID, userName, email).collectLatest {
                _saveUserInfoState.emit(it)
            }
        }
    }

    fun createUserData(){
        viewModelScope.launch {
            createUserDataUseCase().collect{
                _createUserDataState.emit(it)
            }
        }
    }

}