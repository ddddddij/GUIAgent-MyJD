package com.example.jd_sim.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jd_sim.repository.DataRepository
import com.example.jd_sim.domain.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Placeholder for SearchSortType and SearchFilter, will be defined properly
enum class SearchSortType {
    COMPREHENSIVE,
    SALES,
    PRICE_ASC,
    PRICE_DESC
}

data class SearchFilter(
    val priceMin: Double? = null,
    val priceMax: Double? = null,
    val categories: List<String> = emptyList()
)

@HiltViewModel
class SearchResultViewModel @Inject constructor(
    private val repository: DataRepository
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentSortType = MutableStateFlow(SearchSortType.COMPREHENSIVE)
    val currentSortType: StateFlow<SearchSortType> = _currentSortType.asStateFlow()
    
    private val _currentFilter = MutableStateFlow(SearchFilter())
    val currentFilter: StateFlow<SearchFilter> = _currentFilter.asStateFlow()

    private var allProducts: List<Product> = emptyList()

    fun loadSearchResults(keyword: String) {
        viewModelScope.launch {
            _isLoading.value = true
            allProducts = repository.getSearchResults(keyword)
            applyCurrentSortAndFilter()
            _isLoading.value = false
        }
    }

    fun sortProducts(sortType: SearchSortType) {
        _currentSortType.value = sortType
        applyCurrentSortAndFilter()
    }

    fun filterProducts(filter: SearchFilter) {
        _currentFilter.value = filter
        applyCurrentSortAndFilter()
    }
    
    fun resetFilter() {
        _currentFilter.value = SearchFilter()
        applyCurrentSortAndFilter()
    }

    private fun applyCurrentSortAndFilter() {
        var products = allProducts.toList()
        
        // 应用筛选
        if (_currentFilter.value.priceMin != null) {
            products = products.filter { it.price >= _currentFilter.value.priceMin!! }
        }
        if (_currentFilter.value.priceMax != null) {
            products = products.filter { it.price <= _currentFilter.value.priceMax!! }
        }
        if (_currentFilter.value.categories.isNotEmpty()) {
            products = products.filter { it.category in _currentFilter.value.categories }
        }
        
        // 应用排序
        products = when (_currentSortType.value) {
            SearchSortType.COMPREHENSIVE -> products.sortedByDescending { 
                (it.sales ?: 0) * 0.6 + it.rating * 1000 + (10000 - it.price) * 0.1
            }
            SearchSortType.SALES -> products.sortedByDescending { it.sales ?: 0 }
            SearchSortType.PRICE_ASC -> products.sortedBy { it.price }
            SearchSortType.PRICE_DESC -> products.sortedByDescending { it.price }
        }
        
        _products.value = products
    }
}

// Extension property for sales, consider moving this to a more appropriate file.
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