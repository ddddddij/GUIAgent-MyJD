package com.example.MyJD.model

data class ShopPageData(
    val shopInfo: ShopInfo,
    val statistics: List<ShopStatistic>,
    val categories: List<ShopCategory>,
    val products: List<Product>
)

data class ShopInfo(
    val id: String,
    val name: String,
    val avatar: String,
    val followers: String,
    val isFollowed: Boolean,
    val serviceBanner: String,
    val description: String? = null
)

data class ShopStatistic(
    val label: String,
    val value: String,
    val icon: String? = null
)

data class ShopCategory(
    val id: String,
    val name: String,
    val isSelected: Boolean = false
)

enum class ShopCategoryType {
    RECOMMEND,      // 推荐
    SALES,          // 销量
    NEW_PRODUCTS,   // 新品
    PRICE,          // 价格
    IN_STOCK_ONLY   // 仅看有货
}