package com.example.MyJD.model

enum class NotificationType {
    SYSTEM,             // 系统通知
    ORDER,              // 订单相关
    PROMOTION,          // 促销活动
    DELIVERY,           // 物流消息
    REFUND,             // 退款消息
    REVIEW,             // 评价提醒
    GENERAL             // 一般消息
}

data class SystemNotification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val content: String,
    val imageUrl: String? = null,
    val isRead: Boolean = false,
    val timestamp: Long,
    val relatedOrderId: String? = null,
    val relatedProductId: String? = null,
    val actionUrl: String? = null
)