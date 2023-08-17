package com.example.mymall_kotlin.ui.my_cart

import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButton

interface MyCartUtil {

    fun deleteItem(position:Int)


    fun getTotalAmount(totalAmount:Int)

}