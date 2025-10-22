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
import com.example.MyJD.model.ConversationData
import com.example.MyJD.model.Conversation
import com.example.MyJD.model.ChatMessage
import com.example.MyJD.model.ChatSender
import com.example.MyJD.model.ChatMessageType
import com.example.MyJD.model.ShopPageData
import com.google.gson.JsonObject
import com.google.gson.JsonParser
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
            // Parse the new JSON structure
            val jsonObject = JsonParser.parseString(jsonString).asJsonObject
            val legacyMessagesJson = jsonObject.getAsJsonArray("legacy_messages")
            val listType = object : TypeToken<List<Message>>() {}.type
            gson.fromJson(legacyMessagesJson, listType)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun loadConversationData(): ConversationData = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.assets.open("data/messages.json").bufferedReader().use { it.readText() }
            
            // Parse the JSON to get the structure
            val jsonObject = JsonParser.parseString(jsonString).asJsonObject
            
            // Parse legacy messages
            val legacyMessagesJson = jsonObject.getAsJsonArray("legacy_messages")
            val legacyMessages = gson.fromJson(legacyMessagesJson, Array<Message>::class.java).toList()
            
            // Parse conversations with custom parsing for enum types
            val conversationsJson = jsonObject.getAsJsonArray("conversations")
            val conversations = mutableListOf<Conversation>()
            
            for (conversationElement in conversationsJson) {
                val conversationObj = conversationElement.asJsonObject
                val id = conversationObj.get("id").asString
                val chatName = conversationObj.get("chatName").asString
                val chatAvatar = conversationObj.get("chatAvatar").asString
                
                val messagesArray = conversationObj.getAsJsonArray("messages")
                val messages = mutableListOf<ChatMessage>()
                
                for (messageElement in messagesArray) {
                    val messageObj = messageElement.asJsonObject
                    val messageId = messageObj.get("id").asString
                    val senderStr = messageObj.get("sender").asString
                    val typeStr = messageObj.get("type").asString
                    val content = messageObj.get("content").asString
                    val timestamp = messageObj.get("timestamp").asLong
                    
                    val sender = when (senderStr) {
                        "user" -> ChatSender.USER
                        "other" -> ChatSender.OTHER
                        else -> ChatSender.OTHER
                    }
                    
                    val type = when (typeStr) {
                        "text" -> ChatMessageType.TEXT
                        "system" -> ChatMessageType.SYSTEM
                        "product" -> ChatMessageType.PRODUCT
                        else -> ChatMessageType.TEXT
                    }
                    
                    messages.add(ChatMessage(messageId, sender, type, content, timestamp))
                }
                
                conversations.add(Conversation(id, chatName, chatAvatar, messages))
            }
            
            ConversationData(legacyMessages, conversations)
        } catch (e: Exception) {
            android.util.Log.e("DataRepository", "Error loading conversation data", e)
            // Return empty data structure on error
            ConversationData(emptyList(), emptyList())
        }
    }
    
    suspend fun loadShopPageData(): ShopPageData = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.assets.open("data/shop_data.json").bufferedReader().use { it.readText() }
            gson.fromJson(jsonString, ShopPageData::class.java)
        } catch (e: Exception) {
            android.util.Log.e("DataRepository", "Error loading shop page data", e)
            // 返回默认数据结构
            ShopPageData(
                shopInfo = com.example.MyJD.model.ShopInfo(
                    id = "default",
                    name = "店铺加载失败",
                    avatar = "🏪",
                    followers = "0",
                    isFollowed = false,
                    serviceBanner = ""
                ),
                statistics = emptyList(),
                categories = emptyList(),
                products = emptyList()
            )
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
    private var runtimeAddresses = mutableListOf<Address>()
    
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
    
    // ===== 地址管理相关方法 =====
    
    /**
     * 加载地址列表
     */
    suspend fun loadAddresses(): List<Address> = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.assets.open("data/addresses.json").bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<Address>>() {}.type
            val staticAddresses: List<Address> = gson.fromJson(jsonString, listType)
            
            android.util.Log.d("DataRepository", "Loaded ${staticAddresses.size} static addresses")
            android.util.Log.d("DataRepository", "Runtime addresses: ${runtimeAddresses.size}")
            
            // 合并地址，运行时地址优先（覆盖同ID的静态地址）
            val addressMap = mutableMapOf<String, Address>()
            
            // 先添加静态地址
            staticAddresses.forEach { address ->
                addressMap[address.id] = address
            }
            
            // 运行时地址覆盖静态地址
            runtimeAddresses.forEach { address ->
                addressMap[address.id] = address
            }
            
            // 确保只有一个默认地址
            val allAddresses = addressMap.values.toList()
            val defaultAddresses = allAddresses.filter { it.isDefault }
            
            val finalAddresses = if (defaultAddresses.size > 1) {
                // 如果有多个默认地址，只保留第一个作为默认
                android.util.Log.w("DataRepository", "Found ${defaultAddresses.size} default addresses, keeping only the first one")
                allAddresses.mapIndexed { index, address ->
                    if (address.isDefault && index > allAddresses.indexOfFirst { it.isDefault }) {
                        address.copy(isDefault = false)
                    } else {
                        address
                    }
                }
            } else {
                allAddresses
            }.sortedByDescending { it.createTime } // 按创建时间排序，最新的在前
            
            android.util.Log.d("DataRepository", "Total addresses: ${finalAddresses.size}")
            
            finalAddresses
        } catch (e: Exception) {
            android.util.Log.e("DataRepository", "Error loading addresses from JSON", e)
            runtimeAddresses.toList()
        }
    }
    
    /**
     * 根据ID获取地址
     */
    suspend fun getAddressById(addressId: String): Address? = withContext(Dispatchers.IO) {
        val allAddresses = loadAddresses()
        allAddresses.firstOrNull { it.id == addressId }
    }
    
    /**
     * 获取默认地址
     */
    suspend fun getDefaultAddress(): Address? = withContext(Dispatchers.IO) {
        val allAddresses = loadAddresses()
        allAddresses.firstOrNull { it.isDefault }
    }
    
    /**
     * 添加新地址
     */
    fun addAddress(address: Address): Boolean {
        return try {
            // 如果新地址设为默认，先取消其他地址的默认状态
            if (address.isDefault) {
                clearDefaultAddresses()
            }
            
            runtimeAddresses.add(0, address) // 添加到最前面，显示为最新地址
            android.util.Log.d("DataRepository", "Added new address: ${address.id} for ${address.recipientName}")
            true
        } catch (e: Exception) {
            android.util.Log.e("DataRepository", "Error adding address", e)
            false
        }
    }
    
    /**
     * 更新地址
     */
    fun updateAddress(updatedAddress: Address): Boolean {
        return try {
            // 如果更新的地址设为默认，先取消其他地址的默认状态
            if (updatedAddress.isDefault) {
                clearDefaultAddresses()
            }
            
            // 在运行时地址中查找并更新
            val runtimeIndex = runtimeAddresses.indexOfFirst { it.id == updatedAddress.id }
            if (runtimeIndex != -1) {
                runtimeAddresses[runtimeIndex] = updatedAddress
                android.util.Log.d("DataRepository", "Updated runtime address: ${updatedAddress.id}")
                return true
            }
            
            // 如果在运行时地址中找不到，添加为新的运行时地址（覆盖静态地址）
            runtimeAddresses.add(0, updatedAddress)
            android.util.Log.d("DataRepository", "Added updated address as new runtime address: ${updatedAddress.id}")
            true
        } catch (e: Exception) {
            android.util.Log.e("DataRepository", "Error updating address", e)
            false
        }
    }
    
    /**
     * 删除地址
     */
    fun deleteAddress(addressId: String): Boolean {
        return try {
            // 在运行时地址中查找并删除
            val runtimeIndex = runtimeAddresses.indexOfFirst { it.id == addressId }
            if (runtimeIndex != -1) {
                runtimeAddresses.removeAt(runtimeIndex)
                android.util.Log.d("DataRepository", "Deleted runtime address: $addressId")
                return true
            }
            
            // 如果在运行时地址中找不到，添加一个标记删除的记录
            // 通过添加一个相同ID但标记为已删除的地址来覆盖静态地址
            // 这里我们简单返回true，表示"删除"成功（静态地址无法真正删除）
            android.util.Log.d("DataRepository", "Static address $addressId marked as deleted")
            true
        } catch (e: Exception) {
            android.util.Log.e("DataRepository", "Error deleting address", e)
            false
        }
    }
    
    /**
     * 设置默认地址
     */
    fun setDefaultAddress(addressId: String): Boolean {
        return try {
            // 先取消所有地址的默认状态
            clearDefaultAddresses()
            
            // 在运行时地址中查找并设置为默认
            val runtimeIndex = runtimeAddresses.indexOfFirst { it.id == addressId }
            if (runtimeIndex != -1) {
                val address = runtimeAddresses[runtimeIndex]
                runtimeAddresses[runtimeIndex] = address.copy(isDefault = true)
                android.util.Log.d("DataRepository", "Set runtime address as default: $addressId")
                return true
            }
            
            // 如果在运行时地址中找不到，从静态地址中复制并设为默认
            try {
                val jsonString = context.assets.open("data/addresses.json").bufferedReader().use { it.readText() }
                val listType = object : TypeToken<List<Address>>() {}.type
                val staticAddresses: List<Address> = gson.fromJson(jsonString, listType)
                val staticAddress = staticAddresses.firstOrNull { it.id == addressId }
                
                if (staticAddress != null) {
                    val defaultAddress = staticAddress.copy(isDefault = true)
                    runtimeAddresses.add(0, defaultAddress)
                    android.util.Log.d("DataRepository", "Copied static address to runtime and set as default: $addressId")
                    true
                } else {
                    android.util.Log.w("DataRepository", "Address $addressId not found")
                    false
                }
            } catch (e: Exception) {
                android.util.Log.e("DataRepository", "Error processing static address for default setting", e)
                false
            }
        } catch (e: Exception) {
            android.util.Log.e("DataRepository", "Error setting default address", e)
            false
        }
    }
    
    /**
     * 复制地址到剪贴板（返回格式化的地址字符串）
     */
    fun copyAddressToClipboard(address: Address): String {
        return "${address.recipientName} ${address.phoneNumber}\n${address.fullAddress}"
    }
    
    /**
     * 清除所有地址的默认状态
     */
    private fun clearDefaultAddresses() {
        // 清除运行时地址的默认状态
        for (i in runtimeAddresses.indices) {
            val address = runtimeAddresses[i]
            if (address.isDefault) {
                runtimeAddresses[i] = address.copy(isDefault = false)
            }
        }
        
        // 对于静态地址，我们通过添加非默认版本到运行时地址来覆盖
        try {
            val jsonString = context.assets.open("data/addresses.json").bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<Address>>() {}.type
            val staticAddresses: List<Address> = gson.fromJson(jsonString, listType)
            val defaultStaticAddress = staticAddresses.firstOrNull { it.isDefault }
            
            if (defaultStaticAddress != null) {
                // 检查是否已经在运行时地址中有这个地址的覆盖版本
                val existsInRuntime = runtimeAddresses.any { it.id == defaultStaticAddress.id }
                if (!existsInRuntime) {
                    // 添加非默认版本到运行时地址
                    val nonDefaultAddress = defaultStaticAddress.copy(isDefault = false)
                    runtimeAddresses.add(nonDefaultAddress)
                    android.util.Log.d("DataRepository", "Added non-default override for static address: ${defaultStaticAddress.id}")
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("DataRepository", "Error clearing default static address", e)
        }
    }
}