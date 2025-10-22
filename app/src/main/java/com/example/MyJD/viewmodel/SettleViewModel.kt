package com.example.MyJD.viewmodel

import androidx.lifecycle.ViewModel
import com.example.MyJD.model.SettleData
import com.example.MyJD.model.SettlePricing
import com.example.MyJD.model.Coupon
import com.example.MyJD.presenter.SettleContract
import com.example.MyJD.presenter.SettlePresenter
import com.example.MyJD.repository.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SettleUiState(
    val settleData: SettleData? = null,
    val pricing: SettlePricing? = null,
    val quantity: Int = 1,
    val toastMessage: String? = null,
    val isLoading: Boolean = false,
    val shouldNavigateToPaymentSuccess: String? = null,
    val shouldNavigateToAddressList: Boolean = false,
    val showCouponDialog: Boolean = false,
    val availableCoupons: List<Coupon> = emptyList(),
    val currentOrderAmount: Double = 0.0
)

class SettleViewModel(
    private val repository: DataRepository
) : ViewModel(), SettleContract.View {
    
    private val _uiState = MutableStateFlow(SettleUiState())
    val uiState: StateFlow<SettleUiState> = _uiState.asStateFlow()
    
    private val presenter: SettleContract.Presenter = SettlePresenter(repository)
    
    init {
        presenter.attach(this)
    }
    
    override fun onCleared() {
        super.onCleared()
        presenter.detach()
    }
    
    // SettleContract.View implementation
    override fun showSettleData(data: SettleData) {
        _uiState.value = _uiState.value.copy(
            settleData = data,
            pricing = data.pricing,
            quantity = data.product.quantity,
            isLoading = false
        )
    }
    
    override fun updatePricing(pricing: SettlePricing) {
        _uiState.value = _uiState.value.copy(pricing = pricing)
    }
    
    override fun showToast(message: String) {
        _uiState.value = _uiState.value.copy(toastMessage = message)
    }
    
    override fun updateQuantity(quantity: Int) {
        _uiState.value = _uiState.value.copy(quantity = quantity)
    }
    
    override fun navigateBack() {
        // 这个会在UI层处理
    }
    
    override fun navigateToPaymentSuccess(orderAmount: String) {
        _uiState.value = _uiState.value.copy(shouldNavigateToPaymentSuccess = orderAmount)
    }
    
    override fun navigateToAddressList() {
        _uiState.value = _uiState.value.copy(shouldNavigateToAddressList = true)
    }
    
    override fun showCouponDialog(availableCoupons: List<Coupon>, orderAmount: Double) {
        _uiState.value = _uiState.value.copy(
            showCouponDialog = true,
            availableCoupons = availableCoupons,
            currentOrderAmount = orderAmount
        )
    }
    
    // Public methods for UI interaction
    fun loadSettleData(
        productId: String? = null,
        productName: String? = null,
        spec: String? = null,
        price: Double? = null,
        imageUrl: String? = null
    ) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        presenter.loadSettleData(productId, productName, spec, price, imageUrl)
    }
    
    fun loadCartSettleData() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        presenter.loadCartSettleData()
    }
    
    fun loadOrderSettleData(orderId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        presenter.loadOrderSettleData(orderId)
    }
    
    fun onQuantityIncrease() {
        presenter.onQuantityIncrease()
    }
    
    fun onQuantityDecrease() {
        presenter.onQuantityDecrease()
    }
    
    fun onAddressClick() {
        presenter.onAddressClick()
    }
    
    fun onAddressSelected(address: com.example.MyJD.model.Address) {
        presenter.onAddressSelected(address)
    }
    
    fun clearAddressListNavigation() {
        _uiState.value = _uiState.value.copy(shouldNavigateToAddressList = false)
    }
    
    fun onServiceClick() {
        presenter.onServiceClick()
    }
    
    fun onDeliveryClick() {
        presenter.onDeliveryClick()
    }
    
    fun onCouponClick() {
        presenter.onCouponClick()
    }
    
    fun onPaymentClick() {
        presenter.onPaymentClick()
    }
    
    fun clearToast() {
        _uiState.value = _uiState.value.copy(toastMessage = null)
    }
    
    fun clearNavigation() {
        _uiState.value = _uiState.value.copy(shouldNavigateToPaymentSuccess = null)
    }
    
    fun onCouponSelected(coupon: Coupon?) {
        presenter.onCouponSelected(coupon)
    }
    
    fun dismissCouponDialog() {
        _uiState.value = _uiState.value.copy(showCouponDialog = false)
    }
}