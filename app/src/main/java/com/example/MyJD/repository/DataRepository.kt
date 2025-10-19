package com.example.MyJD.repository

import android.content.Context
import com.example.MyJD.model.Banner
import com.example.MyJD.model.Product
import com.example.MyJD.model.CartItem
import com.example.MyJD.model.ShoppingCart
import com.example.MyJD.model.Message
import com.example.MyJD.model.MeTabData
import com.example.MyJD.model.ProductDetail
import com.example.MyJD.model.ProductSpec
import com.example.MyJD.model.CartItemSpec
import com.google.gson.JsonObject
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DataRepository private constructor(private val context: Context) {
    private val gson = Gson()
    
    companion object {
        @Volatile
        private var INSTANCE: DataRepository? = null
        
        fun getInstance(context: Context): DataRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DataRepository(context.applicationContext).also { 
                    INSTANCE = it
                    android.util.Log.d("DataRepository", "Creating singleton instance")
                }
            }
        }
    }

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

    suspend fun loadMessages(): List<Message> = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.assets.open("data/messages.json").bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<Message>>() {}.type
            gson.fromJson(jsonString, listType)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun loadMeTabData(): MeTabData = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.assets.open("data/me_tab.json").bufferedReader().use { it.readText() }
            gson.fromJson(jsonString, MeTabData::class.java)
        } catch (e: Exception) {
            MeTabData(
                memberBenefits = emptyList(),
                promoBanners = emptyList(),
                orderStatuses = emptyList(),
                assetItems = emptyList(),
                serviceItems = emptyList(),
                interactionItems = emptyList(),
                quickActions = emptyList(),
                userStats = com.example.MyJD.model.UserStats(0, 0, 0, 0)
            )
        }
    }

    suspend fun loadUserProfile(): JsonObject = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.assets.open("data/user_profile.json").bufferedReader().use { it.readText() }
            gson.fromJson(jsonString, JsonObject::class.java)
        } catch (e: Exception) {
            JsonObject()
        }
    }

    suspend fun loadProductDetail(productId: String): ProductDetail = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.assets.open("data/product_detail.json").bufferedReader().use { it.readText() }
            gson.fromJson(jsonString, ProductDetail::class.java)
        } catch (e: Exception) {
            // Return a default ProductDetail if loading fails
            ProductDetail(
                id = productId,
                title = "商品信息加载失败",
                images = listOf("📱"),
                currentPrice = 0.0,
                originalPrice = 0.0,
                subsidyPrice = "",
                soldCount = "",
                colors = emptyList(),
                specifications = com.example.MyJD.model.ProductSpecifications("", "", "", ""),
                tags = emptyList(),
                deliveryInfo = com.example.MyJD.model.DeliveryInfo("", "", "", "", "", ""),
                tradeIn = com.example.MyJD.model.TradeInInfo("", 0.0, ""),
                stores = emptyList(),
                reviews = com.example.MyJD.model.ReviewInfo("", "", emptyList(), emptyList())
            )
        }
    }

    suspend fun loadProductSpec(productId: String): ProductSpec = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.assets.open("data/product_specs.json").bufferedReader().use { it.readText() }
            gson.fromJson(jsonString, ProductSpec::class.java)
        } catch (e: Exception) {
            // Return default spec if loading fails
            ProductSpec(
                productId = productId,
                defaultSeries = "iPhone 15",
                defaultColor = "蓝色",
                defaultStorage = "128GB",
                series = emptyList(),
                colors = emptyList(),
                storage = emptyList(),
                promotionInfo = com.example.MyJD.model.PromotionInfo("", 0, emptyList())
            )
        }
    }

    private var shoppingCart = ShoppingCart()
    private var specShoppingCart = mutableListOf<CartItemSpec>()
    
    // StateFlow for reactive cart updates
    private val _specCartFlow = MutableStateFlow<List<CartItemSpec>>(emptyList())
    val specCartFlow: StateFlow<List<CartItemSpec>> = _specCartFlow.asStateFlow()
    
    private val _cartCountFlow = MutableStateFlow(0)
    val cartCountFlow: StateFlow<Int> = _cartCountFlow.asStateFlow()

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

    // 规格购物车相关方法
    fun getSpecShoppingCart(): List<CartItemSpec> {
        return specShoppingCart.toList()
    }

    fun addToSpecCart(cartItemSpec: CartItemSpec) {
        android.util.Log.d("DataRepository", "Adding to cart: $cartItemSpec")
        android.util.Log.d("DataRepository", "Current cart size: ${specShoppingCart.size}")
        
        val existingItemIndex = specShoppingCart.indexOfFirst { 
            it.productId == cartItemSpec.productId && 
            it.series == cartItemSpec.series && 
            it.color == cartItemSpec.color && 
            it.storage == cartItemSpec.storage 
        }
        
        if (existingItemIndex != -1) {
            // 相同规格商品，数量累加
            val existingItem = specShoppingCart[existingItemIndex]
            specShoppingCart[existingItemIndex] = existingItem.copy(
                quantity = existingItem.quantity + cartItemSpec.quantity
            )
            android.util.Log.d("DataRepository", "Updated existing item quantity")
        } else {
            // 新商品，直接添加
            specShoppingCart.add(cartItemSpec)
            android.util.Log.d("DataRepository", "Added new item to cart")
        }
        
        // 更新StateFlow
        updateCartFlows()
        
        android.util.Log.d("DataRepository", "New cart size: ${specShoppingCart.size}")
        android.util.Log.d("DataRepository", "Cart items: ${specShoppingCart.map { "${it.productName} - ${it.quantity}" }}")
    }

    fun removeFromSpecCart(cartItemId: String) {
        specShoppingCart.removeAll { it.id == cartItemId }
        updateCartFlows()
    }

    fun updateSpecCartItemQuantity(cartItemId: String, quantity: Int) {
        val itemIndex = specShoppingCart.indexOfFirst { it.id == cartItemId }
        if (itemIndex != -1) {
            specShoppingCart[itemIndex] = specShoppingCart[itemIndex].copy(quantity = quantity)
            updateCartFlows()
        }
    }

    fun toggleSpecCartItemSelection(cartItemId: String) {
        val itemIndex = specShoppingCart.indexOfFirst { it.id == cartItemId }
        if (itemIndex != -1) {
            val item = specShoppingCart[itemIndex]
            specShoppingCart[itemIndex] = item.copy(selected = !item.selected)
            updateCartFlows()
        }
    }

    fun getSpecCartTotalCount(): Int {
        return specShoppingCart.sumOf { it.quantity }
    }

    fun getSelectedSpecCartTotalPrice(): Double {
        return specShoppingCart.filter { it.selected }.sumOf { it.totalPrice }
    }

    fun getSelectedSpecCartCount(): Int {
        return specShoppingCart.filter { it.selected }.sumOf { it.quantity }
    }
    
    private fun updateCartFlows() {
        _specCartFlow.value = specShoppingCart.toList()
        _cartCountFlow.value = getSpecCartTotalCount()
        android.util.Log.d("DataRepository", "StateFlows updated - Cart items: ${_specCartFlow.value.size}, Total count: ${_cartCountFlow.value}")
    }
}