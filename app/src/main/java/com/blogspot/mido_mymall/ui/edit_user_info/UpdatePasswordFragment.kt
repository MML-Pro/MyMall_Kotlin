package com.blogspot.mido_mymall.ui.edit_user_info

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
import com.blogspot.mido_mymall.R
import com.blogspot.mido_mymall.databinding.FragmentUpdatePasswordBinding
import com.blogspot.mido_mymall.ui.MainActivity
import com.blogspot.mido_mymall.util.Constants
import com.blogspot.mido_mymall.util.Resource
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
                                getString(R.string.password_updated_successfully),
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
            oldPassword.isEmpty() -> showError(binding.oldPasswordED,
                getString(R.string.old_password_is_empty))
            oldPassword.length < 6 -> showError(
                binding.oldPasswordED,
                getString(R.string.old_password_is_below_than_6_characters)
            )

            newPassword.isEmpty() -> showError(binding.newtPasswordED,
                getString(R.string.new_password_is_empty))
            confirmationPassword.isEmpty() -> showError(
                binding.confirmNewPassword,
                getString(R.string.confirm_password_is_empty)
            )

            oldPassword == newPassword -> showError(
                binding.newtPasswordED,
                getString(R.string.new_password_cannot_be_the_old_one)
            )

            newPassword.length < 6 -> showError(
                binding.newtPasswordED,
                getString(R.string.new_password_cannot_be_below_than_6_characters)
            )

            confirmationPassword.length < 6 -> showError(
                binding.confirmNewPassword,
                getString(R.string.new_password_cannot_be_below_than_6_characters)
            )

            newPassword != confirmationPassword -> showError(
                binding.confirmNewPassword,
                getString(R.string.new_password_not_equal_the_confirmation_password)
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