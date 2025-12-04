package com.example.myjd.model

data class ProductSpec(
    val productId: String,
    val defaultSeries: String,
    val defaultColor: String,
    val defaultStorage: String,
    val series: List<SeriesOption>,
    val colors: List<ColorOption>,
    val storage: List<StorageOption>,
    val promotionInfo: PromotionInfo
)

data class SeriesOption(
    val name: String,
    val available: Boolean,
    val selected: Boolean = false
)

data class ColorOption(
    val name: String,
    val colorCode: String,
    val image: String,
    val price: Double,
    val originalPrice: Double,
    val available: Boolean,
    val stockTag: String,
    val selected: Boolean = false
)

data class StorageOption(
    val capacity: String,
    val available: Boolean,
    val selected: Boolean = false,
    val priceIncrease: Double = 0.0
)

data class PromotionInfo(
    val subsidyText: String,
    val subsidyAmount: Int,
    val tags: List<String>
)