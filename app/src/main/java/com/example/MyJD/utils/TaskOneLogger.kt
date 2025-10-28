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
 * 任务一日志记录模型
 * 任务一：在首页中搜索「iPhone 15」，并查看搜索结果的第一个商品
 */
data class TaskOneLog(
    val id: String,
    val action: TaskOneAction,
    val details: Map<String, String>,
    val timestamp: Long,
    val sessionId: String = ""
) {
    fun toReadableString(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val timeStr = dateFormat.format(Date(timestamp))
        
        return when (action) {
            TaskOneAction.SEARCH_INITIATED -> {
                "[$timeStr] 开始搜索: 关键词='${details["keyword"]}'"
            }
            TaskOneAction.SEARCH_RESULTS_LOADED -> {
                "[$timeStr] 搜索结果加载: 找到${details["resultCount"]}个商品"
            }
            TaskOneAction.FIRST_PRODUCT_VIEWED -> {
                "[$timeStr] 查看第一个商品: '${details["productName"]}' (ID: ${details["productId"]})"
            }
            TaskOneAction.TASK_COMPLETED -> {
                "[$timeStr] 任务完成: 搜索关键词='${details["searchKeyword"]}', 查看商品='${details["viewedProductName"]}'"
            }
        }
    }
}

/**
 * 任务一操作类型枚举
 */
enum class TaskOneAction {
    SEARCH_INITIATED,       // 发起搜索
    SEARCH_RESULTS_LOADED,  // 搜索结果加载完成
    FIRST_PRODUCT_VIEWED,   // 查看第一个商品
    TASK_COMPLETED          // 任务完成
}

/**
 * 任务一专用日志记录器
 */
object TaskOneLogger {
    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .create()
    private const val TASK_ONE_LOG_FILE = "task_one_logs.json"
    private const val TAG = "TaskOneLogger"
    
    /**
     * 记录搜索发起
     */
    fun logSearchInitiated(context: Context, keyword: String, sessionId: String = generateSessionId()) {
        val logEntry = TaskOneLog(
            id = generateLogId(),
            action = TaskOneAction.SEARCH_INITIATED,
            details = mapOf(
                "keyword" to keyword,
                "searchTime" to System.currentTimeMillis().toString()
            ),
            timestamp = System.currentTimeMillis(),
            sessionId = sessionId
        )
        saveLog(context, logEntry)
        Log.d(TAG, "Search initiated: $keyword")
    }
    
    /**
     * 记录搜索结果加载
     */
    fun logSearchResultsLoaded(context: Context, keyword: String, resultCount: Int, sessionId: String = generateSessionId()) {
        val logEntry = TaskOneLog(
            id = generateLogId(),
            action = TaskOneAction.SEARCH_RESULTS_LOADED,
            details = mapOf(
                "keyword" to keyword,
                "resultCount" to resultCount.toString(),
                "loadTime" to System.currentTimeMillis().toString()
            ),
            timestamp = System.currentTimeMillis(),
            sessionId = sessionId
        )
        saveLog(context, logEntry)
        Log.d(TAG, "Search results loaded: $resultCount results for '$keyword'")
    }
    
    /**
     * 记录查看第一个商品
     */
    fun logFirstProductViewed(context: Context, productId: String, productName: String, sessionId: String = generateSessionId()) {
        val logEntry = TaskOneLog(
            id = generateLogId(),
            action = TaskOneAction.FIRST_PRODUCT_VIEWED,
            details = mapOf(
                "productId" to productId,
                "productName" to productName,
                "viewTime" to System.currentTimeMillis().toString()
            ),
            timestamp = System.currentTimeMillis(),
            sessionId = sessionId
        )
        saveLog(context, logEntry)
        Log.d(TAG, "First product viewed: $productName (ID: $productId)")
    }
    
    /**
     * 记录任务完成
     */
    fun logTaskCompleted(context: Context, searchKeyword: String, viewedProductName: String, sessionId: String = generateSessionId()) {
        val logEntry = TaskOneLog(
            id = generateLogId(),
            action = TaskOneAction.TASK_COMPLETED,
            details = mapOf(
                "searchKeyword" to searchKeyword,
                "viewedProductName" to viewedProductName,
                "completionTime" to System.currentTimeMillis().toString(),
                "success" to "true"
            ),
            timestamp = System.currentTimeMillis(),
            sessionId = sessionId
        )
        saveLog(context, logEntry)
        Log.d(TAG, "Task completed: searched '$searchKeyword', viewed '$viewedProductName'")
    }
    
    /**
     * 保存日志到持久化存储
     */
    private fun saveLog(context: Context, taskLog: TaskOneLog) {
        try {
            // 使用应用内部存储确保可靠性
            val dataDir = File(context.filesDir, "persistent_data")
            if (!dataDir.exists()) {
                dataDir.mkdirs()
                Log.d(TAG, "Created persistent_data directory: ${dataDir.absolutePath}")
            }
            
            val logFile = File(dataDir, TASK_ONE_LOG_FILE)
            Log.d(TAG, "Saving task one log to: ${logFile.absolutePath}")
            
            // 读取现有日志
            val existingLogs = loadExistingLogs(logFile)
            
            // 添加新日志
            existingLogs.add(taskLog)
            val updatedJson = gson.toJson(existingLogs)
            
            // 保存到文件
            logFile.writeText(updatedJson)
            Log.d(TAG, "Successfully saved task one log: ${taskLog.action}")
            Log.d(TAG, "Total task one logs: ${existingLogs.size}")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error saving task one log", e)
            e.printStackTrace()
        }
    }
    
    /**
     * 加载现有日志
     */
    private fun loadExistingLogs(logFile: File): MutableList<TaskOneLog> {
        return if (logFile.exists()) {
            try {
                val jsonContent = logFile.readText()
                if (jsonContent.isNotBlank()) {
                    val listType = object : TypeToken<MutableList<TaskOneLog>>() {}.type
                    val logs = gson.fromJson<MutableList<TaskOneLog>>(jsonContent, listType)
                    if (logs != null) {
                        Log.d(TAG, "Loaded ${logs.size} existing task one logs")
                        logs
                    } else {
                        mutableListOf()
                    }
                } else {
                    mutableListOf()
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error reading task one logs", e)
                mutableListOf()
            }
        } else {
            Log.d(TAG, "No existing task one logs found, starting fresh")
            mutableListOf()
        }
    }
    
    /**
     * 获取所有任务一日志
     */
    fun getAllLogs(context: Context): List<TaskOneLog> {
        return try {
            val dataDir = File(context.filesDir, "persistent_data")
            val logFile = File(dataDir, TASK_ONE_LOG_FILE)
            val logs = loadExistingLogs(logFile)
            Log.d(TAG, "Retrieved ${logs.size} task one logs")
            logs
        } catch (e: Exception) {
            Log.e(TAG, "Error getting all task one logs", e)
            emptyList()
        }
    }
    
    /**
     * 清除所有任务一日志
     */
    fun clearLogs(context: Context) {
        try {
            val dataDir = File(context.filesDir, "persistent_data")
            val logFile = File(dataDir, TASK_ONE_LOG_FILE)
            if (logFile.exists()) {
                logFile.delete()
                Log.d(TAG, "Cleared all task one logs")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing task one logs", e)
        }
    }
    
    /**
     * 检查任务一是否完成
     */
    fun isTaskOneCompleted(context: Context): Boolean {
        val logs = getAllLogs(context)
        
        // 检查是否有搜索iPhone 15的记录
        val hasiPhone15Search = logs.any { log ->
            log.action == TaskOneAction.SEARCH_INITIATED && 
            log.details["keyword"]?.contains("iPhone 15", ignoreCase = true) == true
        }
        
        // 检查是否有查看第一个商品的记录
        val hasFirstProductView = logs.any { log ->
            log.action == TaskOneAction.FIRST_PRODUCT_VIEWED
        }
        
        // 检查是否有任务完成记录
        val hasTaskCompletion = logs.any { log ->
            log.action == TaskOneAction.TASK_COMPLETED
        }
        
        val isCompleted = hasiPhone15Search && hasFirstProductView && hasTaskCompletion
        
        Log.d(TAG, "Task one completion check: search=$hasiPhone15Search, view=$hasFirstProductView, completion=$hasTaskCompletion, result=$isCompleted")
        
        return isCompleted
    }
    
    /**
     * 获取任务一完成状态详情
     */
    fun getTaskOneCompletionDetails(context: Context): Map<String, Any> {
        val logs = getAllLogs(context)
        
        val searchLogs = logs.filter { it.action == TaskOneAction.SEARCH_INITIATED }
        val resultLogs = logs.filter { it.action == TaskOneAction.SEARCH_RESULTS_LOADED }
        val viewLogs = logs.filter { it.action == TaskOneAction.FIRST_PRODUCT_VIEWED }
        val completionLogs = logs.filter { it.action == TaskOneAction.TASK_COMPLETED }
        
        return mapOf(
            "totalLogs" to logs.size,
            "searchCount" to searchLogs.size,
            "resultCount" to resultLogs.size,
            "viewCount" to viewLogs.size,
            "completionCount" to completionLogs.size,
            "isCompleted" to isTaskOneCompleted(context),
            "lastSearchKeyword" to (searchLogs.lastOrNull()?.details?.get("keyword") ?: ""),
            "lastViewedProduct" to (viewLogs.lastOrNull()?.details?.get("productName") ?: ""),
            "logs" to logs.map { it.toReadableString() }
        )
    }
    
    /**
     * 生成日志ID
     */
    private fun generateLogId(): String {
        val timestamp = System.currentTimeMillis()
        val random = (1000..9999).random()
        return "task1_log_${timestamp}_$random"
    }
    
    /**
     * 生成会话ID
     */
    private fun generateSessionId(): String {
        val timestamp = System.currentTimeMillis()
        val random = (10000..99999).random()
        return "task1_session_${timestamp}_$random"
    }
    
    /**
     * 获取日志文件路径（用于调试）
     */
    fun getLogFilePath(context: Context): String {
        val dataDir = File(context.filesDir, "persistent_data")
        val logFile = File(dataDir, TASK_ONE_LOG_FILE)
        return logFile.absolutePath
    }
}