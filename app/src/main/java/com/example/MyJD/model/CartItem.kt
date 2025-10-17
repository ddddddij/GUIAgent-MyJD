package com.example.MyJD.model

data class CartItem(
    val id: String,
    val product: Product,
    val quantity: Int,
    val isSelected: Boolean = false,
    val addedTime: Long = System.currentTimeMillis()
) {
    val totalPrice: Double
        get() = product.price * quantity
}

data class ShoppingCart(
    val items: List<CartItem> = emptyList(),
    val isEmpty: Boolean = items.isEmpty()
) {
    val selectedItems: List<CartItem>
        get() = items.filter { it.isSelected }
    
    val selectedItemsCount: Int
        get() = selectedItems.sumOf { it.quantity }
    
    val selectedItemsTotalPrice: Double
        get() = selectedItems.sumOf { it.totalPrice }
    
    val totalItemsCount: Int
        get() = items.sumOf { it.quantity }
    
    val allItemsTotalPrice: Double
        get() = items.sumOf { it.totalPrice }
}