package com.example.MyJD.utils

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

object TaskEightLogger {
    private const val TAG = "TaskEightLogger"
    private const val LOG_FILE_NAME = "task_eight_log.txt"
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    
    private fun getLogFile(context: Context): File {
        val dataDir = File("/data/data/com.example.MyJD/files/persistent_data")
        if (!dataDir.exists()) {
            dataDir.mkdirs()
        }
        return File(dataDir, LOG_FILE_NAME)
    }
    
    fun logTaskStart(context: Context) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 任务八开始：计算首页共展示了多少件商品"
        writeToLog(context, message)
    }
    
    fun logHomePageEntered(context: Context) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 进入首页"
        writeToLog(context, message)
    }
    
    fun logProductsLoaded(context: Context, productCount: Int) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 首页商品加载完成，共计：$productCount 件商品"
        writeToLog(context, message)
    }
    
    fun logTaskCompleted(context: Context, totalProducts: Int) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 任务八完成：首页共展示了 $totalProducts 件商品"
        writeToLog(context, message)
    }
    
    fun logError(context: Context, error: String) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 错误: $error"
        writeToLog(context, message)
    }
    
    private fun writeToLog(context: Context, message: String) {
        try {
            val logFile = getLogFile(context)
            FileWriter(logFile, true).use { writer ->
                writer.appendLine(message)
            }
            Log.d(TAG, message)
        } catch (e: Exception) {
            Log.e(TAG, "写入日志文件失败", e)
        }
    }
    
    fun readLog(context: Context): String {
        return try {
            val logFile = getLogFile(context)
            if (logFile.exists()) {
                logFile.readText()
            } else {
                "日志文件不存在"
            }
        } catch (e: Exception) {
            Log.e(TAG, "读取日志文件失败", e)
            "读取日志文件失败: ${e.message}"
        }
    }
    
    fun clearLog(context: Context) {
        try {
            val logFile = getLogFile(context)
            if (logFile.exists()) {
                logFile.delete()
            }
            Log.d(TAG, "日志文件已清除")
        } catch (e: Exception) {
            Log.e(TAG, "清除日志文件失败", e)
        }
    }
    
    fun isTaskCompleted(context: Context): Boolean {
        val logContent = readLog(context)
        return logContent.contains("任务八完成：首页共展示了")
    }
    
    fun getProductCount(context: Context): Int {
        val logContent = readLog(context)
        val regex = "任务八完成：首页共展示了 (\\d+) 件商品".toRegex()
        val matchResult = regex.find(logContent)
        return matchResult?.groupValues?.get(1)?.toIntOrNull() ?: 0
    }
}