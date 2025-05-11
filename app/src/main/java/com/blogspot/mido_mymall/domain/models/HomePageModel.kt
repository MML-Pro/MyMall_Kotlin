package com.blogspot.mido_mymall.domain.models

class HomePageModel {
    var type: Int
    var backgroundColor: String? = null
    var sliderModelList: ArrayList<SliderModel>? = null
    var isAd: Boolean = false // <-- تمت إضافة هذا الحقل (القيمة الافتراضية false تعني صورة)


    constructor(type: Int, sliderModelList: ArrayList<SliderModel>) {
        this.type = type
        this.sliderModelList = sliderModelList
    }

    //################ Strip Ad ####################
    var image: String? = null

    constructor(type: Int, Image: String, backgroundColor: String?) {
        this.type = type
        this.image = Image
        this.backgroundColor = backgroundColor
    }

    var productName: String? = null

    var horizontalProductScrollModelList: ArrayList<HorizontalProductScrollModel>? = null


    // *** كونستركتور جديد لـ Strip Ad Banner ***
    constructor(type: Int, forAdFlag: Boolean) { // استخدم boolean لتمييز الكونستركتور
        // التحقق من أن النوع صحيح وأن العلم صحيح (للأمان)
        require(type == STRIP_AD_BANNER) { "Type must be STRIP_AD_BANNER for Ad constructor" }
        require(forAdFlag) { "forAdFlag must be true for Ad constructor" }
        this.type = type
        this.isAd = true // <<< تحديد أن هذا العنصر هو إعلان
        // image و backgroundColor سيبقيان null (أو قيمتهما الافتراضية)
    }


    //################ Horizontal Products View ####################
    var viewAllProductList: ArrayList<WishListModel> = ArrayList()

    constructor(
        type: Int, productName: String,
        horizontalProductScrollModelList: ArrayList<HorizontalProductScrollModel>,
        viewAllProductList: ArrayList<WishListModel>, backgroundColor: String) {

        this.type = type
        this.productName = productName
        this.horizontalProductScrollModelList = horizontalProductScrollModelList
        this.viewAllProductList = viewAllProductList
        this.backgroundColor = backgroundColor
    }

    //################ Horizontal Products View ####################

    //################ Grid Products View ####################
    constructor(
        type: Int,
        productName: String,
        horizontalProductScrollModelList: ArrayList<HorizontalProductScrollModel>,
        backgroundColor: String
    ) {
        this.type = type
        this.productName = productName
        this.horizontalProductScrollModelList = horizontalProductScrollModelList
        this.backgroundColor = backgroundColor
    }

    companion object {
        const val BANNER_SLIDER = 0
        const val STRIP_AD_BANNER = 1
        const val HORIZONTAL_PRODUCT_VIEW = 2
        const val GRID_PRODUCT_VIEW = 3
    }
}
