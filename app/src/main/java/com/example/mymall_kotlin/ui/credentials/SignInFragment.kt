package com.example.mymall_kotlin.ui.credentials

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
import com.example.mymall_kotlin.databinding.FragmentSigninBinding
import com.example.mymall_kotlin.ui.MainActivity
import com.example.mymall_kotlin.ui.MainActivityViewModel
import com.example.mymall_kotlin.util.Resource
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

    private val googleActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
//           val data: Intent? = result.data

                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)


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
                val email = s.toString()
                val isValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
                if (!isValid) {
                    // Show an error message
                    binding.inputEmail.error = "Invalid email"
                } else {
                    // Clear the error message
                    binding.inputEmail.error = null
                }

                // Update the button state
                updateButtonState()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        binding.inputPassword.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = s.toString()
                val isValid = password.length >= 6
                if (!isValid) {
                    // Show an error message
                    binding.inputPassword.error = "Password must have at least 6 characters"
                } else {
                    // Clear the error message
                    binding.inputPassword.error = null
                }
                // Update the button state
                updateButtonState()
            }

            override fun afterTextChanged(s: Editable?) {

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

                            (requireActivity() as MainActivity).apply {
                                signOutItem?.isEnabled = true
                                mainActivityViewModel.getUserInfo()
                            }

                            findNavController().navigate(
                                SignInFragmentDirections.actionLoginFragmentToHomeFragment()
                            )

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

                            (requireActivity() as MainActivity).apply {
                                signOutItem?.isEnabled = true
                                mainActivityViewModel.getUserInfo()
                            }

                            findNavController().navigate(SignInFragmentDirections.actionLoginFragmentToHomeFragment())
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