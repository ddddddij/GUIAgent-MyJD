package com.example.MyJD.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.MyJD.model.Product
import com.example.MyJD.model.ProductSortType
import com.example.MyJD.presenter.SearchResultContract
import com.example.MyJD.presenter.SearchResultPresenter
import com.example.MyJD.presenter.SearchFilter
import com.example.MyJD.presenter.SearchSortType
import com.example.MyJD.repository.DataRepository

class SearchResultViewModel(
    private val repository: DataRepository,
    private val keyword: String,
    private val context: Context
) : ViewModel(), SearchResultContract.View {
    
    private val presenter: SearchResultContract.Presenter = SearchResultPresenter(repository)
    
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()
    
    private val _currentSortType = MutableStateFlow(SearchSortType.COMPREHENSIVE)
    val currentSortType: StateFlow<SearchSortType> = _currentSortType.asStateFlow()
    
    private val _showFilterDialog = MutableStateFlow(false)
    val showFilterDialog: StateFlow<Boolean> = _showFilterDialog.asStateFlow()
    
    private val _navigationEvent = MutableStateFlow<String?>(null)
    val navigationEvent: StateFlow<String?> = _navigationEvent.asStateFlow()
    
    private val _searchKeyword = MutableStateFlow(keyword)
    val searchKeyword: StateFlow<String> = _searchKeyword.asStateFlow()
    
    private val _currentFilter = MutableStateFlow(SearchFilter())
    val currentFilter: StateFlow<SearchFilter> = _currentFilter.asStateFlow()
    
    init {
        presenter.attach(this)
        loadSearchResults()
    }
    
    override fun onCleared() {
        super.onCleared()
        presenter.detach()
    }
    
    fun loadSearchResults() {
        presenter.loadSearchResults(_searchKeyword.value)
    }
    
    fun onSortClicked(sortType: SearchSortType) {
        presenter.sortProducts(sortType)
    }
    
    fun onFilterClicked() {
        presenter.onFilterClicked()
    }
    
    fun onProductClicked(productId: String) {
        presenter.onProductClicked(productId)
        
        // 记录查看第一个商品的日志（如果点击的是第一个商品）
        val products = _products.value
        if (products.isNotEmpty() && products.first().id == productId) {
            val firstProduct = products.first()
            android.util.Log.d("SearchResultViewModel", "First product viewed: ${firstProduct.name}")
            
            // 任务一日志记录：查看第一个商品
            repository.logTaskOneFirstProductViewed(productId, firstProduct.name)
            
            // 如果是搜索iPhone 15相关的，记录任务完成
            if (keyword.contains("iPhone 15", ignoreCase = true)) {
                repository.logTaskOneCompleted(keyword, firstProduct.name)
            }
        }
    }
    
    fun applyFilter(filter: SearchFilter) {
        _currentFilter.value = filter
        presenter.filterProducts(filter)
    }
    
    fun resetFilter() {
        _currentFilter.value = SearchFilter()
        presenter.resetFilter()
    }
    
    fun updateSearchKeyword(keyword: String) {
        _searchKeyword.value = keyword
    }
    
    fun onSearchClicked() {
        presenter.loadSearchResults(_searchKeyword.value)
    }
    
    fun clearToast() {
        _toastMessage.value = null
    }
    
    fun clearNavigationEvent() {
        _navigationEvent.value = null
    }
    
    fun dismissFilterDialog() {
        _showFilterDialog.value = false
    }
    
    // SearchResultContract.View implementations
    override fun showProducts(products: List<Product>) {
        _products.value = products
        
        android.util.Log.d("SearchResultViewModel", "Search results loaded: ${products.size} products for keyword: $keyword")
        
        // 任务一日志记录：搜索结果加载
        repository.logTaskOneSearchResults(keyword, products.size)
    }
    
    override fun showToast(message: String) {
        _toastMessage.value = message
    }
    
    override fun showLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }
    
    override fun updateSortType(sortType: ProductSortType) {
        _currentSortType.value = when (sortType) {
            ProductSortType.DEFAULT -> SearchSortType.COMPREHENSIVE
            ProductSortType.SALES_DESC -> SearchSortType.SALES
            ProductSortType.PRICE_LOW_TO_HIGH -> SearchSortType.PRICE_ASC
            ProductSortType.PRICE_HIGH_TO_LOW -> SearchSortType.PRICE_DESC
            else -> SearchSortType.COMPREHENSIVE
        }
    }
    
    override fun showFilterDialog() {
        _showFilterDialog.value = true
    }
    
    override fun hideFilterDialog() {
        _showFilterDialog.value = false
    }
    
    override fun navigateToProductDetail(productId: String) {
        _navigationEvent.value = productId
    }
}

class SearchResultViewModelFactory(
    private val repository: DataRepository,
    private val keyword: String,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchResultViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchResultViewModel(repository, keyword, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}