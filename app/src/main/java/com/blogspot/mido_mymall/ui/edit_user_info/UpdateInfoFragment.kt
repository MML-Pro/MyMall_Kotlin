package com.blogspot.mido_mymall.ui.edit_user_info

import android.app.Activity
import android.app.Dialog
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

    private var allUpdatesSuccessful = false

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

            removePhotoButton.setOnClickListener {
                profileImage.setImageResource(R.drawable.account)
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

                } else if (!(currentUserName.equals(newUserName)) && currentEmail.equals(newEmail) && !this@UpdateInfoFragment::userImageUri.isInitialized
                ) {


                    nameEditText.error = null
                    updateUserInfoViewModel.updateUserName(newUserName)
                } else if ((currentUserName.equals(newUserName)) && !currentEmail.equals(newEmail) && !this@UpdateInfoFragment::userImageUri.isInitialized
                ) {
                    emailEditText.error = null


                    this@UpdateInfoFragment.newEmail = newEmail
                    reAuthenticateDialog(newEmail)


                } else if (currentUserName.equals(newUserName) && currentEmail.equals(newEmail) && this@UpdateInfoFragment::userImageUri.isInitialized
                ) {
                    nameEditText.error = null
                    emailEditText.error = null

                    val data = imageFromUriToByteArray(userImageUri)
                    updateUserInfoViewModel.updateUserProfileImage(data)

                } else if (!currentUserName.equals(newUserName) && !currentEmail.equals(newEmail) && !this@UpdateInfoFragment::userImageUri.isInitialized
                ) {

                    nameEditText.error = null
                    emailEditText.error = null

                    updateUserInfoViewModel.updateUserName(newUserName)

                    this@UpdateInfoFragment.newEmail = newEmail
                    reAuthenticateDialog(newEmail)

                } else if (!currentUserName.equals(newUserName) && currentEmail.equals(newEmail) && this@UpdateInfoFragment::userImageUri.isInitialized
                ) {

                    updateUserInfoViewModel.updateUserName(newUserName)
                    val data = imageFromUriToByteArray(userImageUri)
                    updateUserInfoViewModel.updateUserProfileImage(data)

                } else if (currentUserName.equals(newUserName) && !currentEmail.equals(newEmail) && this@UpdateInfoFragment::userImageUri.isInitialized
                ) {

                    emailEditText.error = null

                    this@UpdateInfoFragment.newEmail = newEmail
                    reAuthenticateDialog(newEmail)
                    val data = imageFromUriToByteArray(userImageUri)
                    updateUserInfoViewModel.updateUserProfileImage(data)

                } else if (!currentUserName.equals(newUserName) && !currentEmail.equals(newEmail) && this@UpdateInfoFragment::userImageUri.isInitialized) {
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
            repeatOnLifecycle(Lifecycle.State.STARTED){
                mainActivityViewModel.userInfo.collect{
                    if(it is Resource.Success){
                        it.data?.let { documentSnapshot->
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

                            (requireActivity() as MainActivity).navHeaderMainBinding.mainUserName.text = currentUserName
                            (requireActivity() as MainActivity).navHeaderMainBinding.mainEmail.text = currentEmail
                        }

                    }else if(it is Resource.Error){
                        Log.e(TAG, "onCreate: ${it.message.toString()}" )
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
                                "username updated successfully",
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
                                "Email updated successfully",
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
                            Toast.makeText(
                                requireContext(),
                                "Profile image updated successfully",
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
        if (requestCode == REQUEST_CODE_SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    data.data?.let {
                        userImageUri = it
                    }
                }
            }
        }
    }


    private fun selectImage() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            pickMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "image/*"
            selectImageLauncher.launch("image/*")
        } else {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
            }
        }
    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Permission Required")
            .setMessage("This permission is required to perform the operation.")
            .setPositiveButton("Grant") { _, _ ->
                requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            .setNegativeButton("Deny") { _, _ ->
                // Handle the case when the user denies the permission
            }
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}