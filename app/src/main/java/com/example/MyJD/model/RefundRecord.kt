package com.example.MyJD.model

enum class RefundServiceType {
    REFUND_ONLY,        // 仅退款
    RETURN_AND_REFUND,  // 退货退款
    EXCHANGE            // 换货
}

enum class RefundStatus {
    PENDING,            // 待处理
    APPROVED,           // 已同意
    REJECTED,           // 已拒绝
    PROCESSING,         // 处理中
    COMPLETED,          // 已完成
    CANCELLED           // 已取消
}

enum class RefundReason {
    QUALITY_ISSUE,      // 质量问题
    SIZE_NOT_FIT,       // 尺寸不合适
    DESCRIPTION_MISMATCH, // 与描述不符
    RECEIVED_WRONG_ITEM, // 收到商品不对
    NOT_SATISFIED,      // 不喜欢/不满意
    OTHER               // 其他
}

data class RefundRecord(
    val id: String,
    val orderId: String,
    val orderItemId: String,
    val serviceType: RefundServiceType,
    val status: RefundStatus,
    val reason: RefundReason,
    val description: String,
    val refundAmount: Double,
    val applyTime: Long,
    val processTime: Long? = null,
    val completeTime: Long? = null,
    val images: List<String> = emptyList(),
    val adminReply: String? = null
)