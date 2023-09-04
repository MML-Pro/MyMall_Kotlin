package com.blogspot.mido_mymall.ui.home

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.bumptech.glide.Glide
import com.blogspot.mido_mymall.R
import com.blogspot.mido_mymall.databinding.GridProductLayoutBinding
import com.blogspot.mido_mymall.databinding.HorizontalScrollLayoutBinding
import com.blogspot.mido_mymall.databinding.SlidingAdLayoutBinding
import com.blogspot.mido_mymall.databinding.StripAdLayoutBinding
import com.blogspot.mido_mymall.domain.models.HomePageModel
import com.blogspot.mido_mymall.domain.models.HorizontalProductScrollModel
import com.blogspot.mido_mymall.domain.models.SliderModel
import com.blogspot.mido_mymall.domain.models.WishListModel
import java.util.Timer
import java.util.TimerTask
import kotlin.math.abs

class HomePageAdapter(homePageModelList: ArrayList<HomePageModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val homePageModelList: ArrayList<HomePageModel>

    private var lastPosition = -1

//    private var diffCallback = object :DiffUtil.ItemCallback<HomePageModel>(){
//        override fun areItemsTheSame(oldItem: HomePageModel, newItem: HomePageModel): Boolean {
//            TODO("Not yet implemented")
//        }
//
//        override fun areContentsTheSame(oldItem: HomePageModel, newItem: HomePageModel): Boolean {
//            TODO("Not yet implemented")
//        }
//
//    }

    init {
        this.homePageModelList = homePageModelList
        recycledViewPool = RecycledViewPool()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HomePageModel.BANNER_SLIDER -> {
                val slidingAdLayoutBinding: SlidingAdLayoutBinding = SlidingAdLayoutBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                BannerSliderViewHolder(slidingAdLayoutBinding)
            }

            HomePageModel.STRIP_AD_BANNER -> {
                val stripAdLayoutBinding: StripAdLayoutBinding = StripAdLayoutBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                StripAdBannerViewHolder(stripAdLayoutBinding)
            }

            HomePageModel.HORIZONTAL_PRODUCT_VIEW -> {
                val horizontalScrollLayoutBinding: HorizontalScrollLayoutBinding =
                    HorizontalScrollLayoutBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                HorizontalProductViewHolder(horizontalScrollLayoutBinding)
            }

            HomePageModel.GRID_PRODUCT_VIEW -> {
                val gridProductLayoutBinding: GridProductLayoutBinding =
                    GridProductLayoutBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                GridProductsViewHolder(gridProductLayoutBinding)
            }

            else -> {
                val slidingAdLayoutBinding: SlidingAdLayoutBinding = SlidingAdLayoutBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                BannerSliderViewHolder(slidingAdLayoutBinding)
            }
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        when (homePageModelList[position].type) {

            HomePageModel.BANNER_SLIDER -> {
                val sliderModelList: List<SliderModel>? =
                    homePageModelList[position].sliderModelList
                (holder as BannerSliderViewHolder).setBannerSliderViewPager(sliderModelList!!)
            }

            HomePageModel.STRIP_AD_BANNER -> {
                val resource = homePageModelList[position].image!!
                val color: String = homePageModelList[position].backgroundColor!!
                (holder as StripAdBannerViewHolder).setStripAd(resource, color)
            }

            HomePageModel.HORIZONTAL_PRODUCT_VIEW -> {
                val horizontalLayoutTitle: String = homePageModelList[position].productName!!
                val horizontalProductScrollModelList: ArrayList<HorizontalProductScrollModel> =
                    homePageModelList[position].horizontalProductScrollModelList!!

                val viewAllProducts: ArrayList<WishListModel> =
                    homePageModelList[position].viewAllProductList

                val backgroundColor: String = homePageModelList[position].backgroundColor!!
                (holder as HorizontalProductViewHolder)
                    .setHorizontalProductsLayout(
                        horizontalLayoutTitle,
                        horizontalProductScrollModelList, viewAllProducts,
                        backgroundColor
                    )
            }

            HomePageModel.GRID_PRODUCT_VIEW -> {
                val gridTitle: String = homePageModelList[position].productName!!
                val gridColor: String = homePageModelList[position].backgroundColor!!
                val gridProductScrollModelList: ArrayList<HorizontalProductScrollModel> =
                    homePageModelList[position].horizontalProductScrollModelList!!
                (holder as GridProductsViewHolder)
                    .setGridProductLayout(gridTitle, gridProductScrollModelList, gridColor)
            }
        }

        if(lastPosition < position) {
            val animation = AnimationUtils.loadAnimation(
                holder.itemView.context,
                R.anim.fade_in
            )
            holder.itemView.animation = animation

            lastPosition = position
        }
    }


    override fun getItemViewType(position: Int): Int {
        return when (homePageModelList[position].type) {
            0 -> HomePageModel.BANNER_SLIDER
            1 -> HomePageModel.STRIP_AD_BANNER
            2 -> HomePageModel.HORIZONTAL_PRODUCT_VIEW
            3 -> HomePageModel.GRID_PRODUCT_VIEW
            else -> -1
        }

//        return super.getItemViewType(position);
    }

    override fun getItemCount(): Int {
        return homePageModelList.size
    }

    private class BannerSliderViewHolder(private val binding: SlidingAdLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            private const val DELAY_TIME: Long = 3000
            private const val PERIOD_TIME: Long = 3000

            //        private boolean isFirstRun = true;
            //        private List<SliderModel> sliderModelList;
            private var currentPage = 0
        }

        private var timer: Timer? = null
        private var sliderHandler: Handler? = null
        private var update: Runnable? = null
        private lateinit var onPageChangeCallback: OnPageChangeCallback
        private var arrangedList = arrayListOf<SliderModel>()


        @SuppressLint("ClickableViewAccessibility")
        fun setBannerSliderViewPager(sliderModelList: List<SliderModel>) {
            currentPage = 2
            if (timer != null) {
                timer!!.cancel()
            }

            for (x in sliderModelList.indices) {
                arrangedList.add(x, sliderModelList[x])
            }
            arrangedList.add(0, sliderModelList[sliderModelList.size - 2])
            arrangedList.add(1, sliderModelList[sliderModelList.size - 1])
            arrangedList.add(0, sliderModelList[0])
            arrangedList.add(1, sliderModelList[0])
            val sliderAdapter = SliderAdapter()

            sliderAdapter.asyncListDiffer.submitList(arrangedList)

            binding.bannerSliderViewPager.apply {
                adapter = sliderAdapter
                clipToPadding = false
                clipChildren = false
                offscreenPageLimit = 3
            }

//            binding.bannerSliderViewPager.setPageMargin(20);
//            binding.bannerSliderViewPager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

            val compositePageTransformer = CompositePageTransformer()
            compositePageTransformer.addTransformer(MarginPageTransformer(20))
            compositePageTransformer.addTransformer { page: View, position: Float ->
                val r = 1 - abs(position)
                page.scaleY = 0.85f + r * 0.15f
            }
            binding.bannerSliderViewPager.setPageTransformer(compositePageTransformer)
            binding.bannerSliderViewPager.currentItem = currentPage

            onPageChangeCallback = object : OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    currentPage = position
                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    if (state == ViewPager.SCROLL_STATE_IDLE) {
                        pageLopper(arrangedList)
                    }
                }
            }
            binding.bannerSliderViewPager.registerOnPageChangeCallback(onPageChangeCallback)
            startBannerSliderShow(sliderModelList)
            binding.bannerSliderViewPager.setOnTouchListener { v, event ->
                pageLopper(arrangedList)
                timer!!.cancel()
                if (event.action == MotionEvent.ACTION_UP) {
                    startBannerSliderShow(arrangedList)
                }
                false
            }
        }



        @Throws(Throwable::class)
        fun finalize() {
//            super.finalize()
            binding.bannerSliderViewPager.clearFocus()
            timer!!.cancel()
            sliderHandler!!.removeCallbacks(update!!)
            binding.bannerSliderViewPager.unregisterOnPageChangeCallback(onPageChangeCallback)
            timer = null
            sliderHandler = null
        }

        private fun startBannerSliderShow(sliderModelList: List<SliderModel>?) {

//            isFirstRun = currentPage == 0;
            sliderHandler = Handler()
            update = Runnable {
                if (currentPage >= sliderModelList!!.size) {
                    currentPage = 0
                }
                binding.bannerSliderViewPager.setCurrentItem(
                    currentPage++,
                    true
                )
            }
            timer = Timer()
            timer!!.schedule(object : TimerTask() {
                override fun run() {
                    sliderHandler!!.post(update!!)
                }
            }, DELAY_TIME, PERIOD_TIME)
        }

        private fun stopBannerSlideShow() {
            timer!!.cancel()
        }

        private fun pageLopper(sliderModelList: List<SliderModel>?) {
            if (currentPage == sliderModelList!!.size - 2) {
                currentPage = 2
                binding.bannerSliderViewPager.setCurrentItem(currentPage, false)
            }
            if (currentPage == 1) {
                currentPage = sliderModelList.size - 3
                binding.bannerSliderViewPager.setCurrentItem(currentPage, false)
            }
        }


    }

    private class StripAdBannerViewHolder(private val stripAdLayoutBinding: StripAdLayoutBinding) :
        RecyclerView.ViewHolder(stripAdLayoutBinding.root) {


        fun setStripAd(resource: String, color: String) {
            Glide.with(stripAdLayoutBinding.root.context)
                .load(resource)
                .placeholder(R.drawable.placeholder_image)
                .into(stripAdLayoutBinding.stripAdImage)
            stripAdLayoutBinding.root.setBackgroundColor(Color.parseColor(color))
        }
    }

    private inner class HorizontalProductViewHolder(private val binding: HorizontalScrollLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.horizontalScrollLayoutRV.setRecycledViewPool(recycledViewPool)
        }


        fun setHorizontalProductsLayout(
            title: String,
            productScrollModelList: ArrayList<HorizontalProductScrollModel>,
            viewAllProductList: ArrayList<WishListModel>,
            color: String
        ) {
            binding.horizontalScrollLayoutTitle.text = title
            binding.container.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))
            if (productScrollModelList.size > 8) {
                binding.hsViewAllButton.visibility = View.VISIBLE

                binding.hsViewAllButton.setOnClickListener { view ->
                    val hSModelArray: Array<HorizontalProductScrollModel> =
                        productScrollModelList.toArray(arrayOfNulls<HorizontalProductScrollModel>(0))

                    val viewAllProductArray: Array<WishListModel> =
                        viewAllProductList.toArray(arrayOfNulls<WishListModel>(0))

                    findNavController(binding.root)
                        .navigate(
                            HomeFragmentDirections.actionHomeFragmentToViewAllFragment(
                                0, hSModelArray, viewAllProductArray, title = title
                            )
                        )
                }
            } else {
                binding.hsViewAllButton.visibility = View.INVISIBLE
            }
            val horizontalProductScrollAdapter = HorizontalProductScrollAdapter()

            horizontalProductScrollAdapter.asyncListDiffer.submitList(productScrollModelList)

            binding.horizontalScrollLayoutRV.layoutManager = LinearLayoutManager(
                binding.root.context, LinearLayoutManager.HORIZONTAL, false
            )
            binding.horizontalScrollLayoutRV.adapter = horizontalProductScrollAdapter
        }
    }

    private inner class GridProductsViewHolder(private val binding: GridProductLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setGridProductLayout(
            title: String,
            horizontalProductScrollModelList: ArrayList<HorizontalProductScrollModel>,
            backgroundColor: String) {

            binding.titleTV.text = title
            binding.container.backgroundTintList = ColorStateList.valueOf(
                Color.parseColor(
                    backgroundColor
                )
            )


//            binding.productsGridView
//                    .setAdapter(new GridProductAdapter(horizontalProductScrollModelList));
            for (i in 0..3) {
                val hsProductImage: ImageView =
                    binding.gridLayout.getChildAt(i).findViewById(R.id.hsProductImage)
                val hsProductName: TextView =
                    binding.gridLayout.getChildAt(i).findViewById(R.id.hsProductName)
                val hsProductDescription: TextView =
                    binding.gridLayout.getChildAt(i).findViewById(R.id.hsProductDescription)
                val hsProductPrice: TextView =
                    binding.gridLayout.getChildAt(i).findViewById(R.id.hsProductPrice)


//                hsProductImage.setImageResource(horizontalProductScrollModelList.get(i).getProductImage());
                Glide.with(binding.root.context)
                    .load(horizontalProductScrollModelList[i].productImage)
                    .placeholder(R.drawable.placeholder_image)
                    .into(hsProductImage)
                hsProductName.text = horizontalProductScrollModelList[i].productName
                hsProductDescription.text = horizontalProductScrollModelList[i].productSubtitle
                hsProductPrice.text = "EGP.${horizontalProductScrollModelList[i].productPrice}/-"
                val productID: String = horizontalProductScrollModelList[i].productID.toString()

                binding.gridLayout.getChildAt(i).setOnClickListener {
                    findNavController(binding.root)
                        .navigate(
                            HomeFragmentDirections.actionHomeFragmentToProductDetailsFragment(
                                productID
                            )
                        )
                }
            }
            binding.viewAllButton.setOnClickListener { view ->
                val hSModelArray: Array<HorizontalProductScrollModel> =
                    horizontalProductScrollModelList.toArray(
                        arrayOfNulls<HorizontalProductScrollModel>(0))

                if (findNavController(binding.root).currentDestination?.id == R.id.homeFragment) {
                    findNavController(binding.root)
                        .navigate(HomeFragmentDirections.actionHomeFragmentToViewAllFragment(
                                1,
                                hSModelArray,
                                null, title = title
                            )
                        )
                }
            }
        }
    }

    companion object {
        private lateinit var recycledViewPool: RecycledViewPool
    }
}