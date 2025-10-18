package com.example.MyJD.repository

import android.content.Context
import com.example.MyJD.model.Banner
import com.example.MyJD.model.Product
import com.example.MyJD.model.CartItem
import com.example.MyJD.model.ShoppingCart
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataRepository(private val context: Context) {
    private val gson = Gson()

    suspend fun loadProducts(): List<Product> = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.assets.open("data/products.json").bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<Product>>() {}.type
            gson.fromJson(jsonString, listType)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun loadBanners(): List<Banner> = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.assets.open("data/banners.json").bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<Banner>>() {}.type
            gson.fromJson(jsonString, listType)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private var shoppingCart = ShoppingCart()

    fun getShoppingCart(): ShoppingCart {
        return shoppingCart
    }

    fun addToCart(product: Product, quantity: Int = 1) {
        val existingItem = shoppingCart.items.find { it.product.id == product.id }
        val updatedItems = if (existingItem != null) {
            shoppingCart.items.map { item ->
                if (item.product.id == product.id) {
                    item.copy(quantity = item.quantity + quantity)
                } else {
                    item
                }
            }
        } else {
            shoppingCart.items + CartItem(
                id = "${product.id}_${System.currentTimeMillis()}",
                product = product,
                quantity = quantity
            )
        }
        shoppingCart = ShoppingCart(updatedItems)
    }

    fun removeFromCart(cartItemId: String) {
        val updatedItems = shoppingCart.items.filter { it.id != cartItemId }
        shoppingCart = ShoppingCart(updatedItems)
    }

    fun updateCartItemQuantity(cartItemId: String, quantity: Int) {
        val updatedItems = shoppingCart.items.map { item ->
            if (item.id == cartItemId) {
                item.copy(quantity = quantity)
            } else {
                item
            }
        }
        shoppingCart = ShoppingCart(updatedItems)
    }

    fun toggleCartItemSelection(cartItemId: String) {
        val updatedItems = shoppingCart.items.map { item ->
            if (item.id == cartItemId) {
                item.copy(isSelected = !item.isSelected)
            } else {
                item
            }
        }
        shoppingCart = ShoppingCart(updatedItems)
    }
}