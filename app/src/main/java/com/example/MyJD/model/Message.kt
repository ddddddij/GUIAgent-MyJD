package com.example.MyJD.model

enum class MessageType {
    CUSTOMER_SERVICE,    // 客服
    LOGISTICS,          // 物流
    REMINDER,           // 提醒
    PROMOTION,          // 优惠
    REVIEW              // 点评
}

enum class MessageSubType {
    ALL,               // 全部
    SHOPPING,          // 购物
    INSTANT_DELIVERY,  // 秒送
    TAKEAWAY          // 外卖
}

data class Message(
    val id: String,
    val type: MessageType,
    val subType: MessageSubType,
    val senderName: String,
    val senderAvatar: String? = null,
    val content: String,
    val timestamp: Long,
    val isRead: Boolean = false,
    val isOfficial: Boolean = false,
    val hasUnreadDot: Boolean = false
)