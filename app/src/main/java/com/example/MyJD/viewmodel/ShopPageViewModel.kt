package com.example.myjd.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myjd.domain.model.CartItemSpec
import com.example.myjd.domain.model.Product
import com.example.myjd.domain.model.ShopCategory
import com.example.myjd.domain.model.ShopPageData
import com.example.myjd.repository.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log
import javax.inject.Inject

@HiltViewModel
class ShopPageViewModel @Inject constructor(
    private val repository: DataRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _shopData = MutableStateFlow<ShopPageData?>(null)
    val shopData: StateFlow<ShopPageData?> = _shopData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent.asStateFlow()

    init {
        loadShopData()
    }

    fun loadShopData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val data = repository.loadShopPageData()
                _shopData.value = data
                Log.d("ShopPageViewModel", "Loaded shop data: ${data.shopInfo.name}")
            } catch (e: Exception) {
                _toastMessage.value = "店铺数据加载失败"
                Log.e("ShopPageViewModel", "Failed to load shop data", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onCategorySelected(categoryId: String) {
        _shopData.value?.let { currentData ->
            val updatedCategories = currentData.categories.map { category ->
                category.copy(isSelected = category.id == categoryId)
            }
            _shopData.value = currentData.copy(categories = updatedCategories)
            Log.d("ShopPageViewModel", "Category selected: $categoryId")
        }
    }

    fun onProductClick(product: Product) {
        Log.d("ShopPageViewModel", "Product clicked: ${product.name}")
        _navigationEvent.value = NavigationEvent(NavigationType.ToProductDetail(product.id))
    }

    fun onAddToCartClick(product: Product) {
        viewModelScope.launch {
            try {
                val cartItem = CartItemSpec(
                    id = "cart_${product.id}_${System.currentTimeMillis()}",
                    productId = product.id,
                    productName = product.name,
                    series = product.versions.firstOrNull() ?: "标准版", // Use product.versions for series
                    color = product.colors.firstOrNull() ?: "默认",
                    storage = product.versions.firstOrNull() ?: "标准版", // Assuming storage is tied to version
                    image = product.imageUrl,
                    price = product.price,
                    originalPrice = product.originalPrice ?: product.price,
                    quantity = 1,
                    selected = true,
                    storeName = product.storeName
                )

                repository.addToSpecCart(cartItem)
                _toastMessage.value = "已添加到购物车"
                Log.d("ShopPageViewModel", "Added to cart: ${product.name}")
            } catch (e: Exception) {
                _toastMessage.value = "添加到购物车失败"
                Log.e("ShopPageViewModel", "Failed to add to cart", e)
            }
        }
    }

    fun onBackClick() {
        Log.d("ShopPageViewModel", "Back button clicked")
        _navigationEvent.value = NavigationEvent(NavigationType.Back)
    }

    fun clearNavigationEvent() {
        _navigationEvent.value = null
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    data class NavigationEvent(
        val type: NavigationType,
        val productId: String? = null
    )

    sealed class NavigationType {
        object Back : NavigationType()
        data class ToProductDetail(val productId: String) : NavigationType()
    }
}
