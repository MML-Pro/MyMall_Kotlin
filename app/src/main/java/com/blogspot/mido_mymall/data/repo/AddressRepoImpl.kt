package com.blogspot.mido_mymall.data.repo

import android.util.Log
import com.blogspot.mido_mymall.domain.models.AddressesModel
import com.blogspot.mido_mymall.domain.repo.AddressRepo
import com.blogspot.mido_mymall.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class AddressRepoImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
) : AddressRepo {


    override suspend fun getAddress(): Flow<Resource<DocumentSnapshot>> {
        val result = MutableStateFlow<Resource<DocumentSnapshot>>(Resource.Ideal())

        result.value = Resource.Loading()

        firebaseFirestore.collection("USERS")
            .document(firebaseAuth.currentUser?.uid!!)
            .collection("USER_DATA")
            .document("MY_ADDRESS")
            .get()
            .addOnSuccessListener {
                result.value = Resource.Success(it)
            }.addOnFailureListener {
                result.value = Resource.Error(it.message.toString())
            }

        return result
    }


        // --- تعديل دالة addAddress ---
        override suspend fun addAddress(
            // --- إزالة المعاملات القديمة ---
            // addressesModelList: ArrayList<AddressesModel>,
            city: String,
            localityOrStreet: String,
            flatNumberOrBuildingName: String,
            pinCode: String,
            state: String,
            landMark: String?,
            fullName: String,
            mobileNumber: String,
            alternateMobileNumber: String?
            // selectedAddress: Int
        ): Flow<Resource<Boolean>> = callbackFlow { // استخدام callbackFlow للتعامل مع addOnSuccessListener/FailureListener
            send(Resource.Loading()) // إرسال حالة التحميل أولاً

            val currentUser = firebaseAuth.currentUser
            // التحقق من تسجيل دخول المستخدم
            if (currentUser == null) {
                send(Resource.Error("User not logged in"))
                close() // إغلاق الـ Flow
                return@callbackFlow // الخروج من callbackFlow lambda
            }

            // مرجع لمستند العناوين
            val documentRef = firebaseFirestore.collection("USERS")
                .document(currentUser.uid)
                .collection("USER_DATA")
                .document("MY_ADDRESS")

            // استخدام Transaction لضمان القراءة والكتابة كوحدة واحدة (ذرية)
            firebaseFirestore.runTransaction { transaction ->
                val snapshot = transaction.get(documentRef) // قراءة المستند الحالي داخل الـ transaction
                val currentListSize = snapshot.getLong("list_size") ?: 0L // الحصول على الحجم الحالي

                val newIndex = currentListSize + 1 // تحديد الفهرس الجديد (مُعتمد على 1)
                val newIndexStr = newIndex.toString()

                // بناء خريطة البيانات للتحديث
                val updateMap = mutableMapOf<String, Any>()

                // 1. إضافة بيانات العنوان الجديد
                updateMap["list_size"] = newIndex // تحديث الحجم
                updateMap["city_$newIndexStr"] = city
                updateMap["localityOrStreet_$newIndexStr"] = localityOrStreet
                updateMap["flatNumberOrBuildingName_$newIndexStr"] = flatNumberOrBuildingName
                updateMap["pinCode_$newIndexStr"] = pinCode
                updateMap["state_$newIndexStr"] = state
//                landMark?.let { if (it.isNotBlank()) updateMap["landMark_$newIndexStr"] = it } // إضافة العلامة إذا لم تكن فارغة
                updateMap["landMark_$newIndexStr"] = landMark ?: "" // <-- استخدم ?: "" لحفظ "" إذا كان landMark هو null
                updateMap["fullName_$newIndexStr"] = fullName
                updateMap["mobileNumber_$newIndexStr"] = mobileNumber
//                alternateMobileNumber?.let { if (it.isNotBlank()) updateMap["alternateMobileNumber_$newIndexStr"] = it } // إضافة الرقم البديل إذا لم يكن فارغًا
                updateMap["alternateMobileNumber_$newIndexStr"] = alternateMobileNumber ?: "" // <-- استخدم ?: "" لحفظ "" إذا كان altMobile هو null
                updateMap["selected_$newIndexStr"] = true // جعل العنوان الجديد هو المحدد

                // 2. إلغاء تحديد جميع العناوين القديمة
                for (i in 1..currentListSize) { // المرور على الفهارس القديمة
                    updateMap["selected_${i}"] = false
                }

                // تنفيذ التحديث داخل الـ transaction
                if (snapshot.exists()) { // إذا كان المستند موجودًا، قم بالتحديث
                    transaction.update(documentRef, updateMap)
                } else { // إذا كان المستند غير موجود (أول عنوان)، قم بالإنشاء
                    transaction.set(documentRef, updateMap)
                }


                // لا حاجة لإرجاع قيمة من لامدا Transaction إذا نجحت
                null
            }.addOnSuccessListener {
                Log.d("AddressRepoImpl", "Transaction success - Address added/selection updated.")
                trySend(Resource.Success(true)).isSuccess // إرسال حالة النجاح
                close() // إغلاق الـ Flow
            }.addOnFailureListener { e ->
                Log.e("AddressRepoImpl", "Transaction failure: ${e.message}", e)
                trySend(Resource.Error(e.message ?: "Failed to add address")).isFailure // إرسال حالة الخطأ
                close() // إغلاق الـ Flow
            }

            // للحفاظ على الـ Flow مفتوحًا حتى يتم استدعاء close() أو إلغاؤه
            awaitClose { Log.d("AddressRepoImpl", "AddAddress Flow cleanup.") }
        }

        // ... (باقي الدوال في الـ Repo) ...


    override suspend fun updateSelectedAddress(
        selectedAddress: Int,
        previousAddress: Int
    ): Flow<Resource<Boolean>> {
        val result = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())

        result.value = Resource.Loading()

        val updateSelection: HashMap<String, Any> = HashMap()
        updateSelection["selected_" + (previousAddress + 1).toString()] = false
        updateSelection["selected_" + java.lang.String.valueOf(selectedAddress + 1)] =
            true

//        previousAddress = selectedAddress

        firebaseFirestore.collection("USERS")
            .document(firebaseAuth.currentUser?.uid!!)
            .collection("USER_DATA").document("MY_ADDRESS")
            .update(updateSelection)
            .addOnSuccessListener {
                result.value = Resource.Success(true)
            }.addOnFailureListener {
                result.value = Resource.Error(it.message.toString())
            }

        return result
    }

    override suspend fun updateAddressInfo(

        position: Long,
        city: String,
        localityOrStreet: String,
        flatNumberOrBuildingName: String,
        pinCode: String,
        state: String,
        landMark: String?,
        fullName: String,
        mobileNumber: String,
        alternateMobileNumber: String?
    ): Flow<Resource<Boolean>> {
        val result = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())

        result.value = Resource.Loading()

        val updateAddressMap: HashMap<String, Any> = HashMap()

        updateAddressMap["list_size"] = (position + 1)
        updateAddressMap["city_" + ((position + 1)).toString()] = city
        updateAddressMap["localityOrStreet_" + (position + 1).toString()] =
            localityOrStreet
        updateAddressMap["flatNumberOrBuildingName_" + (position + 1).toString()] =
            flatNumberOrBuildingName
        updateAddressMap["pinCode_" + (position + 1).toString()] = pinCode
        updateAddressMap["state_" + (position + 1).toString()] = state
//        updateAddressMap["landMark_" + (position + 1).toString()] =
//            landMark.toString()

        updateAddressMap["landMark_" + (position + 1).toString()] =
            landMark ?: ""

        updateAddressMap["fullName_" + (position + 1).toString()] = fullName
        updateAddressMap["mobileNumber_" + (position + 1).toString()] = mobileNumber

        updateAddressMap["alternateMobileNumber_" + (position + 1).toString()] = alternateMobileNumber ?: ""


        firebaseFirestore.collection("USERS")
            .document(firebaseAuth.currentUser?.uid!!)
            .collection("USER_DATA").document("MY_ADDRESS")
            .update(updateAddressMap).addOnSuccessListener {
                result.value = Resource.Success(true)
            }.addOnFailureListener {
                result.value = Resource.Error(it.message.toString())
            }

        return result


    }

    // داخل AddressRepoImpl
    override suspend fun removeAddress(
        addressesModelList: List<AddressesModel>, // يقبل List
        position: Int,
        selectedCurrentIndex: Int
    ): Flow<Resource<Boolean>> = callbackFlow {
        send(Resource.Loading())

        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            send(Resource.Error("User not logged in"))
            close(); return@callbackFlow
        }

        val documentRef = firebaseFirestore.collection("USERS")
            .document(currentUser.uid)
            .collection("USER_DATA")
            .document("MY_ADDRESS")

        firebaseFirestore.runTransaction { transaction ->
            val snapshot = transaction.get(documentRef)
            if (!snapshot.exists()) {
                Log.w("AddressRepoImpl", "Remove: Address document not found.")
                return@runTransaction true // لا يوجد شيء لحذفه
            }

            val currentListSize = snapshot.getLong("list_size") ?: 0L
            val indexToDelete1Based = position + 1 // فهرس Firestore (يبدأ من 1)

            if (indexToDelete1Based < 1 || indexToDelete1Based > currentListSize) {
                Log.e("AddressRepoImpl", "Remove: Invalid position ($position) for list size $currentListSize")
                throw FirebaseFirestoreException(
                    "Invalid position ($position) to delete for list size $currentListSize",
                    FirebaseFirestoreException.Code.OUT_OF_RANGE
                )
            }

            val updates = mutableMapOf<String, Any?>() // خريطة التحديثات النهائية
            var finalSelected1BasedIndex: Long = -1 // الفهرس (1-based) الذي يجب أن يكون محددًا
            val deletedAddressWasSelected = (position == selectedCurrentIndex)

            // --- 1. نقل بيانات العناصر اللاحقة خطوة للخلف ---
            // i هو الفهرس القديم (1-based), newIndex هو الفهرس الجديد (1-based)
            for (i in (indexToDelete1Based + 1)..currentListSize) {
                val sourceIndexStr = i.toString()
                val targetIndex = i - 1
                val targetIndexStr = targetIndex.toString()

                // نسخ الحقول
                updates["city_$targetIndexStr"] = snapshot.getString("city_$sourceIndexStr")
                updates["localityOrStreet_$targetIndexStr"] = snapshot.getString("localityOrStreet_$sourceIndexStr")
                updates["flatNumberOrBuildingName_$targetIndexStr"] = snapshot.getString("flatNumberOrBuildingName_$sourceIndexStr")
                updates["pinCode_$targetIndexStr"] = snapshot.getString("pinCode_$sourceIndexStr")
                updates["state_$targetIndexStr"] = snapshot.getString("state_$sourceIndexStr")
                updates["landMark_$targetIndexStr"] = snapshot.getString("landMark_$sourceIndexStr") // يبقى null إذا كان null
                updates["fullName_$targetIndexStr"] = snapshot.getString("fullName_$sourceIndexStr")
                updates["mobileNumber_$targetIndexStr"] = snapshot.getString("mobileNumber_$sourceIndexStr")
                updates["alternateMobileNumber_$targetIndexStr"] = snapshot.getString("alternateMobileNumber_$sourceIndexStr") // يبقى null إذا كان null

                // نسخ حالة التحديد وتتبع الفهرس المحدد الجديد
                val isSelected = snapshot.getBoolean("selected_$sourceIndexStr") ?: false
                updates["selected_$targetIndexStr"] = isSelected
                if (isSelected) {
                    finalSelected1BasedIndex = targetIndex // حفظ الفهرس الجديد (1-based)
                }
            }

            // --- 2. تحديد حالة التحديد النهائية ---
            val newListSize = currentListSize - 1
            if (deletedAddressWasSelected && newListSize > 0) {
                // إذا حذفنا المحدد والقائمة ليست فارغة، حدد الأول
                updates["selected_1"] = true
            } else if (!deletedAddressWasSelected && finalSelected1BasedIndex == -1L && newListSize > 0) {
                // إذا لم نحذف المحدد ولم يكن أي عنصر آخر محددًا والقائمة ليست فارغة، حدد الأول
                updates["selected_1"] = true
            }
            // إذا لم يتم حذف المحدد وكان هناك عنصر آخر محدد، فإن حالته تم نسخها أعلاه

            // --- 3. حذف حقول الفهرس الأخير القديم ---
            // هذه الحقول أصبحت غير ضرورية بعد نقل البيانات أو إذا كانت القائمة فارغة

                val lastIndexStr = currentListSize.toString()
                updates["city_$lastIndexStr"] = FieldValue.delete()
                updates["localityOrStreet_$lastIndexStr"] = FieldValue.delete()
                updates["flatNumberOrBuildingName_$lastIndexStr"] = FieldValue.delete()
                updates["pinCode_$lastIndexStr"] = FieldValue.delete()
                updates["state_$lastIndexStr"] = FieldValue.delete()
                updates["landMark_$lastIndexStr"] = FieldValue.delete()
                updates["fullName_$lastIndexStr"] = FieldValue.delete()
                updates["mobileNumber_$lastIndexStr"] = FieldValue.delete()
                updates["alternateMobileNumber_$lastIndexStr"] = FieldValue.delete()
                updates["selected_$lastIndexStr"] = FieldValue.delete()

            // --- 4. تحديث حجم القائمة ---
            updates["list_size"] = newListSize // تعيين الحجم الصحيح (0 إذا كان آخر عنصر)

            Log.d("AddressRepoImpl", "[Transaction][Remove] Final 'updates' map: $updates")

            // --- 5. تنفيذ التحديث ---
            // .update() ستطبق التغييرات وتحذف الحقول المحددة بـ FieldValue.delete()
            transaction.update(documentRef, updates)

            true // للإشارة إلى نجاح Transaction Lambda
        }.addOnSuccessListener { transactionResult ->
            if (transactionResult != null) { // التحقق من نجاح الـ Lambda
                Log.d("AddressRepoImpl", "Transaction success - Address removed / re-indexed.")
                trySend(Resource.Success(true)).isSuccess
            } else {
                // هذا قد يحدث إذا كان المستند غير موجود أصلاً
                Log.w("AddressRepoImpl", "Transaction completed (maybe document didn't exist).")
                trySend(Resource.Success(true)).isSuccess // إرسال نجاح على أي حال
            }
            close()
        }.addOnFailureListener { e ->
            Log.e("AddressRepoImpl", "Transaction failure during remove: ${e.message}", e)
            trySend(Resource.Error(e.message ?: "Failed to remove address")).isFailure
            close()
        }

        awaitClose { Log.d("AddressRepoImpl", "RemoveAddress Flow cleanup.") }
    } // نهاية removeAddress


//    override suspend fun removeAddress(
//        addressesModelList: ArrayList<AddressesModel>,
//        position: Int
//    ): Flow<Resource<Boolean>> {
//
//        val result = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())
//
//        result.value = Resource.Loading()
//
//        val updateAddressMap: HashMap<String, Any> = HashMap()
//        var x = 0
//        var selected = -1
//
//        for (i in 0 until addressesModelList.size) {
//
//            if (i != position) {
//                x++
//
//                updateAddressMap["city_" + (x).toString()] = addressesModelList[i].city
//                updateAddressMap["localityOrStreet_" + (x).toString()] =
//                    addressesModelList[i].localityOrStreet
//
//                updateAddressMap["flatNumberOrBuildingName_" + (x).toString()] =
//                    addressesModelList[i].flatNumberOrBuildingName
//                updateAddressMap["pinCode_" + (x).toString()] = addressesModelList[i].pinCode
//                updateAddressMap["state_" + (x).toString()] = addressesModelList[i].state
//                updateAddressMap["landMark_" + (x).toString()] =
//                    addressesModelList[i].landMark.toString()
//                updateAddressMap["fullName_" + (x).toString()] = addressesModelList[i].fullName
//                updateAddressMap["mobileNumber_" + (x).toString()] =
//                    addressesModelList[i].mobileNumber
//
//                updateAddressMap["alternateMobileNumber_" + (x).toString()] =
//                    addressesModelList[i].alternateMobileNumber.toString()
//
//                if (addressesModelList[position].selected) {
//                    if (position - 1 >= 0) {
//                        if (x == position - 1) {
//                            updateAddressMap["selected_$x"] = true
//                            selected = x
//                        }else {
//                            updateAddressMap["selected_" + (x).toString()] = addressesModelList[i].selected
//                        }
//                    } else {
//                        if (x == 1) {
//                            updateAddressMap["selected_$x"] = true
//                            selected = x
//                        }else {
//                            updateAddressMap["selected_" + (x).toString()] = addressesModelList[i].selected
//                        }
//                    }
//                } else {
//                    updateAddressMap["selected_" + (x).toString()] = addressesModelList[i].selected
//
//                }
//            }
//
//        }
//        updateAddressMap["list_size"] = x
//
//        firebaseFirestore.collection("USERS")
//            .document(firebaseAuth.currentUser?.uid!!)
//            .collection("USER_DATA").document("MY_ADDRESS")
//            .set(updateAddressMap)
//            .addOnSuccessListener {
//
//                addressesModelList.removeAt(position)
//
//                if (selected != -1) {
//                    SELECTED_ADDRESS = selected - 1
//                    addressesModelList[selected - 1].selected = true
//                }
//
//                result.value = Resource.Success(true)
//
//
//            }.addOnFailureListener {
//                result.value = Resource.Error(it.message.toString())
//            }
//        return result
//    }

}