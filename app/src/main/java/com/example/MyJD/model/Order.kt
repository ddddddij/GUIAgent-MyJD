package com.example.MyJD.model

enum class OrderStatus {
    PENDING_PAYMENT,    // 待付款
    PENDING_SHIPMENT,   // 待发货
    PENDING_RECEIPT,    // 待收货
    PENDING_REVIEW,     // 待评价
    COMPLETED,          // 已完成
    CANCELLED           // 已取消
}

enum class PaymentMethod {
    ONLINE_PAYMENT,     // 在线支付
    BAITIAO_PAYMENT,    // 白条支付
    COD                 // 货到付款
}

enum class CancelReason {
    WRONG_ORDER,        // 信息填写错误，重新下单
    FOUND_CHEAPER,      // 有更便宜的
    NO_LONGER_NEED,     // 不想要了
    OTHER               // 其他原因
}

data class OrderItem(
    val product: Product,
    val quantity: Int,
    val price: Double,
    val selectedColor: String? = null,
    val selectedVersion: String? = null
) {
    val totalPrice: Double
        get() = price * quantity
}

data class Order(
    val id: String,
    val userId: String,
    val items: List<OrderItem>,
    val status: OrderStatus,
    val paymentMethod: PaymentMethod,
    val shippingAddress: Address,
    val totalAmount: Double,
    val createTime: Long,
    val payTime: Long? = null,
    val shipTime: Long? = null,
    val receiveTime: Long? = null,
    val cancelTime: Long? = null,
    val cancelReason: CancelReason? = null,
    val trackingNumber: String? = null,
    val isReviewed: Boolean = false
) {
    val itemCount: Int
        get() = items.sumOf { it.quantity }
}