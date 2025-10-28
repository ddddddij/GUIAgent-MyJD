package com.example.MyJD.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.MyJD.model.ProductDetail
import com.example.MyJD.repository.DataRepository
import com.example.MyJD.utils.TaskFourteenLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val repository: DataRepository,
    private val context: Context
) : ViewModel() {
    
    private val _productDetail = MutableStateFlow<ProductDetail?>(null)
    val productDetail: StateFlow<ProductDetail?> = _productDetail.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _selectedColorIndex = MutableStateFlow(0)
    val selectedColorIndex: StateFlow<Int> = _selectedColorIndex.asStateFlow()
    
    private val _selectedPurchaseType = MutableStateFlow(0)
    val selectedPurchaseType: StateFlow<Int> = _selectedPurchaseType.asStateFlow()
    
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()
    
    fun loadProductDetail(productId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val detail = repository.loadProductDetail(productId)
                _productDetail.value = detail
                _selectedColorIndex.value = detail.selectedColorIndex
                _selectedPurchaseType.value = detail.selectedPurchaseType
                _isFavorite.value = detail.isFavorite
                
                // 任务十四日志记录：如果是iPhone15商品，记录相关操作
                if (detail.title.contains("iPhone15") || detail.title.contains("iPhone 15")) {
                    TaskFourteenLogger.logTaskStart(context)
                    TaskFourteenLogger.logProductDetailEntered(context, detail.title)
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun selectColor(index: Int) {
        _selectedColorIndex.value = index
        _productDetail.value?.let { detail ->
            _productDetail.value = detail.copy(selectedColorIndex = index)
        }
    }
    
    fun selectPurchaseType(index: Int) {
        _selectedPurchaseType.value = index
        _productDetail.value?.let { detail ->
            _productDetail.value = detail.copy(selectedPurchaseType = index)
        }
    }
    
    fun toggleFavorite() {
        val newFavoriteState = !_isFavorite.value
        _isFavorite.value = newFavoriteState
        _productDetail.value?.let { detail ->
            _productDetail.value = detail.copy(isFavorite = newFavoriteState)
        }
    }
    
    fun addToCart() {
        _productDetail.value?.let { detail ->
            // Convert ProductDetail to Product for cart
            val product = com.example.MyJD.model.Product(
                id = detail.id,
                name = detail.title,
                price = detail.currentPrice,
                originalPrice = detail.originalPrice,
                brand = "Apple",
                category = "手机数码",
                imageUrl = detail.images.firstOrNull() ?: "📱",
                storeId = "jd_official",
                storeName = "京东自营",
                colors = detail.colors.map { it.name },
                stock = 100,
                rating = 4.8f,
                reviewCount = 1000,
                description = detail.title
            )
            repository.addToCart(product, 1)
        }
    }
    
    fun getCurrentPrice(): Double {
        return _productDetail.value?.currentPrice ?: 0.0
    }
    
    fun getSelectedColor(): String {
        val detail = _productDetail.value
        val index = _selectedColorIndex.value
        return if (detail != null && index < detail.colors.size) {
            detail.colors[index].name
        } else {
            ""
        }
    }
    
    fun onReviewSectionViewed() {
        _productDetail.value?.let { detail ->
            if (detail.title.contains("iPhone15") || detail.title.contains("iPhone 15")) {
                TaskFourteenLogger.logReviewSectionViewed(context)
            }
        }
    }
    
    fun onReviewsLoaded(reviewCount: Int) {
        _productDetail.value?.let { detail ->
            if (detail.title.contains("iPhone15") || detail.title.contains("iPhone 15")) {
                TaskFourteenLogger.logReviewsLoaded(context, reviewCount)
                TaskFourteenLogger.logTaskCompleted(context, reviewCount)
            }
        }
    }
}

class ProductDetailViewModelFactory(
    private val repository: DataRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductDetailViewModel::class.java)) {
            return ProductDetailViewModel(repository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}