package com.example.MyJD.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.MyJD.model.ProductSpec
import com.example.MyJD.model.SpecSelection
import com.example.MyJD.model.CartItemSpec
import com.example.MyJD.repository.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductSpecViewModel(
    private val repository: DataRepository,
    private val productId: String
) : ViewModel() {

    private val _productSpec = MutableStateFlow<ProductSpec?>(null)
    val productSpec: StateFlow<ProductSpec?> = _productSpec.asStateFlow()

    private val _specSelection = MutableStateFlow(
        SpecSelection(
            productId = productId,
            selectedSeries = "",
            selectedColor = "",
            selectedStorage = "",
            quantity = 1
        )
    )
    val specSelection: StateFlow<SpecSelection> = _specSelection.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadProductSpec()
    }

    private fun loadProductSpec() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val spec = repository.loadProductSpec(productId)
                _productSpec.value = spec
                
                // 初始化默认选择
                val defaultColor = spec.colors.find { it.selected } ?: spec.colors.firstOrNull()
                val defaultSeries = spec.series.find { it.selected } ?: spec.series.firstOrNull()
                val defaultStorage = spec.storage.find { it.selected } ?: spec.storage.firstOrNull()
                
                _specSelection.value = _specSelection.value.copy(
                    selectedSeries = defaultSeries?.name ?: spec.defaultSeries,
                    selectedColor = defaultColor?.name ?: spec.defaultColor,
                    selectedStorage = defaultStorage?.capacity ?: spec.defaultStorage,
                    currentPrice = defaultColor?.price ?: 0.0,
                    originalPrice = defaultColor?.originalPrice ?: 0.0,
                    currentImage = defaultColor?.image ?: "",
                    stockAvailable = defaultColor?.available ?: false
                )
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectSeries(seriesName: String) {
        _specSelection.value = _specSelection.value.copy(selectedSeries = seriesName)
        updatePrice()
    }

    fun selectColor(colorName: String) {
        val spec = _productSpec.value ?: return
        val selectedColor = spec.colors.find { it.name == colorName } ?: return
        
        _specSelection.value = _specSelection.value.copy(
            selectedColor = colorName,
            currentPrice = selectedColor.price,
            originalPrice = selectedColor.originalPrice,
            currentImage = selectedColor.image,
            stockAvailable = selectedColor.available
        )
    }

    fun selectStorage(storageName: String) {
        _specSelection.value = _specSelection.value.copy(selectedStorage = storageName)
        updatePrice()
    }

    fun updateQuantity(quantity: Int) {
        if (quantity in 1..99) {
            _specSelection.value = _specSelection.value.copy(quantity = quantity)
        }
    }

    fun increaseQuantity() {
        val current = _specSelection.value.quantity
        if (current < 99) {
            updateQuantity(current + 1)
        }
    }

    fun decreaseQuantity() {
        val current = _specSelection.value.quantity
        if (current > 1) {
            updateQuantity(current - 1)
        }
    }

    private fun updatePrice() {
        val spec = _productSpec.value ?: return
        val selectedColor = spec.colors.find { it.name == _specSelection.value.selectedColor }
        if (selectedColor != null) {
            _specSelection.value = _specSelection.value.copy(
                currentPrice = selectedColor.price,
                originalPrice = selectedColor.originalPrice,
                currentImage = selectedColor.image,
                stockAvailable = selectedColor.available
            )
        }
    }

    fun addToCart(): Boolean {
        val selection = _specSelection.value
        if (!selection.isValid()) return false

        val spec = _productSpec.value ?: return false
        val cartItem = CartItemSpec(
            id = "${productId}_${System.currentTimeMillis()}",
            productId = productId,
            productName = "Apple/苹果 iPhone 15 (A3092) ${selection.selectedStorage}",
            series = selection.selectedSeries,
            color = selection.selectedColor,
            storage = selection.selectedStorage,
            image = selection.currentImage,
            price = selection.currentPrice,
            originalPrice = selection.originalPrice,
            quantity = selection.quantity,
            promotionTags = spec.promotionInfo.tags,
            subsidyInfo = "比加入时降¥${spec.promotionInfo.subsidyAmount}",
            storeName = "Apple产品京东自营旗舰店"
        )

        android.util.Log.d("ProductSpecViewModel", "Attempting to add item to cart...")
        android.util.Log.d("ProductSpecViewModel", "Cart before adding: ${repository.getSpecCartTotalCount()} items")
        
        repository.addToSpecCart(cartItem)
        
        android.util.Log.d("ProductSpecViewModel", "Item added to cart: $cartItem")
        android.util.Log.d("ProductSpecViewModel", "Cart after adding: ${repository.getSpecCartTotalCount()} items")
        android.util.Log.d("ProductSpecViewModel", "All cart items: ${repository.getSpecShoppingCart().map { "${it.productName} x${it.quantity}" }}")
        return true
    }

    fun canAddToCart(): Boolean {
        return _specSelection.value.isValid()
    }

    fun getCartTotalCount(): Int {
        return repository.getSpecCartTotalCount()
    }

    class Factory(
        private val repository: DataRepository,
        private val productId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProductSpecViewModel::class.java)) {
                return ProductSpecViewModel(repository, productId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}