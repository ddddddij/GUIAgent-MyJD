package com.example.MyJD.presenter

import com.example.MyJD.model.Order

interface OrderContract {
    
    interface View {
        fun showOrders(orders: List<Order>)
        fun showToast(message: String)
        fun updateSelectedTab(tabIndex: Int)
        fun navigateToPayment(orderId: String)
        fun showDeleteConfirmDialog(orderId: String)
    }
    
    interface Presenter {
        fun attach(view: View)
        fun detach()
        fun loadOrders()
        fun onTabSelected(tabIndex: Int)
        fun onActionClicked(action: String, orderId: String)
        fun onDeleteConfirmed(orderId: String)
    }
}

enum class OrderTab(val displayName: String) {
    ALL("全部"),
    PENDING_PAYMENT("待付款"),
    PENDING_RECEIPT("待收货"),
    PENDING_USE("待使用"),
    PENDING_REVIEW("待评价")
}