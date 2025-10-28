package com.example.MyJD.presenter

import com.example.MyJD.model.Order
import com.example.MyJD.model.OrderStatus
import com.example.MyJD.repository.DataRepository

class OrderPresenter(
    private val repository: DataRepository
) : OrderContract.Presenter {
    
    private var view: OrderContract.View? = null
    private var allOrders: List<Order> = emptyList()
    private var currentTabIndex: Int = 0
    
    override fun attach(view: OrderContract.View) {
        this.view = view
    }
    
    override fun detach() {
        this.view = null
    }
    
    override fun loadOrders() {
        allOrders = repository.getOrders()
        filterOrdersByTab(currentTabIndex)
    }
    
    override fun onTabSelected(tabIndex: Int) {
        currentTabIndex = tabIndex
        view?.updateSelectedTab(tabIndex)
        filterOrdersByTab(tabIndex)
    }
    
    override fun onActionClicked(action: String, orderId: String) {
        when (action) {
            "删除订单" -> {
                view?.showDeleteConfirmDialog(orderId)
            }
            "去支付" -> {
                android.util.Log.d("OrderPresenter", "Attempting to pay order: $orderId")
                val success = repository.payOrder(orderId)
                if (success) {
                    android.util.Log.d("OrderPresenter", "Order $orderId paid successfully")
                    view?.showToast("支付成功")
                    loadOrders() // 刷新订单列表以移除已支付订单
                } else {
                    android.util.Log.w("OrderPresenter", "Failed to pay order: $orderId")
                    view?.showToast("支付失败")
                }
            }
            "取消订单" -> {
                android.util.Log.d("OrderPresenter", "Attempting to cancel order: $orderId")
                val success = repository.cancelOrder(orderId)
                if (success) {
                    android.util.Log.d("OrderPresenter", "Order $orderId cancelled successfully")
                    view?.showToast("订单已取消")
                    loadOrders() // 刷新订单列表
                } else {
                    android.util.Log.w("OrderPresenter", "Failed to cancel order: $orderId")
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
                    view?.showToast("取消订单失败，当前状态：$statusText（只有待付款订单可以取消）")
                }
            }
            "确认收货" -> {
                android.util.Log.d("OrderPresenter", "Attempting to confirm receipt for order: $orderId")
                val success = repository.confirmReceipt(orderId)
                if (success) {
                    android.util.Log.d("OrderPresenter", "Order $orderId receipt confirmed successfully")
                    view?.showToast("已确认收货")
                    loadOrders() // 刷新订单列表
                } else {
                    android.util.Log.w("OrderPresenter", "Failed to confirm receipt for order: $orderId")
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
                    view?.showToast("确认收货失败，当前状态：$statusText（只有待收货订单可以确认收货）")
                }
            }
            "再次购买" -> view?.showToast("功能开发中")
            "申请售后" -> view?.showToast("功能开发中")
            "查看物流" -> view?.showToast("功能开发中")
            "查看券码" -> view?.showToast("功能开发中")
            "去评价" -> view?.showToast("功能开发中")
            else -> view?.showToast("功能开发中")
        }
    }
    
    override fun onDeleteConfirmed(orderId: String) {
        val success = repository.deleteOrder(orderId)
        if (success) {
            view?.showToast("订单已删除")
            loadOrders() // 刷新订单列表
        } else {
            view?.showToast("删除订单失败")
        }
    }
    
    private fun filterOrdersByTab(tabIndex: Int) {
        val filteredOrders = when (tabIndex) {
            0 -> allOrders // 全部
            1 -> allOrders.filter { it.status == OrderStatus.PENDING_PAYMENT } // 待付款
            2 -> allOrders.filter { it.status == OrderStatus.PENDING_RECEIPT } // 待收货
            3 -> allOrders.filter { it.status == OrderStatus.PENDING_SHIPMENT } // 待使用 (待发货)
            4 -> allOrders.filter { it.status == OrderStatus.PENDING_REVIEW } // 待评价
            else -> allOrders
        }
        view?.showOrders(filteredOrders)
    }
}