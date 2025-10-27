package com.example.MyJD.utils

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

data class TaskLog(
    val id: String,
    val taskType: String,
    val taskDescription: String,
    val action: String,
    val details: Map<String, String>,
    val timestamp: Long,
    val completed: Boolean
)

object TaskLogger {
    private val gson = Gson()
    private const val TASK_LOGS_FILE = "task_logs.json"
    private const val TAG = "TaskLogger"
    
    fun logSearchTask(
        context: Context,
        keyword: String,
        resultCount: Int,
        firstProductViewed: Boolean = false,
        firstProductId: String = "",
        firstProductName: String = ""
    ) {
        val taskLog = TaskLog(
            id = generateLogId(),
            taskType = "SEARCH",
            taskDescription = "搜索「$keyword」",
            action = "搜索操作",
            details = mapOf(
                "searchKeyword" to keyword,
                "searchResultCount" to resultCount.toString(),
                "firstProductViewed" to firstProductViewed.toString(),
                "firstProductId" to firstProductId,
                "firstProductName" to firstProductName
            ),
            timestamp = System.currentTimeMillis(),
            completed = true
        )
        saveLog(context, taskLog)
    }
    
    fun logAddToCart(
        context: Context,
        productId: String,
        productName: String,
        color: String = "",
        version: String = "",
        quantity: Int = 1,
        price: Double = 0.0
    ) {
        val taskLog = TaskLog(
            id = generateLogId(),
            taskType = "ADD_TO_CART",
            taskDescription = "将「$productName」加入购物车",
            action = "添加到购物车",
            details = mapOf(
                "productId" to productId,
                "productName" to productName,
                "color" to color,
                "version" to version,
                "quantity" to quantity.toString(),
                "price" to price.toString()
            ),
            timestamp = System.currentTimeMillis(),
            completed = true
        )
        saveLog(context, taskLog)
    }
    
    fun logPurchase(
        context: Context,
        productId: String,
        productName: String,
        orderId: String,
        color: String = "",
        version: String = ""
    ) {
        val taskLog = TaskLog(
            id = generateLogId(),
            taskType = "PURCHASE",
            taskDescription = "立即购买「$productName」",
            action = "立即购买",
            details = mapOf(
                "productId" to productId,
                "productName" to productName,
                "orderId" to orderId,
                "color" to color,
                "version" to version
            ),
            timestamp = System.currentTimeMillis(),
            completed = true
        )
        saveLog(context, taskLog)
    }
    
    fun logViewAllOrders(context: Context) {
        val taskLog = TaskLog(
            id = generateLogId(),
            taskType = "VIEW_ORDERS",
            taskDescription = "查看我的全部订单",
            action = "查看全部订单",
            details = mapOf("page" to "全部订单"),
            timestamp = System.currentTimeMillis(),
            completed = true
        )
        saveLog(context, taskLog)
    }
    
    fun logCheckoutCart(
        context: Context,
        selectedItems: List<String>,
        totalAmount: Double
    ) {
        val taskLog = TaskLog(
            id = generateLogId(),
            taskType = "CHECKOUT_CART",
            taskDescription = "购物车结算",
            action = "勾选商品并结算",
            details = mapOf(
                "selectedItems" to selectedItems.joinToString(","),
                "totalAmount" to totalAmount.toString(),
                "itemCount" to selectedItems.size.toString()
            ),
            timestamp = System.currentTimeMillis(),
            completed = true
        )
        saveLog(context, taskLog)
    }
    
    fun logPayOrder(
        context: Context,
        orderId: String,
        paymentMethod: String = ""
    ) {
        val taskLog = TaskLog(
            id = generateLogId(),
            taskType = "PAY_ORDER",
            taskDescription = "支付订单",
            action = "结算待付款订单",
            details = mapOf(
                "orderId" to orderId,
                "paymentMethod" to paymentMethod
            ),
            timestamp = System.currentTimeMillis(),
            completed = true
        )
        saveLog(context, taskLog)
    }
    
    fun logSendMessage(
        context: Context,
        storeName: String,
        message: String
    ) {
        val taskLog = TaskLog(
            id = generateLogId(),
            taskType = "SEND_MESSAGE",
            taskDescription = "给「$storeName」发消息",
            action = "发送店铺消息",
            details = mapOf(
                "storeName" to storeName,
                "message" to message
            ),
            timestamp = System.currentTimeMillis(),
            completed = true
        )
        saveLog(context, taskLog)
    }
    
    fun logCountProducts(
        context: Context,
        productCount: Int,
        location: String = "首页"
    ) {
        val taskLog = TaskLog(
            id = generateLogId(),
            taskType = "COUNT_PRODUCTS",
            taskDescription = "计算${location}商品数量",
            action = "统计商品数量",
            details = mapOf(
                "location" to location,
                "productCount" to productCount.toString()
            ),
            timestamp = System.currentTimeMillis(),
            completed = true
        )
        saveLog(context, taskLog)
    }
    
    fun logCalculatePrice(
        context: Context,
        totalPrice: Double,
        location: String = "购物车"
    ) {
        val taskLog = TaskLog(
            id = generateLogId(),
            taskType = "CALCULATE_PRICE",
            taskDescription = "计算${location}总价",
            action = "计算价格",
            details = mapOf(
                "location" to location,
                "totalPrice" to totalPrice.toString()
            ),
            timestamp = System.currentTimeMillis(),
            completed = true
        )
        saveLog(context, taskLog)
    }
    
    fun logCountOrders(
        context: Context,
        orderCount: Int,
        orderType: String = "待收货"
    ) {
        val taskLog = TaskLog(
            id = generateLogId(),
            taskType = "COUNT_ORDERS",
            taskDescription = "计算${orderType}订单数量",
            action = "统计订单数量",
            details = mapOf(
                "orderType" to orderType,
                "orderCount" to orderCount.toString()
            ),
            timestamp = System.currentTimeMillis(),
            completed = true
        )
        saveLog(context, taskLog)
    }
    
    fun logCountMessages(
        context: Context,
        messageCount: Int,
        messageType: String = "京东客服消息"
    ) {
        val taskLog = TaskLog(
            id = generateLogId(),
            taskType = "COUNT_MESSAGES",
            taskDescription = "计算${messageType}数量",
            action = "统计消息数量",
            details = mapOf(
                "messageType" to messageType,
                "messageCount" to messageCount.toString()
            ),
            timestamp = System.currentTimeMillis(),
            completed = true
        )
        saveLog(context, taskLog)
    }
    
    fun logCheckDelivery(
        context: Context,
        deliveryService: String = "京东秒送",
        estimatedTime: String = ""
    ) {
        val taskLog = TaskLog(
            id = generateLogId(),
            taskType = "CHECK_DELIVERY",
            taskDescription = "查看${deliveryService}物流信息",
            action = "查看物流状态",
            details = mapOf(
                "deliveryService" to deliveryService,
                "estimatedTime" to estimatedTime
            ),
            timestamp = System.currentTimeMillis(),
            completed = true
        )
        saveLog(context, taskLog)
    }
    
    fun logCountComments(
        context: Context,
        productName: String,
        commentCount: Int
    ) {
        val taskLog = TaskLog(
            id = generateLogId(),
            taskType = "COUNT_COMMENTS",
            taskDescription = "计算「$productName」评论数量",
            action = "统计评论数量",
            details = mapOf(
                "productName" to productName,
                "commentCount" to commentCount.toString()
            ),
            timestamp = System.currentTimeMillis(),
            completed = true
        )
        saveLog(context, taskLog)
    }
    
    fun logComplexPurchase(
        context: Context,
        searchKeyword: String,
        priceRange: String,
        productName: String,
        paymentMethod: String,
        couponUsed: String
    ) {
        val taskLog = TaskLog(
            id = generateLogId(),
            taskType = "COMPLEX_PURCHASE",
            taskDescription = "搜索筛选并购买「$productName」",
            action = "复杂购买流程",
            details = mapOf(
                "searchKeyword" to searchKeyword,
                "priceRange" to priceRange,
                "productName" to productName,
                "paymentMethod" to paymentMethod,
                "couponUsed" to couponUsed
            ),
            timestamp = System.currentTimeMillis(),
            completed = true
        )
        saveLog(context, taskLog)
    }
    
    fun logStoreNavigation(
        context: Context,
        productName: String,
        storeName: String,
        purchasedProduct: String
    ) {
        val taskLog = TaskLog(
            id = generateLogId(),
            taskType = "STORE_NAVIGATION",
            taskDescription = "进入商品详情和店铺页面",
            action = "店铺导航和购买",
            details = mapOf(
                "originalProduct" to productName,
                "storeName" to storeName,
                "purchasedProduct" to purchasedProduct
            ),
            timestamp = System.currentTimeMillis(),
            completed = true
        )
        saveLog(context, taskLog)
    }
    
    fun logCancelOrders(
        context: Context,
        cancelledOrderIds: List<String>
    ) {
        val taskLog = TaskLog(
            id = generateLogId(),
            taskType = "CANCEL_ORDERS",
            taskDescription = "取消待付款订单",
            action = "取消和删除订单",
            details = mapOf(
                "cancelledOrderIds" to cancelledOrderIds.joinToString(","),
                "orderCount" to cancelledOrderIds.size.toString()
            ),
            timestamp = System.currentTimeMillis(),
            completed = true
        )
        saveLog(context, taskLog)
    }
    
    fun logAddAddress(
        context: Context,
        address: String,
        isDefault: Boolean = false
    ) {
        val taskLog = TaskLog(
            id = generateLogId(),
            taskType = "ADD_ADDRESS",
            taskDescription = "新建地址「$address」",
            action = "添加新地址",
            details = mapOf(
                "address" to address,
                "isDefault" to isDefault.toString()
            ),
            timestamp = System.currentTimeMillis(),
            completed = true
        )
        saveLog(context, taskLog)
    }
    
    fun logSetMessageMute(
        context: Context,
        storeName: String
    ) {
        val taskLog = TaskLog(
            id = generateLogId(),
            taskType = "SET_MESSAGE_MUTE",
            taskDescription = "设置「$storeName」消息免打扰",
            action = "设置消息免打扰",
            details = mapOf(
                "storeName" to storeName
            ),
            timestamp = System.currentTimeMillis(),
            completed = true
        )
        saveLog(context, taskLog)
    }
    
    private fun saveLog(context: Context, taskLog: TaskLog) {
        try {
            // 主要保存位置：应用的Documents目录，更易访问
            val documentsDir = File(context.getExternalFilesDir(null)?.parentFile?.parentFile, "Documents/MyJD")
            if (!documentsDir.exists()) {
                documentsDir.mkdirs()
                Log.d(TAG, "Created Documents directory: ${documentsDir.absolutePath}")
            }
            val primaryLogsFile = File(documentsDir, TASK_LOGS_FILE)
            
            // 备用保存位置：应用内部目录
            val backupDir = File(context.getExternalFilesDir(null), "logs")
            if (!backupDir.exists()) {
                backupDir.mkdirs()
            }
            val backupLogsFile = File(backupDir, TASK_LOGS_FILE)
            
            Log.d(TAG, "Saving log to primary: ${primaryLogsFile.absolutePath}")
            Log.d(TAG, "Backup location: ${backupLogsFile.absolutePath}")
            
            // 读取现有日志
            val existingLogs = loadExistingLogs(primaryLogsFile, backupLogsFile)
            
            // 添加新日志
            existingLogs.add(taskLog)
            val updatedJson = gson.toJson(existingLogs)
            
            // 同时保存到两个位置
            try {
                primaryLogsFile.writeText(updatedJson)
                Log.d(TAG, "Successfully saved to primary location")
            } catch (e: Exception) {
                Log.w(TAG, "Failed to save to primary location, using backup", e)
            }
            
            try {
                backupLogsFile.writeText(updatedJson)
                Log.d(TAG, "Successfully saved to backup location")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save to backup location", e)
            }
            
            // 同步到assets同级目录（方便查看）
            syncToAssetsDir(context, updatedJson)
            
            Log.d(TAG, "Successfully saved log: ${taskLog.taskType} - ${taskLog.taskDescription}")
            Log.d(TAG, "Total logs now: ${existingLogs.size}")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error saving log", e)
            e.printStackTrace()
        }
    }
    
    private fun loadExistingLogs(primaryFile: File, backupFile: File): MutableList<TaskLog> {
        // 优先从主文件读取，失败则从备用文件读取
        val filesToTry = listOf(primaryFile, backupFile)
        
        for (file in filesToTry) {
            if (file.exists()) {
                try {
                    val jsonContent = file.readText()
                    if (jsonContent.isNotBlank()) {
                        val listType = object : TypeToken<MutableList<TaskLog>>() {}.type
                        val logs = gson.fromJson<MutableList<TaskLog>>(jsonContent, listType)
                        if (logs != null) {
                            Log.d(TAG, "Loaded ${logs.size} existing logs from ${file.name}")
                            return logs
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Error reading from ${file.name}", e)
                }
            }
        }
        
        Log.d(TAG, "No existing logs found, starting fresh")
        return mutableListOf()
    }
    
    private fun syncToAssetsDir(context: Context, jsonContent: String) {
        try {
            // 尝试保存到项目assets目录附近（如果有权限）
            val projectDir = File("/storage/emulated/0/MyJD_Logs")
            if (!projectDir.exists()) {
                projectDir.mkdirs()
            }
            val assetsFile = File(projectDir, TASK_LOGS_FILE)
            assetsFile.writeText(jsonContent)
            Log.d(TAG, "Synced to accessible location: ${assetsFile.absolutePath}")
        } catch (e: Exception) {
            Log.w(TAG, "Could not sync to external accessible location", e)
        }
    }
    
    private fun generateLogId(): String {
        val timestamp = System.currentTimeMillis()
        val random = (1000..9999).random()
        return "log_${timestamp}_$random"
    }
    
    fun getAllLogs(context: Context): List<TaskLog> {
        return try {
            // 尝试从主要位置读取
            val documentsDir = File(context.getExternalFilesDir(null)?.parentFile?.parentFile, "Documents/MyJD")
            val primaryLogsFile = File(documentsDir, TASK_LOGS_FILE)
            
            // 备用位置
            val backupDir = File(context.getExternalFilesDir(null), "logs")
            val backupLogsFile = File(backupDir, TASK_LOGS_FILE)
            
            val logs = loadExistingLogs(primaryLogsFile, backupLogsFile)
            Log.d(TAG, "getAllLogs returning ${logs.size} logs")
            logs
        } catch (e: Exception) {
            Log.e(TAG, "Error getting all logs", e)
            e.printStackTrace()
            emptyList()
        }
    }
    
    fun clearLogs(context: Context) {
        try {
            // 清除主要位置
            val documentsDir = File(context.getExternalFilesDir(null)?.parentFile?.parentFile, "Documents/MyJD")
            val primaryLogsFile = File(documentsDir, TASK_LOGS_FILE)
            if (primaryLogsFile.exists()) {
                primaryLogsFile.delete()
                Log.d(TAG, "Cleared logs from primary: ${primaryLogsFile.absolutePath}")
            }
            
            // 清除备用位置
            val backupDir = File(context.getExternalFilesDir(null), "logs")
            val backupLogsFile = File(backupDir, TASK_LOGS_FILE)
            if (backupLogsFile.exists()) {
                backupLogsFile.delete()
                Log.d(TAG, "Cleared logs from backup: ${backupLogsFile.absolutePath}")
            }
            
            // 清除外部同步位置
            val projectDir = File("/storage/emulated/0/MyJD_Logs")
            val syncFile = File(projectDir, TASK_LOGS_FILE)
            if (syncFile.exists()) {
                syncFile.delete()
                Log.d(TAG, "Cleared logs from sync location: ${syncFile.absolutePath}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing logs", e)
            e.printStackTrace()
        }
    }
    
    /**
     * Get the absolute paths of all task logs files for debugging purposes
     */
    fun getLogFilePaths(context: Context): List<String> {
        val paths = mutableListOf<String>()
        
        // 主要位置
        val documentsDir = File(context.getExternalFilesDir(null)?.parentFile?.parentFile, "Documents/MyJD")
        val primaryLogsFile = File(documentsDir, TASK_LOGS_FILE)
        paths.add("Primary: ${primaryLogsFile.absolutePath}")
        
        // 备用位置
        val backupDir = File(context.getExternalFilesDir(null), "logs")
        val backupLogsFile = File(backupDir, TASK_LOGS_FILE)
        paths.add("Backup: ${backupLogsFile.absolutePath}")
        
        // 外部同步位置
        val projectDir = File("/storage/emulated/0/MyJD_Logs")
        val syncFile = File(projectDir, TASK_LOGS_FILE)
        paths.add("Sync: ${syncFile.absolutePath}")
        
        return paths
    }
    
    /**
     * Debug method to show current logging status
     */
    fun debugLogging(context: Context) {
        Log.d(TAG, "=== TaskLogger Debug Info ===")
        val paths = getLogFilePaths(context)
        paths.forEach { path ->
            Log.d(TAG, path)
        }
        
        // Check current logs count
        val logs = getAllLogs(context)
        Log.d(TAG, "Current total logs: ${logs.size}")
        
        // Show last few logs
        val recentLogs = logs.takeLast(3)
        recentLogs.forEach { log ->
            Log.d(TAG, "Recent: ${log.taskType} - ${log.taskDescription} at ${log.timestamp}")
        }
        Log.d(TAG, "=========================")
    }
    
    /**
     * Force sync all log locations
     */
    fun forceSyncLogs(context: Context) {
        val logs = getAllLogs(context)
        if (logs.isNotEmpty()) {
            val jsonContent = gson.toJson(logs)
            syncToAssetsDir(context, jsonContent)
            Log.d(TAG, "Force synced ${logs.size} logs to all locations")
        }
    }
    
    /**
     * Initialize logging system and merge existing logs from assets
     */
    fun initializeLogging(context: Context) {
        try {
            Log.d(TAG, "Initializing TaskLogger...")
            
            // 尝试从assets目录读取已有的日志
            val existingLogsFromAssets = loadLogsFromAssets(context)
            val currentLogs = getAllLogs(context).toMutableList()
            
            // 合并日志（避免重复）
            val existingIds = currentLogs.map { it.id }.toSet()
            val newLogsFromAssets = existingLogsFromAssets.filter { it.id !in existingIds }
            
            if (newLogsFromAssets.isNotEmpty()) {
                currentLogs.addAll(newLogsFromAssets)
                
                // 保存合并后的日志
                val mergedJson = gson.toJson(currentLogs)
                syncAllLocations(context, mergedJson)
                
                Log.d(TAG, "Merged ${newLogsFromAssets.size} logs from assets, total now: ${currentLogs.size}")
            } else {
                Log.d(TAG, "No new logs to merge from assets")
            }
            
            debugLogging(context)
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing logging", e)
        }
    }
    
    private fun loadLogsFromAssets(context: Context): List<TaskLog> {
        return try {
            val inputStream = context.assets.open("data/task_logs.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<TaskLog>>() {}.type
            gson.fromJson<List<TaskLog>>(jsonString, listType) ?: emptyList()
        } catch (e: Exception) {
            Log.d(TAG, "Could not load logs from assets: ${e.message}")
            emptyList()
        }
    }
    
    private fun syncAllLocations(context: Context, jsonContent: String) {
        // 主要位置
        val documentsDir = File(context.getExternalFilesDir(null)?.parentFile?.parentFile, "Documents/MyJD")
        if (!documentsDir.exists()) documentsDir.mkdirs()
        val primaryFile = File(documentsDir, TASK_LOGS_FILE)
        
        // 备用位置
        val backupDir = File(context.getExternalFilesDir(null), "logs")
        if (!backupDir.exists()) backupDir.mkdirs()
        val backupFile = File(backupDir, TASK_LOGS_FILE)
        
        try {
            primaryFile.writeText(jsonContent)
            backupFile.writeText(jsonContent)
            syncToAssetsDir(context, jsonContent)
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing to all locations", e)
        }
    }
}