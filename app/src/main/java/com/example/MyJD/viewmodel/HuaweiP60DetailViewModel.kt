package com.example.MyJD.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.MyJD.model.ProductDetail
import com.example.MyJD.presenter.HuaweiP60DetailContract
import com.example.MyJD.presenter.HuaweiP60DetailPresenter
import com.example.MyJD.repository.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HuaweiP60DetailViewModel(
    private val repository: DataRepository,
    private val context: Context
) : ViewModel(), HuaweiP60DetailContract.View {
    
    private val presenter = HuaweiP60DetailPresenter(repository)
    
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
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        presenter.attachView(this)
    }
    
    override fun onCleared() {
        super.onCleared()
        presenter.detachView()
    }
    
    fun loadProductDetail(productId: String) {
        presenter.loadProductDetail(productId)
    }
    
    fun toggleFavorite() {
        presenter.toggleFavorite()
    }
    
    fun selectColor(colorIndex: Int) {
        _selectedColorIndex.value = colorIndex
        presenter.selectColor(colorIndex)
    }
    
    fun selectPurchaseType(typeIndex: Int) {
        _selectedPurchaseType.value = typeIndex
        presenter.selectPurchaseType(typeIndex)
    }
    
    fun addToCart() {
        presenter.addToCart()
    }
    
    fun onReviewSectionViewed() {
        presenter.onReviewSectionViewed()
    }
    
    fun onReviewsLoaded(reviewCount: Int) {
        presenter.onReviewsLoaded(reviewCount)
    }
    
    override fun showLoading() {
        _isLoading.value = true
    }
    
    override fun hideLoading() {
        _isLoading.value = false
    }
    
    override fun showProductDetail(productDetail: ProductDetail) {
        _productDetail.value = productDetail
        _selectedColorIndex.value = productDetail.selectedColorIndex
        _selectedPurchaseType.value = productDetail.selectedPurchaseType
        _isFavorite.value = productDetail.isFavorite
    }
    
    override fun showError(message: String) {
        _error.value = message
        _isLoading.value = false
    }
    
    override fun showAddToCartSuccess() {
        // This will be handled in the UI layer
    }
    
    override fun showFavoriteToggled(isFavorite: Boolean) {
        _isFavorite.value = isFavorite
    }
}

class HuaweiP60DetailViewModelFactory(
    private val repository: DataRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HuaweiP60DetailViewModel::class.java)) {
            return HuaweiP60DetailViewModel(repository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}