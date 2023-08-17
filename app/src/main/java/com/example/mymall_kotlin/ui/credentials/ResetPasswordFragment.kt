package com.example.mymall_kotlin.ui.credentials

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation.findNavController
import androidx.transition.TransitionManager
import com.example.mymall_kotlin.R
import com.example.mymall_kotlin.databinding.FragmentResetPasswordBinding
import com.example.mymall_kotlin.ui.credentials.ResetPasswordViewModel
import com.example.mymall_kotlin.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ResetPasswordFragment : Fragment() {

    companion object{
        private const val TAG = "ResetPasswordFragment"
    }

    private var _binding:FragmentResetPasswordBinding?=null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ResetPasswordViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentResetPasswordBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editTextTextEmailAddress.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkInputs()
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

       binding.resetPasswordButton.setOnClickListener {
           viewModel.resetPassword(binding.editTextTextEmailAddress.text.toString())
       }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.resetPasswordState.collect{ response->
                    when(response){
                        is Resource.Loading ->{
                            TransitionManager.beginDelayedTransition(binding.EmailIconAndTextLinearLayout)
                            binding.emailConfirmationTV.visibility = View.GONE
                            binding.emailIcon.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.VISIBLE
                            binding.resetPasswordButton.isEnabled = false
                        }

                        is Resource.Success ->{

                            Log.d(TAG, "resetPasswordState: ${response.data.toString()}")


                            val scaleAnimation = ScaleAnimation(1f, 0f, 1f, 0f)
                            scaleAnimation.duration = 100
                            scaleAnimation.interpolator = AccelerateInterpolator()
                            scaleAnimation.repeatMode = Animation.REVERSE
                            scaleAnimation.repeatCount = 1

                            binding.emailConfirmationTV.text = getString(R.string.email_confirmation_success_message)
                            binding.emailIcon.setColorFilter(resources.getColor(R.color.success))
                            binding.emailConfirmationTV.setTextColor(
                                requireActivity().resources.getColor(R.color.success)
                            )
                            TransitionManager.beginDelayedTransition(binding.EmailIconAndTextLinearLayout)
                            binding.emailIcon.visibility = View.VISIBLE
                            binding.emailConfirmationTV.visibility = View.VISIBLE

                            scaleAnimation.setAnimationListener(object :
                                Animation.AnimationListener {
                                override fun onAnimationStart(animation: Animation) {}
                                override fun onAnimationEnd(animation: Animation) {

                                    Toast.makeText(requireContext(), "Animation done", Toast.LENGTH_SHORT).show()
                                    binding.progressBar.visibility = View.GONE
                                }

                                override fun onAnimationRepeat(animation: Animation) {}
                            })

                            binding.EmailIconAndTextLinearLayout.animation = scaleAnimation
                        }

                        is Resource.Error ->{
                            binding.progressBar.visibility = View.GONE
                            TransitionManager.beginDelayedTransition(
                                binding.EmailIconAndTextLinearLayout
                            )
                            binding.emailConfirmationTV.setTextColor(
                                requireActivity().resources.getColor(R.color.colorPrimary)
                            )
                            binding.emailConfirmationTV.text = response.message.toString()
                            binding.emailIcon.visibility = View.VISIBLE
                            binding.emailConfirmationTV.visibility = View.VISIBLE
                            binding.resetPasswordButton.isEnabled = true
                        }

                        else -> {}
                    }
                }
            }
        }

        binding.goBackTV.setOnClickListener {
            findNavController(requireView())
                .popBackStack()
        }


    }

    private fun checkInputs() {
        binding.resetPasswordButton.isEnabled =
            (!TextUtils.isEmpty(binding.editTextTextEmailAddress.text)
                    && Patterns.EMAIL_ADDRESS
                .matcher(binding.editTextTextEmailAddress.text).matches())
    }
}