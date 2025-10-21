package com.example.MyJD.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.MyJD.model.Order
import com.example.MyJD.presenter.OrderContract
import com.example.MyJD.presenter.OrderPresenter
import com.example.MyJD.presenter.OrderTab
import com.example.MyJD.repository.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OrderUiState(
    val orders: List<Order> = emptyList(),
    val selectedTabIndex: Int = 0,
    val isLoading: Boolean = false,
    val toastMessage: String? = null,
    val shouldNavigateToPayment: String? = null,
    val showDeleteDialog: String? = null
)

class OrderViewModel(
    private val repository: DataRepository
) : ViewModel(), OrderContract.View {
    
    private val _uiState = MutableStateFlow(OrderUiState())
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()
    
    private val presenter: OrderContract.Presenter = OrderPresenter(repository)
    
    init {
        presenter.attach(this)
        loadOrders()
    }
    
    override fun onCleared() {
        super.onCleared()
        presenter.detach()
    }
    
    // OrderContract.View implementation
    override fun showOrders(orders: List<Order>) {
        _uiState.value = _uiState.value.copy(
            orders = orders,
            isLoading = false
        )
    }
    
    override fun showToast(message: String) {
        _uiState.value = _uiState.value.copy(toastMessage = message)
    }
    
    override fun updateSelectedTab(tabIndex: Int) {
        _uiState.value = _uiState.value.copy(selectedTabIndex = tabIndex)
    }
    
    override fun navigateToPayment(orderId: String) {
        _uiState.value = _uiState.value.copy(shouldNavigateToPayment = orderId)
    }
    
    override fun showDeleteConfirmDialog(orderId: String) {
        _uiState.value = _uiState.value.copy(showDeleteDialog = orderId)
    }
    
    // Public methods for UI interaction
    fun onTabSelected(tabIndex: Int) {
        presenter.onTabSelected(tabIndex)
    }
    
    fun onActionClicked(action: String, orderId: String) {
        presenter.onActionClicked(action, orderId)
    }
    
    fun clearToast() {
        _uiState.value = _uiState.value.copy(toastMessage = null)
    }
    
    fun clearNavigation() {
        _uiState.value = _uiState.value.copy(shouldNavigateToPayment = null)
    }
    
    fun clearDeleteDialog() {
        _uiState.value = _uiState.value.copy(showDeleteDialog = null)
    }
    
    fun onDeleteConfirmed(orderId: String) {
        presenter.onDeleteConfirmed(orderId)
        clearDeleteDialog()
    }
    
    fun initializeWithTab(orderType: String) {
        val tabIndex = when (orderType) {
            "all" -> 0
            "pending_payment" -> 1
            "pending_receipt" -> 2
            "pending_use" -> 3
            "pending_review" -> 4
            else -> 0
        }
        onTabSelected(tabIndex)
    }
    
    private fun loadOrders() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            presenter.loadOrders()
        }
    }
    
    fun getTabDisplayName(index: Int): String {
        return OrderTab.values().getOrNull(index)?.displayName ?: "全部"
    }
}