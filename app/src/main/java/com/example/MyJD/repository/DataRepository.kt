package com.example.MyJD.repository

import android.content.Context
import com.example.MyJD.model.Banner
import com.example.MyJD.model.Product
import com.example.MyJD.model.CartItem
import com.example.MyJD.model.ShoppingCart
import com.example.MyJD.model.Message
import com.example.MyJD.model.MuteSetting
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
import com.example.MyJD.model.ConversationSummary
import com.example.MyJD.model.Coupon
import com.example.MyJD.model.ChatMessage
import com.example.MyJD.model.ChatSender
import com.example.MyJD.model.ChatMessageType
import com.example.MyJD.model.ShopPageData
import com.example.MyJD.utils.TaskOneLogger
import com.example.MyJD.utils.TaskFourLogger
import com.example.MyJD.utils.TaskSixLogger
import com.example.MyJD.utils.TaskEightLogger
import com.example.MyJD.utils.TaskNineLogger
import com.example.MyJD.utils.TaskTenLogger
import com.example.MyJD.utils.TaskElevenLogger
import com.example.MyJD.utils.TaskFourteenLogger
import com.example.MyJD.utils.TaskSixteenLogger
import com.example.MyJD.utils.TaskSeventeenLogger
import com.example.MyJD.utils.TaskEighteenLogger
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class DataRepository private constructor(private val context: Context) {
    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .create()
    
    // 数据文件路径 - 使用内部存储确保可靠性
    private val dataDir = File(context.filesDir, "persistent_data")
    private val cartItemsFile = File(dataDir, "cart_items.json")
    private val ordersFile = File(dataDir, "orders.json")
    private val addressesFile = File(dataDir, "addresses.json")
    private val newMessagesFile = File(dataDir, "new_messages.json")
    private val muteSettingsFile = File(dataDir, "mute_settings.json")
    
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
    
    // 数据持久化方法
    private fun loadPersistentData() {
        try {
            // 加载购物车数据
            if (cartItemsFile.exists()) {
                val jsonContent = cartItemsFile.readText()
                if (jsonContent.isNotBlank()) {
                    val listType = object : TypeToken<List<CartItemSpec>>() {}.type
                    val loadedCartItems: List<CartItemSpec>? = gson.fromJson(jsonContent, listType)
                    if (loadedCartItems != null && loadedCartItems.isNotEmpty()) {
                        specShoppingCart.clear()
                        specShoppingCart.addAll(loadedCartItems)
                        android.util.Log.d("DataRepository", "Loaded ${specShoppingCart.size} cart items from persistent file")
                    } else {
                        loadInitialCartDataFromAssets()
                    }
                } else {
                    loadInitialCartDataFromAssets()
                }
            } else {
                // 如果没有持久化数据，从assets加载初始数据
                loadInitialCartDataFromAssets()
            }
            
            // 额外检查：如果购物车仍然为空，强制从assets加载
            if (specShoppingCart.isEmpty()) {
                android.util.Log.d("DataRepository", "Cart is empty after initial load, force loading from assets")
                loadInitialCartDataFromAssets()
            }
            
            // 加载订单数据
            if (ordersFile.exists()) {
                val jsonContent = ordersFile.readText()
                if (jsonContent.isNotBlank()) {
                    val listType = object : TypeToken<List<Order>>() {}.type
                    val loadedOrders: List<Order>? = gson.fromJson(jsonContent, listType)
                    if (loadedOrders != null) {
                        runtimeOrders.clear()
                        runtimeOrders.addAll(loadedOrders)
                        android.util.Log.d("DataRepository", "Loaded ${runtimeOrders.size} orders from file")
                    }
                }
            }
            
            // 加载地址数据
            if (addressesFile.exists()) {
                val jsonContent = addressesFile.readText()
                if (jsonContent.isNotBlank()) {
                    val listType = object : TypeToken<List<Address>>() {}.type
                    val loadedAddresses: List<Address>? = gson.fromJson(jsonContent, listType)
                    if (loadedAddresses != null) {
                        runtimeAddresses.clear()
                        runtimeAddresses.addAll(loadedAddresses)
                        android.util.Log.d("DataRepository", "Loaded ${runtimeAddresses.size} addresses from file")
                    }
                }
            }
            
            // 加载新消息数据
            if (newMessagesFile.exists()) {
                val jsonContent = newMessagesFile.readText()
                if (jsonContent.isNotBlank()) {
                    val listType = object : TypeToken<List<ChatMessage>>() {}.type
                    val loadedMessages: List<ChatMessage>? = gson.fromJson(jsonContent, listType)
                    if (loadedMessages != null) {
                        newMessages.clear()
                        newMessages.addAll(loadedMessages)
                        android.util.Log.d("DataRepository", "Loaded ${newMessages.size} new messages from file")
                    }
                }
            }
            
            // 加载免打扰设置数据
            if (muteSettingsFile.exists()) {
                val jsonContent = muteSettingsFile.readText()
                if (jsonContent.isNotBlank()) {
                    val listType = object : TypeToken<List<MuteSetting>>() {}.type
                    val loadedSettings: List<MuteSetting>? = gson.fromJson(jsonContent, listType)
                    if (loadedSettings != null) {
                        muteSettings.clear()
                        muteSettings.addAll(loadedSettings)
                        android.util.Log.d("DataRepository", "Loaded ${muteSettings.size} mute settings from file")
                    }
                }
            }
            
            // 更新购物车计数
            updateCartFlows()
        } catch (e: Exception) {
            android.util.Log.e("DataRepository", "Error loading persistent data", e)
        }
    }
    
    /**
     * 公共方法：强制从assets重新加载购物车数据
     */
    fun forceLoadCartDataFromAssets() {
        loadInitialCartDataFromAssets()
    }
    
    /**
     * 从assets加载初始购物车数据并转换为CartItemSpec格式
     */
    private fun loadInitialCartDataFromAssets() {
        try {
            val jsonString = context.assets.open("data/cart_items.json").bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<CartItem>>() {}.type
            val assetCartItems: List<CartItem> = gson.fromJson(jsonString, listType)
            
            // 转换为CartItemSpec格式
            val convertedItems = assetCartItems.map { cartItem ->
                CartItemSpec(
                    id = cartItem.id,
                    productId = cartItem.product.id,
                    productName = cartItem.product.name,
                    series = cartItem.product.selectedVersion ?: cartItem.product.versions.firstOrNull() ?: "",
                    color = cartItem.product.selectedColor ?: cartItem.product.colors.firstOrNull() ?: "",
                    storage = cartItem.product.selectedVersion ?: cartItem.product.versions.firstOrNull() ?: "",
                    image = cartItem.product.imageUrl,
                    price = cartItem.product.price,
                    originalPrice = cartItem.product.originalPrice ?: cartItem.product.price * 1.2,
                    quantity = cartItem.quantity,
                    selected = cartItem.isSelected,
                    promotionTags = listOf("保价"),
                    subsidyInfo = "政府补贴满1000减100",
                    storeName = cartItem.product.storeName,
                    storeTag = "自营"
                )
            }
            
            specShoppingCart.clear()
            specShoppingCart.addAll(convertedItems)
            
            // 保存到持久化文件中
            saveCartItems()
            
            // 更新StateFlow
            updateCartFlows()
            
            android.util.Log.d("DataRepository", "Loaded and converted ${convertedItems.size} cart items from assets")
        } catch (e: Exception) {
            android.util.Log.e("DataRepository", "Error loading initial cart data from assets", e)
        }
    }
    
    private fun saveCartItems() {
        try {
            val jsonContent = gson.toJson(specShoppingCart)
            cartItemsFile.writeText(jsonContent)
            android.util.Log.d("DataRepository", "Saved ${specShoppingCart.size} cart items to ${cartItemsFile.absolutePath}")
        } catch (e: Exception) {
            android.util.Log.e("DataRepository", "Error saving cart items to ${cartItemsFile.absolutePath}", e)
        }
    }
    
    private fun saveOrders() {
        try {
            val jsonContent = gson.toJson(runtimeOrders)
            ordersFile.writeText(jsonContent)
            android.util.Log.d("DataRepository", "Saved ${runtimeOrders.size} orders to ${ordersFile.absolutePath}")
        } catch (e: Exception) {
            android.util.Log.e("DataRepository", "Error saving orders to ${ordersFile.absolutePath}", e)
        }
    }
    
    private fun saveAddresses() {
        try {
            val jsonContent = gson.toJson(runtimeAddresses)
            addressesFile.writeText(jsonContent)
            android.util.Log.d("DataRepository", "Saved ${runtimeAddresses.size} addresses to ${addressesFile.absolutePath}")
        } catch (e: Exception) {
            android.util.Log.e("DataRepository", "Error saving addresses to ${addressesFile.absolutePath}", e)
        }
    }
    
    private fun saveNewMessages() {
        try {
            val jsonContent = gson.toJson(newMessages)
            newMessagesFile.writeText(jsonContent)
            android.util.Log.d("DataRepository", "Saved ${newMessages.size} new messages to ${newMessagesFile.absolutePath}")
        } catch (e: Exception) {
            android.util.Log.e("DataRepository", "Error saving new messages to ${newMessagesFile.absolutePath}", e)
        }
    }
    
    private fun saveMuteSettings() {
        try {
            val jsonContent = gson.toJson(muteSettings)
            muteSettingsFile.writeText(jsonContent)
            android.util.Log.d("DataRepository", "Saved ${muteSettings.size} mute settings to ${muteSettingsFile.absolutePath}")
        } catch (e: Exception) {
            android.util.Log.e("DataRepository", "Error saving mute settings to ${muteSettingsFile.absolutePath}", e)
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
            
            // 去重合并：运行时订单优先，静态订单中已存在于运行时的订单会被过滤掉
            val runtimeOrderIds = runtimeOrders.map { it.id }.toSet()
            val uniqueStaticOrders = staticOrders.filter { it.id !in runtimeOrderIds }
            val allOrders = runtimeOrders + uniqueStaticOrders
            
            android.util.Log.d("DataRepository", "Total orders after deduplication: ${allOrders.size}")
            
            allOrders
        } catch (e: Exception) {
            android.util.Log.e("DataRepository", "Error loading orders from JSON", e)
            runtimeOrders.toList()
        }
    }
    
    /**
     * 清理运行时订单中可能存在的重复或无效状态
     */
    fun cleanupRuntimeOrders() {
        try {
            val jsonString = context.assets.open("data/orders.json").bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<Order>>() {}.type
            val staticOrders: List<Order> = gson.fromJson(jsonString, listType)
            
            // 移除运行时订单中与静态订单相同ID但状态相同的订单（可能是无效的重复）
            val initialSize = runtimeOrders.size
            runtimeOrders.removeAll { runtimeOrder ->
                val staticOrder = staticOrders.find { it.id == runtimeOrder.id }
                staticOrder != null && staticOrder.status == runtimeOrder.status
            }
            
            if (runtimeOrders.size != initialSize) {
                android.util.Log.d("DataRepository", "Cleaned up ${initialSize - runtimeOrders.size} duplicate runtime orders")
                saveOrders()
            }
        } catch (e: Exception) {
            android.util.Log.e("DataRepository", "Error cleaning up runtime orders", e)
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
        val orderId = generateOrderId()
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
        
        // 保存订单数据
        saveOrders()
        
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
                storeName = "京东自营",
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

    suspend fun getHuaweiP60ProductDetail(productId: String): ProductDetail? = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.assets.open("data/huawei_p60_detail.json").bufferedReader().use { it.readText() }
            gson.fromJson(jsonString, ProductDetail::class.java)
        } catch (e: Exception) {
            android.util.Log.e("DataRepository", "Failed to load Huawei P60 detail", e)
            null
        }
    }

    suspend fun getHuaweiMate60ProductDetail(productId: String): ProductDetail? = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.assets.open("data/huawei_mate60_detail.json").bufferedReader().use { it.readText() }
            gson.fromJson(jsonString, ProductDetail::class.java)
        } catch (e: Exception) {
            android.util.Log.e("DataRepository", "Failed to load Huawei Mate60 detail", e)
            null
        }
    }

    suspend fun getHuaweiNova11ProductDetail(productId: String): ProductDetail? = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.assets.open("data/huawei_nova11_detail.json").bufferedReader().use { it.readText() }
            gson.fromJson(jsonString, ProductDetail::class.java)
        } catch (e: Exception) {
            android.util.Log.e("DataRepository", "Failed to load Huawei Nova11 detail", e)
            null
        }
    }

    suspend fun getThinkPadProductDetail(productId: String): ProductDetail? = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.assets.open("data/thinkpad_detail.json").bufferedReader().use { it.readText() }
            gson.fromJson(jsonString, ProductDetail::class.java)
        } catch (e: Exception) {
            android.util.Log.e("DataRepository", "Failed to load ThinkPad detail", e)
            null
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
    
    // 订单ID计数器
    private var orderCounter = 1
    private var newMessages = mutableListOf<ChatMessage>()
    private var muteSettings = mutableListOf<MuteSetting>()
    
    init {
        // 确保数据目录存在
        try {
            if (!dataDir.exists()) {
                val created = dataDir.mkdirs()
                android.util.Log.d("DataRepository", "Created data directory: $created at ${dataDir.absolutePath}")
            } else {
                android.util.Log.d("DataRepository", "Data directory exists at ${dataDir.absolutePath}")
            }
            // 初始化时加载持久化数据
            loadPersistentData()
            
            // 清理可能存在的重复运行时订单
            cleanupRuntimeOrders()
            
            // 初始化订单计数器
            initializeOrderCounter()
        } catch (e: Exception) {
            android.util.Log.e("DataRepository", "Error initializing data directory", e)
        }
    }
    
    // 优惠券存储
    private var availableCoupons = mutableListOf<Coupon>().apply {
        add(Coupon.createDefault()) // 添加默认的满3000减50优惠券
    }
    
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
        
        // 保存购物车数据
        saveCartItems()
        
        android.util.Log.d("DataRepository", "New cart size: ${specShoppingCart.size}")
        android.util.Log.d("DataRepository", "Cart items: ${specShoppingCart.map { "${it.productName} - ${it.quantity}" }}")
    }

    fun removeFromSpecCart(cartItemId: String) {
        specShoppingCart.removeAll { it.id == cartItemId }
        updateCartFlows()
        saveCartItems()
    }

    fun updateSpecCartItemQuantity(cartItemId: String, quantity: Int) {
        val itemIndex = specShoppingCart.indexOfFirst { it.id == cartItemId }
        if (itemIndex != -1) {
            specShoppingCart[itemIndex] = specShoppingCart[itemIndex].copy(quantity = quantity)
            updateCartFlows()
            saveCartItems()
        }
    }

    fun toggleSpecCartItemSelection(cartItemId: String) {
        val itemIndex = specShoppingCart.indexOfFirst { it.id == cartItemId }
        if (itemIndex != -1) {
            val item = specShoppingCart[itemIndex]
            specShoppingCart[itemIndex] = item.copy(selected = !item.selected)
            updateCartFlows()
            saveCartItems()
        }
    }

    /**
     * 检查是否所有商品都被选中
     */
    fun isAllSpecCartSelected(): Boolean {
        if (specShoppingCart.isEmpty()) return false
        return specShoppingCart.all { it.selected }
    }

    /**
     * 全选/取消全选所有商品
     */
    fun toggleAllSpecCartSelection() {
        val shouldSelectAll = !isAllSpecCartSelected()
        specShoppingCart.forEachIndexed { index, item ->
            specShoppingCart[index] = item.copy(selected = shouldSelectAll)
        }
        updateCartFlows()
        saveCartItems()
        android.util.Log.d("DataRepository", "Toggle all selection: $shouldSelectAll")
    }

    /**
     * 全选/取消全选某个店铺的商品
     */
    fun toggleStoreSpecCartSelection(storeName: String) {
        val storeItems = specShoppingCart.filter { it.storeName == storeName }
        if (storeItems.isEmpty()) return

        val shouldSelectAll = !storeItems.all { it.selected }
        specShoppingCart.forEachIndexed { index, item ->
            if (item.storeName == storeName) {
                specShoppingCart[index] = item.copy(selected = shouldSelectAll)
            }
        }
        updateCartFlows()
        saveCartItems()
        android.util.Log.d("DataRepository", "Toggle store '$storeName' selection: $shouldSelectAll")
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
        saveCartItems()
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
        // 任务六日志记录：确认付款操作
        TaskSixLogger.logConfirmPayment(context)
        
        // 首先在运行时订单中查找
        val runtimeIndex = runtimeOrders.indexOfFirst { it.id == orderId }
        if (runtimeIndex != -1) {
            val order = runtimeOrders[runtimeIndex]
            if (order.status == OrderStatus.PENDING_PAYMENT) {
                // 更新订单状态为待收货
                runtimeOrders[runtimeIndex] = order.copy(
                    status = OrderStatus.PENDING_RECEIPT,
                    payTime = System.currentTimeMillis(),
                    shipTime = System.currentTimeMillis() + 3600000L // 假设1小时后发货
                )
                
                android.util.Log.d("DataRepository", "Order $orderId status updated to PENDING_RECEIPT")
                
                // 任务六日志记录：付款成功
                TaskSixLogger.logPaymentSuccess(context, orderId)
                
                // 检查是否为第一个待付款订单的完成，如果是则记录任务完成
                if (isFirstPendingPaymentOrder(orderId)) {
                    TaskSixLogger.logTaskCompleted(context)
                }
                
                saveOrders()
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
                
                // 任务六日志记录：付款成功
                TaskSixLogger.logPaymentSuccess(context, orderId)
                
                // 检查是否为第一个待付款订单的完成，如果是则记录任务完成
                if (isFirstPendingPaymentOrder(orderId)) {
                    TaskSixLogger.logTaskCompleted(context)
                }
                
                saveOrders()
                return true
            }
        } catch (e: Exception) {
            android.util.Log.e("DataRepository", "Error processing static order $orderId", e)
        }
        
        android.util.Log.w("DataRepository", "Order $orderId not found or not in PENDING_PAYMENT status")
        // 任务六日志记录：付款失败
        TaskSixLogger.logPaymentFailed(context, "订单不存在或状态不正确")
        return false
    }
    
    /**
     * 检查给定订单是否是第一个待付款订单
     */
    private fun isFirstPendingPaymentOrder(orderId: String): Boolean {
        val allOrders = getOrders()
        val pendingPaymentOrders = allOrders
            .filter { it.status == OrderStatus.PENDING_PAYMENT }
            .sortedBy { it.createTime } // 按创建时间排序，最早的为第一个
        return pendingPaymentOrders.isNotEmpty() && pendingPaymentOrders.first().id == orderId
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
                saveOrders()
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
                    saveOrders()
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
            saveAddresses()
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
                saveAddresses()
                android.util.Log.d("DataRepository", "Updated runtime address: ${updatedAddress.id}")
                return true
            }
            
            // 如果在运行时地址中找不到，添加为新的运行时地址（覆盖静态地址）
            runtimeAddresses.add(0, updatedAddress)
            saveAddresses()
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
                saveAddresses()
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
        
        // 保存地址更改
        saveAddresses()
        
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
    
    // ==================== 优惠券管理功能 ====================
    
    /**
     * 获取所有可用的优惠券
     */
    fun getAvailableCoupons(): List<Coupon> {
        return availableCoupons.filter { !it.isUsed && !it.isExpired }
    }
    
    /**
     * 获取指定金额可用的优惠券
     */
    fun getAvailableCoupons(orderAmount: Double): List<Coupon> {
        return availableCoupons.filter { it.isAvailable(orderAmount) }
    }
    
    /**
     * 根据ID获取优惠券
     */
    fun getCouponById(couponId: String): Coupon? {
        return availableCoupons.find { it.id == couponId }
    }
    
    /**
     * 使用优惠券
     */
    fun useCoupon(couponId: String): Boolean {
        val couponIndex = availableCoupons.indexOfFirst { it.id == couponId }
        return if (couponIndex != -1) {
            val coupon = availableCoupons[couponIndex]
            if (!coupon.isUsed && !coupon.isExpired) {
                availableCoupons[couponIndex] = coupon.copy(isUsed = true)
                android.util.Log.d("DataRepository", "Used coupon: $couponId")
                true
            } else {
                android.util.Log.w("DataRepository", "Cannot use coupon: $couponId (used: ${coupon.isUsed}, expired: ${coupon.isExpired})")
                false
            }
        } else {
            android.util.Log.w("DataRepository", "Coupon not found: $couponId")
            false
        }
    }
    
    /**
     * 重置优惠券状态（用于测试）
     */
    fun resetCoupons() {
        availableCoupons.forEach { coupon ->
            val index = availableCoupons.indexOf(coupon)
            availableCoupons[index] = coupon.copy(isUsed = false)
        }
        android.util.Log.d("DataRepository", "Reset all coupons")
    }
    
    suspend fun getConversationSummaries(): List<ConversationSummary> = withContext(Dispatchers.IO) {
        try {
            val conversationData = loadConversationData()
            val allNewMessages = getNewMessages()
            
            conversationData.conversations.map { conversation ->
                // 获取该对话的所有消息（包括原始消息和新消息）
                val conversationNewMessages = allNewMessages.filter { it.conversationId == conversation.id }
                val allMessages = conversation.messages + conversationNewMessages
                
                // 找到最新消息
                val lastMessage = allMessages.sortedByDescending { it.timestamp }.firstOrNull()
                
                ConversationSummary(
                    id = conversation.id,
                    chatName = conversation.chatName,
                    chatAvatar = conversation.chatAvatar,
                    lastMessage = lastMessage?.content ?: "暂无消息",
                    lastMessageTime = lastMessage?.timestamp ?: 0L,
                    hasUnread = false // 可以根据需要实现未读状态逻辑
                )
            }.sortedByDescending { it.lastMessageTime } // 按最新消息时间排序
        } catch (e: Exception) {
            android.util.Log.e("DataRepository", "Error getting conversation summaries", e)
            emptyList()
        }
    }

    // 新消息管理方法
    fun addNewMessage(message: ChatMessage) {
        newMessages.add(message)
        saveNewMessages()
        android.util.Log.d("DataRepository", "Added new message: ${message.content}")
    }
    
    fun getNewMessages(): List<ChatMessage> {
        return newMessages.toList()
    }
    
    fun clearNewMessages() {
        newMessages.clear()
        saveNewMessages()
        android.util.Log.d("DataRepository", "Cleared all new messages")
    }
    
    // 免打扰设置管理方法
    fun setMuteSetting(senderName: String, isMuted: Boolean) {
        val existingIndex = muteSettings.indexOfFirst { it.senderName == senderName }
        val newSetting = MuteSetting(
            senderName = senderName,
            isMuted = isMuted,
            timestamp = System.currentTimeMillis()
        )
        
        if (existingIndex != -1) {
            muteSettings[existingIndex] = newSetting
        } else {
            muteSettings.add(newSetting)
        }
        
        saveMuteSettings()
        android.util.Log.d("DataRepository", "Set mute setting for $senderName: $isMuted")
    }
    
    fun getMuteSetting(senderName: String): Boolean {
        return muteSettings.find { it.senderName == senderName }?.isMuted ?: false
    }
    
    fun getAllMuteSettings(): List<MuteSetting> {
        return muteSettings.toList()
    }
    
    // ==================== 任务一日志管理功能 ====================
    
    /**
     * 记录搜索发起操作（任务一相关）
     */
    fun logTaskOneSearchInitiated(keyword: String) {
        TaskOneLogger.logSearchInitiated(context, keyword)
        android.util.Log.d("DataRepository", "Task one: Search initiated for '$keyword'")
    }
    
    /**
     * 记录搜索结果加载（任务一相关）
     */
    fun logTaskOneSearchResults(keyword: String, resultCount: Int) {
        TaskOneLogger.logSearchResultsLoaded(context, keyword, resultCount)
        android.util.Log.d("DataRepository", "Task one: Search results loaded - $resultCount results for '$keyword'")
    }
    
    /**
     * 记录查看第一个商品（任务一相关）
     */
    fun logTaskOneFirstProductViewed(productId: String, productName: String) {
        TaskOneLogger.logFirstProductViewed(context, productId, productName)
        android.util.Log.d("DataRepository", "Task one: First product viewed - '$productName' (ID: $productId)")
    }
    
    /**
     * 记录任务一完成
     */
    fun logTaskOneCompleted(searchKeyword: String, viewedProductName: String) {
        TaskOneLogger.logTaskCompleted(context, searchKeyword, viewedProductName)
        android.util.Log.d("DataRepository", "Task one: Task completed - searched '$searchKeyword', viewed '$viewedProductName'")
    }
    
    /**
     * 检查任务一是否完成
     */
    fun isTaskOneCompleted(): Boolean {
        return TaskOneLogger.isTaskOneCompleted(context)
    }
    
    /**
     * 获取任务一完成状态详情
     */
    fun getTaskOneCompletionDetails(): Map<String, Any> {
        return TaskOneLogger.getTaskOneCompletionDetails(context)
    }
    
    /**
     * 获取任务一日志文件路径
     */
    fun getTaskOneLogFilePath(): String {
        return TaskOneLogger.getLogFilePath(context)
    }
    
    /**
     * 清除任务一日志
     */
    fun clearTaskOneLogs() {
        TaskOneLogger.clearLogs(context)
        android.util.Log.d("DataRepository", "Task one: All logs cleared")
    }
    
    // ==================== 任务四日志管理功能 ====================
    
    /**
     * 记录访问「我的」页面（任务四相关）
     */
    fun logTaskFourMePageVisited() {
        TaskFourLogger.logMePageVisited(context)
        android.util.Log.d("DataRepository", "Task four: Me page visited")
    }
    
    /**
     * 记录找到订单管理区域（任务四相关）
     */
    fun logTaskFourOrdersSectionFound() {
        TaskFourLogger.logOrdersSectionFound(context)
        android.util.Log.d("DataRepository", "Task four: Orders section found")
    }
    
    /**
     * 记录点击「全部订单」（任务四相关）
     */
    fun logTaskFourAllOrdersClicked() {
        TaskFourLogger.logAllOrdersClicked(context)
        android.util.Log.d("DataRepository", "Task four: All orders clicked")
    }
    
    /**
     * 记录订单列表加载完成（任务四相关）
     */
    fun logTaskFourOrderListLoaded(orderCount: Int) {
        TaskFourLogger.logOrderListLoaded(context, orderCount)
        android.util.Log.d("DataRepository", "Task four: Order list loaded - $orderCount orders")
    }
    
    /**
     * 记录查看订单详情（任务四相关）
     */
    fun logTaskFourOrderDetailsViewed(orderId: String, status: String) {
        TaskFourLogger.logOrderDetailsViewed(context, orderId, status)
        android.util.Log.d("DataRepository", "Task four: Order details viewed - $orderId, status: $status")
    }
    
    /**
     * 记录任务四完成
     */
    fun logTaskFourCompleted(totalOrders: Int) {
        TaskFourLogger.logTaskCompleted(context, totalOrders)
        android.util.Log.d("DataRepository", "Task four: Task completed - viewed all orders, total: $totalOrders")
    }
    
    // 任务六相关方法
    /**
     * 测试任务六日志功能
     */
    fun testTaskSixLogger() {
        TaskSixLogger.logTaskStart(context)
        TaskSixLogger.logOrderFound(context, "test_order_001")
        TaskSixLogger.logClickPayButton(context)
        TaskSixLogger.logPaymentSuccess(context, "test_order_001")
        TaskSixLogger.logTaskCompleted(context)
    }
    
    /**
     * 获取任务六日志内容
     */
    fun getTaskSixLog(): String {
        return TaskSixLogger.readLog(context)
    }
    
    /**
     * 清除任务六日志
     */
    fun clearTaskSixLog() {
        TaskSixLogger.clearLog(context)
    }
    
    /**
     * 检查任务六是否完成
     */
    fun isTaskSixCompleted(): Boolean {
        return TaskSixLogger.isTaskCompleted(context)
    }
    
    /**
     * 检查任务四是否完成
     */
    fun isTaskFourCompleted(): Boolean {
        return TaskFourLogger.isTaskFourCompleted(context)
    }
    
    /**
     * 获取任务四完成状态详情
     */
    fun getTaskFourCompletionDetails(): Map<String, Any> {
        return TaskFourLogger.getTaskFourCompletionDetails(context)
    }
    
    /**
     * 获取任务四日志文件路径
     */
    fun getTaskFourLogFilePath(): String {
        return TaskFourLogger.getLogFilePath(context)
    }
    
    /**
     * 清除任务四日志
     */
    fun clearTaskFourLogs() {
        TaskFourLogger.clearLogs(context)
        android.util.Log.d("DataRepository", "Task four: All logs cleared")
    }
    
    /**
     * 初始化订单计数器
     */
    private fun initializeOrderCounter() {
        val allOrders = getOrders()
        if (allOrders.isNotEmpty()) {
            // 找到最大的订单编号
            val maxOrderNumber = allOrders.mapNotNull { order ->
                val id = order.id
                if (id.startsWith("order_")) {
                    id.substring(6).toIntOrNull()
                } else {
                    null
                }
            }.maxOrNull() ?: 0
            
            orderCounter = maxOrderNumber + 1
        } else {
            orderCounter = 1
        }
        android.util.Log.d("DataRepository", "Initialized order counter to: $orderCounter")
    }
    
    /**
     * 生成新的订单ID
     */
    private fun generateOrderId(): String {
        val id = "order_${String.format("%03d", orderCounter)}"
        orderCounter++
        android.util.Log.d("DataRepository", "Generated order ID: $id")
        return id
    }
    
    /**
     * 测试支付流程
     */
    fun testPaymentFlow() {
        android.util.Log.d("DataRepository", "=== 开始测试支付流程 ===")
        
        // 1. 创建测试订单
        val orderId = createOrder(
            productId = "test_product",
            productName = "测试商品",
            storeName = "测试店铺",
            imageUrl = "test_image.jpg",
            price = 100.0,
            quantity = 1,
            selectedColor = "红色",
            selectedVersion = "64GB"
        )
        android.util.Log.d("DataRepository", "创建订单: $orderId")
        
        // 2. 检查订单状态
        val createdOrder = getOrderById(orderId)
        android.util.Log.d("DataRepository", "创建的订单状态: ${createdOrder?.status}")
        
        // 3. 获取所有待付款订单
        val pendingOrders = getOrders().filter { it.status == OrderStatus.PENDING_PAYMENT }
        android.util.Log.d("DataRepository", "当前待付款订单数量: ${pendingOrders.size}")
        
        // 4. 支付订单
        val payResult = payOrder(orderId)
        android.util.Log.d("DataRepository", "支付结果: $payResult")
        
        // 5. 再次检查订单状态
        val paidOrder = getOrderById(orderId)
        android.util.Log.d("DataRepository", "支付后订单状态: ${paidOrder?.status}")
        
        // 6. 检查待付款订单数量
        val pendingOrdersAfterPay = getOrders().filter { it.status == OrderStatus.PENDING_PAYMENT }
        android.util.Log.d("DataRepository", "支付后待付款订单数量: ${pendingOrdersAfterPay.size}")
        
        android.util.Log.d("DataRepository", "=== 支付流程测试完成 ===")
    }
    
    // ================ 新增任务支持方法 ================
    
    // 任务八相关方法
    fun testTaskEightLogger() {
        TaskEightLogger.logTaskStart(context)
        TaskEightLogger.logHomePageEntered(context)
        // 使用假数据进行测试，实际使用时会在UI组件中正确调用
        val productCount = 20 // 模拟首页商品数量
        TaskEightLogger.logProductsLoaded(context, productCount)
        TaskEightLogger.logTaskCompleted(context, productCount)
    }
    
    fun getTaskEightLog(): String = TaskEightLogger.readLog(context)
    fun clearTaskEightLog() = TaskEightLogger.clearLog(context)
    fun isTaskEightCompleted(): Boolean = TaskEightLogger.isTaskCompleted(context)
    fun getTaskEightProductCount(): Int = TaskEightLogger.getProductCount(context)
    
    // 任务九相关方法
    fun testTaskNineLogger() {
        TaskNineLogger.logTaskStart(context)
        TaskNineLogger.logCartPageEntered(context)
        val cartItems = getSpecShoppingCart()
        TaskNineLogger.logCartItemsLoaded(context, cartItems.size)
        val totalPrice = getSelectedSpecCartTotalPrice()
        TaskNineLogger.logTotalPriceCalculated(context, totalPrice)
        TaskNineLogger.logTaskCompleted(context, totalPrice)
    }
    
    fun getTaskNineLog(): String = TaskNineLogger.readLog(context)
    fun clearTaskNineLog() = TaskNineLogger.clearLog(context)
    fun isTaskNineCompleted(): Boolean = TaskNineLogger.isTaskCompleted(context)
    fun getTaskNineTotalPrice(): Double = TaskNineLogger.getTotalPrice(context)
    
    // 任务十相关方法
    fun testTaskTenLogger() {
        TaskTenLogger.logTaskStart(context)
        TaskTenLogger.logOrderPageEntered(context)
        TaskTenLogger.logPendingReceiptTabSelected(context)
        val pendingOrders = getOrders().filter { it.status == OrderStatus.PENDING_RECEIPT }
        TaskTenLogger.logPendingReceiptOrdersLoaded(context, pendingOrders.size)
        TaskTenLogger.logTaskCompleted(context, pendingOrders.size)
    }
    
    fun getTaskTenLog(): String = TaskTenLogger.readLog(context)
    fun clearTaskTenLog() = TaskTenLogger.clearLog(context)
    fun isTaskTenCompleted(): Boolean = TaskTenLogger.isTaskCompleted(context)
    fun getTaskTenPendingReceiptOrderCount(): Int = TaskTenLogger.getPendingReceiptOrderCount(context)
    
    // 任务十一相关方法
    fun testTaskElevenLogger() {
        TaskElevenLogger.logTaskStart(context)
        TaskElevenLogger.logMessagePageEntered(context)
        // 使用假数据进行测试，实际使用时会在UI组件中正确调用
        val messageCount = 15 // 模拟消息总数
        TaskElevenLogger.logMessagesLoaded(context, messageCount)
        TaskElevenLogger.logTaskCompleted(context, messageCount)
    }
    
    fun getTaskElevenLog(): String = TaskElevenLogger.readLog(context)
    fun clearTaskElevenLog() = TaskElevenLogger.clearLog(context)
    fun isTaskElevenCompleted(): Boolean = TaskElevenLogger.isTaskCompleted(context)
    fun getTaskElevenMessageCount(): Int = TaskElevenLogger.getMessageCount(context)
    
    // 任务十四相关方法
    fun testTaskFourteenLogger() {
        TaskFourteenLogger.logTaskStart(context)
        TaskFourteenLogger.logProductDetailEntered(context, "iPhone 15")
        TaskFourteenLogger.logReviewSectionViewed(context)
        // 假设iPhone15有20条评论
        val reviewCount = 20
        TaskFourteenLogger.logReviewsLoaded(context, reviewCount)
        TaskFourteenLogger.logTaskCompleted(context, reviewCount)
    }
    
    fun getTaskFourteenLog(): String = TaskFourteenLogger.readLog(context)
    fun clearTaskFourteenLog() = TaskFourteenLogger.clearLog(context)
    fun isTaskFourteenCompleted(): Boolean = TaskFourteenLogger.isTaskCompleted(context)
    fun getTaskFourteenReviewCount(): Int = TaskFourteenLogger.getReviewCount(context)
    
    // 任务十六相关方法
    fun testTaskSixteenLogger() {
        TaskSixteenLogger.logTaskStart(context)
        TaskSixteenLogger.logSearchStarted(context, "iPhone15")
        TaskSixteenLogger.logSearchResultsLoaded(context, 10)
        TaskSixteenLogger.logPriceFilterApplied(context, 5000, 8000)
        TaskSixteenLogger.logCategoryFilterApplied(context, "手机")
        TaskSixteenLogger.logProductSelected(context, "iPhone 15")
        TaskSixteenLogger.logSpecSelected(context, "黑色", "256GB")
        TaskSixteenLogger.logAddToCart(context, "iPhone 15 黑色 256GB")
        TaskSixteenLogger.logCartEntered(context)
        TaskSixteenLogger.logCheckoutStarted(context)
        TaskSixteenLogger.logPaymentMethodSelected(context, "微信支付")
        TaskSixteenLogger.logCouponSelected(context, "满3000减50", 50.0)
        TaskSixteenLogger.logPaymentCompleted(context, "order_test")
        TaskSixteenLogger.logTaskCompleted(context)
    }
    
    fun getTaskSixteenLog(): String = TaskSixteenLogger.readLog(context)
    fun clearTaskSixteenLog() = TaskSixteenLogger.clearLog(context)
    fun isTaskSixteenCompleted(): Boolean = TaskSixteenLogger.isTaskCompleted(context)
    
    // 任务十七相关方法
    fun testTaskSeventeenLogger() {
        TaskSeventeenLogger.logTaskStart(context)
        TaskSeventeenLogger.logHomePageEntered(context)
        TaskSeventeenLogger.logProductDetailEntered(context, "iPhone 15")
        TaskSeventeenLogger.logShopPageEntered(context, "Apple产品京东自营旗舰店")
        TaskSeventeenLogger.logShopProductSelected(context, "iPhone 15 粉色 256GB")
        TaskSeventeenLogger.logProductSpecSelected(context, "粉色", "256GB", 1)
        TaskSeventeenLogger.logBuyNowClicked(context, "iPhone 15 粉色 256GB")
        TaskSeventeenLogger.logOrderCreated(context, "order_test")
        TaskSeventeenLogger.logPaymentCompleted(context, "order_test")
        TaskSeventeenLogger.logPendingReceiptOrdersViewed(context, 1)
        TaskSeventeenLogger.logTaskCompleted(context)
    }
    
    fun getTaskSeventeenLog(): String = TaskSeventeenLogger.readLog(context)
    fun clearTaskSeventeenLog() = TaskSeventeenLogger.clearLog(context)
    fun isTaskSeventeenCompleted(): Boolean = TaskSeventeenLogger.isTaskCompleted(context)
    
    // 任务十八相关方法
    fun testTaskEighteenLogger() {
        TaskEighteenLogger.logTaskStart(context)
        TaskEighteenLogger.logOrderPageEntered(context)
        val pendingOrders = getOrders().filter { it.status == OrderStatus.PENDING_PAYMENT }
        TaskEighteenLogger.logPendingPaymentOrdersFound(context, pendingOrders.size)
        
        // 模拟取消所有待付款订单
        pendingOrders.forEach { order ->
            TaskEighteenLogger.logOrderCancelled(context, order.id)
        }
        TaskEighteenLogger.logAllPendingOrdersCancelled(context, pendingOrders.size)
        
        TaskEighteenLogger.logAllOrdersTabSelected(context)
        val cancelledOrders = getOrders().filter { it.status == OrderStatus.CANCELLED }
        TaskEighteenLogger.logCancelledOrdersFound(context, cancelledOrders.size)
        
        // 模拟删除所有已取消订单
        cancelledOrders.forEach { order ->
            TaskEighteenLogger.logOrderDeleted(context, order.id)
        }
        TaskEighteenLogger.logAllCancelledOrdersDeleted(context, cancelledOrders.size)
        TaskEighteenLogger.logTaskCompleted(context, pendingOrders.size, cancelledOrders.size)
    }
    
    fun getTaskEighteenLog(): String = TaskEighteenLogger.readLog(context)
    fun clearTaskEighteenLog() = TaskEighteenLogger.clearLog(context)
    fun isTaskEighteenCompleted(): Boolean = TaskEighteenLogger.isTaskCompleted(context)
    fun getTaskEighteenCounts(): Pair<Int, Int> = TaskEighteenLogger.getCancelledAndDeletedCounts(context)
    
    /**
     * 测试所有任务的日志功能
     */
    fun testAllTaskLoggers() {
        android.util.Log.d("DataRepository", "=== 开始测试所有任务日志功能 ===")
        
        testTaskEightLogger()
        testTaskNineLogger()
        testTaskTenLogger()
        testTaskElevenLogger()
        testTaskFourteenLogger()
        testTaskSixteenLogger()
        testTaskSeventeenLogger()
        testTaskEighteenLogger()
        
        android.util.Log.d("DataRepository", "=== 所有任务日志功能测试完成 ===")
    }
    
    /**
     * 获取所有任务的完成状态
     */
    fun getAllTasksCompletionStatus(): Map<String, Boolean> {
        return mapOf(
            "任务八" to isTaskEightCompleted(),
            "任务九" to isTaskNineCompleted(),
            "任务十" to isTaskTenCompleted(),
            "任务十一" to isTaskElevenCompleted(),
            "任务十四" to isTaskFourteenCompleted(),
            "任务十六" to isTaskSixteenCompleted(),
            "任务十七" to isTaskSeventeenCompleted(),
            "任务十八" to isTaskEighteenCompleted()
        )
    }
    
    /**
     * 清除所有任务的日志
     */
    fun clearAllTaskLogs() {
        clearTaskEightLog()
        clearTaskNineLog()
        clearTaskTenLog()
        clearTaskElevenLog()
        clearTaskFourteenLog()
        clearTaskSixteenLog()
        clearTaskSeventeenLog()
        clearTaskEighteenLog()
        android.util.Log.d("DataRepository", "所有任务日志已清除")
    }
}