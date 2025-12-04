package com.example.myjd.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myjd.domain.model.Banner
import com.example.myjd.domain.model.Product
import com.example.myjd.repository.DataRepository
import com.example.myjd.common.utils.TaskEightLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: DataRepository,
    private val context: Context
) : ViewModel() {
    
    private val _banners = MutableStateFlow<List<Banner>>(emptyList())
    val banners: StateFlow<List<Banner>> = _banners.asStateFlow()
    
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        // 任务八日志记录：开始任务
        TaskEightLogger.logTaskStart(context)
        TaskEightLogger.logHomePageEntered(context)
        
        loadData()
        // 确保在ViewModel创建时立即加载数据
        android.util.Log.d("HomeViewModel", "HomeViewModel initialized")
    }

    private fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val bannersData = repository.loadBanners()
                val productsData = repository.loadProducts()
                
                _banners.value = bannersData.filter { it.isActive }.sortedBy { it.sortOrder }
                _products.value = productsData
                
                // 任务八日志记录：记录商品数量并完成任务
                TaskEightLogger.logProductsLoaded(context, productsData.size)
                TaskEightLogger.logTaskCompleted(context, productsData.size)
                
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addToCart(product: Product, selectedColor: String, selectedVersion: String, quantity: Int = 1) {
        viewModelScope.launch {
            val cartItemSpec = com.example.myjd.domain.model.CartItemSpec(
                id = "${product.id}_${System.currentTimeMillis()}",
                productId = product.id,
                productName = product.name,
                series = selectedVersion,
                color = selectedColor,
                storage = selectedVersion, // Assuming storage is tied to version for simplicity
                image = product.imageUrl,
                price = product.price,
                originalPrice = product.originalPrice ?: product.price * 1.2,
                quantity = quantity,
                selected = true,
                promotionTags = listOf("保价"),
                subsidyInfo = "政府补贴满1000减100",
                storeName = product.storeName,
                storeTag = "自营"
            )
            repository.addToSpecCart(cartItemSpec)
        }
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

    // 提供刷新数据的方法
    fun refreshData() {
        android.util.Log.d("HomeViewModel", "Refreshing data...")
        loadData()
    }
}