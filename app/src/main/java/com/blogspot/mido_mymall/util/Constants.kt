package com.blogspot.mido_mymall.util

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.Navigation
import com.blogspot.mido_mymall.R
import com.blogspot.mido_mymall.databinding.SignInDialogBinding
import com.blogspot.mido_mymall.ui.home.HomeFragmentDirections
import com.blogspot.mido_mymall.ui.product_details.ProductDetailsFragmentDirections

object Constants {

    const val WEB_CLIENT_ID =
        "119751966934-4tmtpcpheeid9r1j7foa7d6uf27rgv33.apps.googleusercontent.com"


    const val PAY_MOB_BASE_URL = "https://accept.paymob.com/api/"
    const val GET_AUTH_TOKEN = "/auth/tokens/"
    const val PAYMENT_API_KEY = "ZXlKaGJHY2lPaUpJVXpVeE1pSXNJblI1Y0NJNklrcFhWQ0o5LmV5SmpiR0Z6Y3lJNklrMWxjbU5vWVc1MElpd2ljSEp2Wm1sc1pWOXdheUk2T0RVM016YzJMQ0p1WVcxbElqb2lhVzVwZEdsaGJDSjkuSEUxQWJWT2ZLdWRqVG1ZdHJkNTRqNUEwSWd4TThpT1pXU1dPR1ZVZlBtSVBfZU55YjlMT0dDWW9oLXBYSVNBQmtBREE2ek90ejRTdXM4NW1ZQ3doWWc="

    const val GET_ORDER_ID = "/ecommerce/orders/"

    const val RAZORPAY_BASE_URL = "https://api.razorpay.com/v1/"

    var paymentFirstToken = ""
    var paymentOrderId = ""

    @JvmStatic
    fun signInSignUpDialog(
        context: Context,
        destination: Int,
        layoutInflater: LayoutInflater,
        view: View
    ) {
        val dialog = Dialog(context)
        val signInDialogBinding: SignInDialogBinding =
            SignInDialogBinding.inflate(layoutInflater, null, false)
        dialog.setContentView(signInDialogBinding.root)
        signInDialogBinding.signInButton.setOnClickListener {
//            Toast.makeText(context, "sign in", Toast.LENGTH_SHORT).show()
            dialog.cancel()

            if (destination == R.id.homeFragment) {
                Navigation.findNavController(view)
                    .navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
            } else if (destination == R.id.productDetailsFragment) {
                Navigation.findNavController(view)
                    .navigate(ProductDetailsFragmentDirections.actionGlobalLoginFragment())
            }
        }
        signInDialogBinding.signUpButton.setOnClickListener {
            dialog.cancel()
            if (destination == R.id.homeFragment) {
                Navigation.findNavController(view)
                    .navigate(HomeFragmentDirections.actionHomeFragmentToRegisterFragment())
            } else if (destination == R.id.productDetailsFragment) {
                Navigation.findNavController(view)
                    .navigate(ProductDetailsFragmentDirections.actionProductDetailsFragmentToRegisterFragment())
            }
        }
        dialog.show()
    }

    fun setProgressDialog(context: Context): AlertDialog {
        val llPadding = 30
        val ll = LinearLayout(context)
        ll.orientation = LinearLayout.HORIZONTAL
        ll.setPadding(llPadding, llPadding, llPadding, llPadding)
        ll.gravity = Gravity.CENTER
        var llParam = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        llParam.gravity = Gravity.CENTER
        ll.layoutParams = llParam
        val progressBar = ProgressBar(context)
        progressBar.isIndeterminate = true
        progressBar.setPadding(0, 0, llPadding, 0)
        progressBar.layoutParams = llParam
        llParam = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        llParam.gravity = Gravity.CENTER
        val tvText = TextView(context)
        tvText.text = "Loading ..."

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tvText.setTextColor(context.resources.getColor(R.color.black,context.theme))
        }else {
            @Suppress("DEPRECATION")
            tvText.setTextColor(context.resources.getColor(R.color.black))
        }

        tvText.textSize = 20f
        tvText.layoutParams = llParam
        ll.addView(progressBar)
        ll.addView(tvText)
        val builder = AlertDialog.Builder(context)
        builder.setCancelable(false)
        builder.setView(ll)
        val dialog = builder.create()
        //        dialog.show();
        val window = dialog.window
        if (window != null) {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window!!.attributes)
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            dialog.window!!.attributes = layoutParams
        }
        return dialog
    }

}