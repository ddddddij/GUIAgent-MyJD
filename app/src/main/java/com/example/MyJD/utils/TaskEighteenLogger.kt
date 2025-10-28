package com.example.MyJD.utils

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

object TaskEighteenLogger {
    private const val TAG = "TaskEighteenLogger"
    private const val LOG_FILE_NAME = "task_eighteen_log.txt"
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
        val message = "[$timestamp] 任务十八开始：取消我所有的待付款订单，然后在全部订单中删除已取消的订单"
        writeToLog(context, message)
    }
    
    fun logOrderPageEntered(context: Context) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 进入订单页面"
        writeToLog(context, message)
    }
    
    fun logPendingPaymentOrdersFound(context: Context, orderCount: Int) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 找到待付款订单：$orderCount 项"
        writeToLog(context, message)
    }
    
    fun logOrderCancelled(context: Context, orderId: String) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 取消订单：$orderId"
        writeToLog(context, message)
    }
    
    fun logAllPendingOrdersCancelled(context: Context, totalCancelled: Int) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 所有待付款订单已取消，共计：$totalCancelled 项"
        writeToLog(context, message)
    }
    
    fun logAllOrdersTabSelected(context: Context) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 切换到全部订单标签页"
        writeToLog(context, message)
    }
    
    fun logCancelledOrdersFound(context: Context, orderCount: Int) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 在全部订单中找到已取消订单：$orderCount 项"
        writeToLog(context, message)
    }
    
    fun logOrderDeleted(context: Context, orderId: String) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 删除已取消订单：$orderId"
        writeToLog(context, message)
    }
    
    fun logAllCancelledOrdersDeleted(context: Context, totalDeleted: Int) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 所有已取消订单已删除，共计：$totalDeleted 项"
        writeToLog(context, message)
    }
    
    fun logTaskCompleted(context: Context, cancelledCount: Int, deletedCount: Int) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 任务十八完成：已取消 $cancelledCount 项待付款订单，并删除了 $deletedCount 项已取消订单"
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
        return logContent.contains("任务十八完成：已取消")
    }
    
    fun getCancelledAndDeletedCounts(context: Context): Pair<Int, Int> {
        val logContent = readLog(context)
        val regex = "任务十八完成：已取消 (\\d+) 项待付款订单，并删除了 (\\d+) 项已取消订单".toRegex()
        val matchResult = regex.find(logContent)
        val cancelledCount = matchResult?.groupValues?.get(1)?.toIntOrNull() ?: 0
        val deletedCount = matchResult?.groupValues?.get(2)?.toIntOrNull() ?: 0
        return Pair(cancelledCount, deletedCount)
    }
}