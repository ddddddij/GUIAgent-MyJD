package com.example.MyJD.model

enum class AppPage {
    HOME,               // 首页
    CATEGORY,           // 分类
    SHOPPING_CART,      // 购物车
    PROFILE,            // 我的
    MESSAGES,           // 消息
    PRODUCT_DETAIL,     // 商品详情
    PRODUCT_LIST,       // 商品列表
    ORDER_LIST,         // 订单列表
    ORDER_DETAIL,       // 订单详情
    CHECKOUT,           // 结算页
    ADDRESS_MANAGEMENT, // 地址管理
    STORE_PAGE,         // 店铺页面
    REFUND_LIST,        // 售后列表
    REFUND_DETAIL,      // 售后详情
    SETTINGS,           // 设置
    LOGIN               // 登录
}

data class UserProfile(
    val id: String,
    val username: String,
    val nickname: String,
    val avatar: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val isLoggedIn: Boolean = false,
    val loginTime: Long? = null
)

data class AppState(
    val currentPage: AppPage = AppPage.HOME,
    val user: UserProfile? = null,
    val shoppingCart: ShoppingCart = ShoppingCart(),
    val orders: List<Order> = emptyList(),
    val addresses: List<Address> = emptyList(),
    val refundRecords: List<RefundRecord> = emptyList(),
    val favoriteProducts: List<Product> = emptyList(),
    val followedStores: List<Store> = emptyList(),
    val notifications: List<SystemNotification> = emptyList(),
    val searchHistory: List<String> = emptyList(),
    val currentSearchKeyword: String? = null,
    val currentProductFilter: ProductFilter? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val isLoggedIn: Boolean
        get() = user?.isLoggedIn == true
    
    val unreadNotificationCount: Int
        get() = notifications.count { !it.isRead }
    
    val pendingPaymentOrders: List<Order>
        get() = orders.filter { it.status == OrderStatus.PENDING_PAYMENT }
    
    val pendingShipmentOrders: List<Order>
        get() = orders.filter { it.status == OrderStatus.PENDING_SHIPMENT }
    
    val pendingReceiptOrders: List<Order>
        get() = orders.filter { it.status == OrderStatus.PENDING_RECEIPT }
    
    val pendingReviewOrders: List<Order>
        get() = orders.filter { it.status == OrderStatus.PENDING_REVIEW }
    
    val completedOrders: List<Order>
        get() = orders.filter { it.status == OrderStatus.COMPLETED }
}