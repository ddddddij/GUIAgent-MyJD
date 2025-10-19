package com.example.MyJD.model

data class ProductDetail(
    val id: String,
    val title: String,
    val images: List<String>,
    val currentPrice: Double,
    val originalPrice: Double,
    val subsidyPrice: String,
    val soldCount: String,
    val colors: List<ProductColor>,
    val selectedColorIndex: Int = 0,
    val purchaseTypes: List<String> = listOf("单品购买", "以旧换新"),
    val selectedPurchaseType: Int = 0,
    val specifications: ProductSpecifications,
    val tags: List<String>,
    val isFavorite: Boolean = false,
    val deliveryInfo: DeliveryInfo,
    val tradeIn: TradeInInfo,
    val stores: List<StoreInfo>,
    val reviews: ReviewInfo
)

data class ProductColor(
    val name: String,
    val colorCode: String,
    val image: String,
    val subsidyTags: List<String> = emptyList()
)

data class ProductSpecifications(
    val releaseDate: String,
    val processor: String,
    val screenSize: String,
    val displayTech: String
)

data class DeliveryInfo(
    val deliveryTime: String,
    val address: String,
    val shippingFee: String,
    val logistics: String,
    val returnPolicy: String,
    val warranty: String
)

data class TradeInInfo(
    val title: String,
    val maxPrice: Double,
    val applicableModel: String
)

data class StoreInfo(
    val name: String,
    val distance: String,
    val address: String,
    val totalStores: Int
)

data class ReviewInfo(
    val totalCount: String,
    val positiveRate: String,
    val tags: List<ReviewTag>,
    val list: List<ReviewItem>
)

data class ReviewTag(
    val text: String,
    val count: Int,
    val isSelected: Boolean = false
)

data class ReviewItem(
    val username: String,
    val memberLevel: String,
    val content: String,
    val images: List<String>,
    val imageCount: Int,
    val isLiked: Boolean = false
)