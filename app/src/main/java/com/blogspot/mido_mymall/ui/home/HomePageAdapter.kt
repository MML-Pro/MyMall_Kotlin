package com.blogspot.mido_mymall.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.blogspot.mido_mymall.R
import com.blogspot.mido_mymall.databinding.GridProductLayoutBinding
import com.blogspot.mido_mymall.databinding.HorizontalScrollLayoutBinding
import com.blogspot.mido_mymall.databinding.SlidingAdLayoutBinding
import com.blogspot.mido_mymall.databinding.StripAdLayoutBinding
import com.blogspot.mido_mymall.domain.models.HomePageModel
import com.blogspot.mido_mymall.domain.models.HorizontalProductScrollModel
import com.blogspot.mido_mymall.domain.models.SliderModel
import com.blogspot.mido_mymall.domain.models.WishListModel
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import java.util.Timer
import java.util.TimerTask
import kotlin.math.abs

private const val TAG = "HomePageAdapter"
class HomePageAdapter(private val homePageModelList: ArrayList<HomePageModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var lastPosition = -1

    init {
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


    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        when (homePageModelList[position].type) {

            HomePageModel.BANNER_SLIDER -> {
                val sliderModelList: List<SliderModel>? =
                    homePageModelList[position].sliderModelList
                (holder as BannerSliderViewHolder).setBannerSliderViewPager(sliderModelList!!)
            }

            HomePageModel.STRIP_AD_BANNER -> {
//                val resource = homePageModelList[position].image!!
//                val color: String = homePageModelList[position].backgroundColor!!
//                (holder as StripAdBannerViewHolder).setStripAd(resource, color)

                (holder as StripAdBannerViewHolder).bind(model = homePageModelList[position])

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

        if (lastPosition < position) {
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
        }

        private var currentPage = 2 // Start at the third page (consider adjusting this)
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
                        pageLooper(arrangedList)
                    }
                }
            }
            binding.bannerSliderViewPager.registerOnPageChangeCallback(onPageChangeCallback)
            startBannerSliderShow(arrangedList)

            binding.bannerSliderViewPager.setOnTouchListener { v, event ->
                pageLooper(arrangedList)
                timer?.cancel()
                if (event.action == MotionEvent.ACTION_UP) {
                    startBannerSliderShow(arrangedList)
                }
                false
            }
        }

        private fun startBannerSliderShow(sliderModelList: List<SliderModel>?) {
            sliderHandler = Handler()
            update = Runnable {
                if (currentPage >= sliderModelList!!.size) {
                    currentPage = 0
                }
                binding.bannerSliderViewPager.setCurrentItem(currentPage++, true)
            }

            if (timer == null) {
                timer = Timer()
                timer!!.schedule(object : TimerTask() {
                    override fun run() {
                        sliderHandler!!.post(update!!)
                    }
                }, DELAY_TIME, PERIOD_TIME)
            }
        }

        private fun stopBannerSlideShow() {
            timer?.cancel()
        }

        private fun pageLooper(sliderModelList: List<SliderModel>?) {
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

    // تعريف AdLoader مرة واحدة لكل ViewHolder (قد يكون هذا جيدًا أو سيئًا حسب استراتيجيتك)
    // من الأفضل غالبًا تحميل الإعلانات في الـ Fragment/ViewModel وتمريرها
    private var adLoader: AdLoader? = null
    private var currentNativeAd: NativeAd? = null // لتخزين الإعلان المحمل وتدميره لاحقًا

    init {
        // يمكنك تهيئة AdLoader هنا أو داخل bind عند الحاجة لأول مرة
    }

    private fun buildAdLoader(): AdLoader {
       return AdLoader.Builder(
                stripAdLayoutBinding.root.context,
                // استخدم معرف اختبار Native Ad الصحيح!
                stripAdLayoutBinding.root.context.getString(R.string.in_content_ad) // مثال لمعرف اختبار Native
                // أو معرفك الحقيقي للإعلانات المدمجة
            )
            .forNativeAd { nativeAd ->
                Log.d(TAG, "Native Ad Received")
                // تخزين الإعلان لتدميره لاحقًا
                currentNativeAd?.destroy() // تدمير الإعلان القديم إذا كان موجودًا
                currentNativeAd = nativeAd

                // التأكد أن الـ ViewHolder لا يزال صالحًا
                if (adapterPosition == RecyclerView.NO_POSITION) {
                    nativeAd.destroy()
                    return@forNativeAd
                }
                // عرض الإعلان في الحاوية المخصصة
                displayNativeAd(stripAdLayoutBinding.adViewContainer, nativeAd)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e(TAG, "Native Ad Failed To Load: ${adError.message} (Code: ${adError.code})")
                    // يمكنك محاولة إخفاء الحاوية هنا أو عرض شيء آخر
                    stripAdLayoutBinding.adViewContainer.visibility = View.GONE
                    // قم بتنظيف المرجع للإعلان القديم إذا فشل التحميل
                    currentNativeAd = null
                }
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    Log.d(TAG, "AdListener: onAdLoaded event triggered (but native ad is handled in forNativeAd)")
                    // لا تعرض الإعلان هنا، يتم التعامل معه في forNativeAd
                }
            })
            .build() // بناء الـ AdLoader
    }

    // دالة عرض الصورة (تبقى كما هي تقريبًا)
    fun setStripAd(imageUrl: String?, backgroundColorStr: String?) {
        stripAdLayoutBinding.stripAdImage.visibility = View.VISIBLE // <<< تأكد من إظهار ImageView
        if (imageUrl != null) {
            Glide.with(stripAdLayoutBinding.root.context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .into(stripAdLayoutBinding.stripAdImage)
        } else {
             stripAdLayoutBinding.stripAdImage.setImageResource(R.drawable.placeholder_image)
        }
        try {
             stripAdLayoutBinding.root.setBackgroundColor(backgroundColorStr?.toColorInt() ?: Color.TRANSPARENT)
        } catch (e: Exception) {
             stripAdLayoutBinding.root.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    // دالة عرض الإعلان المدمج (مُعدلة بالكامل)
    private fun displayNativeAd(adContainer: FrameLayout, ad: NativeAd) { // استخدم FrameLayout أو ViewGroup
        val context = adContainer.context
        val inflater = LayoutInflater.from(context)

        // 1. تضخيم تخطيط الإعلان المدمج المخصص (native_ad_layout.xml)
        val adView = inflater.inflate(R.layout.native_ad_layout, null) as NativeAdView

        // 2. ربط الـ Views داخل تخطيط الإعلان المدمج
        adView.mediaView = adView.findViewById(R.id.ad_media) // MediaView
        adView.headlineView = adView.findViewById(R.id.ad_headline) // TextView
        adView.bodyView = adView.findViewById(R.id.ad_body) // TextView
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action) // Button
        adView.iconView = adView.findViewById(R.id.ad_app_icon) // ImageView
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser) // TextView (اختياري)
        // ... (أضف أي views أخرى قمت بتعريفها مثل price, store, star rating)

        // 3. تعبئة الـ Views ببيانات الإعلان
        (adView.headlineView as? TextView)?.text = ad.headline
        adView.mediaView?.setMediaContent(ad.mediaContent ?: return) // هام: التعامل مع mediaContent
        adView.mediaView?.setImageScaleType(ImageView.ScaleType.CENTER_CROP) // أو FitCenter حسب تصميمك

        if (ad.body == null) {
            adView.bodyView?.visibility = View.INVISIBLE
        } else {
            adView.bodyView?.visibility = View.VISIBLE
            (adView.bodyView as? TextView)?.text = ad.body
        }

        if (ad.callToAction == null) {
            adView.callToActionView?.visibility = View.INVISIBLE
        } else {
            adView.callToActionView?.visibility = View.VISIBLE
            (adView.callToActionView as? Button)?.text = ad.callToAction
        }

        if (ad.icon == null) {
            adView.iconView?.visibility = View.GONE // أو INVISIBLE
        } else {
            (adView.iconView as? ImageView)?.setImageDrawable(ad.icon?.drawable)
            adView.iconView?.visibility = View.VISIBLE
        }

        if (ad.advertiser == null) {
            adView.advertiserView?.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as? TextView)?.text = ad.advertiser
            adView.advertiserView?.visibility = View.VISIBLE
        }

        // ... (قم بتعبئة أي views أخرى)

        // 4. تسجيل كائن الإعلان مع الـ NativeAdView
        adView.setNativeAd(ad)

        // 5. إضافة تخطيط الإعلان المُعبأ إلى الحاوية المخصصة
        adContainer.removeAllViews() // إزالة أي إعلان قديم من الحاوية
        adContainer.addView(adView)  // إضافة الإعلان الجديد
        adContainer.visibility = View.VISIBLE // تأكد من أن الحاوية مرئية
        Log.d(TAG, "Native Ad displayed in container")
    }

    // دالة الربط الرئيسية
    fun bind(model: HomePageModel) {
        if (model.isAd) {
            Log.d(TAG, "Binding Ad at position: $adapterPosition")
            // إظهار حاوية الإعلان وإخفاء الصورة
            stripAdLayoutBinding.stripAdImage.visibility = View.GONE
            stripAdLayoutBinding.adViewContainer.visibility = View.VISIBLE // إظهار مبدئي للحاوية

            // ---- استراتيجية تحميل محسنة (مثال بسيط) ----
            // تحميل الإعلان فقط إذا لم يكن هناك إعلان محمل حاليًا لهذا الـ ViewHolder
            if (currentNativeAd == null && (adLoader == null || !adLoader!!.isLoading)) {
                 Log.d(TAG, "No current ad or loader not loading. Requesting new ad.")
                 // تأكد من بناء AdLoader إذا لم يكن موجودًا
                 if (adLoader == null) {
                     adLoader = buildAdLoader()
                 }
                 adLoader?.loadAd(AdRequest.Builder().build())
            } else if (currentNativeAd != null) {
                 // إذا كان هناك إعلان محمل بالفعل، فقط اعرضه مرة أخرى
                 Log.d(TAG, "Re-displaying previously loaded native ad.")
                 displayNativeAd(stripAdLayoutBinding.adViewContainer, currentNativeAd!!)
            } else {
                 Log.d(TAG, "AdLoader is currently loading.")
                 // لا تفعل شيئًا، انتظر نتيجة التحميل من الـ listener/callback
                 // قد ترغب في عرض placeholder مؤقت هنا
            }
            // ---- نهاية الاستراتيجية المحسنة ----

            /* // --- الكود القديم (تحميل في كل مرة bind) ---
            stripAdLayoutBinding.adViewContainer.visibility = View.VISIBLE
            // تأكد من بناء AdLoader إذا لم يكن موجودًا
            if (adLoader == null) {
                 adLoader = buildAdLoader()
            }
            // تحقق مما إذا كان AdLoader لا يزال يقوم بالتحميل لتجنب الطلبات المتعددة بسرعة
            if (!adLoader!!.isLoading) {
                Log.d(TAG, "Requesting Ad in Bind")
                adLoader!!.loadAd(AdRequest.Builder().build())
            } else {
                Log.d(TAG, "AdLoader is already loading in Bind")
            }
            */

        } else {
            Log.d(TAG, "Binding Image at position: $adapterPosition")
            // إظهار الصورة وإخفاء حاوية الإعلان
            stripAdLayoutBinding.adViewContainer.visibility = View.GONE
            stripAdLayoutBinding.stripAdImage.visibility = View.VISIBLE // <<< تأكد من إظهار ImageView
            setStripAd(model.image, model.backgroundColor)
            // قم بتنظيف الإعلان القديم عند عرض صورة
            currentNativeAd?.destroy()
            currentNativeAd = null
        }
    }

    // دالة التنظيف عند إعادة التدوير
     fun clearResources() {
         Log.d(TAG, "Clearing resources for position: $adapterPosition")
         // تدمير الإعلان المدمج الحالي
         currentNativeAd?.destroy()
         currentNativeAd = null
         // إزالة Views من حاوية الإعلان (احتياطي)
         stripAdLayoutBinding.adViewContainer.removeAllViews()
         // تنظيف Glide
         Glide.with(stripAdLayoutBinding.root.context).clear(stripAdLayoutBinding.stripAdImage)
         stripAdLayoutBinding.stripAdImage.setImageDrawable(null)
         // إعادة تعيين الرؤية
         stripAdLayoutBinding.adViewContainer.visibility = View.GONE
         stripAdLayoutBinding.stripAdImage.visibility = View.GONE
     }
} // نهاية StripAdBannerViewHolder

    private inner class HorizontalProductViewHolder(private val binding: HorizontalScrollLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.horizontalScrollLayoutRV.setRecycledViewPool(recycledViewPool)
        }


        @SuppressLint("ClickableViewAccessibility")
        fun setHorizontalProductsLayout(
            title: String,
            productScrollModelList: ArrayList<HorizontalProductScrollModel>,
            viewAllProductList: ArrayList<WishListModel>,
            color: String
        ) {
            binding.horizontalScrollLayoutTitle.text = title

            Log.v(TAG, "setHorizontalProductsLayout: title is $title")

            binding.container.backgroundTintList = ColorStateList.valueOf(color.toColorInt())
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

            binding.horizontalScrollLayoutRV.apply {
                layoutManager = LinearLayoutManager(
                    binding.root.context, LinearLayoutManager.HORIZONTAL, false
                )
                adapter = horizontalProductScrollAdapter

                setOnTouchListener { view, _ ->
                    view?.parent?.requestDisallowInterceptTouchEvent(true)

                    false

                }
            }

        }
    }

    private inner class GridProductsViewHolder(private val binding: GridProductLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setGridProductLayout(
            title: String,
            horizontalProductScrollModelList: ArrayList<HorizontalProductScrollModel>,
            backgroundColor: String
        ) {

            binding.titleTV.text = title

            Log.v(TAG, "setGridProductLayout: $title")

            binding.container.backgroundTintList = ColorStateList.valueOf(
                backgroundColor.toColorInt()
            )


//            binding.productsGridView
//                    .setAdapter(new GridProductAdapter(horizontalProductScrollModelList));
            for (i in 0..3) {
                Log.d(
                    TAG,
                    "Product [$i]: ID = ${horizontalProductScrollModelList[i].productID}, " +
                            "Name = ${horizontalProductScrollModelList[i].productName}"
                )


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
                hsProductPrice.text = binding.root.resources.getString(
                    R.string.egp_price,
                    horizontalProductScrollModelList[i].productPrice
                )
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
                        arrayOfNulls<HorizontalProductScrollModel>(0)
                    )

                if (findNavController(binding.root).currentDestination?.id == R.id.homeFragment) {
                    findNavController(binding.root)
                        .navigate(
                            HomeFragmentDirections.actionHomeFragmentToViewAllFragment(
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