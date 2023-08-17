package com.example.mymall_kotlin.ui.credentials

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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.mymall_kotlin.databinding.FragmentSignupBinding
import com.example.mymall_kotlin.domain.models.User
import com.example.mymall_kotlin.ui.MainActivity
import com.example.mymall_kotlin.ui.credentials.SignUpViewModel
import com.example.mymall_kotlin.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment() {


    private val TAG = "SignUpFragment"

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
//    private var firebaseAuth: FirebaseAuth? = null
//    private var firebaseFirestore: FirebaseFirestore? = null

    private val viewModel by viewModels<SignUpViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)

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
                viewModel.saveUserInfoState.collect { response ->
                    when (response) {
                        is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                        is Resource.Success -> {
                            binding.progressBar.visibility = View.GONE

                            Toast.makeText(
                                    requireContext(),
                                "user saved success ${response.data}",
                                Toast.LENGTH_SHORT
                            ).show()
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
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){

                viewModel.createUserDataState.collect{ response->

                    when(response){
                        is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE

                        is Resource.Success -> {
                            Log.d(TAG, "viewModel.createUserDataState: ${response.data.toString()}")

                            findNavController().navigate(SignUpFragmentDirections.actionRegisterFragmentToHomeFragment())
                        }

                        is Resource.Error ->{
                            Toast.makeText(requireContext(), response.message.toString(), Toast.LENGTH_SHORT).show()
                            Log.e(TAG, "onViewCreated: ${response.message.toString()}", )
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
            (requireActivity() as MainActivity).signOutItem?.isEnabled = false
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