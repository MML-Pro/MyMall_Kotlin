package com.example.mymall_kotlin.ui.edit_user_info

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.mymall_kotlin.R
import com.example.mymall_kotlin.databinding.FragmentUpdatePasswordBinding
import com.example.mymall_kotlin.ui.MainActivity
import com.example.mymall_kotlin.util.Constants
import com.example.mymall_kotlin.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "UpdatePasswordFragment"

@AndroidEntryPoint
class UpdatePasswordFragment : Fragment() {

    private var _binding: FragmentUpdatePasswordBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<UpdateUserPasswordViewModel>()

    private var currentEmail = ""

    private val loadingDialog by lazy { Constants.setProgressDialog(requireContext()) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUpdatePasswordBinding.inflate(inflater, container, false)

        currentEmail = (requireActivity() as MainActivity).userEmail

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.updateButton.setOnClickListener {
            
            updatePassword()
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.updateUserPasswordState.collect { response ->
                    when (response) {
                        is Resource.Loading -> {
                            loadingDialog.show()
                        }

                        is Resource.Success -> {
                            loadingDialog.cancel()
                            Toast.makeText(
                                requireContext(),
                                "password updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is Resource.Error -> {
                            loadingDialog.cancel()
                            Toast.makeText(
                                requireContext(),
                                response.message.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                            Log.e(TAG, "updateUserPasswordState: ${response.message.toString()}")
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun updatePassword() {
        val oldPassword = binding.oldPasswordED.text.toString()
        val newPassword = binding.newtPasswordED.text.toString()
        val confirmationPassword = binding.confirmNewPassword.text.toString()

        when {
            oldPassword.isEmpty() -> showError(binding.oldPasswordED, "old password is empty")
            oldPassword.length < 6 -> showError(
                binding.oldPasswordED,
                "old password is below than 6 characters"
            )

            newPassword.isEmpty() -> showError(binding.newtPasswordED, "new password is empty")
            confirmationPassword.isEmpty() -> showError(
                binding.confirmNewPassword,
                "confirm password is empty"
            )

            oldPassword == newPassword -> showError(
                binding.newtPasswordED,
                "new password cannot be the old one"
            )

            newPassword.length < 6 -> showError(
                binding.newtPasswordED,
                "new password cannot be below than 6 characters"
            )

            confirmationPassword.length < 6 -> showError(
                binding.confirmNewPassword,
                "new password cannot be below than 6 characters"
            )

            newPassword != confirmationPassword -> showError(
                binding.confirmNewPassword,
                "new password not equal the confirmation password"
            )

            else -> viewModel.updateUserPassword(currentEmail, oldPassword, newPassword)
        }
    }

    private fun showError(editText: EditText, errorMessage: String) {
        editText.apply {
            requestFocus()
            error = errorMessage
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}