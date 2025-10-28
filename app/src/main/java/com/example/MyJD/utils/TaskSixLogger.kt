package com.example.MyJD.utils

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

object TaskSixLogger {
    private const val TAG = "TaskSixLogger"
    private const val LOG_FILE_NAME = "task_six_log.txt"
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
        val message = "[$timestamp] 任务六开始：结算我的第一个待付款订单"
        writeToLog(context, message)
    }
    
    fun logOrderFound(context: Context, orderId: String) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 找到第一个待付款订单，订单ID: $orderId"
        writeToLog(context, message)
    }
    
    fun logNavigateToOrder(context: Context, orderId: String) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 导航到订单详情页面，订单ID: $orderId"
        writeToLog(context, message)
    }
    
    fun logClickPayButton(context: Context) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 点击付款按钮"
        writeToLog(context, message)
    }
    
    fun logPaymentPageDisplayed(context: Context) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 付款页面显示"
        writeToLog(context, message)
    }
    
    fun logConfirmPayment(context: Context) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 确认付款操作"
        writeToLog(context, message)
    }
    
    fun logPaymentSuccess(context: Context, orderId: String) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 付款成功，订单ID: $orderId"
        writeToLog(context, message)
    }
    
    fun logPaymentFailed(context: Context, reason: String) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 付款失败，原因: $reason"
        writeToLog(context, message)
    }
    
    fun logTaskCompleted(context: Context) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 任务六完成：第一个待付款订单已成功结算"
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
        return logContent.contains("任务六完成：第一个待付款订单已成功结算")
    }
}