package com.blogspot.mido_mymall.ui.credentials

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.blogspot.mido_mymall.R
import com.blogspot.mido_mymall.databinding.FragmentSigninBinding
import com.blogspot.mido_mymall.ui.MainActivity
import com.blogspot.mido_mymall.ui.MainActivityViewModel
import com.blogspot.mido_mymall.util.Resource
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignInFragment : Fragment() {

    companion object {
        private const val TAG = "SignInFragment"
    }


    private var _binding: FragmentSigninBinding? = null
    private val binding get() = _binding!!
//    private FirebaseAuth firebaseAuth;

    private val signInViewModel by viewModels<SignInViewModel>()
    private val signUpViewModel by viewModels<SignUpViewModel>()

    private val signOutViewModel by viewModels<SignOutViewModel>()

    private var userName = ""
    private var userEmail = ""

    private val googleActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
//           val data: Intent? = result.data


                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)

                userName = account.displayName.toString()
                userEmail = account.email.toString()


                signInViewModel.signInWithCredential(credential)

                Log.d(TAG, "googleActivityResultLauncher: ${account.email.toString()}")

            }
        }

    private val mainActivityViewModel by activityViewModels<MainActivityViewModel>()


    //    private FirebaseAuth firebaseAuth;
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSigninBinding.inflate(inflater, container, false)
        //        firebaseAuth = FirebaseAuth.getInstance();
//        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).hide();
//        setHasOptionsMenu(false);

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(signOutViewModel.firebaseAuth.currentUser == null){
            (requireActivity()as MainActivity).destroyAdAfterLogOut()
        }

        binding.btnlogin.setOnClickListener {
            signInViewModel.signIn(
                binding.inputEmail.text.toString(),
                binding.inputPassword.text.toString()
            )
        }

        binding.inputEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.inputEmail.error = null
            }

            override fun afterTextChanged(s: Editable?) {
                val email = s.toString()
                val isValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()

                if (!isValid) {
                    // Show an error message
                    binding.inputEmail.error = getString(R.string.invalid_email)
                } else {
                    // Clear the error message
                    binding.inputEmail.error = null
                }

                // Update the button state
                updateButtonState()
            }

        })

        binding.inputPassword.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.inputPassword.error = null
            }

            override fun afterTextChanged(s: Editable?) {

                val password = s.toString()
                val isValid = password.length >= 6
                if (!isValid) {
                    // Show an error message
                    binding.inputPassword.error =
                        getString(R.string.password_must_have_at_least_6_characters)
                } else {
                    // Clear the error message
                    binding.inputPassword.error = null
                }
                // Update the button state
                updateButtonState()
            }

        })

//        lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                signInViewModel.validation.collect { validation ->
//                    if (validation.email is RegisterValidation.Failed) {
//                        withContext(Dispatchers.Main) {
//                            binding.progressBar.visibility = View.GONE
//                            binding.inputEmail.apply {
//                                requestFocus()
//                                error = validation.email.message
//                            }
//                        }
//                    }
//
//                    if (validation.password is RegisterValidation.Failed) {
//                        withContext(Dispatchers.Main) {
//                            binding.progressBar.visibility = View.GONE
//                            binding.inputPassword.apply {
//                                requestFocus()
//                                error = validation.password.message
//                            }
//                        }
//
//                    }
//                }
//            }
//        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                signInViewModel.login.collect { response ->
                    when (response) {
                        is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                        is Resource.Success -> {
                            binding.progressBar.visibility = View.GONE

                            findNavController().navigate(
                                SignInFragmentDirections.actionLoginFragmentToHomeFragment()
                            ).also {
                                (requireActivity() as MainActivity).apply {
                                    signOutItem?.isEnabled = true
                                    mainActivityViewModel.getUserInfo()
                                    (requireActivity()as MainActivity).requestHomeBanner()
                                }
                            }

                            Log.d(TAG, "signInViewModel.login: ${response.data.toString()}")
                        }

                        is Resource.Error -> {
                            binding.progressBar.visibility = View.GONE
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

        binding.btnGoogle.setOnClickListener {
            signInViewModel.signInGoogle()
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                signInViewModel.googleIntent.collectLatest {
                    when (it) {
                        is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                        is Resource.Success -> {

                            googleActivityResultLauncher.launch(it.data)
                        }

                        is Resource.Error -> {
                            binding.progressBar.visibility = View.GONE
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
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                signInViewModel.credentialSignInResult.collect { response ->
                    when (response) {
                        is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE

                        is Resource.Success -> {
                            binding.progressBar.visibility = View.GONE

                            signUpViewModel.saveUserInfo(
                                signOutViewModel.firebaseAuth.currentUser?.uid!!,
                                userName,
                                userEmail
                            )

                            (requireActivity() as MainActivity).requestHomeBanner()

                        }

                        is Resource.Error -> {
                            Toast.makeText(
                                requireContext(),
                                response.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e(TAG, "credentialSignInResult: ${response.message.toString()}")
                        }

                        else -> {}
                    }

                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                signUpViewModel.saveUserInfoState.collect { response ->
                    when (response) {
                        is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                        is Resource.Success -> {
                            binding.progressBar.visibility = View.GONE

//                            Toast.makeText(
//                                requireContext(),
//                                "user saved success ${response.data}",
//                                Toast.LENGTH_SHORT
//                            ).show()

                            signUpViewModel.createUserData()

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
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){

                signUpViewModel.createUserDataState.collect{ response->

                    when(response){
                        is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE

                        is Resource.Success -> {
                            Log.d(TAG, "signUpViewModel.createUserDataState: ${response.data.toString()}")

                            findNavController().navigate(SignInFragmentDirections.actionLoginFragmentToHomeFragment()).also {
                                (requireActivity() as MainActivity).apply {
                                    signOutItem?.isEnabled = true
                                    mainActivityViewModel.getUserInfo()
                                }
                            }


                        }

                        is Resource.Error ->{
                            Toast.makeText(requireContext(), response.message.toString(), Toast.LENGTH_SHORT).show()
                            Log.e(TAG, "onViewCreated: ${response.message.toString()}" )
                        }

                        else -> {}
                    }

                }

            }
        }

        binding.textViewSignUp.setOnClickListener {
            findNavController().navigate(SignInFragmentDirections.actionLoginFragmentToRegisterFragment())
        }

        binding.forgotPasswordTV.setOnClickListener {
            findNavController().navigate(SignInFragmentDirections.actionLoginFragmentToResetPasswordFragment())
        }


    }

    fun updateButtonState() {
        // Get the input values
        val email = binding.inputEmail.text.toString()
        val password = binding.inputPassword.text.toString()

        // Check if both are valid
        val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordValid = password.length >= 6

        // Enable or disable the button accordingly
        binding.btnlogin.isEnabled = isEmailValid && isPasswordValid
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}