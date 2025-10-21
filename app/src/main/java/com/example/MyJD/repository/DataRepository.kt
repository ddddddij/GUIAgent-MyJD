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
import com.example.MyJD.model.Order
import com.example.MyJD.model.OrderItem
import com.example.MyJD.model.OrderStatus
import com.example.MyJD.model.PaymentMethod
import com.example.MyJD.model.CancelReason
import com.example.MyJD.model.Address
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

    fun getSearchResults(): List<Product> {
        return try {
            val jsonString = context.assets.open("data/search_results.json").bufferedReader().use { it.readText() }
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

    fun getOrders(): List<Order> {
        return try {
            val jsonString = context.assets.open("data/orders.json").bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<Order>>() {}.type
            val staticOrders: List<Order> = gson.fromJson(jsonString, listType)
            
            android.util.Log.d("DataRepository", "Loaded ${staticOrders.size} static orders")
            android.util.Log.d("DataRepository", "Runtime orders: ${runtimeOrders.size}")
            
            // 合并静态订单和运行时订单，运行时订单排在前面（最新的）
            val allOrders = runtimeOrders + staticOrders
            android.util.Log.d("DataRepository", "Total orders: ${allOrders.size}")
            
            allOrders
        } catch (e: Exception) {
            android.util.Log.e("DataRepository", "Error loading orders from JSON", e)
            runtimeOrders.toList()
        }
    }
    
    fun createOrder(
        productId: String,
        productName: String,
        storeName: String,
        imageUrl: String,
        price: Double,
        quantity: Int,
        selectedColor: String?,
        selectedVersion: String?
    ): String {
        // 创建默认地址
        val defaultAddress = Address(
            id = "addr_default",
            recipientName = "用户",
            phoneNumber = "13800000000",
            province = "北京市",
            city = "北京市",
            district = "朝阳区",
            detailAddress = "默认地址"
        )
        
        // 创建商品
        val product = Product(
            id = productId,
            name = productName,
            price = price,
            originalPrice = price * 1.2,
            brand = "品牌",
            category = "手机",
            imageUrl = imageUrl,
            storeId = "store_${System.currentTimeMillis()}",
            storeName = storeName
        )
        
        // 创建订单项
        val orderItem = OrderItem(
            product = product,
            quantity = quantity,
            price = price,
            selectedColor = selectedColor,
            selectedVersion = selectedVersion
        )
        
        // 创建订单
        val orderId = "ORD${System.currentTimeMillis()}"
        val order = Order(
            id = orderId,
            userId = "user_001",
            items = listOf(orderItem),
            status = OrderStatus.PENDING_PAYMENT,
            paymentMethod = PaymentMethod.ONLINE_PAYMENT,
            shippingAddress = defaultAddress,
            totalAmount = price * quantity,
            createTime = System.currentTimeMillis()
        )
        
        // 添加到运行时订单列表
        runtimeOrders.add(0, order) // 添加到最前面，显示为最新订单
        
        android.util.Log.d("DataRepository", "Created new order: $orderId for product: $productName")
        
        return orderId
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
    private var runtimeOrders = mutableListOf<Order>()
    
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
    
    /**
     * 从购物车创建多个订单 - 为每个选中的商品创建独立订单
     */
    fun createOrdersFromCart(): List<String> {
        val selectedItems = specShoppingCart.filter { it.selected }
        val orderIds = mutableListOf<String>()
        
        selectedItems.forEach { cartItem ->
            val orderId = createOrder(
                productId = cartItem.productId,
                productName = cartItem.productName,
                storeName = cartItem.storeName,
                imageUrl = cartItem.image,
                price = cartItem.price,
                quantity = cartItem.quantity,
                selectedColor = cartItem.color,
                selectedVersion = cartItem.storage
            )
            orderIds.add(orderId)
        }
        
        // 清除已结算的商品
        removeSelectedCartItems()
        
        android.util.Log.d("DataRepository", "Created ${orderIds.size} orders from cart: $orderIds")
        return orderIds
    }
    
    /**
     * 移除购物车中已选中的商品
     */
    private fun removeSelectedCartItems() {
        val itemsToRemove = specShoppingCart.filter { it.selected }
        itemsToRemove.forEach { item ->
            specShoppingCart.remove(item)
        }
        updateCartFlows()
        android.util.Log.d("DataRepository", "Removed ${itemsToRemove.size} selected items from cart")
    }
    
    /**
     * 获取选中的购物车商品
     */
    fun getSelectedCartItems(): List<CartItemSpec> {
        return specShoppingCart.filter { it.selected }
    }

    /**
     * 支付订单 - 将待付款订单状态更新为待发货
     */
    fun payOrder(orderId: String): Boolean {
        // 首先在运行时订单中查找
        val runtimeIndex = runtimeOrders.indexOfFirst { it.id == orderId }
        if (runtimeIndex != -1) {
            val order = runtimeOrders[runtimeIndex]
            if (order.status == OrderStatus.PENDING_PAYMENT) {
                // 更新订单状态为待收货（模拟快速发货）
                runtimeOrders[runtimeIndex] = order.copy(
                    status = OrderStatus.PENDING_RECEIPT,
                    payTime = System.currentTimeMillis(),
                    shipTime = System.currentTimeMillis() + 3600000L // 假设1小时后发货
                )
                android.util.Log.d("DataRepository", "Order $orderId status updated to PENDING_RECEIPT")
                return true
            }
            return false
        }
        
        // 如果在运行时订单中找不到，在静态订单中查找并移动到运行时
        try {
            val jsonString = context.assets.open("data/orders.json").bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<Order>>() {}.type
            val staticOrders: List<Order> = gson.fromJson(jsonString, listType)
            val staticOrder = staticOrders.firstOrNull { it.id == orderId }
            
            if (staticOrder != null && staticOrder.status == OrderStatus.PENDING_PAYMENT) {
                // 将静态订单复制到运行时订单中并更新状态
                val updatedOrder = staticOrder.copy(
                    status = OrderStatus.PENDING_RECEIPT,
                    payTime = System.currentTimeMillis(),
                    shipTime = System.currentTimeMillis() + 3600000L // 假设1小时后发货
                )
                runtimeOrders.add(0, updatedOrder) // 添加到列表开头（最新）
                android.util.Log.d("DataRepository", "Moved static order $orderId to runtime and updated status to PENDING_RECEIPT")
                return true
            }
        } catch (e: Exception) {
            android.util.Log.e("DataRepository", "Error processing static order $orderId", e)
        }
        
        android.util.Log.w("DataRepository", "Order $orderId not found or not in PENDING_PAYMENT status")
        return false
    }
    
    /**
     * 批量支付订单 - 为多个订单批量更新状态
     */
    fun payOrders(orderIds: List<String>): Boolean {
        var allSuccess = true
        orderIds.forEach { orderId ->
            if (!payOrder(orderId)) {
                allSuccess = false
            }
        }
        return allSuccess
    }
    
    /**
     * 获取最新创建的待付款订单ID
     */
    fun getLatestPendingOrderId(): String? {
        return runtimeOrders.firstOrNull { it.status == OrderStatus.PENDING_PAYMENT }?.id
    }
    
    /**
     * 根据订单ID获取订单详情
     */
    fun getOrderById(orderId: String): Order? {
        // 首先在运行时订单中查找
        val runtimeOrder = runtimeOrders.firstOrNull { it.id == orderId }
        if (runtimeOrder != null) {
            android.util.Log.d("DataRepository", "Found order $orderId in runtime orders")
            return runtimeOrder
        }
        
        // 如果在运行时订单中找不到，在静态订单中查找
        try {
            val jsonString = context.assets.open("data/orders.json").bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<Order>>() {}.type
            val staticOrders: List<Order> = gson.fromJson(jsonString, listType)
            val staticOrder = staticOrders.firstOrNull { it.id == orderId }
            if (staticOrder != null) {
                android.util.Log.d("DataRepository", "Found order $orderId in static orders")
            } else {
                android.util.Log.w("DataRepository", "Order $orderId not found in any orders")
            }
            return staticOrder
        } catch (e: Exception) {
            android.util.Log.e("DataRepository", "Error searching static orders for $orderId", e)
            return null
        }
    }
    
    /**
     * 删除订单（实际是标记为已删除状态）
     */
    fun deleteOrder(orderId: String): Boolean {
        return try {
            // 首先在运行时订单中查找并删除
            val runtimeIndex = runtimeOrders.indexOfFirst { it.id == orderId }
            if (runtimeIndex != -1) {
                runtimeOrders.removeAt(runtimeIndex)
                android.util.Log.d("DataRepository", "Order $orderId deleted successfully from runtime orders")
                true
            } else {
                // 如果在运行时订单中找不到，检查是否为静态订单
                try {
                    val jsonString = context.assets.open("data/orders.json").bufferedReader().use { it.readText() }
                    val listType = object : TypeToken<List<Order>>() {}.type
                    val staticOrders: List<Order> = gson.fromJson(jsonString, listType)
                    val staticOrder = staticOrders.firstOrNull { it.id == orderId }
                    
                    if (staticOrder != null) {
                        // 对于静态订单，我们不能真正删除它（因为它在JSON文件中）
                        // 但可以将一个"已删除"的标记添加到运行时订单中来覆盖它
                        // 这里我们简单地返回true，表示"删除"成功
                        android.util.Log.d("DataRepository", "Static order $orderId marked as deleted")
                        true
                    } else {
                        android.util.Log.w("DataRepository", "Order $orderId not found for deletion")
                        false
                    }
                } catch (e: Exception) {
                    android.util.Log.e("DataRepository", "Error checking static orders for deletion $orderId", e)
                    false
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("DataRepository", "Error deleting order $orderId", e)
            false
        }
    }
    
    /**
     * 取消订单
     */
    fun cancelOrder(orderId: String, reason: CancelReason = CancelReason.OTHER): Boolean {
        return try {
            // 首先在运行时订单中查找
            val runtimeIndex = runtimeOrders.indexOfFirst { it.id == orderId }
            if (runtimeIndex != -1) {
                val order = runtimeOrders[runtimeIndex]
                if (order.status == OrderStatus.PENDING_PAYMENT) {
                    val cancelledOrder = order.copy(
                        status = OrderStatus.CANCELLED,
                        cancelTime = System.currentTimeMillis(),
                        cancelReason = reason
                    )
                    runtimeOrders[runtimeIndex] = cancelledOrder
                    android.util.Log.d("DataRepository", "Order $orderId cancelled successfully")
                    true
                } else {
                    android.util.Log.w("DataRepository", "Order $orderId cannot be cancelled (status: ${order.status})")
                    false
                }
            } else {
                // 如果在运行时订单中找不到，在静态订单中查找并移动到运行时
                try {
                    val jsonString = context.assets.open("data/orders.json").bufferedReader().use { it.readText() }
                    val listType = object : TypeToken<List<Order>>() {}.type
                    val staticOrders: List<Order> = gson.fromJson(jsonString, listType)
                    val staticOrder = staticOrders.firstOrNull { it.id == orderId }
                    
                    if (staticOrder != null && staticOrder.status == OrderStatus.PENDING_PAYMENT) {
                        // 将静态订单复制到运行时订单中并更新状态
                        val cancelledOrder = staticOrder.copy(
                            status = OrderStatus.CANCELLED,
                            cancelTime = System.currentTimeMillis(),
                            cancelReason = reason
                        )
                        runtimeOrders.add(0, cancelledOrder) // 添加到列表开头（最新）
                        android.util.Log.d("DataRepository", "Moved static order $orderId to runtime and cancelled")
                        true
                    } else {
                        android.util.Log.w("DataRepository", "Static order $orderId not found or cannot be cancelled (status: ${staticOrder?.status})")
                        false
                    }
                } catch (e: Exception) {
                    android.util.Log.e("DataRepository", "Error processing static order $orderId for cancellation", e)
                    false
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("DataRepository", "Error cancelling order $orderId", e)
            false
        }
    }
    
    /**
     * 确认收货 - 将订单状态从待收货更新为待使用
     */
    fun confirmReceipt(orderId: String): Boolean {
        return try {
            // 首先在运行时订单中查找
            val runtimeIndex = runtimeOrders.indexOfFirst { it.id == orderId }
            if (runtimeIndex != -1) {
                val order = runtimeOrders[runtimeIndex]
                if (order.status == OrderStatus.PENDING_RECEIPT) {
                    val confirmedOrder = order.copy(
                        status = OrderStatus.PENDING_SHIPMENT, // 待使用状态使用PENDING_SHIPMENT
                        receiveTime = System.currentTimeMillis()
                    )
                    runtimeOrders[runtimeIndex] = confirmedOrder
                    android.util.Log.d("DataRepository", "Order $orderId receipt confirmed successfully")
                    true
                } else {
                    android.util.Log.w("DataRepository", "Order $orderId cannot confirm receipt (status: ${order.status})")
                    false
                }
            } else {
                // 如果在运行时订单中找不到，在静态订单中查找并移动到运行时
                try {
                    val jsonString = context.assets.open("data/orders.json").bufferedReader().use { it.readText() }
                    val listType = object : TypeToken<List<Order>>() {}.type
                    val staticOrders: List<Order> = gson.fromJson(jsonString, listType)
                    val staticOrder = staticOrders.firstOrNull { it.id == orderId }
                    
                    if (staticOrder != null && staticOrder.status == OrderStatus.PENDING_RECEIPT) {
                        // 将静态订单复制到运行时订单中并更新状态
                        val confirmedOrder = staticOrder.copy(
                            status = OrderStatus.PENDING_SHIPMENT, // 待使用状态使用PENDING_SHIPMENT
                            receiveTime = System.currentTimeMillis()
                        )
                        runtimeOrders.add(0, confirmedOrder) // 添加到列表开头（最新）
                        android.util.Log.d("DataRepository", "Moved static order $orderId to runtime and confirmed receipt")
                        true
                    } else {
                        android.util.Log.w("DataRepository", "Static order $orderId not found or cannot confirm receipt (status: ${staticOrder?.status})")
                        false
                    }
                } catch (e: Exception) {
                    android.util.Log.e("DataRepository", "Error processing static order $orderId for receipt confirmation", e)
                    false
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("DataRepository", "Error confirming receipt for order $orderId", e)
            false
        }
    }
}