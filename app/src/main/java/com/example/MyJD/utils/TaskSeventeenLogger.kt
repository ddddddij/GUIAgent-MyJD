package com.example.MyJD.utils

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

object TaskSeventeenLogger {
    private const val TAG = "TaskSeventeenLogger"
    private const val LOG_FILE_NAME = "task_seventeen_log.txt"
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
        val message = "[$timestamp] 任务十七开始：进入首页iPhone15商品详情并进入店铺主页，然后立即购买店铺中\"iPhone 15 粉色 256GB 1件\"后，查看待收货订单"
        writeToLog(context, message)
    }
    
    fun logHomePageEntered(context: Context) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 进入首页"
        writeToLog(context, message)
    }
    
    fun logProductDetailEntered(context: Context, productName: String) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 进入商品详情页：$productName"
        writeToLog(context, message)
    }
    
    fun logShopPageEntered(context: Context, shopName: String) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 进入店铺主页：$shopName"
        writeToLog(context, message)
    }
    
    fun logShopProductSelected(context: Context, productName: String) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 在店铺中选择商品：$productName"
        writeToLog(context, message)
    }
    
    fun logProductSpecSelected(context: Context, color: String, storage: String, quantity: Int) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 选择商品规格：$color $storage $quantity 件"
        writeToLog(context, message)
    }
    
    fun logBuyNowClicked(context: Context, productName: String) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 点击立即购买：$productName"
        writeToLog(context, message)
    }
    
    fun logOrderCreated(context: Context, orderId: String) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 创建订单：$orderId"
        writeToLog(context, message)
    }
    
    fun logPaymentCompleted(context: Context, orderId: String) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 支付完成，订单ID：$orderId"
        writeToLog(context, message)
    }
    
    fun logPendingReceiptOrdersViewed(context: Context, orderCount: Int) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 查看待收货订单，共有：$orderCount 项"
        writeToLog(context, message)
    }
    
    fun logTaskCompleted(context: Context) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 任务十七完成：已成功从首页进入iPhone15商品详情，进入店铺主页，立即购买iPhone 15 粉色 256GB 1件，并查看了待收货订单"
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
        return logContent.contains("任务十七完成：已成功从首页进入iPhone15商品详情")
    }
}