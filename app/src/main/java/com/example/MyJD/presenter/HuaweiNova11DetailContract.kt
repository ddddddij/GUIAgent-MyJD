package com.example.MyJD.presenter

import com.example.MyJD.model.ProductDetail

interface HuaweiNova11DetailContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showProductDetail(productDetail: ProductDetail)
        fun showError(message: String)
        fun showAddToCartSuccess()
        fun showFavoriteToggled(isFavorite: Boolean)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadProductDetail(productId: String)
        fun toggleFavorite()
        fun selectColor(colorIndex: Int)
        fun selectPurchaseType(typeIndex: Int)
        fun addToCart()
        fun onReviewSectionViewed()
        fun onReviewsLoaded(reviewCount: Int)
    }
}