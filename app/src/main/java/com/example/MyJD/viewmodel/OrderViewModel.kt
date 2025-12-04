package com.example.MyJD.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.MyJD.model.Order
import com.example.MyJD.model.OrderStatus
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
    val showDeleteDialog: String? = null,
    val showPaymentSuccessDialog: Boolean = false
)

class OrderViewModel(
    private val repository: DataRepository,
    private val context: Context
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(OrderUiState())
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()
    
    private var allOrders: List<Order> = emptyList() // Store all fetched orders
    
    init {
        // 任务十日志记录：进入订单页面
        TaskTenLogger.logTaskStart(context)
        TaskTenLogger.logOrderPageEntered(context)
        
        loadOrders()
    }
    
    // Public methods for UI interaction
    fun onTabSelected(tabIndex: Int) {
        _uiState.value = _uiState.value.copy(selectedTabIndex = tabIndex)
        filterOrdersByTab(tabIndex)
        
        // 任务十日志记录：如果选择待收货标签页
        if (tabIndex == 2) { // 待收货标签页索引为2
            TaskTenLogger.logPendingReceiptTabSelected(context)
        }
    }
    
    fun onActionClicked(action: String, orderId: String) {
        viewModelScope.launch {
            when (action) {
                "删除订单" -> {
                    _uiState.value = _uiState.value.copy(showDeleteDialog = orderId)
                }
                "去支付" -> {
                    // 任务六日志记录：如果是点击付款按钮
                    TaskSixLogger.logClickPayButton(context)
                    val success = repository.payOrder(orderId)
                    if (success) {
                        _uiState.value = _uiState.value.copy(showPaymentSuccessDialog = true)
                        loadOrders() // 刷新订单列表以移除已支付订单
                    } else {
                        _uiState.value = _uiState.value.copy(toastMessage = "支付失败")
                    }
                }
                "取消订单" -> {
                    // 任务十八日志记录：取消订单操作
                    val order = allOrders.find { it.id == orderId }
                    if (order?.status == OrderStatus.PENDING_PAYMENT) {
                        TaskEighteenLogger.logTaskStart(context)
                        TaskEighteenLogger.logCancelOrderAttempted(context, orderId)
                    }
                    
                    val success = repository.cancelOrder(orderId)
                    if (success) {
                        _uiState.value = _uiState.value.copy(toastMessage = "订单已取消")
                        loadOrders() // 刷新订单列表
                        // 任务十八日志记录：根据消息判断操作结果
                        TaskEighteenLogger.logCancelOrderSuccess(context)
                        // 检查是否已取消所有待付款订单
                        val pendingPaymentOrders = repository.getOrders().filter { it.status == OrderStatus.PENDING_PAYMENT }
                        if (pendingPaymentOrders.isEmpty()) {
                            TaskEighteenLogger.logAllPendingPaymentOrdersCancelled(context)
                        }
                    } else {
                        // 获取订单状态以提供更具体的错误信息
                        val order = repository.getOrderById(orderId)
                        val statusText = when(order?.status) {
                            OrderStatus.PENDING_PAYMENT -> "待付款"
                            OrderStatus.PENDING_SHIPMENT -> "待使用"
                            OrderStatus.PENDING_RECEIPT -> "待收货"
                            OrderStatus.PENDING_REVIEW -> "待评价"
                            OrderStatus.COMPLETED -> "已完成"
                            OrderStatus.CANCELLED -> "已取消"
                            null -> "未知"
                        }
                        _uiState.value = _uiState.value.copy(toastMessage = "取消订单失败，当前状态：$statusText（只有待付款订单可以取消）")
                    }
                }
                "确认收货" -> {
                    val success = repository.confirmReceipt(orderId)
                    if (success) {
                        _uiState.value = _uiState.value.copy(toastMessage = "已确认收货")
                        loadOrders() // 刷新订单列表
                    } else {
                        val order = repository.getOrderById(orderId)
                        val statusText = when(order?.status) {
                            OrderStatus.PENDING_PAYMENT -> "待付款"
                            OrderStatus.PENDING_SHIPMENT -> "待使用"
                            OrderStatus.PENDING_RECEIPT -> "待收货"
                            OrderStatus.PENDING_REVIEW -> "待评价"
                            OrderStatus.COMPLETED -> "已完成"
                            OrderStatus.CANCELLED -> "已取消"
                            null -> "未知"
                        }
                        _uiState.value = _uiState.value.copy(toastMessage = "确认收货失败，当前状态：$statusText（只有待收货订单可以确认收货）")
                    }
                }
                "再次购买" -> _uiState.value = _uiState.value.copy(toastMessage = "功能开发中")
                "申请售后" -> _uiState.value = _uiState.value.copy(toastMessage = "功能开发中")
                "查看物流" -> _uiState.value = _uiState.value.copy(toastMessage = "功能开发中")
                "查看券码" -> _uiState.value = _uiState.value.copy(toastMessage = "功能开发中")
                "去评价" -> _uiState.value = _uiState.value.copy(toastMessage = "功能开发中")
                else -> _uiState.value = _uiState.value.copy(toastMessage = "功能开发中")
            }
        }
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

    fun clearPaymentSuccessDialog() {
        _uiState.value = _uiState.value.copy(showPaymentSuccessDialog = false)
    }

    fun onDeleteConfirmed(orderId: String) {
        viewModelScope.launch {
            // 任务十八日志记录：删除已取消订单
            val order = allOrders.find { it.id == orderId }
            if (order?.status == OrderStatus.CANCELLED) {
                TaskEighteenLogger.logDeleteCancelledOrderAttempted(context, orderId)
            }
            
            val success = repository.deleteOrder(orderId)
            if (success) {
                _uiState.value = _uiState.value.copy(toastMessage = "订单已删除")
                loadOrders() // 刷新订单列表
                // 任务十八日志记录：根据消息判断操作结果
                TaskEighteenLogger.logDeleteCancelledOrderSuccess(context)
                // 检查是否还有已取消的订单
                val cancelledOrders = repository.getOrders().filter { it.status == OrderStatus.CANCELLED }
                if (cancelledOrders.isEmpty()) {
                    TaskEighteenLogger.logAllCancelledOrdersDeleted(context)
                    TaskEighteenLogger.logTaskCompleted(context)
                }
            } else {
                _uiState.value = _uiState.value.copy(toastMessage = "删除订单失败")
            }
            clearDeleteDialog()
        }
    }
    
    fun initializeWithTab(orderType: String) {
        val tabIndex = when (orderType) {
            "all" -> 0
            "order_pending_payment" -> 1
            "order_pending_receipt" -> 2 // 待收货
            "order_pending_use" -> 3 // 待使用
            "order_pending_review" -> 4
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
            allOrders = repository.getOrders() // Load all orders
            filterOrdersByTab(_uiState.value.selectedTabIndex)
            
            // 任务四日志记录：订单列表加载完成
            repository.logTaskFourOrderListLoaded(allOrders.size)
            
            // 如果订单列表已加载，记录任务完成
            if (allOrders.isNotEmpty()) {
                repository.logTaskFourCompleted(allOrders.size)
            }
            
            // 任务六日志记录：如果当前显示待付款订单且找到第一个待付款订单
            if (_uiState.value.selectedTabIndex == 1 && allOrders.isNotEmpty()) {
                val firstPendingOrder = allOrders.firstOrNull { it.status == OrderStatus.PENDING_PAYMENT }
                firstPendingOrder?.let { order ->
                    TaskSixLogger.logOrderFound(context, order.id)
                }
            }
            
            // 任务十日志记录：如果当前显示待收货订单
            if (_uiState.value.selectedTabIndex == 2) {
                val pendingReceiptOrders = allOrders.filter { it.status == OrderStatus.PENDING_RECEIPT }
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
    }
    
    private fun filterOrdersByTab(tabIndex: Int) {
        val filteredOrders = when (tabIndex) {
            0 -> allOrders // 全部
            1 -> allOrders.filter { it.status == OrderStatus.PENDING_PAYMENT } // 待付款
            2 -> allOrders.filter { it.status == OrderStatus.PENDING_RECEIPT } // 待收货
            3 -> allOrders.filter { it.status == OrderStatus.PENDING_SHIPMENT } // 待使用
            4 -> allOrders.filter { it.status == OrderStatus.PENDING_REVIEW } // 待评价
            else -> allOrders
        }
        _uiState.value = _uiState.value.copy(
            orders = filteredOrders,
            isLoading = false
        )
    }
    
    fun getTabDisplayName(index: Int): String {
        return OrderTab.values().getOrNull(index)?.displayName ?: "全部"
    }
}

enum class OrderTab(val displayName: String) {
    ALL("全部"),
    PENDING_PAYMENT("待付款"),
    PENDING_RECEIPT("待收货"),
    PENDING_SHIPMENT("待使用"),
    PENDING_REVIEW("待评价")
}