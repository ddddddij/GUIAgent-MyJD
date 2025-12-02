package com.example.MyJD.model

data class SpecSelection(
    val productId: String,
    val selectedSeries: String,
    val selectedColor: String,
    val selectedStorage: String,
    val quantity: Int = 1,
    val currentPrice: Double = 0.0,
    val originalPrice: Double = 0.0,
    val currentImage: String = "",
    val stockAvailable: Boolean = true
) {
    fun isValid(): Boolean {
        return selectedSeries.isNotEmpty() && 
               selectedColor.isNotEmpty() && 
               selectedStorage.isNotEmpty() && 
               quantity >= 1 && 
               stockAvailable
    }
    
    fun getTotalPrice(): Double {
        return currentPrice * quantity
    }
}

data class CartItemSpec(
    val id: String,
    val productId: String,
    val productName: String,
    val series: String,
    val color: String,
    val storage: String,
    val image: String,
    val price: Double,
    val originalPrice: Double,
    val quantity: Int,
    val selected: Boolean = true,
    val promotionTags: List<String> = emptyList(),
    val subsidyInfo: String = "",
    val storeName: String = "",
    val storeTag: String = "自营"
) {
    val totalPrice: Double
        get() = price * quantity
        
    fun getSpecText(): String {
        val specs = mutableListOf<String>()
        if (color.isNotEmpty()) specs.add(color)
        if (storage.isNotEmpty()) specs.add(storage)
        return specs.joinToString("，")
    }
}