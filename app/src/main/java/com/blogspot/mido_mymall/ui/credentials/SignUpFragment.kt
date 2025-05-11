package com.blogspot.mido_mymall.ui.credentials

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.blogspot.mido_mymall.databinding.FragmentSignupBinding
import com.blogspot.mido_mymall.domain.models.User
import com.blogspot.mido_mymall.ui.MainActivity
import com.blogspot.mido_mymall.ui.MainActivityViewModel
import com.blogspot.mido_mymall.util.RegisterValidation
import com.blogspot.mido_mymall.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class SignUpFragment : Fragment() {


    private val TAG = "SignUpFragment"

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
//    private var firebaseAuth: FirebaseAuth? = null
//    private var firebaseFirestore: FirebaseFirestore? = null

    private val viewModel by viewModels<SignUpViewModel>()
    private val mainActivityViewModel by activityViewModels<MainActivityViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        (requireActivity() as MainActivity).signOutItem?.isEnabled = false

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.inputUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkInputs()
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        binding.inputEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkInputs()
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        binding.inputPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkInputs()
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        binding.inputConformPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkInputs()
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        binding.btnRegister.setOnClickListener {

            val user = User(
                binding.inputUsername.text.toString(),
                binding.inputEmail.text.toString()
            )

            viewModel.createAccountWithEmailAndPassword(
                user,
                binding.inputPassword.text.toString(),
                binding.inputConformPassword.text.toString()
            )
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.register.collect { response ->
                    when (response) {
                        is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                        is Resource.Success -> {
                            binding.progressBar.visibility = View.GONE

                            viewModel.saveUserInfo(
                                viewModel.firebaseAuth.currentUser!!.uid,
                                binding.inputUsername.text.toString(),
                                binding.inputEmail.text.toString()
                            )

                        }

                        is Resource.Error -> {
                            Log.e(TAG, " viewModel.register: ${response.message}")
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(
                                requireContext(),
                                response.message.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        else -> {
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.registerFailedStatesValidation.collect { validationState ->
                    Log.d("SignUpFragment", "تم استقبال حالة التحقق: $validationState")

                    // مهم: تأكد من إخفاء مؤشر التقدم هنا أيضًا
                    // لأن هذا قد يتم استدعاؤه بعد معالجة الحالة الرئيسية
                    binding.progressBar.visibility = View.GONE

                    // --- التعامل مع نتيجة التحقق من الإيميل ---
                    when (val emailResult = validationState.email) {
                        is RegisterValidation.Failed -> {
                            // وضع رسالة الخطأ على حقل الإيميل

                            Toast.makeText(
                                requireContext(),
                                emailResult.message.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        is RegisterValidation.Success -> {
                            // مسح أي خطأ سابق من حقل الإيميل
//                            binding.emailEditText.error = null
                        }
                        // قد تحتاج لأنواع فرعية أخرى إذا كان لديك أخطاء إيميل محددة
                    }

                    // --- التعامل مع نتيجة التحقق من كلمة المرور ---
                    when (val passwordResult = validationState.password) {
                        is RegisterValidation.Failed -> {
                            Toast.makeText(
                                requireContext(),
                                passwordResult.message.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        is RegisterValidation.Success -> {
                            // مسح الأخطاء من كلا حقلي كلمة المرور

                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.saveUserInfoState.collect { response ->
                    when (response) {
                        is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                        is Resource.Success -> {
                            binding.progressBar.visibility = View.GONE

//                            Toast.makeText(
//                                    requireContext(),
//                                "user saved success ${response.data}",
//                                Toast.LENGTH_SHORT
//                            ).show()
                            viewModel.createUserData()

                        }

                        is Resource.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(
                                requireContext(),
                                response.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {}
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.createUserDataState.collect { response ->

                    when (response) {
                        is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE

                        is Resource.Success -> {
                            Log.d(TAG, "viewModel.createUserDataState: ${response.data.toString()}")

                            findNavController().navigate(SignUpFragmentDirections.actionRegisterFragmentToHomeFragment()).also {
                                mainActivityViewModel.getUserInfo()
                                (requireActivity() as MainActivity).signOutItem?.isEnabled = true
                            }
                        }

                        is Resource.Error -> {
                            Toast.makeText(
                                requireContext(),
                                response.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e(TAG, "onViewCreated: ${response.message.toString()}")
                        }

                        else -> {}
                    }

                }

            }
        }

        binding.alreadyHaveAccount.setOnClickListener {
            findNavController().navigate(SignUpFragmentDirections.actionRegisterFragmentToLoginFragment())
        }

        binding.closeIcon.setOnClickListener {
            (requireActivity() as MainActivity).apply {
                signOutItem?.isEnabled = false
                setNoUserInfoAfterSignOut()
            }
            findNavController().navigate(SignUpFragmentDirections.actionRegisterFragmentToHomeFragment())
        }

    }


    private fun checkInputs() {
        if (!TextUtils.isEmpty(binding.inputUsername.text)) {
            if (!TextUtils.isEmpty(binding.inputEmail.text)) {
                binding.btnRegister.isEnabled = (!TextUtils.isEmpty(binding.inputPassword.text)
                        && !TextUtils.isEmpty(binding.inputConformPassword.text)) && binding.inputPassword.text.toString()
                    .length >= 6 && binding.inputConformPassword.text.toString().length >= 6
            } else {
                binding.btnRegister.isEnabled = false
            }
        } else {
            binding.btnRegister.isEnabled = false
        }
    }

}