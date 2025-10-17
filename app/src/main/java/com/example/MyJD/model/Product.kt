package com.example.MyJD.model

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val originalPrice: Double? = null,
    val brand: String,
    val category: String,
    val imageUrl: String,
    val storeId: String,
    val storeName: String,
    val colors: List<String> = emptyList(),
    val versions: List<String> = emptyList(),
    val selectedColor: String? = null,
    val selectedVersion: String? = null,
    val stock: Int = 0,
    val isInStock: Boolean = true,
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val description: String = "",
    val specifications: Map<String, String> = emptyMap()
)

enum class ProductSortType {
    DEFAULT,
    PRICE_LOW_TO_HIGH,
    PRICE_HIGH_TO_LOW,
    SALES_DESC,
    REVIEW_DESC
}

data class ProductFilter(
    val brand: String? = null,
    val priceMin: Double? = null,
    val priceMax: Double? = null,
    val category: String? = null,
    val sortType: ProductSortType = ProductSortType.DEFAULT
)