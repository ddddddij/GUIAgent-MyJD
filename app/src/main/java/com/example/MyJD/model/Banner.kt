package com.example.MyJD.model

data class Banner(
    val id: String,
    val title: String,
    val subtitle: String,
    val imageUrl: String,
    val actionUrl: String,
    val backgroundColor: String,
    val textColor: String,
    val isActive: Boolean,
    val sortOrder: Int,
    val startTime: Long,
    val endTime: Long,
    val type: String
)