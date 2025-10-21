package com.example.MyJD.presenter

import com.example.MyJD.model.ShopPageData
import com.example.MyJD.model.ShopCategory
import com.example.MyJD.model.Product

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