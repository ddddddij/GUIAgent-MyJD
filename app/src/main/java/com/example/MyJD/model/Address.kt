package com.example.MyJD.model

data class Address(
    val id: String,
    val recipientName: String,
    val phoneNumber: String,
    val province: String,
    val city: String,
    val district: String,
    val detailAddress: String,
    val postCode: String? = null,
    val isDefault: Boolean = false,
    val createTime: Long = System.currentTimeMillis()
) {
    val fullAddress: String
        get() = "$province$city$district$detailAddress"
    
    val displayAddress: String
        get() = "$recipientName $phoneNumber\n$fullAddress"
}