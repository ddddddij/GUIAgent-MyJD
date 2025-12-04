package com.example.myjd.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myjd.model.CartItemSpec
import com.example.myjd.model.Product
import com.example.myjd.model.ProductDetail
import com.example.myjd.repository.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.myjd.common.utils.TaskFourteenLogger
import android.util.Log

class HuaweiMate60DetailViewModel(
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

    private val _showAddToCartSuccess = MutableStateFlow(false)
    val showAddToCartSuccess: StateFlow<Boolean> = _showAddToCartSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadProductDetail(productId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val detail = repository.getHuaweiMate60ProductDetail(productId)
                _productDetail.value = detail
                _selectedColorIndex.value = detail?.selectedColorIndex ?: 0
                _selectedPurchaseType.value = detail?.selectedPurchaseType ?: 0
                _isFavorite.value = detail?.isFavorite ?: false
                TaskFourteenLogger.logProductDetailEntered(context, detail?.title ?: productId)
            } catch (e: Exception) {
                _errorMessage.value = "商品详情加载失败: ${e.message}"
                Log.e("HuaweiMate60DetailViewModel", "Error loading product detail", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite() {
        _productDetail.value?.let { detail ->
            val newFavoriteState = !detail.isFavorite
            _isFavorite.value = newFavoriteState
            _productDetail.value = detail.copy(isFavorite = newFavoriteState)
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

    fun addToCart() {
        viewModelScope.launch {
            _productDetail.value?.let { detail ->
                val selectedColor = detail.colors.getOrNull(_selectedColorIndex.value)?.name ?: "默认颜色"
                val selectedPurchaseType = detail.purchaseTypes.getOrNull(_selectedPurchaseType.value) ?: "默认版本"

                val cartItemSpec = CartItemSpec(
                    id = "${detail.id}_${System.currentTimeMillis()}",
                    productId = detail.id,
                    productName = detail.title,
                    series = selectedPurchaseType,
                    color = selectedColor,
                    storage = selectedPurchaseType, // Assuming storage is tied to purchase type for simplicity
                    image = detail.images.firstOrNull() ?: "",
                    price = detail.currentPrice,
                    originalPrice = detail.originalPrice,
                    quantity = 1,
                    selected = true,
                    promotionTags = listOf("保价"),
                    subsidyInfo = "政府补贴满1000减100",
                    storeName = detail.storeName,
                    storeTag = "自营"
                )
                repository.addToSpecCart(cartItemSpec)
                _showAddToCartSuccess.value = true
                TaskFourteenLogger.logAddToCart(context, "${detail.title} $selectedColor $selectedPurchaseType")
            }
        }
    }

    fun onReviewSectionViewed() {
        TaskFourteenLogger.logReviewSectionViewed(context)
    }

    fun onReviewsLoaded(reviewCount: Int) {
        TaskFourteenLogger.logReviewsLoaded(context, reviewCount)
        TaskFourteenLogger.logTaskCompleted(context, reviewCount)
    }

    fun clearAddToCartSuccess() {
        _showAddToCartSuccess.value = false
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}

class HuaweiMate60DetailViewModelFactory(
    private val repository: DataRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HuaweiMate60DetailViewModel::class.java)) {
            return HuaweiMate60DetailViewModel(repository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

