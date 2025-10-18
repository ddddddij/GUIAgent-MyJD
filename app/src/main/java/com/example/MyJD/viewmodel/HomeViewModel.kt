package com.example.MyJD.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.MyJD.model.Banner
import com.example.MyJD.model.Product
import com.example.MyJD.model.ShoppingCart
import com.example.MyJD.repository.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: DataRepository) : ViewModel() {
    
    private val _banners = MutableStateFlow<List<Banner>>(emptyList())
    val banners: StateFlow<List<Banner>> = _banners.asStateFlow()
    
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()
    
    private val _shoppingCart = MutableStateFlow<ShoppingCart>(ShoppingCart())
    val shoppingCart: StateFlow<ShoppingCart> = _shoppingCart.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val bannersData = repository.loadBanners()
                val productsData = repository.loadProducts()
                
                _banners.value = bannersData.filter { it.isActive }.sortedBy { it.sortOrder }
                _products.value = productsData
                updateShoppingCart()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addToCart(product: Product) {
        repository.addToCart(product)
        updateShoppingCart()
    }

    fun removeFromCart(cartItemId: String) {
        repository.removeFromCart(cartItemId)
        updateShoppingCart()
    }

    fun updateCartItemQuantity(cartItemId: String, quantity: Int) {
        repository.updateCartItemQuantity(cartItemId, quantity)
        updateShoppingCart()
    }

    fun toggleCartItemSelection(cartItemId: String) {
        repository.toggleCartItemSelection(cartItemId)
        updateShoppingCart()
    }

    private fun updateShoppingCart() {
        _shoppingCart.value = repository.getShoppingCart()
    }

    fun getRecommendedProducts(): List<Product> {
        return _products.value.take(10)
    }

    fun getPhoneProducts(): List<Product> {
        return _products.value.filter { it.category == "手机" }
    }

    fun getSupermarketProducts(): List<Product> {
        return _products.value.filter { it.category == "超市" }
    }
}