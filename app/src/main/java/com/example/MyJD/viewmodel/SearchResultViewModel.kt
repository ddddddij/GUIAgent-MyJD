package com.example.MyJD.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.MyJD.model.Product
import com.example.MyJD.repository.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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

class SearchResultViewModel(private val repository: DataRepository) : ViewModel() {

    private var allProducts: List<Product> = emptyList()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _sortType = MutableStateFlow(SearchSortType.COMPREHENSIVE)
    val sortType: StateFlow<SearchSortType> = _sortType.asStateFlow()

    private val _filter = MutableStateFlow(SearchFilter())
    val filter: StateFlow<SearchFilter> = _filter.asStateFlow()

    fun searchProducts(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                allProducts = repository.getSearchResults(query)
                applySortAndFilter()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setSortType(sortType: SearchSortType) {
        _sortType.value = sortType
        applySortAndFilter()
    }

    fun setFilter(filter: SearchFilter) {
        _filter.value = filter
        applySortAndFilter()
    }
    
    private fun applySortAndFilter() {
        var filteredProducts = allProducts

        // Apply filter
        _filter.value.priceMin?.let { min ->
            filteredProducts = filteredProducts.filter { it.price >= min }
        }
        _filter.value.priceMax?.let { max ->
            filteredProducts = filteredProducts.filter { it.price <= max }
        }
        if (_filter.value.categories.isNotEmpty()) {
            filteredProducts = filteredProducts.filter { it.category in _filter.value.categories }
        }

        // Apply sort
        _products.value = when (_sortType.value) {
            SearchSortType.COMPREHENSIVE -> filteredProducts.sortedByDescending { product -> (product.sales ?: 0) * 0.6 + product.rating * 1000 + (10000 - product.price) * 0.1 }
            SearchSortType.SALES -> filteredProducts.sortedByDescending { product -> product.sales ?: 0 }
            SearchSortType.PRICE_ASC -> filteredProducts.sortedBy { product -> product.price }
            SearchSortType.PRICE_DESC -> filteredProducts.sortedByDescending { product -> product.price }
        }
    }
}

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
