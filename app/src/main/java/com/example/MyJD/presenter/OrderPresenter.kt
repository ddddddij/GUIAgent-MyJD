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
        val toastMessage = when (action) {
            "删除订单" -> "订单已删除"
            "再次购买" -> "功能开发中"
            "去支付" -> "功能开发中"
            "取消订单" -> "订单已取消"
            "申请售后" -> "功能开发中"
            "查看物流" -> "功能开发中"
            "确认收货" -> "已确认收货"
            "查看券码" -> "功能开发中"
            "去评价" -> "功能开发中"
            else -> "功能开发中"
        }
        view?.showToast(toastMessage)
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