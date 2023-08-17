package com.example.mymall_kotlin.ui.my_orders

import android.content.res.ColorStateList
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mymall_kotlin.R
import com.example.mymall_kotlin.databinding.MyOrderItemBinding
import com.example.mymall_kotlin.domain.models.MyOrderItemModel
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Transaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "MyOrderAdapter"

class MyOrderAdapter : RecyclerView.Adapter<MyOrderAdapter.ViewHolder>() {

//    private var myRatingsIds = arrayListOf<String>()
//    private var myRatings = arrayListOf<Long>()


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

//    fun submitRatingLists(myRatingsIds: ArrayList<String>, myRatings: ArrayList<Long>) {
//        this.myRatingsIds = myRatingsIds
//        this.myRatings = myRatings
//    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemModel: MyOrderItemModel = asyncListDiffer.currentList[position]
        holder.bind(itemModel)
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    inner class ViewHolder(private val binding: MyOrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(myOrderItemModel: MyOrderItemModel) {

            binding.productName.text = myOrderItemModel.productName

            Glide.with(binding.root.context).load(myOrderItemModel.productImage)
                .placeholder(R.drawable.placeholder_image)
                .into(binding.productImage)


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
                            R.color.colorPrimary
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

            myOrderItemModel.productRating?.toLong()?.let { setRating(it, true) }

            for (i in 1 until binding.rateNowContainer.childCount) {

                Log.d(TAG, "index: $i")

                val starPosition = i

                binding.rateNowContainer.getChildAt(i).setOnClickListener { view ->

//                    setRating(starPosition.toLong(), false)
//
//                    val documentReference: DocumentReference =
//                        FirebaseFirestore.getInstance().collection(
//                            "PRODUCTS"
//                        ).document(myOrderItemModel.productID!!)
//
//                    FirebaseFirestore.getInstance()
//                        .runTransaction { transaction ->
//                            val documentSnapshot = transaction.get(documentReference)
//
//                            if (myOrderItemModel.productRating != 0) {
//                                val increase =
//                                    documentSnapshot.getLong(((starPosition + 1).toString() + "_star") + 1)
//
//                                val decrease = (documentSnapshot.getLong(
//                                    (myOrderItemModel.productRating?.plus(1)).toString() + "_star"))!! -1
//
//
//                                transaction.update(
//                                    documentReference,
//                                    (starPosition +1).toString() + "_star",
//                                    increase
//                                )
//                                transaction.update(
//                                    documentReference,
//                                    (myOrderItemModel.productRating?.plus(1)).toString() + "_star",
//                                    decrease
//                                )
//
//                            } else {
//                                val increase =
//                                    documentSnapshot.getLong(((starPosition+1).toString() + "_star") + 1)
//
//                                transaction.update(documentReference, (starPosition+1).toString()+ "_star",
//                                    increase
//                                )
//                            }
//                        }.addOnSuccessListener {
//                            myOrderItemModel.productRating = starPosition+1
//
//                            if (myRatingsIds.contains(myOrderItemModel.productID)) {
//                                myRatings[myRatingsIds.indexOf(myOrderItemModel.productID)] =
//                                    starPosition.toLong()+1
//                            } else {
//                                myRatingsIds.add(myOrderItemModel.productID!!)
//                                myRatings.add(starPosition.toLong() +1)
//                            }
//
//                        }

                }
            }


            binding.root.setOnClickListener { view ->
                findNavController(view).navigate(
                    MyOrdersFragmentDirections.actionNavMyOrdersToOrderDetailsFragment(
                        myOrderItemModel
                    )
                )
            }
        }

        @Suppress("DEPRECATION")
        private fun setRating(starPosition: Long, isGetRating: Boolean) {
            for (i in 0 until binding.rateNowContainer.childCount) {
                val starBtn = binding.rateNowContainer.getChildAt(i) as ImageView
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    starBtn.imageTintList =
                        ColorStateList.valueOf(
                            binding.root.resources.getColor(
                                R.color.mediumGray,
                                binding.root.context.theme
                            )
                        )
                } else {
                    starBtn.imageTintList =
                        ColorStateList.valueOf(binding.root.resources.getColor(R.color.mediumGray))
                }

                if (isGetRating) {
                    if (i < starPosition) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            starBtn.imageTintList =
                                ColorStateList.valueOf(
                                    binding.root.resources.getColor(
                                        R.color.ratingColor,
                                        binding.root.context.theme
                                    )
                                )
                        } else {
                            starBtn.imageTintList =
                                ColorStateList.valueOf(binding.root.resources.getColor(R.color.ratingColor))
                        }
                    }
                } else {
                    if (i <= starPosition) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            starBtn.imageTintList =
                                ColorStateList.valueOf(
                                    binding.root.resources.getColor(
                                        R.color.ratingColor,
                                        binding.root.context.theme
                                    )
                                )
                        } else {
                            starBtn.imageTintList =
                                ColorStateList.valueOf(binding.root.resources.getColor(R.color.ratingColor))
                        }
                    }
                }
            }
        }


    }
}