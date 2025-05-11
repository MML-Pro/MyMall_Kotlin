package com.blogspot.mido_mymall.ui.edit_user_info

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.blogspot.mido_mymall.R
import com.blogspot.mido_mymall.databinding.FragmentUpdateInfoBinding
import com.blogspot.mido_mymall.databinding.ReauthenticateDialogBinding
import com.blogspot.mido_mymall.ui.MainActivity
import com.blogspot.mido_mymall.ui.MainActivityViewModel
import com.blogspot.mido_mymall.ui.credentials.SignInViewModel
import com.blogspot.mido_mymall.util.Constants
import com.blogspot.mido_mymall.util.Resource
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

@AndroidEntryPoint
class UpdateInfoFragment : Fragment() {

    private var _binding: FragmentUpdateInfoBinding? = null
    private val binding get() = _binding!!

    private lateinit var selectImageLauncher: ActivityResultLauncher<String>
    private lateinit var pickMediaLauncher: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private lateinit var userImageUri: Uri

    private val loadingDialog by lazy { Constants.setProgressDialog(requireContext()) }

    companion object {
        const val REQUEST_CODE_SELECT_IMAGE = 1
        private const val TAG = "UpdateInfoFragment"
    }

    private val updateUserInfoViewModel by viewModels<UpdateUserInfoViewModel>()
    private val signInViewModel by viewModels<SignInViewModel>()

    private var currentUserName = ""
    private var currentEmail = ""
    private lateinit var newEmail: String

    private lateinit var reAuthenticateAndConfirmationDialog: Dialog

//    private var allUpdatesSuccessful = false

    private var isImageRemoved = false


    private val mainActivityViewModel by activityViewModels<MainActivityViewModel>()

    private val googleActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
//           val data: Intent? = result.data

                reAuthenticateAndConfirmationDialog.cancel()
                loadingDialog.cancel()

                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)

                if (this::newEmail.isInitialized) {
                    updateUserInfoViewModel.updateUserEmail(
                        null, newEmail,
                        null, account.idToken
                    )
                }

                Log.d(TAG, "googleActivityResultLauncher: ${account.email.toString()}")

            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUpdateInfoBinding.inflate(inflater, container, false)

        currentUserName = (requireActivity() as MainActivity).userName

        currentEmail = (requireActivity() as MainActivity).userEmail

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // إعداد لاقطات الصور
        selectImageLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                if (uri != null) {
                    try {

                        userImageUri = uri

                        Glide.with(this).load(uri)
                            .into(binding.profileImage)


//                        selectedImagePath = RealFilePath.getPath(requireContext(), uri).toString()

                    } catch (ex: Exception) {
                        Toast.makeText(requireContext(), ex.message.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }


        pickMediaLauncher =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    try {
//
                        userImageUri = uri


                        Glide.with(this).load(uri)
                            .into(binding.profileImage)

//
//                        val data = imageFromUriToByteArray(uri)
//                        updateUserInfoViewModel.updateUserProfileImage(data)
//
                    } catch (ex: Exception) {
                        Toast.makeText(requireContext(), ex.message.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }


        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    // Permission is granted, you can proceed with the required operation
                    // Call your method or perform the desired action here

                    selectImage()

                } else {

                    if (shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        // Permission rationale should be shown
                        showPermissionRationaleDialog()
                    }


                }
            }

        binding.apply {
            nameEditText.setText(currentUserName)
            emailEditText.setText(currentEmail)

            Glide.with(this@UpdateInfoFragment)
                .load((requireActivity() as MainActivity).userImage)
                .placeholder(R.drawable.account)
                .into(profileImage)

            changePhotoButton.setOnClickListener {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                    pickMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                } else {
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {


                        // No need to show permission rationale, request the permission directly
                        requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)

                    } else {
                        selectImage()
                    }
                }

            }

            removePhotoButton.setOnClickListener {
                // ---> التحقق أولاً <---
                // --- إذا لم تكن الصورة الحالية هي الافتراضية، تابع العملية ---
                // 1. تعيين الصورة الافتراضية محلياً في الواجهة
                profileImage.setImageResource(R.drawable.account)

                // 2. تحويل الصورة الافتراضية (Drawable) إلى ByteArray
                val defaultImageByteArray = drawableToByteArray(R.drawable.account)

                // 3. التحقق من نجاح التحويل قبل الاستدعاء
                if (defaultImageByteArray != null) {
                    // 4. استدعاء دالة تحديث الصورة في ViewModel بالبيانات الجديدة (للصورة الافتراضية)
                    updateUserInfoViewModel.updateUserProfileImage(defaultImageByteArray).also {
                        isImageRemoved = true
                    }
                    // ملاحظة: لا نحتاج لإظهار Toast هنا، لأن المراقب الخاص بـ
                    // updateUserProfileImageState سيفعل ذلك عند نجاح أو فشل التحديث.
                    // سيظهر Toast مثل "Profile image updated successfully".
                } else {
                    isImageRemoved = false
                    // إبلاغ المستخدم بفشل تجهيز الصورة الافتراضية
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.an_error_occurred_while_setting_the_default_image),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(TAG, "Failed to convert R.drawable.account to ByteArray")
                }
            }


            updateButton.setOnClickListener {

                val newUserName = nameEditText.text.toString()
                val newEmail = emailEditText.text.toString()
                val isValidEmail = Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()

                if (TextUtils.isEmpty(newUserName)) {
                    nameEditText.apply {
                        requestFocus()
                        error = "userName is Empty"
                    }
                } else if (TextUtils.isEmpty(newEmail)) {
                    emailEditText.apply {
                        requestFocus()
                        error = "email is Empty"
                    }
                } else if (!isValidEmail) {
                    // Show an error message

                    emailEditText.apply {
                        requestFocus()
                        error = "Invalid email"
                    }

                }


                if (!(currentUserName.equals(newUserName)) && currentEmail.equals(newEmail) &&
                    !this@UpdateInfoFragment::userImageUri.isInitialized) {
                    nameEditText.error = null
                    updateUserInfoViewModel.updateUserName(newUserName)
                }

                if ((currentUserName.equals(newUserName)) && !currentEmail.equals(newEmail) &&
                    !this@UpdateInfoFragment::userImageUri.isInitialized) {
                    emailEditText.error = null
                    this@UpdateInfoFragment.newEmail = newEmail
                    reAuthenticateDialog(newEmail)
                }


                 if (currentUserName.equals(newUserName) && currentEmail.equals(newEmail) &&
                     this@UpdateInfoFragment::userImageUri.isInitialized) {
                    nameEditText.error = null
                    emailEditText.error = null
                    val data = imageFromUriToByteArray(userImageUri)
                    updateUserInfoViewModel.updateUserProfileImage(data) }


                if (!currentUserName.equals(newUserName) && !currentEmail.equals(newEmail) &&
                    !this@UpdateInfoFragment::userImageUri.isInitialized) {
                    nameEditText.error = null
                    emailEditText.error = null

                    updateUserInfoViewModel.updateUserName(newUserName)

                    this@UpdateInfoFragment.newEmail = newEmail
                    reAuthenticateDialog(newEmail) }

                if (!currentUserName.equals(newUserName) && currentEmail.equals(newEmail) &&
                    this@UpdateInfoFragment::userImageUri.isInitialized) {

                    Log.d(TAG, "onViewCreated: update image and user called")

                    updateUserInfoViewModel.updateUserName(newUserName)
                    val data = imageFromUriToByteArray(userImageUri)
                    updateUserInfoViewModel.updateUserProfileImage(data).also {
                        Log.d(TAG, "updateUserProfileImage:  called")
                    }
                }


                if (currentUserName.equals(newUserName) &&
                    !currentEmail.equals(newEmail) &&
                    this@UpdateInfoFragment::userImageUri.isInitialized) {
                    emailEditText.error = null
                    this@UpdateInfoFragment.newEmail = newEmail
                    reAuthenticateDialog(newEmail)
                    val data = imageFromUriToByteArray(userImageUri)
                    updateUserInfoViewModel.updateUserProfileImage(data) }


                 if (!currentUserName.equals(newUserName) &&
                     !currentEmail.equals(newEmail) &&
                     this@UpdateInfoFragment::userImageUri.isInitialized) {
                    // Clear the error message
                    nameEditText.error = null
                    emailEditText.error = null

                    if (this@UpdateInfoFragment::userImageUri.isInitialized) {
                        val data = imageFromUriToByteArray(userImageUri)
                        updateUserInfoViewModel.updateUserProfileImage(data)
                    }
                    updateUserInfoViewModel.updateUserName(newUserName)
                    this@UpdateInfoFragment.newEmail = newEmail
                    reAuthenticateDialog(newEmail)

                }


            }

        }


        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                signInViewModel.googleIntent.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            loadingDialog.create()
                            loadingDialog.show()
                        }

                        is Resource.Success -> {
                            googleActivityResultLauncher.launch(it.data)
                        }

                        is Resource.Error -> {
                            loadingDialog.cancel()
                            Toast.makeText(
                                requireContext(),
                                it.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e(TAG, "onViewCreated: ${it.message.toString()}")
                        }

                        else -> {}
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainActivityViewModel.userInfo.collect {
                    if (it is Resource.Success) {
                        it.data?.let { documentSnapshot ->
//                            Glide.with(this@UpdateInfoFragment)
//                                .load(documentSnapshot.get("profileImage"))
//                                .placeholder(R.drawable.profile_placeholder)
//                                .into(binding.profileImage)

                            currentUserName = documentSnapshot.get("userName").toString()

                            currentEmail = documentSnapshot.get("email").toString()

                            binding.apply {
                                nameEditText.setText(currentUserName)
                                emailEditText.setText(currentEmail)
                            }

                            (requireActivity() as MainActivity).navHeaderMainBinding.mainUserName.text =
                                currentUserName
                            (requireActivity() as MainActivity).navHeaderMainBinding.mainEmail.text =
                                currentEmail
                        }

                    } else if (it is Resource.Error) {
                        Log.e(TAG, "onCreate: ${it.message.toString()}")
                    }
                }
            }
        }


//        lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                combine(
//                    updateUserInfoViewModel.updateUserNameState,
//                    updateUserInfoViewModel.updateUserEmailState,
//                    updateUserInfoViewModel.updateUserProfileImageState
//
//                ) { updateUserNameState, updateUserEmailState, updateUserProfileImageState ->
//                    val userNameSuccess =
//                        updateUserNameState is Resource.Success && updateUserNameState.data == true
//                    val emailSuccess =
//                        updateUserEmailState is Resource.Success && updateUserEmailState.data == true
//                    val profileImageSuccess =
//                        updateUserProfileImageState is Resource.Success && updateUserProfileImageState.data == true
//
//
//                    // Update the allUpdatesSuccessful flag
//                    allUpdatesSuccessful = userNameSuccess && emailSuccess && profileImageSuccess
//
//                    // Show the combined toast if all updates were successful
//
////
//                    allUpdatesSuccessful
//                }.collect {
//                    if (allUpdatesSuccessful) {
//                        Toast.makeText(
//                            requireContext(),
//                            "All data updated successfully",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//
//                }
//            }
//
//        }

//
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                updateUserInfoViewModel.updateUserNameState.collect { response ->

                    when (response) {
                        is Resource.Loading -> {
                            loadingDialog.show()
                        }

                        is Resource.Success -> {
                            loadingDialog.cancel()
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.username_updated_successfully),
                                Toast.LENGTH_SHORT
                            ).show()
                            mainActivityViewModel.getUserInfo()

                        }

                        is Resource.Error -> {
                            loadingDialog.cancel()
                            Log.e(TAG, "updateUserNameState: ${response.message.toString()}")
                        }

                        else -> {}
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                updateUserInfoViewModel.updateUserEmailState.collect { response ->

                    when (response) {
                        is Resource.Loading -> {
//                            loadingDialog.create()
                            loadingDialog.show()
                        }

                        is Resource.Success -> {
                            loadingDialog.cancel()
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.email_updated_successfully),
                                Toast.LENGTH_SHORT
                            ).show()
                            mainActivityViewModel.getUserInfo()

                        }

                        is Resource.Error -> {

                            loadingDialog.cancel()
                            Log.e(TAG, "updateUserNameState: ${response.message.toString()}")
                        }

                        else -> {
                            loadingDialog.cancel()
                        }
                    }

                }

            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {


                updateUserInfoViewModel.updateUserProfileImageState.collect { response ->

                    when (response) {
                        is Resource.Loading -> {
//                            loadingDialog.create()
                            loadingDialog.show()
                        }

                        is Resource.Success -> {

                            loadingDialog.cancel()
                            if (isImageRemoved) {
                                (requireActivity() as MainActivity).navHeaderMainBinding.mainProfileImage.setImageResource(
                                    R.drawable.account
                                )
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.profile_image_removed_successfully),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                binding.profileImage.setImageURI(this@UpdateInfoFragment.userImageUri)
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.profile_image_updated_successfully),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            mainActivityViewModel.getUserInfo().also {
                                isImageRemoved = false
                            }
                        }

                        is Resource.Error -> {
                            isImageRemoved = false
                            loadingDialog.cancel()
                            Log.e(TAG, "updateUserNameState: ${response.message.toString()}")
                        }

                        else -> {}
                    }

                }

            }
        }
    }

    private fun reAuthenticateDialog(newEmail: String) {
        reAuthenticateAndConfirmationDialog = Dialog(requireContext())

        val reAuthenticateDialogBinding: ReauthenticateDialogBinding =
            ReauthenticateDialogBinding.inflate(layoutInflater, null, false)

        reAuthenticateAndConfirmationDialog.setContentView(reAuthenticateDialogBinding.root)

        reAuthenticateAndConfirmationDialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )


        reAuthenticateDialogBinding.doneButton.setOnClickListener {

            val password = reAuthenticateDialogBinding.passwordEditText.text.toString()

            if (password.isNotEmpty()) {
                reAuthenticateDialogBinding.passwordEditText.error = null
                updateUserInfoViewModel.updateUserEmail(currentEmail, newEmail, password)

                Log.d(TAG, "reAuthenticateDialog: called")
            } else {
                reAuthenticateDialogBinding.passwordEditText.apply {
                    requestFocus()
                    error = "Password is empty"
                }
            }
        }.also {
            reAuthenticateAndConfirmationDialog.cancel()

        }

        reAuthenticateDialogBinding.btnGoogle.setOnClickListener {
            signInViewModel.signInGoogle()
        }

        reAuthenticateAndConfirmationDialog.create()
        reAuthenticateAndConfirmationDialog.show()
    }

    private fun imageFromUriToByteArray(uri: Uri?): ByteArray {
        val inputStream = uri?.let { requireActivity().contentResolver.openInputStream(it) }
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        return data
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: called")
        if (requestCode == REQUEST_CODE_SELECT_IMAGE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    if (data != null) {
                        data.data?.let {
                            userImageUri = it
                            val newImage = getBytesFromUri(requireContext(), it)
                            updateUserInfoViewModel.updateUserProfileImage(newImage!!)
                        }
                    } else {
                        Log.d(TAG, "onActivityResult: data is null")
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.an_error_occurred_while_changing_the_image_please_try_again),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                Activity.RESULT_CANCELED -> {
                    Log.e(TAG, "onActivityResult: result is canceled")
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.cancelled_by_user_image_not_updated),
                        Toast.LENGTH_LONG
                    ).show()
                }

                else -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.an_error_occurred_while_changing_the_image_please_try_again),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }


// ... (كل الكود السابق يبقى كما هو بالضبط في ملف UpdateInfoFragment.kt) ...
// ... (بما في ذلك تعريفات الـ Launchers، onCreateView، onViewCreated، requestPermissionLauncher callback، إلخ) ...
// ... (والتعديل السابق في changePhotoButton ليفحص عند API 33) ...

    // --- دالة selectImage() - هنا التعديل الوحيد المقترح لحل مشكلة الأجهزة القديمة ---
    private fun selectImage() {
        // الهدف: استخدام ACTION_GET_CONTENT دائمًا عند استدعاء هذه الدالة لأنه أكثر توافقًا
        // الكود الأصلي كان يستخدم ACTION_PICK + startActivityForResult للأجهزة الأقدم من Q
        // الآن، سنجعله يستخدم selectImageLauncher (الذي هو GetContent) لجميع الحالات.


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.d(
                TAG,
                "selectImage function called. Launching image picker using GetContent (selectImageLauncher)."
            )
            try {
                selectImageLauncher.launch("image/*")
            } catch (e: Exception) {
                // قد يحدث خطأ إذا لم يكن هناك تطبيق لمعالجة الانتنت
                Log.e(TAG, "Error launching selectImageLauncher (GetContent)", e)
                Toast.makeText(
                    requireContext(),
                    "Cannot open image picker. No suitable application found.",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
            }
        }
    }

    private fun showPermissionRationaleDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.permission_required))
            .setMessage(getString(R.string.this_permission_is_required_to_perform_the_operation))
            .setPositiveButton(getString(R.string.allow)) { _, _ ->
                requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            .setNegativeButton(getString(R.string.deny)) { _, _ ->
                // Handle the case when the user denies the permission
            }
            .show()
    }

    /**
     * تحويل معرف مورد Drawable إلى ByteArray.
     * @param drawableId معرف المورد (مثل R.drawable.account).
     * @param format صيغة الضغط (مثل Bitmap.CompressFormat.PNG).
     * @param quality جودة الضغط (0-100).
     * @return بيانات الصورة كـ ByteArray أو null في حالة الخطأ.
     */
    private fun drawableToByteArray(
        drawableId: Int,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
        quality: Int = 90
    ): ByteArray? {
        return try {
            // طريقة أكثر أمانًا للحصول على Drawable والتعامل مع أنواع مختلفة (مثل VectorDrawable)
            val drawable = ContextCompat.getDrawable(requireContext(), drawableId)
            if (drawable == null) {
                Log.e(TAG, "Drawable resource not found for ID: $drawableId")
                return null
            }

            // تحويل الـ Drawable إلى Bitmap
            // تحديد حجم افتراضي إذا كان Drawable لا يملك حجمًا ذاتيًا (مثل بعض VectorDrawables)
            val bitmap = drawable.toBitmap(
                width = drawable.intrinsicWidth.coerceAtLeast(1),
                height = drawable.intrinsicHeight.coerceAtLeast(1)
            )

            // ضغط الـ Bitmap إلى ByteArrayOutputStream
            val baos = ByteArrayOutputStream()
            bitmap.compress(format, quality, baos)

            // إرجاع الـ ByteArray
            baos.toByteArray()
        } catch (e: Exception) {
            Log.e(TAG, "Error converting drawable to byte array", e)
            null // إرجاع null في حالة حدوث أي خطأ
        }
    }
    // ---> نهاية الدالة المساعدة <---

    // دالة التحويل التي تم إنشاؤها في الخطوة 1
    private fun getBytesFromUri(context: Context, uri: Uri): ByteArray? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.readBytes()
            }
        } catch (e: Exception) { // استخدام Exception لالتقاط أي خطأ محتمل (IOException, SecurityException, etc.)
            Log.e(TAG, "Error reading bytes from URI: $uri", e)
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}