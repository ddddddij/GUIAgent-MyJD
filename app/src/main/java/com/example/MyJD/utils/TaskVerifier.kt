package com.example.MyJD.utils

import android.content.Context
import android.util.Log
import com.example.MyJD.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

object TaskVerifier {
    private val gson = Gson()
    private const val TAG = "TaskVerifier"
    
    data class VerificationResult(
        val isCompleted: Boolean,
        val message: String,
        val details: Map<String, Any> = emptyMap()
    )
    
    // 1. 验证搜索iPhone 15任务
    fun verifySearchiPhone15(context: Context): VerificationResult {
        val logs = TaskLogger.getAllLogs(context)
        val searchLogs = logs.filter { 
            it.taskType == "SEARCH" && 
            it.details["searchKeyword"]?.contains("iPhone 15", ignoreCase = true) == true &&
            it.details["firstProductViewed"] == "true"
        }
        
        return if (searchLogs.isNotEmpty()) {
            val latestLog = searchLogs.maxByOrNull { it.timestamp }
            VerificationResult(
                isCompleted = true,
                message = "已完成搜索iPhone 15并查看第一个商品",
                details = mapOf(
                    "searchTime" to (latestLog?.timestamp ?: 0),
                    "resultCount" to (latestLog?.details?.get("searchResultCount") ?: "0"),
                    "firstProduct" to (latestLog?.details?.get("firstProductName") ?: "")
                )
            )
        } else {
            VerificationResult(
                isCompleted = false,
                message = "未完成搜索iPhone 15或未查看第一个商品"
            )
        }
    }
    
    // 2. 验证将iPhone 15加入购物车
    fun verifyAddiPhone15ToCart(context: Context): VerificationResult {
        return try {
            val cartItems = loadCartItems(context)
            val iPhone15Items = cartItems.filter { 
                it.product.name.contains("iPhone 15", ignoreCase = true) &&
                it.product.selectedColor == "蓝色" &&
                it.product.selectedVersion == "128GB"
            }
            
            if (iPhone15Items.isNotEmpty()) {
                VerificationResult(
                    isCompleted = true,
                    message = "购物车中存在iPhone 15 蓝色 128GB",
                    details = mapOf(
                        "cartItemId" to iPhone15Items.first().id,
                        "quantity" to iPhone15Items.first().quantity,
                        "addedTime" to iPhone15Items.first().addedTime
                    )
                )
            } else {
                VerificationResult(
                    isCompleted = false,
                    message = "购物车中不存在iPhone 15 蓝色 128GB"
                )
            }
        } catch (e: Exception) {
            VerificationResult(
                isCompleted = false,
                message = "检查购物车数据失败: ${e.message}"
            )
        }
    }
    
    // 3. 验证立即购买iPhone 15
    fun verifyPurchaseiPhone15(context: Context): VerificationResult {
        return try {
            val orders = loadOrders(context)
            val iPhone15Orders = orders.filter { order ->
                order.items.any { item ->
                    item.product.name.contains("iPhone 15", ignoreCase = true)
                }
            }
            
            if (iPhone15Orders.isNotEmpty()) {
                val latestOrder = iPhone15Orders.maxByOrNull { it.createTime }
                VerificationResult(
                    isCompleted = true,
                    message = "存在iPhone 15的订单",
                    details = mapOf(
                        "orderId" to (latestOrder?.id ?: ""),
                        "status" to (latestOrder?.status ?: ""),
                        "createTime" to (latestOrder?.createTime ?: 0),
                        "totalAmount" to (latestOrder?.totalAmount ?: 0.0)
                    )
                )
            } else {
                VerificationResult(
                    isCompleted = false,
                    message = "不存在iPhone 15的订单"
                )
            }
        } catch (e: Exception) {
            VerificationResult(
                isCompleted = false,
                message = "检查订单数据失败: ${e.message}"
            )
        }
    }
    
    // 4. 验证查看全部订单
    fun verifyViewAllOrders(context: Context): VerificationResult {
        val logs = TaskLogger.getAllLogs(context)
        val viewOrderLogs = logs.filter { 
            it.taskType == "VIEW_ORDERS" && 
            it.details["page"] == "全部订单"
        }
        
        return if (viewOrderLogs.isNotEmpty()) {
            val latestLog = viewOrderLogs.maxByOrNull { it.timestamp }
            VerificationResult(
                isCompleted = true,
                message = "已查看全部订单",
                details = mapOf(
                    "viewTime" to (latestLog?.timestamp ?: 0)
                )
            )
        } else {
            VerificationResult(
                isCompleted = false,
                message = "未查看全部订单"
            )
        }
    }
    
    // 5. 验证购物车勾选第一个商品并结算
    fun verifyCheckoutFirstCartItem(context: Context): VerificationResult {
        val logs = TaskLogger.getAllLogs(context)
        val checkoutLogs = logs.filter { 
            it.taskType == "CHECKOUT_CART" &&
            it.details["itemCount"]?.toIntOrNull() ?: 0 >= 1
        }
        
        return if (checkoutLogs.isNotEmpty()) {
            val latestLog = checkoutLogs.maxByOrNull { it.timestamp }
            VerificationResult(
                isCompleted = true,
                message = "已勾选商品并完成结算",
                details = mapOf(
                    "checkoutTime" to (latestLog?.timestamp ?: 0),
                    "selectedItems" to (latestLog?.details?.get("selectedItems") ?: ""),
                    "totalAmount" to (latestLog?.details?.get("totalAmount") ?: "0")
                )
            )
        } else {
            VerificationResult(
                isCompleted = false,
                message = "未完成购物车商品勾选和结算"
            )
        }
    }
    
    // 6. 验证结算第一个待付款订单
    fun verifyPayFirstPendingOrder(context: Context): VerificationResult {
        val logs = TaskLogger.getAllLogs(context)
        val payLogs = logs.filter { it.taskType == "PAY_ORDER" }
        
        return if (payLogs.isNotEmpty()) {
            val latestLog = payLogs.maxByOrNull { it.timestamp }
            VerificationResult(
                isCompleted = true,
                message = "已支付待付款订单",
                details = mapOf(
                    "payTime" to (latestLog?.timestamp ?: 0),
                    "orderId" to (latestLog?.details?.get("orderId") ?: ""),
                    "paymentMethod" to (latestLog?.details?.get("paymentMethod") ?: "")
                )
            )
        } else {
            VerificationResult(
                isCompleted = false,
                message = "未支付待付款订单"
            )
        }
    }
    
    // 7. 验证给Apple店铺发消息
    fun verifySendMessageToAppleStore(context: Context): VerificationResult {
        val logs = TaskLogger.getAllLogs(context)
        val messageLogs = logs.filter { 
            it.taskType == "SEND_MESSAGE" && 
            it.details["storeName"]?.contains("Apple", ignoreCase = true) == true
        }
        
        return if (messageLogs.isNotEmpty()) {
            val latestLog = messageLogs.maxByOrNull { it.timestamp }
            VerificationResult(
                isCompleted = true,
                message = "已向Apple店铺发送消息",
                details = mapOf(
                    "sendTime" to (latestLog?.timestamp ?: 0),
                    "storeName" to (latestLog?.details?.get("storeName") ?: ""),
                    "message" to (latestLog?.details?.get("message") ?: "")
                )
            )
        } else {
            VerificationResult(
                isCompleted = false,
                message = "未向Apple店铺发送消息"
            )
        }
    }
    
    // 8. 验证计算首页商品数量
    fun verifyCountHomeProducts(context: Context): VerificationResult {
        val logs = TaskLogger.getAllLogs(context)
        val countLogs = logs.filter { 
            it.taskType == "COUNT_PRODUCTS" && 
            it.details["location"] == "首页"
        }
        
        return if (countLogs.isNotEmpty()) {
            val latestLog = countLogs.maxByOrNull { it.timestamp }
            VerificationResult(
                isCompleted = true,
                message = "已计算首页商品数量",
                details = mapOf(
                    "countTime" to (latestLog?.timestamp ?: 0),
                    "productCount" to (latestLog?.details?.get("productCount") ?: "0")
                )
            )
        } else {
            VerificationResult(
                isCompleted = false,
                message = "未计算首页商品数量"
            )
        }
    }
    
    // 9. 验证计算购物车总价
    fun verifyCalculateCartTotalPrice(context: Context): VerificationResult {
        val logs = TaskLogger.getAllLogs(context)
        val calculateLogs = logs.filter { 
            it.taskType == "CALCULATE_PRICE" && 
            it.details["location"] == "购物车"
        }
        
        return if (calculateLogs.isNotEmpty()) {
            val latestLog = calculateLogs.maxByOrNull { it.timestamp }
            VerificationResult(
                isCompleted = true,
                message = "已计算购物车总价",
                details = mapOf(
                    "calculateTime" to (latestLog?.timestamp ?: 0),
                    "totalPrice" to (latestLog?.details?.get("totalPrice") ?: "0")
                )
            )
        } else {
            VerificationResult(
                isCompleted = false,
                message = "未计算购物车总价"
            )
        }
    }
    
    // 10. 验证计算待收货订单数量
    fun verifyCountPendingReceiptOrders(context: Context): VerificationResult {
        val logs = TaskLogger.getAllLogs(context)
        val countLogs = logs.filter { 
            it.taskType == "COUNT_ORDERS" && 
            it.details["orderType"] == "待收货"
        }
        
        return if (countLogs.isNotEmpty()) {
            val latestLog = countLogs.maxByOrNull { it.timestamp }
            VerificationResult(
                isCompleted = true,
                message = "已计算待收货订单数量",
                details = mapOf(
                    "countTime" to (latestLog?.timestamp ?: 0),
                    "orderCount" to (latestLog?.details?.get("orderCount") ?: "0")
                )
            )
        } else {
            VerificationResult(
                isCompleted = false,
                message = "未计算待收货订单数量"
            )
        }
    }
    
    // 11. 验证计算京东客服消息数量
    fun verifyCountCustomerServiceMessages(context: Context): VerificationResult {
        val logs = TaskLogger.getAllLogs(context)
        val countLogs = logs.filter { 
            it.taskType == "COUNT_MESSAGES" && 
            it.details["messageType"]?.contains("京东客服", ignoreCase = true) == true
        }
        
        return if (countLogs.isNotEmpty()) {
            val latestLog = countLogs.maxByOrNull { it.timestamp }
            VerificationResult(
                isCompleted = true,
                message = "已计算京东客服消息数量",
                details = mapOf(
                    "countTime" to (latestLog?.timestamp ?: 0),
                    "messageCount" to (latestLog?.details?.get("messageCount") ?: "0")
                )
            )
        } else {
            VerificationResult(
                isCompleted = false,
                message = "未计算京东客服消息数量"
            )
        }
    }
    
    // 12. 验证查看京东秒送物流信息
    fun verifyCheckJDSecsongDelivery(context: Context): VerificationResult {
        val logs = TaskLogger.getAllLogs(context)
        val deliveryLogs = logs.filter { 
            it.taskType == "CHECK_DELIVERY" && 
            it.details["deliveryService"]?.contains("京东秒送", ignoreCase = true) == true
        }
        
        return if (deliveryLogs.isNotEmpty()) {
            val latestLog = deliveryLogs.maxByOrNull { it.timestamp }
            VerificationResult(
                isCompleted = true,
                message = "已查看京东秒送物流信息",
                details = mapOf(
                    "checkTime" to (latestLog?.timestamp ?: 0),
                    "estimatedTime" to (latestLog?.details?.get("estimatedTime") ?: "")
                )
            )
        } else {
            VerificationResult(
                isCompleted = false,
                message = "未查看京东秒送物流信息"
            )
        }
    }
    
    // 13. 验证检查购物车iPhone15并结算
    fun verifyCheckCartForiPhone15AndCheckout(context: Context): VerificationResult {
        val cartVerification = verifyAddiPhone15ToCart(context)
        val checkoutLogs = TaskLogger.getAllLogs(context).filter { it.taskType == "CHECKOUT_CART" }
        
        return if (cartVerification.isCompleted && checkoutLogs.isNotEmpty()) {
            VerificationResult(
                isCompleted = true,
                message = "已检查购物车中的iPhone15并完成结算",
                details = mapOf(
                    "hasIPhone15" to true,
                    "checkoutCompleted" to true
                )
            )
        } else {
            VerificationResult(
                isCompleted = false,
                message = "未完成iPhone15检查或结算流程"
            )
        }
    }
    
    // 14. 验证计算iPhone15评论数量
    fun verifyCountiPhone15Comments(context: Context): VerificationResult {
        val logs = TaskLogger.getAllLogs(context)
        val commentLogs = logs.filter { 
            it.taskType == "COUNT_COMMENTS" && 
            it.details["productName"]?.contains("iPhone15", ignoreCase = true) == true
        }
        
        return if (commentLogs.isNotEmpty()) {
            val latestLog = commentLogs.maxByOrNull { it.timestamp }
            VerificationResult(
                isCompleted = true,
                message = "已计算iPhone15评论数量",
                details = mapOf(
                    "countTime" to (latestLog?.timestamp ?: 0),
                    "commentCount" to (latestLog?.details?.get("commentCount") ?: "0")
                )
            )
        } else {
            VerificationResult(
                isCompleted = false,
                message = "未计算iPhone15评论数量"
            )
        }
    }
    
    // 15. 验证添加多个iPhone15到购物车
    fun verifyAddMultipleiPhone15ToCart(context: Context): VerificationResult {
        return try {
            val cartItems = loadCartItems(context)
            val iPhone15Items = cartItems.filter { 
                it.product.name.contains("iPhone 15", ignoreCase = true)
            }
            
            val blueCount = iPhone15Items.filter { 
                it.product.selectedColor == "蓝色" && it.product.selectedVersion == "128GB" 
            }.sumOf { it.quantity }
            
            val blackCount = iPhone15Items.filter { 
                it.product.selectedColor == "黑色" && it.product.selectedVersion == "256GB" 
            }.sumOf { it.quantity }
            
            val pinkCount = iPhone15Items.filter { 
                it.product.selectedColor == "粉色" && it.product.selectedVersion == "128GB" 
            }.sumOf { it.quantity }
            
            val totalCount = blueCount + blackCount + pinkCount
            
            if (blueCount >= 1 && blackCount >= 2 && pinkCount >= 3 && totalCount >= 6) {
                VerificationResult(
                    isCompleted = true,
                    message = "已添加指定规格和数量的iPhone15到购物车",
                    details = mapOf(
                        "blueCount" to blueCount,
                        "blackCount" to blackCount,
                        "pinkCount" to pinkCount,
                        "totalCount" to totalCount
                    )
                )
            } else {
                VerificationResult(
                    isCompleted = false,
                    message = "购物车中iPhone15规格或数量不符合要求",
                    details = mapOf(
                        "blueCount" to blueCount,
                        "blackCount" to blackCount,
                        "pinkCount" to pinkCount,
                        "requiredTotal" to 6
                    )
                )
            }
        } catch (e: Exception) {
            VerificationResult(
                isCompleted = false,
                message = "检查购物车数据失败: ${e.message}"
            )
        }
    }
    
    // 16. 验证复杂搜索和购买流程
    fun verifyComplexSearchAndPurchase(context: Context): VerificationResult {
        val logs = TaskLogger.getAllLogs(context)
        val complexLogs = logs.filter { 
            it.taskType == "COMPLEX_PURCHASE" &&
            it.details["searchKeyword"]?.contains("iPhone15", ignoreCase = true) == true &&
            it.details["priceRange"] == "5000-8000" &&
            it.details["productName"]?.contains("iPhone15 黑色 256GB", ignoreCase = true) == true &&
            it.details["paymentMethod"]?.contains("微信", ignoreCase = true) == true &&
            it.details["couponUsed"]?.contains("满3000减50", ignoreCase = true) == true
        }
        
        return if (complexLogs.isNotEmpty()) {
            val latestLog = complexLogs.maxByOrNull { it.timestamp }
            VerificationResult(
                isCompleted = true,
                message = "已完成复杂搜索筛选购买流程",
                details = latestLog?.details ?: emptyMap()
            )
        } else {
            VerificationResult(
                isCompleted = false,
                message = "未完成完整的搜索筛选购买流程"
            )
        }
    }
    
    // 17. 验证复杂店铺导航流程
    fun verifyComplexStoreNavigation(context: Context): VerificationResult {
        val logs = TaskLogger.getAllLogs(context)
        val navigationLogs = logs.filter { 
            it.taskType == "STORE_NAVIGATION" &&
            it.details["originalProduct"]?.contains("iPhone15", ignoreCase = true) == true &&
            it.details["purchasedProduct"]?.contains("iPhone 15 粉色 256GB", ignoreCase = true) == true
        }
        
        return if (navigationLogs.isNotEmpty()) {
            val latestLog = navigationLogs.maxByOrNull { it.timestamp }
            VerificationResult(
                isCompleted = true,
                message = "已完成店铺导航和购买流程",
                details = latestLog?.details ?: emptyMap()
            )
        } else {
            VerificationResult(
                isCompleted = false,
                message = "未完成店铺导航和购买流程"
            )
        }
    }
    
    // 18. 验证取消所有待付款订单
    fun verifyCancelAllPendingOrders(context: Context): VerificationResult {
        val logs = TaskLogger.getAllLogs(context)
        val cancelLogs = logs.filter { it.taskType == "CANCEL_ORDERS" }
        
        return if (cancelLogs.isNotEmpty()) {
            val latestLog = cancelLogs.maxByOrNull { it.timestamp }
            VerificationResult(
                isCompleted = true,
                message = "已取消待付款订单",
                details = mapOf(
                    "cancelTime" to (latestLog?.timestamp ?: 0),
                    "cancelledOrderIds" to (latestLog?.details?.get("cancelledOrderIds") ?: ""),
                    "orderCount" to (latestLog?.details?.get("orderCount") ?: "0")
                )
            )
        } else {
            VerificationResult(
                isCompleted = false,
                message = "未取消待付款订单"
            )
        }
    }
    
    // 19. 验证新建地址
    fun verifyAddNewAddress(context: Context): VerificationResult {
        return try {
            val addresses = loadAddresses(context)
            val wuhanAddress = addresses.find { 
                it.province == "湖北省" && 
                it.city == "武汉市" && 
                it.district == "洪山区" && 
                it.detailAddress.contains("文秀街9号") &&
                it.isDefault
            }
            
            if (wuhanAddress != null) {
                VerificationResult(
                    isCompleted = true,
                    message = "已新建武汉洪山区地址并设为默认",
                    details = mapOf(
                        "addressId" to wuhanAddress.id,
                        "recipientName" to wuhanAddress.recipientName,
                        "isDefault" to wuhanAddress.isDefault,
                        "createTime" to wuhanAddress.createTime
                    )
                )
            } else {
                VerificationResult(
                    isCompleted = false,
                    message = "未找到武汉洪山区文秀街9号默认地址"
                )
            }
        } catch (e: Exception) {
            VerificationResult(
                isCompleted = false,
                message = "检查地址数据失败: ${e.message}"
            )
        }
    }
    
    // 20. 验证设置Apple店铺消息免打扰
    fun verifySetAppleStoreMessageMute(context: Context): VerificationResult {
        val logs = TaskLogger.getAllLogs(context)
        val muteLogs = logs.filter { 
            it.taskType == "SET_MESSAGE_MUTE" && 
            it.details["storeName"]?.contains("Apple", ignoreCase = true) == true
        }
        
        return if (muteLogs.isNotEmpty()) {
            val latestLog = muteLogs.maxByOrNull { it.timestamp }
            VerificationResult(
                isCompleted = true,
                message = "已设置Apple店铺消息免打扰",
                details = mapOf(
                    "setTime" to (latestLog?.timestamp ?: 0),
                    "storeName" to (latestLog?.details?.get("storeName") ?: "")
                )
            )
        } else {
            VerificationResult(
                isCompleted = false,
                message = "未设置Apple店铺消息免打扰"
            )
        }
    }
    
    // 数据加载辅助函数
    private fun loadCartItems(context: Context): List<CartItem> {
        return try {
            val inputStream = context.assets.open("data/cart_items.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<CartItem>>() {}.type
            gson.fromJson<List<CartItem>>(jsonString, listType)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    private fun loadOrders(context: Context): List<Order> {
        return try {
            val inputStream = context.assets.open("data/orders.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<Order>>() {}.type
            gson.fromJson<List<Order>>(jsonString, listType)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    private fun loadAddresses(context: Context): List<Address> {
        return try {
            val inputStream = context.assets.open("data/addresses.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<Address>>() {}.type
            gson.fromJson<List<Address>>(jsonString, listType)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    // 验证所有任务的总函数
    fun verifyAllTasks(context: Context): Map<String, VerificationResult> {
        return mapOf(
            "verifySearchiPhone15" to verifySearchiPhone15(context),
            "verifyAddiPhone15ToCart" to verifyAddiPhone15ToCart(context),
            "verifyPurchaseiPhone15" to verifyPurchaseiPhone15(context),
            "verifyViewAllOrders" to verifyViewAllOrders(context),
            "verifyCheckoutFirstCartItem" to verifyCheckoutFirstCartItem(context),
            "verifyPayFirstPendingOrder" to verifyPayFirstPendingOrder(context),
            "verifySendMessageToAppleStore" to verifySendMessageToAppleStore(context),
            "verifyCountHomeProducts" to verifyCountHomeProducts(context),
            "verifyCalculateCartTotalPrice" to verifyCalculateCartTotalPrice(context),
            "verifyCountPendingReceiptOrders" to verifyCountPendingReceiptOrders(context),
            "verifyCountCustomerServiceMessages" to verifyCountCustomerServiceMessages(context),
            "verifyCheckJDSecsongDelivery" to verifyCheckJDSecsongDelivery(context),
            "verifyCheckCartForiPhone15AndCheckout" to verifyCheckCartForiPhone15AndCheckout(context),
            "verifyCountiPhone15Comments" to verifyCountiPhone15Comments(context),
            "verifyAddMultipleiPhone15ToCart" to verifyAddMultipleiPhone15ToCart(context),
            "verifyComplexSearchAndPurchase" to verifyComplexSearchAndPurchase(context),
            "verifyComplexStoreNavigation" to verifyComplexStoreNavigation(context),
            "verifyCancelAllPendingOrders" to verifyCancelAllPendingOrders(context),
            "verifyAddNewAddress" to verifyAddNewAddress(context),
            "verifySetAppleStoreMessageMute" to verifySetAppleStoreMessageMute(context)
        )
    }
    
    /**
     * 执行所有任务验证并将结果输出到logcat
     * 这是用于显示所有20个任务验证结果的主要函数
     */
    fun verifyAllTasksAndLogToLogcat(context: Context) {
        Log.i(TAG, "======================================")
        Log.i(TAG, "         开始验证所有任务")
        Log.i(TAG, "======================================")
        
        val taskNames = listOf(
            "任务1: 搜索iPhone 15并查看第一个商品",
            "任务2: 将iPhone 15 蓝色 128GB加入购物车", 
            "任务3: 立即购买iPhone 15",
            "任务4: 查看全部订单",
            "任务5: 购物车勾选第一个商品并结算",
            "任务6: 结算第一个待付款订单",
            "任务7: 给Apple店铺发消息",
            "任务8: 计算首页商品数量",
            "任务9: 计算购物车总价",
            "任务10: 计算待收货订单数量",
            "任务11: 计算京东客服消息数量",
            "任务12: 查看京东秒送物流信息",
            "任务13: 检查购物车iPhone15并结算",
            "任务14: 计算iPhone15评论数量",
            "任务15: 添加多件iPhone15到购物车",
            "任务16: 复杂搜索和购买流程",
            "任务17: 复杂店铺导航流程",
            "任务18: 取消所有待付款订单",
            "任务19: 新建武汉地址",
            "任务20: 设置Apple店铺消息免打扰"
        )
        
        val verificationMethods = listOf(
            { verifySearchiPhone15(context) },
            { verifyAddiPhone15ToCart(context) },
            { verifyPurchaseiPhone15(context) },
            { verifyViewAllOrders(context) },
            { verifyCheckoutFirstCartItem(context) },
            { verifyPayFirstPendingOrder(context) },
            { verifySendMessageToAppleStore(context) },
            { verifyCountHomeProducts(context) },
            { verifyCalculateCartTotalPrice(context) },
            { verifyCountPendingReceiptOrders(context) },
            { verifyCountCustomerServiceMessages(context) },
            { verifyCheckJDSecsongDelivery(context) },
            { verifyCheckCartForiPhone15AndCheckout(context) },
            { verifyCountiPhone15Comments(context) },
            { verifyAddMultipleiPhone15ToCart(context) },
            { verifyComplexSearchAndPurchase(context) },
            { verifyComplexStoreNavigation(context) },
            { verifyCancelAllPendingOrders(context) },
            { verifyAddNewAddress(context) },
            { verifySetAppleStoreMessageMute(context) }
        )
        
        var completedCount = 0
        val totalCount = taskNames.size
        
        taskNames.forEachIndexed { index, taskName ->
            try {
                val result = verificationMethods[index]()
                val status = if (result.isCompleted) "✅ 已完成" else "❌ 未完成"
                
                Log.i(TAG, "")
                Log.i(TAG, "[$taskName]")
                Log.i(TAG, "状态: $status")
                Log.i(TAG, "详情: ${result.message}")
                
                if (result.details.isNotEmpty()) {
                    Log.i(TAG, "详细信息:")
                    result.details.forEach { (key, value) ->
                        Log.i(TAG, "  - $key: $value")
                    }
                }
                
                if (result.isCompleted) {
                    completedCount++
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "验证${taskName}时发生错误: ${e.message}", e)
            }
        }
        
        Log.i(TAG, "")
        Log.i(TAG, "======================================")
        Log.i(TAG, "         任务验证总结")
        Log.i(TAG, "======================================")
        Log.i(TAG, "总任务数: $totalCount")
        Log.i(TAG, "已完成: $completedCount")
        Log.i(TAG, "未完成: ${totalCount - completedCount}")
        Log.i(TAG, "完成率: ${String.format("%.1f", (completedCount.toFloat() / totalCount) * 100)}%")
        Log.i(TAG, "======================================")
    }
    
    /**
     * 执行单个任务验证并输出到logcat
     */
    fun verifySingleTaskAndLog(context: Context, taskId: Int) {
        val taskNames = mapOf(
            1 to "搜索iPhone 15并查看第一个商品",
            2 to "将iPhone 15 蓝色 128GB加入购物车",
            3 to "立即购买iPhone 15",
            4 to "查看全部订单",
            5 to "购物车勾选第一个商品并结算",
            6 to "结算第一个待付款订单",
            7 to "给Apple店铺发消息",
            8 to "计算首页商品数量",
            9 to "计算购物车总价",
            10 to "计算待收货订单数量",
            11 to "计算京东客服消息数量",
            12 to "查看京东秒送物流信息",
            13 to "检查购物车iPhone15并结算",
            14 to "计算iPhone15评论数量",
            15 to "添加多件iPhone15到购物车",
            16 to "复杂搜索和购买流程",
            17 to "复杂店铺导航流程",
            18 to "取消所有待付款订单",
            19 to "新建武汉地址",
            20 to "设置Apple店铺消息免打扰"
        )
        
        val verificationMethods = mapOf(
            1 to { verifySearchiPhone15(context) },
            2 to { verifyAddiPhone15ToCart(context) },
            3 to { verifyPurchaseiPhone15(context) },
            4 to { verifyViewAllOrders(context) },
            5 to { verifyCheckoutFirstCartItem(context) },
            6 to { verifyPayFirstPendingOrder(context) },
            7 to { verifySendMessageToAppleStore(context) },
            8 to { verifyCountHomeProducts(context) },
            9 to { verifyCalculateCartTotalPrice(context) },
            10 to { verifyCountPendingReceiptOrders(context) },
            11 to { verifyCountCustomerServiceMessages(context) },
            12 to { verifyCheckJDSecsongDelivery(context) },
            13 to { verifyCheckCartForiPhone15AndCheckout(context) },
            14 to { verifyCountiPhone15Comments(context) },
            15 to { verifyAddMultipleiPhone15ToCart(context) },
            16 to { verifyComplexSearchAndPurchase(context) },
            17 to { verifyComplexStoreNavigation(context) },
            18 to { verifyCancelAllPendingOrders(context) },
            19 to { verifyAddNewAddress(context) },
            20 to { verifySetAppleStoreMessageMute(context) }
        )
        
        val taskName = taskNames[taskId]
        val verificationMethod = verificationMethods[taskId]
        
        if (taskName != null && verificationMethod != null) {
            try {
                val result = verificationMethod()
                val status = if (result.isCompleted) "✅ 已完成" else "❌ 未完成"
                
                Log.i(TAG, "")
                Log.i(TAG, "======== 任务${taskId}验证结果 ========")
                Log.i(TAG, "任务: $taskName")
                Log.i(TAG, "状态: $status")
                Log.i(TAG, "详情: ${result.message}")
                
                if (result.details.isNotEmpty()) {
                    Log.i(TAG, "详细信息:")
                    result.details.forEach { (key, value) ->
                        Log.i(TAG, "  - $key: $value")
                    }
                }
                Log.i(TAG, "===============================")
                
            } catch (e: Exception) {
                Log.e(TAG, "验证任务${taskId}时发生错误: ${e.message}", e)
            }
        } else {
            Log.e(TAG, "无效的任务ID: $taskId")
        }
    }
}