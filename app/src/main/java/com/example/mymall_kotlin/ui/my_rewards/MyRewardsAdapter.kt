package com.example.mymall_kotlin.ui.my_rewards

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mymall_kotlin.R
import com.example.mymall_kotlin.databinding.MiniRewardsItemLayoutBinding
import com.example.mymall_kotlin.databinding.RewardItemLayoutBinding
import com.example.mymall_kotlin.domain.models.CartItemModel
import com.example.mymall_kotlin.domain.models.RewardModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyRewardsAdapter(
    cartModelList:ArrayList<CartItemModel>?,
    cartItemPosition:Int?,
    useMiniLayout: Boolean, couponRV: RecyclerView?,
    selectedCoupon: LinearLayout?,
    productOriginalPrice: String?,
    selectedCouponTitle: TextView?,
    selectedCouponValidity: TextView?,
    selectedCouponBody: TextView?,
    discountedPriceTV: TextView?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var differCallback = object : DiffUtil.ItemCallback<RewardModel>() {
        override fun areItemsTheSame(oldItem: RewardModel, newItem: RewardModel): Boolean {
            return oldItem.couponBody == newItem.couponBody
        }

        override fun areContentsTheSame(oldItem: RewardModel, newItem: RewardModel): Boolean {
            return oldItem == newItem
        }

    }

    constructor(
        useMiniLayout: Boolean
    ) : this(null,null,useMiniLayout, null, null, null, null, null, null, null) {
        this.useMiniLayout = useMiniLayout
    }


    val asyncListDiffer = AsyncListDiffer(this, differCallback)

    private var cartItemPosition:Int?=-1
    private var cartModelList:ArrayList<CartItemModel>?
    private var useMiniLayout = false
    private var couponRV: RecyclerView? = null
    private var selectedCoupon: LinearLayout?
    private var productOriginalPrice: String?
    private var couponTitle: TextView?
    private var couponValidity: TextView?
    private var couponBody: TextView?
    private var discountedPriceTV: TextView?


    init {
        this.cartModelList = cartModelList
        this.useMiniLayout = useMiniLayout
        this.couponRV = couponRV
        this.selectedCoupon = selectedCoupon
        this.productOriginalPrice = productOriginalPrice
        this.couponTitle = selectedCouponTitle
        this.couponValidity = selectedCouponValidity
        this.couponBody = selectedCouponBody
        this.discountedPriceTV = discountedPriceTV
        this.cartItemPosition = cartItemPosition
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (useMiniLayout) {
            val miniRewardsItemLayoutBinding: MiniRewardsItemLayoutBinding =
                MiniRewardsItemLayoutBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            MiniRewardsViewHolder(miniRewardsItemLayoutBinding)
        } else {
            val rewardItemLayoutBinding: RewardItemLayoutBinding = RewardItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            MyRewardsViewHolder(rewardItemLayoutBinding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (useMiniLayout) {
            (holder as MiniRewardsViewHolder).bind(asyncListDiffer.currentList[position])
        } else {
            val currentItem: RewardModel = asyncListDiffer.currentList[position]
            (holder as MyRewardsViewHolder).bind(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    inner class MyRewardsViewHolder(private val binding: RewardItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(rewardModel: RewardModel) {
            if (rewardModel.type.equals("Discount")) {
                binding.couponTitle.text = rewardModel.type
            } else {
                binding.couponTitle.text = "FLAT RS. " + rewardModel.discountOrAmount + " OFF"
            }

//            Date date = (Date)rewardModel.getTimestamp();
            val date: Date = rewardModel.timestamp.toDate()
            val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
            binding.couponValidity.text = "till " + simpleDateFormat.format(date)
            binding.couponBody.text = rewardModel.couponBody
        }
    }

    inner class MiniRewardsViewHolder(private val miniRewardsItemLayoutBinding: MiniRewardsItemLayoutBinding) :
        RecyclerView.ViewHolder(miniRewardsItemLayoutBinding.root) {
        @Suppress("DEPRECATION")
        fun bind(rewardModel: RewardModel) {

            miniRewardsItemLayoutBinding.couponTitle.text = rewardModel.type
            val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)

            if (rewardModel.alreadyUsed) {
                miniRewardsItemLayoutBinding.couponValidity.text = "Already used"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    miniRewardsItemLayoutBinding.couponValidity.setTextColor(
                        itemView.resources.getColor(
                            R.color.colorPrimary, itemView.context.theme
                        )
                    )
                } else {
                    miniRewardsItemLayoutBinding.couponValidity.setTextColor(
                        itemView.resources.getColor(
                            R.color.colorPrimary
                        )
                    )
                }
                miniRewardsItemLayoutBinding.couponTitle.setTextColor(Color.parseColor("#50ffffff"))
                miniRewardsItemLayoutBinding.couponBody.setTextColor(Color.parseColor("#50ffffff"))
            } else {

                miniRewardsItemLayoutBinding.couponTitle.setTextColor(Color.parseColor("#ffffff"))
                miniRewardsItemLayoutBinding.couponBody.setTextColor(Color.parseColor("#ffffff"))

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    miniRewardsItemLayoutBinding.couponValidity.setTextColor(
                        itemView.resources.getColor(
                            R.color.couponValidityColor, itemView.context.theme
                        )
                    )
                } else {
                    miniRewardsItemLayoutBinding.couponValidity.setTextColor(
                        itemView.resources.getColor(
                            R.color.couponValidityColor
                        )
                    )
                }

                val date: Date = rewardModel.timestamp.toDate()
                miniRewardsItemLayoutBinding.couponValidity.text =
                    "till " + simpleDateFormat.format(date)
            }

            miniRewardsItemLayoutBinding.couponBody.text = rewardModel.couponBody

            miniRewardsItemLayoutBinding.root.setOnClickListener {

                if (!rewardModel.alreadyUsed) {

                    couponTitle?.text = rewardModel.type
                    val date: Date = rewardModel.timestamp.toDate()
                    couponValidity?.text = "till " + simpleDateFormat.format(date)
                    couponBody?.text = rewardModel.couponBody

                    if (productOriginalPrice?.toLong()!! > rewardModel.lowerLimit?.toLong()!!
                        && productOriginalPrice?.toLong()!! < rewardModel.upperLimit?.toLong()!!
                    ) {

                        if (rewardModel.type.equals("Discount")) {
                            val discountAmount: Long =
                                productOriginalPrice?.toLong()!! * rewardModel.discountOrAmount.toLong() / 100
                            discountedPriceTV?.text =
                                "Rs. ${productOriginalPrice?.toLong()!!.minus(discountAmount)}/-"
                        } else {
                            discountedPriceTV?.text = "Rs. ${
                                productOriginalPrice?.toLong()!!
                                    .minus(rewardModel.discountOrAmount.toLong())
                            }/-"
                        }

                        if(cartItemPosition != null && cartItemPosition != -1) {
                            cartModelList?.get(cartItemPosition!!)?.selectedCouponId =
                                rewardModel.couponId
                        }

                    } else {

                        if(cartItemPosition != null && cartItemPosition != -1) {
                            cartModelList?.get(cartItemPosition!!)?.selectedCouponId = null
                        }

                        discountedPriceTV?.text = "Invalid"
                        Toast.makeText(
                            miniRewardsItemLayoutBinding.root.context,
                            "Sorry! Product doesn't match the coupon terms",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    if (couponRV?.visibility == View.GONE) {
                        couponRV?.visibility = View.VISIBLE
                        selectedCoupon?.visibility = View.GONE
                    } else {
                        couponRV?.visibility = View.GONE
                        selectedCoupon?.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}