package com.example.MyJD.presenter

import com.example.MyJD.model.Product
import com.example.MyJD.repository.DataRepository

class SearchResultPresenter(
    private val repository: DataRepository
) : SearchResultContract.Presenter {
    
    private var view: SearchResultContract.View? = null
    private var allProducts: List<Product> = emptyList()
    private var filteredProducts: List<Product> = emptyList()
    private var currentSortType: SearchSortType = SearchSortType.COMPREHENSIVE
    private var currentFilter: SearchFilter = SearchFilter()
    
    override fun attach(view: SearchResultContract.View) {
        this.view = view
    }
    
    override fun detach() {
        this.view = null
    }
    
    override fun loadSearchResults(keyword: String) {
        view?.showLoading(true)
        
        // 模拟从search_results.json加载数据
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            allProducts = repository.getSearchResults()
            filteredProducts = allProducts
            applyCurrentSortAndFilter()
            view?.showLoading(false)
        }, 500)
    }
    
    override fun sortProducts(sortType: SearchSortType) {
        currentSortType = sortType
        view?.updateSortType(
            when (sortType) {
                SearchSortType.COMPREHENSIVE -> com.example.MyJD.model.ProductSortType.DEFAULT
                SearchSortType.SALES -> com.example.MyJD.model.ProductSortType.SALES_DESC
                SearchSortType.PRICE_ASC -> com.example.MyJD.model.ProductSortType.PRICE_LOW_TO_HIGH
                SearchSortType.PRICE_DESC -> com.example.MyJD.model.ProductSortType.PRICE_HIGH_TO_LOW
            }
        )
        applyCurrentSortAndFilter()
    }
    
    override fun filterProducts(filter: SearchFilter) {
        currentFilter = filter
        applyCurrentSortAndFilter()
        view?.hideFilterDialog()
    }
    
    override fun onProductClicked(productId: String) {
        view?.navigateToProductDetail(productId)
    }
    
    override fun onSortClicked(sortType: SearchSortType) {
        sortProducts(sortType)
    }
    
    override fun onFilterClicked() {
        view?.showFilterDialog()
    }
    
    override fun resetFilter() {
        currentFilter = SearchFilter()
        applyCurrentSortAndFilter()
    }
    
    private fun applyCurrentSortAndFilter() {
        var products = allProducts.toList()
        
        // 应用筛选
        if (currentFilter.priceMin != null) {
            products = products.filter { it.price >= currentFilter.priceMin!! }
        }
        if (currentFilter.priceMax != null) {
            products = products.filter { it.price <= currentFilter.priceMax!! }
        }
        if (currentFilter.categories.isNotEmpty()) {
            products = products.filter { it.category in currentFilter.categories }
        }
        
        // 应用排序
        products = when (currentSortType) {
            SearchSortType.COMPREHENSIVE -> products.sortedByDescending { 
                // 综合排序：考虑销量、评分、价格等因素
                (it.sales ?: 0) * 0.6 + it.rating * 1000 + (10000 - it.price) * 0.1
            }
            SearchSortType.SALES -> products.sortedByDescending { it.sales ?: 0 }
            SearchSortType.PRICE_ASC -> products.sortedBy { it.price }
            SearchSortType.PRICE_DESC -> products.sortedByDescending { it.price }
        }
        
        filteredProducts = products
        view?.showProducts(filteredProducts)
    }
}

// 扩展Product模型以支持销量字段
val Product.sales: Int?
    get() = when (this.id) {
        "iphone15_001" -> 12580
        "iphone15_256" -> 15600
        "iphone15_pro_001" -> 18900
        "iphone15_pro_max_001" -> 23100
        "iphone15_plus_001" -> 8500
        "airpods_pro_2" -> 28600
        "huawei_p60_001" -> 9200
        "headphone_001" -> 12800
        else -> (1000..30000).random()
    }