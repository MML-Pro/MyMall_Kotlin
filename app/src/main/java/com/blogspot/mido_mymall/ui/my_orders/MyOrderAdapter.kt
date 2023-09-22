package com.blogspot.mido_mymall.ui.my_orders

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.blogspot.mido_mymall.R
import com.blogspot.mido_mymall.databinding.MyOrderItemBinding
import com.blogspot.mido_mymall.domain.models.MyOrderItemModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.log

private const val TAG = "MyOrderAdapter"

class MyOrderAdapter : RecyclerView.Adapter<MyOrderAdapter.ViewHolder>() {


    private val diffCallback = object : DiffUtil.ItemCallback<MyOrderItemModel>() {
        override fun areItemsTheSame(
            oldItem: MyOrderItemModel,
            newItem: MyOrderItemModel
        ): Boolean {
            return oldItem.orderID == newItem.orderID
        }

        override fun areContentsTheSame(
            oldItem: MyOrderItemModel,
            newItem: MyOrderItemModel
        ): Boolean {
            return oldItem == newItem
        }

    }

    val asyncListDiffer = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val myOrderItemBinding: MyOrderItemBinding = MyOrderItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(myOrderItemBinding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemModel: MyOrderItemModel = asyncListDiffer.currentList[position]
        holder.bind(itemModel)

    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    inner class ViewHolder(private val binding: MyOrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        //        private val productNamesAdapter = ProductNamesAdapter()
//        private val productNamesList = arrayListOf<String>()
        fun bind(myOrderItemModel: MyOrderItemModel) {


            binding.orderIdTV.text = "Order ID: ${myOrderItemModel.orderID}"

            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            // Set margin values in pixels
            val marginStart =
                binding.root.resources.getDimensionPixelSize(R.dimen.my_orders_text_views_margin_start)
            val marginTop =
                binding.root.resources.getDimensionPixelSize(R.dimen.my_orders_text_views_margin_top)
            val marginEnd =
                binding.root.resources.getDimensionPixelSize(R.dimen.my_orders_text_views_margin_end)
            val marginBottom =
                binding.root.resources.getDimensionPixelSize(R.dimen.my_orders_text_views_margin_bottom)

            // Loop through the product list
            for (product in myOrderItemModel.productList) {
                val productNameTextView = TextView(binding.root.context).apply {
                    text = product.productName
                    ellipsize = TextUtils.TruncateAt.END
                    maxLines = 2
                    setTextColor(ContextCompat.getColor(context, R.color.productDetailsTextColor))
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen._12ssp))
//                    setTypeface(typeface, Typeface.BOLD)
                    layoutParams.setMargins(marginStart, marginTop, marginEnd, marginBottom)
                }
                binding.productNamesContainer.addView(productNameTextView, layoutParams)
            }


//            Log.d(TAG, "bind: ${myOrderItemModel.productList.size}")
//
//            myOrderItemModel.productList.forEach {
//                Log.d(TAG, "bind: productName $${it.productName}")
//            }
//
//
//           myOrderItemModel.productList.forEach {
//               productNamesList.add(it.productName)
//           }.also {
//               productNamesAdapter.asyncListDiffer.submitList(productNamesList)
//           }
//
//            binding.productNamesRV.apply {
//                adapter = productNamesAdapter
//                layoutManager = LinearLayoutManager(binding.root.context,LinearLayoutManager.VERTICAL,false)
//            }


            val date: Date? = when (myOrderItemModel.orderStatus) {
                "ORDERED" -> myOrderItemModel.orderDate!!
                "PACKED" -> myOrderItemModel.packedDate!!
                "SHIPPED" -> myOrderItemModel.shippedDate!!
                "DELIVERED" -> myOrderItemModel.deliveredDate!!
                "CANCELLED" -> myOrderItemModel.cancelledDate!!
                else -> {
                    null
                }
            }

            val simpleDateFormat = SimpleDateFormat("EEE, d MMM yyyy", Locale.ENGLISH)

            val formattedDate = simpleDateFormat.format(date!!)


            binding.orderDeliveredDate.text = "${myOrderItemModel.orderStatus} on " + formattedDate

            if (myOrderItemModel.orderStatus == "CANCELLED") {
                binding.orderIndicator.imageTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.btnRed
                        )
                    )

            } else {
                binding.orderIndicator.imageTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.success
                        )
                    )
            }
//            binding.ratingBar.rating = myOrderItemModel.productRating


            binding.root.setOnClickListener { view ->
                findNavController(view).navigate(
                    MyOrdersFragmentDirections.actionNavMyOrdersToOrderDetailsFragment(
                        myOrderItemModel
                    )
                )
            }
        }


    }
}