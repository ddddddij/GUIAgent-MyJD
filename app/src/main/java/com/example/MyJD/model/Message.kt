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

// Chat message types for conversation detail
enum class ChatMessageType {
    TEXT,       // 文本消息
    SYSTEM,     // 系统提示
    PRODUCT     // 商品卡片
}

enum class ChatSender {
    USER,       // 用户自己
    OTHER       // 对方（客服/商家）
}

// Individual chat message in a conversation
data class ChatMessage(
    val id: String,
    val sender: ChatSender,
    val type: ChatMessageType,
    val content: String,
    val timestamp: Long
)

// Conversation data structure
data class Conversation(
    val id: String,
    val chatName: String,
    val chatAvatar: String,
    val messages: MutableList<ChatMessage>
)

// Container for all conversation data
data class ConversationData(
    val legacy_messages: List<Message>,
    val conversations: List<Conversation>
)

// Product card data (for product type messages)
data class ProductCard(
    val productId: String,
    val productName: String,
    val productPrice: String,
    val productImage: String
)