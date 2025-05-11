package com.blogspot.mido_mymall.ui.my_cart

import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButton

interface MyCartUtil {

    fun deleteItem(position:Int)


    fun getTotalAmount(totalAmount:Double)

    fun onQuantityChanged(position: Int, newQuantity: Long) // <<< أضف هذه الدالة


}