package com.example.MyJD.model

data class Store(
    val id: String,
    val name: String,
    val logo: String,
    val description: String,
    val rating: Float = 0f,
    val followerCount: Int = 0,
    val isFollowed: Boolean = false,
    val products: List<Product> = emptyList(),
    val establishedTime: Long,
    val location: String? = null,
    val serviceScore: Float = 0f,
    val logisticsScore: Float = 0f,
    val qualityScore: Float = 0f
)