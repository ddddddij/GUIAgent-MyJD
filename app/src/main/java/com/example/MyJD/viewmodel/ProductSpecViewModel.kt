package com.example.myjd.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myjd.domain.model.ProductSpec
import com.example.myjd.domain.model.SpecSelection
import com.example.myjd.domain.model.CartItemSpec
import com.example.myjd.repository.DataRepository
import com.example.myjd.common.utils.PricingUtils
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
    private var productType: String = ""
    private var basePrice: Double = 0.0

    private val _productSpec = MutableStateFlow<ProductSpec?>(null)
    val productSpec: StateFlow<ProductSpec?> = _productSpec.asStateFlow()

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

    fun initialize(productId: String, productType: String, basePrice: Double) {
        this.productId = productId
        this.productType = productType
        this.basePrice = basePrice
        _specSelection.value = _specSelection.value.copy(productId = productId)
        loadProductSpec()
    }



                        private fun loadProductSpec() {



                            viewModelScope.launch {



                                _isLoading.value = true



                                try {



                                    val spec = when (productType) {



                                        "HuaweiP60" -> repository.loadHuaweiP60ProductSpec(productId)



                                        "HuaweiMate60" -> repository.loadHuaweiMate60ProductSpec(productId)



                                        "HuaweiNova11" -> repository.loadHuaweiNova11ProductSpec(productId)



                                        else -> repository.loadProductSpec(productId)



                                    }



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
                                    
                                    // 根据默认选择的系列和存储计算初始价格
                                    updatePriceBasedOnSpecs()



                                    // Handle error



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
        val spec = _productSpec.value ?: return
        
        // Calculate price based on series and storage using PricingUtils
        val calculatedPrice = when {
            productType.startsWith("Huawei") -> {
                when {
                    productType == "HuaweiP60" -> 
                        PricingUtils.calculateHuaweiP60Price(selection.selectedSeries, selection.selectedStorage)
                    productType == "HuaweiMate60" -> 
                        PricingUtils.calculateHuaweiMate60Price(selection.selectedSeries, selection.selectedStorage)
                    productType == "HuaweiNova11" -> 
                        PricingUtils.calculateHuaweiNova11Price(selection.selectedSeries, selection.selectedStorage)
                    else -> basePrice
                }
            }
            else -> {
                // iPhone products
                PricingUtils.calculatePrice(selection.selectedSeries, selection.selectedStorage)
            }
        }
        
        // Use calculated price if valid, otherwise use base price
        val newPrice = if (calculatedPrice > 0) calculatedPrice else basePrice
        val newOriginalPrice = newPrice + 400 // Original price is typically base + 400
        
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



                storeName = if (productType.startsWith("Huawei")) "Huawei产品京东自营旗舰店" else "Apple官方旗舰店"



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



                storeName = if (productType.startsWith("Huawei")) "Huawei产品京东自营旗舰店" else "Apple官方旗舰店",



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
