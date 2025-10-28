package com.example.MyJD.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.MyJD.model.Order
import com.example.MyJD.model.OrderStatus
import com.example.MyJD.presenter.OrderContract
import com.example.MyJD.presenter.OrderPresenter
import com.example.MyJD.presenter.OrderTab
import com.example.MyJD.repository.DataRepository
import com.example.MyJD.utils.TaskSixLogger
import com.example.MyJD.utils.TaskTenLogger
import com.example.MyJD.utils.TaskSeventeenLogger
import com.example.MyJD.utils.TaskEighteenLogger
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
    private val repository: DataRepository,
    private val context: Context
) : ViewModel(), OrderContract.View {
    
    private val _uiState = MutableStateFlow(OrderUiState())
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()
    
    private val presenter: OrderContract.Presenter = OrderPresenter(repository)
    
    init {
        presenter.attach(this)
        
        // 任务十日志记录：进入订单页面
        TaskTenLogger.logTaskStart(context)
        TaskTenLogger.logOrderPageEntered(context)
        
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
        
        // 任务四日志记录：订单列表加载完成
        repository.logTaskFourOrderListLoaded(orders.size)
        
        // 如果订单列表已加载，记录任务完成
        if (orders.isNotEmpty()) {
            repository.logTaskFourCompleted(orders.size)
        }
        
        // 任务六日志记录：如果当前显示待付款订单且找到第一个待付款订单
        if (_uiState.value.selectedTabIndex == 1 && orders.isNotEmpty()) {
            val firstPendingOrder = orders.firstOrNull { it.status == OrderStatus.PENDING_PAYMENT }
            firstPendingOrder?.let { order ->
                TaskSixLogger.logOrderFound(context, order.id)
            }
        }
        
        // 任务十日志记录：如果当前显示待收货订单
        if (_uiState.value.selectedTabIndex == 2) {
            val pendingReceiptOrders = orders.filter { it.status == OrderStatus.PENDING_RECEIPT }
            TaskTenLogger.logPendingReceiptOrdersLoaded(context, pendingReceiptOrders.size)
            TaskTenLogger.logTaskCompleted(context, pendingReceiptOrders.size)
            
            // 任务十七日志记录：查看待收货订单（专门针对iPhone 15 粉色订单）
            val iphone15PinkOrders = pendingReceiptOrders.filter { order ->
                order.items.any { item ->
                    item.product.name.contains("iPhone 15") && item.product.name.contains("粉色") && item.product.name.contains("256GB")
                }
            }
            if (iphone15PinkOrders.isNotEmpty()) {
                TaskSeventeenLogger.logPendingReceiptOrdersViewed(context, iphone15PinkOrders.size)
                TaskSeventeenLogger.logTaskCompleted(context)
            }
        }
    }
    
    override fun showToast(message: String) {
        _uiState.value = _uiState.value.copy(toastMessage = message)
        
        // 任务十八日志记录：根据消息判断操作结果
        when (message) {
            "订单已取消" -> {
                TaskEighteenLogger.logCancelOrderSuccess(context)
                // 检查是否已取消所有待付款订单
                val pendingPaymentOrders = _uiState.value.orders.filter { it.status == OrderStatus.PENDING_PAYMENT }
                if (pendingPaymentOrders.size <= 1) { // 当前这个即将被取消
                    TaskEighteenLogger.logAllPendingPaymentOrdersCancelled(context)
                }
            }
            "订单已删除" -> {
                TaskEighteenLogger.logDeleteCancelledOrderSuccess(context)
                // 检查是否还有已取消的订单
                val cancelledOrders = _uiState.value.orders.filter { it.status == OrderStatus.CANCELLED }
                if (cancelledOrders.size <= 1) { // 当前这个即将被删除
                    TaskEighteenLogger.logAllCancelledOrdersDeleted(context)
                    TaskEighteenLogger.logTaskCompleted(context)
                }
            }
        }
    }
    
    override fun updateSelectedTab(tabIndex: Int) {
        _uiState.value = _uiState.value.copy(selectedTabIndex = tabIndex)
    }
    
    override fun navigateToPayment(orderId: String) {
        _uiState.value = _uiState.value.copy(shouldNavigateToPayment = orderId)
        // 任务六日志记录：导航到付款页面
        TaskSixLogger.logNavigateToOrder(context, orderId)
        TaskSixLogger.logPaymentPageDisplayed(context)
    }
    
    override fun showDeleteConfirmDialog(orderId: String) {
        _uiState.value = _uiState.value.copy(showDeleteDialog = orderId)
    }
    
    // Public methods for UI interaction
    fun onTabSelected(tabIndex: Int) {
        // 任务十日志记录：如果选择待收货标签页
        if (tabIndex == 2) { // 待收货标签页索引为2
            TaskTenLogger.logPendingReceiptTabSelected(context)
        }
        presenter.onTabSelected(tabIndex)
    }
    
    fun onActionClicked(action: String, orderId: String) {
        // 任务六日志记录：如果是点击付款按钮
        if (action == "去支付") {
            TaskSixLogger.logClickPayButton(context)
        }
        
        // 任务十八日志记录：取消订单操作
        if (action == "取消订单") {
            // 检查是否是待付款订单
            val order = _uiState.value.orders.find { it.id == orderId }
            if (order?.status == OrderStatus.PENDING_PAYMENT) {
                TaskEighteenLogger.logTaskStart(context)
                TaskEighteenLogger.logCancelOrderAttempted(context, orderId)
            }
        }
        
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
        // 任务十八日志记录：删除已取消订单
        val order = _uiState.value.orders.find { it.id == orderId }
        if (order?.status == OrderStatus.CANCELLED) {
            TaskEighteenLogger.logDeleteCancelledOrderAttempted(context, orderId)
        }
        
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
        
        // 任务六日志记录：如果是待付款订单页面，开始任务
        if (orderType == "pending_payment") {
            TaskSixLogger.logTaskStart(context)
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