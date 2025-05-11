package com.blogspot.mido_mymall.ui.my_cart

import android.annotation.SuppressLint
import android.app.Dialog
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.mido_mymall.R
import com.blogspot.mido_mymall.databinding.CartItemLayoutBinding
import com.blogspot.mido_mymall.databinding.CartTotalAmountLayoutBinding
import com.blogspot.mido_mymall.databinding.QuantityDialogBinding
import com.blogspot.mido_mymall.domain.models.CartItemModel
import com.blogspot.mido_mymall.domain.models.RewardModel
import com.blogspot.mido_mymall.util.safeParseDouble
import com.bumptech.glide.Glide
import java.util.Locale

class CartAdapter(
    showDeleteButton: Boolean,
    private val myCartUtil: MyCartUtil? = null,
    private val deliveryUtil: DeliveryUtil? = null,
    private val isDeliveryFragment: Boolean

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
            return oldItem.productImage == newItem.productImage &&
                    oldItem.productName == newItem.productName &&
                    oldItem.freeCoupons == newItem.freeCoupons &&
                    oldItem.productPrice == newItem.productPrice &&
                    oldItem.cuttedPrice == newItem.cuttedPrice &&
                    oldItem.productQuantity == newItem.productQuantity
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

//    fun deleteItem(position: Int) {
//        val newList = asyncListDiffer.currentList.toMutableList()
//        newList.removeAt(position)
//
//        asyncListDiffer.submitList(newList)
//
////        if (newList.size == 1) {
////            asyncListDiffer.submitList(null)
////        } else {
////            asyncListDiffer.submitList(newList)
////
////        }
//
//    }

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

        val cartItemLayoutBinding: CartItemLayoutBinding = CartItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CartItemViewHolder(cartItemLayoutBinding)
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

                        totalItems++

                        totalItemsPrice += if (asyncListDiffer.currentList[position].selectedCouponId.isNullOrEmpty()) {
                            asyncListDiffer.currentList[i].productPrice?.toInt()!! * quantity?.toInt()!!
                        } else {
                            asyncListDiffer.currentList[i].discountedPrice?.toInt()!! * quantity!!.toInt()
                        }

                        if (asyncListDiffer.currentList[i].cuttedPrice?.isNotEmpty() == true) {
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
                myCartUtil?.getTotalAmount(totalAmount.toDouble())

                if (lastPosition < position) {
                    val animation = AnimationUtils.loadAnimation(
                        holder.itemView.context,
                        R.anim.fade_in
                    )
                    holder.itemView.animation = animation

                    lastPosition = position
                }


            }
        }

    }


    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    internal inner class CartItemViewHolder(private val cartItemLayoutBinding: CartItemLayoutBinding) :
        RecyclerView.ViewHolder(cartItemLayoutBinding.root) {


        @Suppress("DEPRECATION")
        @SuppressLint("UseCompatLoadingForDrawables", "StringFormatInvalid", "StringFormatMatches")
        fun bind(cartItemModel: CartItemModel, position: Int) {
//
//            cartItemLayoutBinding.productImage.setImageResource(cartItemModel.getProductImage());
            Glide.with(cartItemLayoutBinding.root)
                .load(cartItemModel.productImage)
                .placeholder(R.drawable.placeholder_image)
                .into(cartItemLayoutBinding.productImage)
            cartItemLayoutBinding.productName.text = cartItemModel.productName

//            updateOfferDiscountDisplay(cartItemModel, cartItemModel.productQuantity)


            // In stock
            if (cartItemModel.inStock == true) {
                val freeCouponNo = cartItemModel.freeCoupons

                Log.d(TAG, "freeCouponNo: ${cartItemModel.freeCoupons}")

                when {
                    freeCouponNo == 0L -> {
                        cartItemLayoutBinding.freeCouponIcon.visibility = View.INVISIBLE
                        cartItemLayoutBinding.freeCouponTV.visibility = View.INVISIBLE
                    }

                    freeCouponNo!! > 0L -> {
                        cartItemLayoutBinding.freeCouponIcon.visibility = View.VISIBLE
                        cartItemLayoutBinding.freeCouponTV.visibility = View.VISIBLE
                        if (freeCouponNo == 1L) {
                            cartItemLayoutBinding.freeCouponTV.text =
                                cartItemLayoutBinding.root.resources.getString(
                                    R.string.free_coupon, freeCouponNo
                                )

                            //                        "free ${freeCouponNo} Coupon"
                        } else {
                            cartItemLayoutBinding.freeCouponTV.text =
                                cartItemLayoutBinding.root.resources.getString(
                                    R.string.free_coupons, freeCouponNo
                                )
                        }
                    }

                    else -> {
                        cartItemLayoutBinding.freeCouponIcon.visibility = View.INVISIBLE
                        cartItemLayoutBinding.freeCouponTV.visibility = View.INVISIBLE
                    }
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
                        cartItemLayoutBinding.couponRedemptionButton.text =
                            cartItemLayoutBinding.root.resources.getString(R.string.coupon)
                    }.also {
                        val offerDiscountedAmount: Long


                        if (rewardModelList?.get(position)?.discountOrAmount != null) {
                            offerDiscountedAmount =
                                cartItemModel.productPrice?.toLong()!! * rewardModelList?.get(
                                    position
                                )?.discountOrAmount?.toLong()!! / 100

                            cartItemLayoutBinding.couponsApplied.visibility = View.VISIBLE
                            cartItemLayoutBinding.couponsApplied.text =
                                cartItemLayoutBinding.root.resources.getString(
                                    R.string.coupon_applied,
                                    offerDiscountedAmount.toString()
                                )

                        }


                    }
                } else {
                    cartItemLayoutBinding.couponsApplied.visibility = View.INVISIBLE
                    cartItemLayoutBinding.cartCouponRedemptionLayout.setBackgroundColor(
                        itemView.resources.getColor(
                            R.color.couponRed,
                            itemView.context.theme
                        )
                    )
                    cartItemLayoutBinding.couponRedemptionTV.text =
                        cartItemLayoutBinding.root.resources.getString(R.string.apply_your_coupon_here)
                    cartItemLayoutBinding.couponRedemptionButton.text =
                        cartItemLayoutBinding.root.resources.getString(R.string.redeem)
                }

                val productPrice =
                    (cartItemModel.productPrice?.toDouble()!! * cartItemModel.productQuantity!!)

                cartItemLayoutBinding.productPrice.text =
                    cartItemLayoutBinding.root.resources.getString(
                        R.string.egp_price,
                        productPrice.toString()
                    )



                cartItemLayoutBinding.cuttedPrice.text =
                    cartItemLayoutBinding.root.resources.getString(
                        R.string.egp_price,
                        cartItemModel.cuttedPrice
                    )
                cartItemLayoutBinding.productQty.visibility = View.VISIBLE

                cartItemLayoutBinding.productQty.text =
                    cartItemLayoutBinding.root.resources.getString(
                        R.string.qty_number,
                        cartItemModel.productQuantity
                    )

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

                        // التحقق من أن الكمية المدخلة ضمن الحد الأقصى وأنها ليست صفرًا
                        if (quantity <= cartItemModel.maxQuantity!! && quantity > 0L) { // تم تغيير != 0L إلى > 0L
                            // استخدم adapterPosition لأنه أكثر أمانًا داخل المستمعات
                            val updatedPosition = adapterPosition
                            if (updatedPosition != RecyclerView.NO_POSITION) {
                                // الحصول على القائمة الحالية قبل التعديل إذا لزم الأمر
                                val currentList = asyncListDiffer.currentList
                                if (updatedPosition < currentList.size) {
                                    // تحديث كمية المنتج في بيانات الـ Adapter
                                    currentList[updatedPosition].productQuantity = quantity

                                    // ******** بداية التعديل المطلوب ********
                                    // 1. حساب سعر المنتج بناءً على الكمية الجديدة
                                    //    استخدم toDoubleOrNull للأمان في حال كان السعر غير صالح
                                    val productPriceDouble =
                                        currentList[updatedPosition].productPrice?.toDoubleOrNull()
                                            ?: 0.0
                                    val calculatedPrice = productPriceDouble * quantity

                                    // 2. تنسيق السعر المحسوب ليقتصر على خانتين عشريتين
                                    //    استخدم Locale.ENGLISH لضمان استخدام النقطة كفاصل عشري
                                    val formattedPrice =
                                        String.format(Locale.ENGLISH, "%.2f", calculatedPrice)

                                    // 3. تحديث واجهة المستخدم باستخدام السعر المنسق
                                    cartItemLayoutBinding.productPrice.text =
                                        cartItemLayoutBinding.root.resources.getString(
                                            R.string.egp_price, // تأكد أن هذا النص المصدر يقبل %s أو %f
                                            formattedPrice      // استخدم السعر المنسق هنا
                                        )
                                    // ******** نهاية التعديل المطلوب ********

                                    // تحديث نص الكمية المعروض
                                    cartItemLayoutBinding.productQty.text =
                                        cartItemLayoutBinding.root.resources.getString(
                                            R.string.qty_number,
                                            quantity // استخدم الكمية الجديدة مباشرة
                                        )

                                    // إعلام الـ Fragment بتغيير الكمية لإعادة حساب الإجمالي
                                    myCartUtil?.onQuantityChanged(
                                        updatedPosition,
                                        quantity
                                    )
                                    deliveryUtil?.onQuantityChanged(
                                        updatedPosition,
                                        quantity
                                    )

                                    updateOfferDiscountDisplay(cartItemModel, quantity)

                                } else {
                                    Log.w(
                                        TAG,
                                        "Attempted to update_info item at invalid position: $updatedPosition"
                                    )
                                }
                            } else {
                                Log.w(TAG, "Attempted to update_info item with NO_POSITION")
                            }
                            quantityDialog.dismiss()
                        } else {
                            // التعامل مع الكمية غير الصالحة (إما صفر أو أكبر من الحد الأقصى)
                            val errorMsg = if (quantity == 0L) {
                                "Quantity cannot be zero."
                            } else {
                                "Quantity cannot exceed ${cartItemModel.maxQuantity}"
                            }
                            Toast.makeText(itemView.context, errorMsg, Toast.LENGTH_SHORT).show()
                            // لا نغلق الـ dialog هنا لكي يتمكن المستخدم من تصحيح الإدخال
                            // quantityDialog.dismiss()
                        }
                    }


                    quantityDialog.show()
                }
                cartItemModel.offersApply?.let {
                    if (it > 0) {
                        cartItemLayoutBinding.offersApplied.visibility = View.VISIBLE

                        val cuttedPrice = safeParseDouble(cartItemModel.cuttedPrice)
                        val productPrice = safeParseDouble(cartItemModel.productPrice)

                        val calc = productPrice - cuttedPrice

                        val offerDiscountedAmount = safeParseDouble(calc.toString())

                        Log.d(TAG, "cuttedPrice: $cuttedPrice")
                        Log.d(TAG, "productPrice: $productPrice")

                        Log.d(TAG, "bind: offerDiscountedAmount $offerDiscountedAmount")
                        //
                        //                    val formattedOfferAmount =
                        //                        String.format(Locale.ENGLISH, "%.2f", offerDiscountedAmount)

                        //
                        //                    cartItemLayoutBinding.offersApplied.text =
                        //                        cartItemLayoutBinding.root.resources.getString(
                        //                            R.string.offer_applied_egp, formattedOfferAmount
                        //                        )
                        updateOfferDiscountDisplay(cartItemModel, cartItemModel.productQuantity!!)

                    } else {
                        cartItemLayoutBinding.offersApplied.visibility = View.INVISIBLE
                    }
                }
//                cartItemLayoutBinding.freeCouponIcon.visibility = View.VISIBLE
//                cartItemLayoutBinding.freeCouponTV.visibility = View.VISIBLE

            } else {
                cartItemLayoutBinding.productPrice.text =
                    cartItemLayoutBinding.root.resources.getString(R.string.out_of_stock)
                cartItemLayoutBinding.productPrice.setTextColor(
                    cartItemLayoutBinding.root
                        .resources.getColor(R.color.btnRed)
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

            if (isDeliveryFragment) {
                cartItemLayoutBinding.cartCouponRedemptionLayout.visibility = View.VISIBLE
            } else {
                cartItemLayoutBinding.cartCouponRedemptionLayout.visibility = View.GONE
            }
            cartItemLayoutBinding.couponRedemptionButton.setOnClickListener {

                deliveryUtil?.couponRedemptionButtonClick(
                    cartItemModel.productPrice!!,
                    cartItemModel.cuttedPrice!!,
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

        // --- >> دالة مساعدة جديدة لحساب وتحديث عرض الخصم << ---
        @SuppressLint("StringFormatMatches")
        private fun updateOfferDiscountDisplay(item: CartItemModel, quantity: Long) {
            var offerSavingPerUnit = 0.0
            var showOffer = false

            // حساب التوفير فقط من العرض (السعر المشطوب) - لكل وحدة
            item.offersApply?.let {
                if (it > 0) {
                    val productPrice = safeParseDouble(item.productPrice)
                    val cuttedPrice = safeParseDouble(item.cuttedPrice)
                    if (cuttedPrice > productPrice) { // تأكد أن السعر المشطوب أعلى فعلاً
                        offerSavingPerUnit = cuttedPrice - productPrice
                        showOffer = true
                        Log.d(
                            TAG,
                            "Item ${item.productName}: Offer saving per unit: $offerSavingPerUnit"
                        )
                    }
                }
            }

            // تحديث واجهة المستخدم الخاصة بخصم العرض فقط
            if (showOffer) {
                val totalOfferSaving = offerSavingPerUnit * quantity
                val formattedSaving = String.format(Locale.ENGLISH, "%.2f", totalOfferSaving)
                cartItemLayoutBinding.offersApplied.text =
                    cartItemLayoutBinding.root.resources.getString(
                        R.string.offer_applied_egp, // استخدم النص المصدر المناسب
                        formattedSaving
                    )
                cartItemLayoutBinding.offersApplied.visibility = View.VISIBLE
                Log.d(
                    TAG,
                    "Updated Offer Applied display for qty $quantity. Text: ${cartItemLayoutBinding.offersApplied.text}"
                )
            } else {
                cartItemLayoutBinding.offersApplied.visibility = View.INVISIBLE
                Log.d(TAG, "Item ${item.productName}: No offer discount to display.")
            }
            // هذا الإصدار لا يتعامل مع couponsApplied TextView
            // cartItemLayoutBinding.couponsApplied.visibility = View.INVISIBLE // قد ترغب في إخفائه هنا أيضًا إذا كان العرض والكوبون يستخدمان نفس المساحة
        }
        // --- >> نهاية الدالة المساعدة المبسطة << ---

    }


    internal inner class CartTotalAmountViewHolder(private val binding: CartTotalAmountLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            totalItemsPriceTV = binding.cartSummaryTotalItemsPrice
            totalAmountTV = binding.cartSummaryTotalAmountTV
        }

        fun setTotalAmount(
            totalItems: Int, totalItemsPrice: Int, deliveryPrice: String,
            totalAmount: Int, savedAmount: Int
        ) {

            binding.totalItems.text = "Price ($totalItems items)"
            binding.cartSummaryTotalItemsPrice.text = "EGP.$totalItemsPrice/-"
            if (deliveryPrice == "Free") {
                binding.cartSummaryDeliveryPrice.text = deliveryPrice
            } else {
                binding.cartSummaryDeliveryPrice.text = "EGP.$deliveryPrice/-"
            }
            totalAmountTV?.text = "EGP.$totalAmount/-"
            totalAmountTV?.text = "EGP.$totalAmount/-"
            binding.savedAmount.text = "You saved EGP.$savedAmount/- on this order"
            val totalAmountParent = totalAmountTV?.parent?.parent as? LinearLayout
            if (totalItemsPrice == 0) {
//                myCartUtil?.deleteItem(adapterPosition)
                try {
                    if (asyncListDiffer.currentList[adapterPosition].type == CartItemModel.TOTAL_AMOUNT) {
                        myCartUtil?.deleteItem(adapterPosition)
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