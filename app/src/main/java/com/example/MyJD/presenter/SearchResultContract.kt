package com.example.MyJD.presenter

import com.example.MyJD.model.Product
import com.example.MyJD.model.ProductSortType

interface SearchResultContract {
    
    interface View {
        fun showProducts(products: List<Product>)
        fun showToast(message: String)
        fun showLoading(isLoading: Boolean)
        fun updateSortType(sortType: ProductSortType)
        fun showFilterDialog()
        fun hideFilterDialog()
        fun navigateToProductDetail(productId: String)
    }
    
    interface Presenter {
        fun attach(view: View)
        fun detach()
        fun loadSearchResults(keyword: String)
        fun sortProducts(sortType: SearchSortType)
        fun filterProducts(filter: SearchFilter)
        fun onProductClicked(productId: String)
        fun onSortClicked(sortType: SearchSortType)
        fun onFilterClicked()
        fun resetFilter()
    }
}

data class SearchFilter(
    val priceMin: Double? = null,
    val priceMax: Double? = null,
    val categories: List<String> = emptyList()
)

enum class SearchSortType {
    COMPREHENSIVE,  // 综合
    SALES,          // 销量
    PRICE_ASC,      // 价格升序
    PRICE_DESC      // 价格降序
}