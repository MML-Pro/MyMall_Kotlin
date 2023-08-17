package com.example.mymall_kotlin.ui.my_cart

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mymall_kotlin.R
import com.example.mymall_kotlin.databinding.CartItemLayoutBinding
import com.example.mymall_kotlin.databinding.CartTotalAmountLayoutBinding
import com.example.mymall_kotlin.databinding.QuantityDialogBinding
import com.example.mymall_kotlin.domain.models.CartItemModel
import com.example.mymall_kotlin.domain.models.RewardModel

class CartAdapter(
    showDeleteButton: Boolean,
    private val cartTotalAmount: TextView? = null,
    private val myCartUtil: MyCartUtil? = null,
    private val deliveryUtil: DeliveryUtil? = null,
    private val isDeliveryFragment:Boolean

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var totalItemsPriceTV: TextView? = null
    private var totalAmountTV: TextView? = null

    companion object {
        private const val TAG = "CartAdapter"
    }

    private var rewardModelList: ArrayList<RewardModel>? = null

    //    private val cartTotalAmount: TextView
    private val showDeleteButton: Boolean

    private val differCallback = object : DiffUtil.ItemCallback<CartItemModel>() {
        override fun areItemsTheSame(oldItem: CartItemModel, newItem: CartItemModel): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: CartItemModel, newItem: CartItemModel): Boolean {
            return oldItem == newItem
        }

    }

    val asyncListDiffer = AsyncListDiffer(this, differCallback)

    private var lastPosition = -1


    init {
//        this.asyncListDiffer.currentList = asyncListDiffer.currentList
//        this.cartTotalAmount = cartTotalAmount
        this.showDeleteButton = showDeleteButton
//        setHasStableIds(true)
    }

//    override fun getItemId(position: Int): Long {
//        return asyncListDiffer.currentList[position].productId.hashCode().toLong()
//    }

    fun submitRewardList(rewardModelList: ArrayList<RewardModel>) {
        this.rewardModelList = rewardModelList
    }

    override fun getItemViewType(position: Int): Int {
        return when (asyncListDiffer.currentList[position].type) {
            0 -> CartItemModel.CART_ITEM
            1 -> CartItemModel.TOTAL_AMOUNT
            else -> -1
        }
    }

    fun deleteItem(position: Int) {
        val newList = asyncListDiffer.currentList.toMutableList()
        newList.removeAt(position)

        if (newList.size == 1) {
            asyncListDiffer.submitList(null)
        } else {
            asyncListDiffer.submitList(newList)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CartItemModel.CART_ITEM -> {
                val cartItemLayoutBinding: CartItemLayoutBinding = CartItemLayoutBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                CartItemViewHolder(cartItemLayoutBinding)
            }

            CartItemModel.TOTAL_AMOUNT -> {
                val cartTotalAmountLayoutBinding: CartTotalAmountLayoutBinding =
                    CartTotalAmountLayoutBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                CartTotalAmountViewHolder(cartTotalAmountLayoutBinding)
            }

            else -> {
                val cartItemLayoutBinding: CartItemLayoutBinding = CartItemLayoutBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                CartItemViewHolder(cartItemLayoutBinding)
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        when (asyncListDiffer.currentList[position].type) {
            CartItemModel.CART_ITEM -> {
                val cartItemModel: CartItemModel = asyncListDiffer.currentList[position]

                (holder as CartItemViewHolder).bind(cartItemModel, position)

                if (lastPosition < position) {
                    val animation = AnimationUtils.loadAnimation(
                        holder.itemView.context,
                        R.anim.fade_in
                    )
                    holder.itemView.animation = animation

                    lastPosition = position
                }
            }

            CartItemModel.TOTAL_AMOUNT -> {
                var totalItems = 0
                var totalItemsPrice = 0
                val deliveryPrice: String
                val totalAmount: Int
                var savedAmount = 0
//                var i = 0
                for (i in 0 until asyncListDiffer.currentList.size) {
                    if (asyncListDiffer.currentList[i].type == CartItemModel.CART_ITEM
                        && asyncListDiffer.currentList[i].inStock == true
                    ) {

                        val quantity = asyncListDiffer.currentList[i].productQuantity

                        totalItems = (totalItemsPrice + quantity!!).toInt()

                        totalItemsPrice += if (asyncListDiffer.currentList[position].selectedCouponId.isNullOrEmpty()) {
                            asyncListDiffer.currentList[i].productPrice?.toInt()!! * quantity.toInt()
                        } else {
                            asyncListDiffer.currentList[i].discountedPrice?.toInt()!! * quantity.toInt()
                        }

                        if (asyncListDiffer.currentList[i].cuttedPrice?.isNotEmpty()!!) {
                            savedAmount += (asyncListDiffer.currentList[i].cuttedPrice?.toInt()!! - asyncListDiffer.currentList[i].productPrice?.toInt()!!) * quantity.toInt()

                            if (!asyncListDiffer.currentList[position].selectedCouponId.isNullOrEmpty()) {
                                savedAmount += (asyncListDiffer.currentList[i].productPrice?.toInt()!! - asyncListDiffer.currentList[i].discountedPrice?.toInt()!!) * quantity.toInt()
                            }

                        } else {
                            if (asyncListDiffer.currentList[position].selectedCouponId?.isNotEmpty()!!) {
                                savedAmount += (asyncListDiffer.currentList[i].productPrice?.toInt()!! - asyncListDiffer.currentList[i].discountedPrice?.toInt()!!) * quantity.toInt()
                            }
                        }

                    }
                }
                if (totalItemsPrice > 500) {
                    deliveryPrice = "Free"
                    totalAmount = totalItemsPrice
                } else {
                    deliveryPrice = "60"
                    totalAmount = totalItemsPrice + 60
                }

//                String totalItems = asyncListDiffer.currentList.get(position).getTotalItems();
//                String totalItemPrice = asyncListDiffer.currentList.get(position).getTotalItemPrice();

                asyncListDiffer.currentList[position].totalItems = totalItems
                asyncListDiffer.currentList[position].totalItemsPrice = totalItemsPrice
                asyncListDiffer.currentList[position].deliveryPrice = deliveryPrice
                asyncListDiffer.currentList[position].totalAmount = totalAmount
                asyncListDiffer.currentList[position].savedAmount = savedAmount


                Log.d(TAG, "onBindViewHolder: totalItems $totalItems")
                Log.d(TAG, "onBindViewHolder: totalItems $totalItemsPrice")
                Log.d(TAG, "onBindViewHolder: totalItems $deliveryPrice")
                Log.d(TAG, "onBindViewHolder: totalItems $totalAmount")
                Log.d(TAG, "onBindViewHolder: totalItems $savedAmount")



                (holder as CartTotalAmountViewHolder).setTotalAmount(
                    totalItems, totalItemsPrice, deliveryPrice, totalAmount, savedAmount
                )
                myCartUtil?.getTotalAmount(totalAmount)

                if (lastPosition < position) {
                    val animation = AnimationUtils.loadAnimation(
                        holder.itemView.context,
                        R.anim.fade_in
                    )
                    holder.itemView.animation = animation

                    lastPosition = position
                }


            }

            else -> return
        }
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    internal inner class CartItemViewHolder(private val cartItemLayoutBinding: CartItemLayoutBinding) :
        RecyclerView.ViewHolder(cartItemLayoutBinding.root) {


        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(cartItemModel: CartItemModel, position: Int) {
//
//            cartItemLayoutBinding.productImage.setImageResource(cartItemModel.getProductImage());
            Glide.with(cartItemLayoutBinding.root)
                .load(cartItemModel.productImage)
                .placeholder(R.drawable.placeholder_image)
                .into(cartItemLayoutBinding.productImage)
            cartItemLayoutBinding.productName.text = cartItemModel.productName


            // In stock
            if (cartItemModel.inStock == true) {
                val freeCouponNo: Long = cartItemModel.freeCoupons!!
                if (freeCouponNo > 0) {
                    cartItemLayoutBinding.freeCouponIcon.visibility = View.VISIBLE
                    cartItemLayoutBinding.freeCouponTV.visibility = View.VISIBLE
                    if (freeCouponNo == 1L) {
                        cartItemLayoutBinding.freeCouponTV.text = "free $freeCouponNo Coupon"
                    } else {
                        cartItemLayoutBinding.freeCouponTV.text = "free $freeCouponNo Coupons"
                    }
                } else {
                    cartItemLayoutBinding.freeCouponIcon.visibility = View.INVISIBLE
                    cartItemLayoutBinding.freeCouponTV.visibility = View.INVISIBLE
                }
                cartItemLayoutBinding.cartCouponRedemptionLayout.visibility = View.VISIBLE

//                cartItemLayoutBinding


                if (asyncListDiffer.currentList[position].selectedCouponId != null) {
                    rewardModelList?.forEach {
                        cartItemLayoutBinding.cartCouponRedemptionLayout.background =
                            itemView.resources.getDrawable(
                                R.drawable.reward_gradient_background,
                                itemView.context.theme
                            )
                        cartItemLayoutBinding.couponRedemptionTV.text = it.couponBody
                        cartItemLayoutBinding.couponRedemptionButton.text = "Coupon"
                    }.also {
                        var offerDiscountedAmount: Long = 0


                        if (cartItemModel.productPrice != null && rewardModelList?.get(position)?.discountOrAmount != null) {
                            offerDiscountedAmount =
                                cartItemModel.productPrice!!.toLong() * rewardModelList?.get(
                                    position
                                )?.discountOrAmount?.toLong()!! / 100

                            cartItemLayoutBinding.couponsApplied.visibility = View.VISIBLE
                            cartItemLayoutBinding.couponsApplied.text =
                                "Coupon applied -${offerDiscountedAmount}"

                        }


                    }
                } else {
                    cartItemLayoutBinding.couponsApplied.visibility = View.INVISIBLE
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        cartItemLayoutBinding.cartCouponRedemptionLayout.setBackgroundColor(
                            itemView.resources.getColor(
                                R.color.couponRed,
                                itemView.context.theme
                            )
                        )
                    } else {
                        cartItemLayoutBinding.cartCouponRedemptionLayout.setBackgroundColor(
                            itemView.resources.getColor(
                                R.color.couponRed
                            )
                        )

                    }
                    cartItemLayoutBinding.couponRedemptionTV.text = "Apply your coupon here"
                    cartItemLayoutBinding.couponRedemptionButton.text = "Redeem"
                }


                cartItemLayoutBinding.productPrice.text =
                    "Rs. ${(cartItemModel.productPrice?.toInt()!! * cartItemModel.productQuantity!!)} /-"
                cartItemLayoutBinding.productPrice.setTextColor(
                    cartItemLayoutBinding.root
                        .resources.getColor(R.color.black)
                )
                cartItemLayoutBinding.cuttedPrice.text = "Rs. " + cartItemModel.cuttedPrice + " /-"
                cartItemLayoutBinding.productQty.visibility = View.VISIBLE

                cartItemLayoutBinding.productQty.text = "Qty: " + cartItemModel.productQuantity

                cartItemLayoutBinding.productQty.setOnClickListener { view ->
                    val quantityDialogBinding: QuantityDialogBinding =
                        QuantityDialogBinding.inflate(
                            LayoutInflater.from(cartItemLayoutBinding.root.context),
                            null, false
                        )
                    val quantityDialog =
                        Dialog(cartItemLayoutBinding.root.context)
                    quantityDialog.window!!
                        .setLayout(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    quantityDialog.setContentView(quantityDialogBinding.root)
                    quantityDialog.setCancelable(false)

                    quantityDialogBinding.quantityEditText.hint =
                        "Max ${cartItemModel.maxQuantity}"

                    quantityDialogBinding.cancelButton.setOnClickListener { quantityDialog.cancel() }
                    quantityDialogBinding.okButton.setOnClickListener {

                        var quantity = 0L
                        if (!TextUtils.isEmpty(quantityDialogBinding.quantityEditText.text.toString())) {
                            quantity =
                                quantityDialogBinding.quantityEditText.text.toString().toLong()
                        }


                        if (quantity <= cartItemModel.maxQuantity!! && quantity != 0L) {

                            asyncListDiffer.currentList[position].productQuantity =
                                quantityDialogBinding.quantityEditText.text.toString().toLong()

                            cartItemLayoutBinding.productPrice.text =
                                "Rs. ${(cartItemModel.productPrice?.toInt()!! * quantity)} /-"

                            cartItemLayoutBinding.productQty.text =
                                "Qty: " + quantityDialogBinding.quantityEditText.text

                            notifyItemChanged(asyncListDiffer.currentList.size - 1)
                            quantityDialog.cancel()
                        } else {
                            quantityDialog.cancel()
                        }


                    }
                    quantityDialog.show()
                }
                if (cartItemModel.offersApply!! > 0) {
                    cartItemLayoutBinding.offersApplied.visibility = View.VISIBLE

                    val offerDiscountedAmount = cartItemModel.cuttedPrice?.toLong()
                        ?.minus(cartItemModel.productPrice?.toLong()!!)

                    cartItemLayoutBinding.offersApplied.text =
                        "Offer applied - Rs.$offerDiscountedAmount/-"
                } else {
                    cartItemLayoutBinding.offersApplied.visibility = View.INVISIBLE
                }
                cartItemLayoutBinding.freeCouponIcon.visibility = View.VISIBLE
                cartItemLayoutBinding.freeCouponTV.visibility = View.VISIBLE

            } else {
                cartItemLayoutBinding.productPrice.text = "Out of stock"
                cartItemLayoutBinding.productPrice.setTextColor(
                    cartItemLayoutBinding.root
                        .resources.getColor(R.color.colorPrimary)
                )
                cartItemLayoutBinding.cuttedPrice.text = ""
                cartItemLayoutBinding.cartCouponRedemptionLayout.visibility = View.GONE
                cartItemLayoutBinding.productQty.visibility = View.GONE
                cartItemLayoutBinding.freeCouponIcon.visibility = View.GONE
                cartItemLayoutBinding.freeCouponTV.visibility = View.GONE
                cartItemLayoutBinding.couponsApplied.visibility = View.GONE
                cartItemLayoutBinding.offersApplied.visibility = View.GONE
                val params =
                    cartItemLayoutBinding.removeItemButton.layoutParams as MarginLayoutParams
                params.setMargins(0, 100, 0, 0)
                cartItemLayoutBinding.removeItemButton.layoutParams = params
            }
            if (showDeleteButton) {
                cartItemLayoutBinding.removeItemButton.visibility = View.VISIBLE
            } else {
                cartItemLayoutBinding.removeItemButton.visibility = View.GONE
            }

            if(isDeliveryFragment){
                cartItemLayoutBinding.cartCouponRedemptionLayout.visibility = View.VISIBLE
            }else{
                cartItemLayoutBinding.cartCouponRedemptionLayout.visibility = View.GONE
            }
            cartItemLayoutBinding.couponRedemptionButton.setOnClickListener {

                deliveryUtil?.couponRedemptionButtonClick(
                    cartItemModel.productPrice.toString(),
                    cartItemModel.cuttedPrice.toString(),
                    position,
                    cartCouponRedemptionLayout = cartItemLayoutBinding.cartCouponRedemptionLayout,
                    couponRedemptionButton = cartItemLayoutBinding.couponRedemptionButton,
                    couponRedemptionTV = cartItemLayoutBinding.couponRedemptionTV,
                    couponsApplied = cartItemLayoutBinding.couponsApplied,
                    productPriceTV = cartItemLayoutBinding.productPrice,
                    totalItemsPriceTV = totalItemsPriceTV,
                    totalAmountTV = totalAmountTV,
                    quantity = cartItemModel.productQuantity!!
                )

            }



            cartItemLayoutBinding.removeItemButton.setOnClickListener { view ->
//                if (!ProductDetailsFragment.running_cart_query) {
//                    ProductDetailsFragment.running_cart_query = true
//                    FirebaseDbQueries.removeFromCart(
//                        position,
//                        cartItemLayoutBinding.getRoot().getContext(),
//                        cartTotalAmount
//                    )

                myCartUtil?.deleteItem(adapterPosition)
            }
        }
    }


    internal inner class CartTotalAmountViewHolder(private val binding: CartTotalAmountLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            totalItemsPriceTV = binding.totalItemsPrice
            totalAmountTV = binding.totalAmountTv
        }

        fun setTotalAmount(
            totalItems: Int, totalItemsPrice: Int, deliveryPrice: String,
            totalAmount: Int, savedAmount: Int
        ) {

            binding.totalItems.text = "Price ($totalItems items)"
            binding.totalItemsPrice.text = "Rs.$totalItemsPrice/-"
            if (deliveryPrice == "Free") {
                binding.deliveryPrice.text = deliveryPrice
            } else {
                binding.deliveryPrice.text = "Rs.$deliveryPrice/-"
            }
            binding.totalAmountTv.text = "Rs.$totalAmount/-"
            cartTotalAmount?.text = "Rs.$totalAmount/-"
            binding.savedAmount.text = "You saved Rs.$savedAmount/- on this order"
            val totalAmountParent = cartTotalAmount?.parent?.parent as? LinearLayout
            if (totalItemsPrice == 0) {
//                myCartUtil?.deleteItem(adapterPosition)
                try {
                    if (asyncListDiffer.currentList[adapterPosition].type == CartItemModel.TOTAL_AMOUNT) {
                        deleteItem(adapterPosition)
                    }
                } catch (ex: Exception) {
                    Log.e(TAG, "setTotalAmount: $ex")
                    Log.e(TAG, "setTotalAmount: ${ex.cause.toString()}")
                }

                totalAmountParent?.visibility = View.GONE
            } else {
                totalAmountParent?.visibility = View.VISIBLE
            }
        }
    }
}