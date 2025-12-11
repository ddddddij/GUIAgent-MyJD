package com.example.jd_sim.ui.screen.productdetail

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jd_sim.domain.model.ProductSpec
import com.example.jd_sim.domain.model.SpecSelection
import com.example.jd_sim.domain.model.CartItemSpec
import com.example.jd_sim.domain.repository.DataRepository
import com.example.jd_sim.common.utils.PricingUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductSpecViewModel @Inject constructor(
    private val repository: DataRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private var productId: String = ""
    private var basePrice: Double = 0.0

    private val _productSpec = MutableStateFlow<ProductSpec?>(null)
    val productSpec: StateFlow<ProductSpec?> = _productSpec.asStateFlow()

    private val _productDetail = MutableStateFlow<com.example.jd_sim.domain.model.ProductDetail?>(null)
    val productDetail: StateFlow<com.example.jd_sim.domain.model.ProductDetail?> = _productDetail.asStateFlow()

    private val _isLoadingDetail = MutableStateFlow(false)
    val isLoadingDetail: StateFlow<Boolean> = _isLoadingDetail.asStateFlow()

    private val _specSelection = MutableStateFlow(
        SpecSelection(
            productId = "",
            selectedSeries = "",
            selectedColor = "",
            selectedStorage = "",
            quantity = 1
        )
    )
    val specSelection: StateFlow<SpecSelection> = _specSelection.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun initialize(productId: String) {
        this.productId = productId
        _specSelection.value = _specSelection.value.copy(productId = productId)
        loadProductDetail()
    }

    private fun loadProductDetail() {
        viewModelScope.launch {
            _isLoadingDetail.value = true
            try {
                val detail = repository.loadProductDetail(productId)
                _productDetail.value = detail
                basePrice = detail.currentPrice
                loadProductSpec()
            } finally {
                _isLoadingDetail.value = false
            }
        }
    }



    private fun getProductTypeFromTitle(title: String): String {
        return when {
            title.contains("P60", ignoreCase = true) || title.contains("华为P60", ignoreCase = true) || title.contains("Huawei P60", ignoreCase = true) -> "HuaweiP60"
            title.contains("Mate60", ignoreCase = true) || title.contains("华为Mate60", ignoreCase = true) || title.contains("Huawei Mate 60", ignoreCase = true) -> "HuaweiMate60"
            title.contains("Nova11", ignoreCase = true) || title.contains("华为Nova11", ignoreCase = true) || title.contains("Huawei Nova 11", ignoreCase = true) -> "HuaweiNova11"
            title.contains("ThinkPad", ignoreCase = true) -> "ThinkPad"
            title.contains("iPhone", ignoreCase = true) -> "iPhone"
            else -> "Default"
        }
    }

    private fun loadProductSpec() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val spec = repository.loadProductSpec(productId)
                _productSpec.value = spec

                val defaultColor = spec.colors.find { it.selected } ?: spec.colors.firstOrNull()
                val defaultSeries = spec.series.find { it.selected } ?: spec.series.firstOrNull()
                val defaultStorage = spec.storage.find { it.selected } ?: spec.storage.firstOrNull()

                _specSelection.value = _specSelection.value.copy(
                    selectedSeries = defaultSeries?.name ?: spec.defaultSeries,
                    selectedColor = defaultColor?.name ?: spec.defaultColor,
                    selectedStorage = defaultStorage?.capacity ?: spec.defaultStorage,
                    currentPrice = basePrice,
                    originalPrice = basePrice + 400,
                    currentImage = defaultColor?.image ?: "",
                    stockAvailable = defaultColor?.available ?: false
                )
                updatePriceBasedOnSpecs()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectSeries(seriesName: String) {
        _specSelection.value = _specSelection.value.copy(selectedSeries = seriesName)
        updatePriceBasedOnSpecs()
    }

    fun selectColor(colorName: String) {
        val spec = _productSpec.value ?: return
        val selectedColor = spec.colors.find { it.name == colorName } ?: return
        _specSelection.value = _specSelection.value.copy(
            selectedColor = colorName,
            currentImage = selectedColor.image,
            stockAvailable = selectedColor.available
        )
        updatePriceBasedOnSpecs()
    }

    fun selectStorage(storageName: String) {
        _specSelection.value = _specSelection.value.copy(selectedStorage = storageName)
        updatePriceBasedOnSpecs()
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

    private fun updatePriceBasedOnSpecs() {
        val selection = _specSelection.value
        val productType = _productDetail.value?.title?.let { getProductTypeFromTitle(it) } ?: "Default"

        val calculatedPrice = when (productType) {
            "HuaweiP60" -> PricingUtils.calculateHuaweiP60Price(selection.selectedSeries, selection.selectedStorage)
            "HuaweiMate60" -> PricingUtils.calculateHuaweiMate60Price(selection.selectedSeries, selection.selectedStorage)
            "HuaweiNova11" -> PricingUtils.calculateHuaweiNova11Price(selection.selectedSeries, selection.selectedStorage)
            "ThinkPad" -> PricingUtils.calculateThinkPadPrice(selection.selectedSeries, selection.selectedStorage)
            else -> PricingUtils.calculatePrice(selection.selectedSeries, selection.selectedStorage)
        }
        val newPrice = if (calculatedPrice > 0) calculatedPrice else basePrice
        val newOriginalPrice = newPrice + 400
        _specSelection.value = _specSelection.value.copy(
            currentPrice = newPrice,
            originalPrice = newOriginalPrice
        )
    }

    fun addToCart(): Boolean {
        val selection = _specSelection.value
        if (!selection.isValid()) return false
        val spec = _productSpec.value ?: return false
        val cartItem = CartItemSpec(
            id = "${productId}_${System.currentTimeMillis()}",
            productId = productId,
            productName = "${selection.selectedSeries} ${selection.selectedStorage}",
            series = selection.selectedSeries,
            color = selection.selectedColor,
            storage = selection.selectedStorage,
            image = selection.currentImage,
            price = selection.currentPrice,
            originalPrice = selection.originalPrice,
            quantity = selection.quantity,
            promotionTags = spec.promotionInfo.tags,
            subsidyInfo = "比加入时降¥${spec.promotionInfo.subsidyAmount}",
            storeName = _productDetail.value?.storeName ?: "京东自营"
        )
        android.util.Log.d("ProductSpecViewModel", "Attempting to add item to cart...")
        android.util.Log.d("ProductSpecViewModel", "Cart before adding: ${repository.getSpecCartTotalCount()} items")
        repository.addToSpecCart(cartItem)
        android.util.Log.d("ProductSpecViewModel", "Item added to cart: $cartItem")
        android.util.Log.d("ProductSpecViewModel", "Cart after adding: ${repository.getSpecCartTotalCount()} items")
        android.util.Log.d("ProductSpecViewModel", "All cart items: ${repository.getSpecShoppingCart().map { "${it.productName} x${it.quantity}" }}")
        return true
    }

    private val _createdOrderId = MutableStateFlow<String?>(null)
    val createdOrderId: StateFlow<String?> = _createdOrderId.asStateFlow()

    fun buyNow() {
        val selection = _specSelection.value
        if (!selection.isValid()) return
        _productSpec.value ?: return
        val productName = "${selection.selectedSeries} ${selection.selectedStorage}"
        val orderId = repository.createOrder(
            productId = productId,
            productName = productName,
            storeName = _productDetail.value?.storeName ?: "京东自营",
            imageUrl = selection.currentImage,
            price = selection.currentPrice,
            quantity = selection.quantity,
            selectedColor = selection.selectedColor,
            selectedVersion = selection.selectedStorage
        )
        _createdOrderId.value = orderId
    }



    fun canAddToCart(): Boolean {

        return _specSelection.value.isValid()

    }



    fun getCartTotalCount(): Int {
        return repository.getSpecCartTotalCount()
    }
}
