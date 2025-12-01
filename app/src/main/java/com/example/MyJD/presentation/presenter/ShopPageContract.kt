package com.example.MyJD.presentation.presenter

import com.example.MyJD.presentation.model.ShopPageData
import com.example.MyJD.presentation.model.ShopCategory
import com.example.MyJD.domain.model.Product

interface ShopPageContract {
    
    interface View {
        fun showShopData(shopData: ShopPageData)
        fun showLoading(show: Boolean)
        fun showToast(message: String)
        fun updateCategories(categories: List<ShopCategory>)
        fun navigateToProductDetail(productId: String)
        fun navigateBack()
    }
    
    interface Presenter {
        fun attach(view: View)
        fun detach()
        fun loadShopData()
        fun onCategorySelected(categoryId: String)
        fun onProductClick(product: Product)
        fun onAddToCartClick(product: Product)
        fun onBackClick()
    }
}