package com.example.MyJD.utils

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * 任务四日志记录模型
 * 任务四：在「我的」页面查看我的全部订单
 */
data class TaskFourLog(
    val id: String,
    val action: TaskFourAction,
    val details: Map<String, String>,
    val timestamp: Long,
    val sessionId: String = ""
) {
    fun toReadableString(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val timeStr = dateFormat.format(Date(timestamp))
        
        return when (action) {
            TaskFourAction.ME_PAGE_VISITED -> {
                "[$timeStr] 访问「我的」页面"
            }
            TaskFourAction.ORDERS_SECTION_FOUND -> {
                "[$timeStr] 在「我的」页面找到订单管理区域"
            }
            TaskFourAction.ALL_ORDERS_CLICKED -> {
                "[$timeStr] 点击「全部订单」入口"
            }
            TaskFourAction.ORDER_LIST_LOADED -> {
                "[$timeStr] 订单列表加载完成: 共${details["orderCount"]}个订单"
            }
            TaskFourAction.ORDER_DETAILS_VIEWED -> {
                "[$timeStr] 查看订单详情: 订单ID='${details["orderId"]}', 状态='${details["status"]}'"
            }
            TaskFourAction.TASK_COMPLETED -> {
                "[$timeStr] 任务完成: 成功查看全部订单，共${details["totalOrders"]}个"
            }
        }
    }
}

/**
 * 任务四操作类型枚举
 */
enum class TaskFourAction {
    ME_PAGE_VISITED,        // 访问「我的」页面
    ORDERS_SECTION_FOUND,   // 找到订单管理区域
    ALL_ORDERS_CLICKED,     // 点击「全部订单」
    ORDER_LIST_LOADED,      // 订单列表加载完成
    ORDER_DETAILS_VIEWED,   // 查看订单详情
    TASK_COMPLETED          // 任务完成
}

/**
 * 任务四专用日志记录器
 */
object TaskFourLogger {
    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .create()
    private const val TASK_FOUR_LOG_FILE = "task_four_logs.json"
    private const val TAG = "TaskFourLogger"
    
    /**
     * 记录访问「我的」页面
     */
    fun logMePageVisited(context: Context, sessionId: String = generateSessionId()) {
        val logEntry = TaskFourLog(
            id = generateLogId(),
            action = TaskFourAction.ME_PAGE_VISITED,
            details = mapOf(
                "visitTime" to System.currentTimeMillis().toString()
            ),
            timestamp = System.currentTimeMillis(),
            sessionId = sessionId
        )
        saveLog(context, logEntry)
        Log.d(TAG, "Me page visited")
    }
    
    /**
     * 记录找到订单管理区域
     */
    fun logOrdersSectionFound(context: Context, sessionId: String = generateSessionId()) {
        val logEntry = TaskFourLog(
            id = generateLogId(),
            action = TaskFourAction.ORDERS_SECTION_FOUND,
            details = mapOf(
                "foundTime" to System.currentTimeMillis().toString()
            ),
            timestamp = System.currentTimeMillis(),
            sessionId = sessionId
        )
        saveLog(context, logEntry)
        Log.d(TAG, "Orders section found on Me page")
    }
    
    /**
     * 记录点击「全部订单」
     */
    fun logAllOrdersClicked(context: Context, sessionId: String = generateSessionId()) {
        val logEntry = TaskFourLog(
            id = generateLogId(),
            action = TaskFourAction.ALL_ORDERS_CLICKED,
            details = mapOf(
                "clickTime" to System.currentTimeMillis().toString()
            ),
            timestamp = System.currentTimeMillis(),
            sessionId = sessionId
        )
        saveLog(context, logEntry)
        Log.d(TAG, "All orders clicked")
    }
    
    /**
     * 记录订单列表加载完成
     */
    fun logOrderListLoaded(context: Context, orderCount: Int, sessionId: String = generateSessionId()) {
        val logEntry = TaskFourLog(
            id = generateLogId(),
            action = TaskFourAction.ORDER_LIST_LOADED,
            details = mapOf(
                "orderCount" to orderCount.toString(),
                "loadTime" to System.currentTimeMillis().toString()
            ),
            timestamp = System.currentTimeMillis(),
            sessionId = sessionId
        )
        saveLog(context, logEntry)
        Log.d(TAG, "Order list loaded: $orderCount orders")
    }
    
    /**
     * 记录查看订单详情
     */
    fun logOrderDetailsViewed(context: Context, orderId: String, status: String, sessionId: String = generateSessionId()) {
        val logEntry = TaskFourLog(
            id = generateLogId(),
            action = TaskFourAction.ORDER_DETAILS_VIEWED,
            details = mapOf(
                "orderId" to orderId,
                "status" to status,
                "viewTime" to System.currentTimeMillis().toString()
            ),
            timestamp = System.currentTimeMillis(),
            sessionId = sessionId
        )
        saveLog(context, logEntry)
        Log.d(TAG, "Order details viewed: $orderId, status: $status")
    }
    
    /**
     * 记录任务完成
     */
    fun logTaskCompleted(context: Context, totalOrders: Int, sessionId: String = generateSessionId()) {
        val logEntry = TaskFourLog(
            id = generateLogId(),
            action = TaskFourAction.TASK_COMPLETED,
            details = mapOf(
                "totalOrders" to totalOrders.toString(),
                "completionTime" to System.currentTimeMillis().toString(),
                "success" to "true"
            ),
            timestamp = System.currentTimeMillis(),
            sessionId = sessionId
        )
        saveLog(context, logEntry)
        Log.d(TAG, "Task completed: viewed all orders, total: $totalOrders")
    }
    
    /**
     * 保存日志到持久化存储
     */
    private fun saveLog(context: Context, taskLog: TaskFourLog) {
        try {
            // 使用应用内部存储确保可靠性
            val dataDir = File(context.filesDir, "persistent_data")
            if (!dataDir.exists()) {
                dataDir.mkdirs()
                Log.d(TAG, "Created persistent_data directory: ${dataDir.absolutePath}")
            }
            
            val logFile = File(dataDir, TASK_FOUR_LOG_FILE)
            Log.d(TAG, "Saving task four log to: ${logFile.absolutePath}")
            
            // 读取现有日志
            val existingLogs = loadExistingLogs(logFile)
            
            // 添加新日志
            existingLogs.add(taskLog)
            val updatedJson = gson.toJson(existingLogs)
            
            // 保存到文件
            logFile.writeText(updatedJson)
            Log.d(TAG, "Successfully saved task four log: ${taskLog.action}")
            Log.d(TAG, "Total task four logs: ${existingLogs.size}")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error saving task four log", e)
            e.printStackTrace()
        }
    }
    
    /**
     * 加载现有日志
     */
    private fun loadExistingLogs(logFile: File): MutableList<TaskFourLog> {
        return if (logFile.exists()) {
            try {
                val jsonContent = logFile.readText()
                if (jsonContent.isNotBlank()) {
                    val listType = object : TypeToken<MutableList<TaskFourLog>>() {}.type
                    val logs = gson.fromJson<MutableList<TaskFourLog>>(jsonContent, listType)
                    if (logs != null) {
                        Log.d(TAG, "Loaded ${logs.size} existing task four logs")
                        logs
                    } else {
                        mutableListOf()
                    }
                } else {
                    mutableListOf()
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error reading task four logs", e)
                mutableListOf()
            }
        } else {
            Log.d(TAG, "No existing task four logs found, starting fresh")
            mutableListOf()
        }
    }
    
    /**
     * 获取所有任务四日志
     */
    fun getAllLogs(context: Context): List<TaskFourLog> {
        return try {
            val dataDir = File(context.filesDir, "persistent_data")
            val logFile = File(dataDir, TASK_FOUR_LOG_FILE)
            val logs = loadExistingLogs(logFile)
            Log.d(TAG, "Retrieved ${logs.size} task four logs")
            logs
        } catch (e: Exception) {
            Log.e(TAG, "Error getting all task four logs", e)
            emptyList()
        }
    }
    
    /**
     * 清除所有任务四日志
     */
    fun clearLogs(context: Context) {
        try {
            val dataDir = File(context.filesDir, "persistent_data")
            val logFile = File(dataDir, TASK_FOUR_LOG_FILE)
            if (logFile.exists()) {
                logFile.delete()
                Log.d(TAG, "Cleared all task four logs")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing task four logs", e)
        }
    }
    
    /**
     * 检查任务四是否完成
     */
    fun isTaskFourCompleted(context: Context): Boolean {
        val logs = getAllLogs(context)
        
        // 检查是否有访问「我的」页面的记录
        val hasMePageVisit = logs.any { log ->
            log.action == TaskFourAction.ME_PAGE_VISITED
        }
        
        // 检查是否有找到订单管理区域的记录
        val hasOrdersSectionFound = logs.any { log ->
            log.action == TaskFourAction.ORDERS_SECTION_FOUND
        }
        
        // 检查是否有点击「全部订单」的记录
        val hasAllOrdersClicked = logs.any { log ->
            log.action == TaskFourAction.ALL_ORDERS_CLICKED
        }
        
        // 检查是否有订单列表加载完成的记录
        val hasOrderListLoaded = logs.any { log ->
            log.action == TaskFourAction.ORDER_LIST_LOADED
        }
        
        // 检查是否有任务完成记录
        val hasTaskCompletion = logs.any { log ->
            log.action == TaskFourAction.TASK_COMPLETED
        }
        
        val isCompleted = hasMePageVisit && hasOrdersSectionFound && hasAllOrdersClicked && 
                         hasOrderListLoaded && hasTaskCompletion
        
        Log.d(TAG, "Task four completion check: mePage=$hasMePageVisit, ordersSection=$hasOrdersSectionFound, " +
                  "allOrders=$hasAllOrdersClicked, orderList=$hasOrderListLoaded, " +
                  "completion=$hasTaskCompletion, result=$isCompleted")
        
        return isCompleted
    }
    
    /**
     * 获取任务四完成状态详情
     */
    fun getTaskFourCompletionDetails(context: Context): Map<String, Any> {
        val logs = getAllLogs(context)
        
        val mePageVisitLogs = logs.filter { it.action == TaskFourAction.ME_PAGE_VISITED }
        val ordersSectionLogs = logs.filter { it.action == TaskFourAction.ORDERS_SECTION_FOUND }
        val allOrdersClickLogs = logs.filter { it.action == TaskFourAction.ALL_ORDERS_CLICKED }
        val orderListLoadLogs = logs.filter { it.action == TaskFourAction.ORDER_LIST_LOADED }
        val orderDetailsViewLogs = logs.filter { it.action == TaskFourAction.ORDER_DETAILS_VIEWED }
        val completionLogs = logs.filter { it.action == TaskFourAction.TASK_COMPLETED }
        
        return mapOf(
            "totalLogs" to logs.size,
            "mePageVisitCount" to mePageVisitLogs.size,
            "ordersSectionCount" to ordersSectionLogs.size,
            "allOrdersClickCount" to allOrdersClickLogs.size,
            "orderListLoadCount" to orderListLoadLogs.size,
            "orderDetailsViewCount" to orderDetailsViewLogs.size,
            "completionCount" to completionLogs.size,
            "isCompleted" to isTaskFourCompleted(context),
            "lastViewedOrderCount" to (orderListLoadLogs.lastOrNull()?.details?.get("orderCount") ?: "0"),
            "lastViewedOrderId" to (orderDetailsViewLogs.lastOrNull()?.details?.get("orderId") ?: ""),
            "logs" to logs.map { it.toReadableString() }
        )
    }
    
    /**
     * 生成日志ID
     */
    private fun generateLogId(): String {
        val timestamp = System.currentTimeMillis()
        val random = (1000..9999).random()
        return "task4_log_${timestamp}_$random"
    }
    
    /**
     * 生成会话ID
     */
    private fun generateSessionId(): String {
        val timestamp = System.currentTimeMillis()
        val random = (10000..99999).random()
        return "task4_session_${timestamp}_$random"
    }
    
    /**
     * 获取日志文件路径（用于调试）
     */
    fun getLogFilePath(context: Context): String {
        val dataDir = File(context.filesDir, "persistent_data")
        val logFile = File(dataDir, TASK_FOUR_LOG_FILE)
        return logFile.absolutePath
    }
}