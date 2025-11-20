package com.example.MyJD.model

data class Coupon(
    val id: String,
    val name: String,
    val description: String,
    val discountAmount: Double, // 优惠金额
    val minAmount: Double, // 最低消费金额
    val isUsed: Boolean = false,
    val isExpired: Boolean = false,
    val validUntil: String = "2024-12-31"
) {
    /**
     * 检查优惠券是否可用
     */
    fun isAvailable(orderAmount: Double): Boolean {
        return !isUsed && !isExpired && orderAmount >= minAmount
    }
    
    /**
     * 获取优惠券显示文本
     */
    fun getDisplayText(): String {
        return "满${minAmount.toInt()}减${discountAmount.toInt()}"
    }
    
    companion object {
        /**
         * 创建默认的满3000减50优惠券
         */
        fun createDefault(): Coupon {
            return Coupon(
                id = "coupon_3000_50",
                name = "满减券",
                description = "满3000减50",
                discountAmount = 50.0,
                minAmount = 3000.0,
                validUntil = "2024-12-31"
            )
        }
    }
}