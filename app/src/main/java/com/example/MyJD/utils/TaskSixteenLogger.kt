package com.example.MyJD.utils

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

object TaskSixteenLogger {
    private const val TAG = "TaskSixteenLogger"
    private const val LOG_FILE_NAME = "task_sixteen_log.txt"
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
        val message = "[$timestamp] 任务十六开始：搜索iPhone15并筛选价格在5000-8000之间的手机类别，将iPhone15 黑色 256GB加入购物车后，选择微信支付，满3000减50优惠券结算"
        writeToLog(context, message)
    }
    
    fun logSearchStarted(context: Context, keyword: String) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 开始搜索：$keyword"
        writeToLog(context, message)
    }
    
    fun logSearchResultsLoaded(context: Context, resultCount: Int) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 搜索结果加载完成，共找到：$resultCount 个结果"
        writeToLog(context, message)
    }
    
    fun logPriceFilterApplied(context: Context, minPrice: Int, maxPrice: Int) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 应用价格筛选：¥$minPrice - ¥$maxPrice"
        writeToLog(context, message)
    }
    
    fun logCategoryFilterApplied(context: Context, category: String) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 应用分类筛选：$category"
        writeToLog(context, message)
    }
    
    fun logProductSelected(context: Context, productName: String) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 选择商品：$productName"
        writeToLog(context, message)
    }
    
    fun logSpecSelected(context: Context, color: String, storage: String) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 选择规格：$color $storage"
        writeToLog(context, message)
    }
    
    fun logAddToCart(context: Context, productName: String) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 将商品加入购物车：$productName"
        writeToLog(context, message)
    }
    
    fun logCartEntered(context: Context) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 进入购物车页面"
        writeToLog(context, message)
    }
    
    fun logCheckoutStarted(context: Context) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 开始结算"
        writeToLog(context, message)
    }
    
    fun logPaymentMethodSelected(context: Context, paymentMethod: String) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 选择支付方式：$paymentMethod"
        writeToLog(context, message)
    }
    
    fun logCouponSelected(context: Context, couponName: String) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 选择优惠券：$couponName"
        writeToLog(context, message)
    }
    
    fun logPaymentCompleted(context: Context, orderId: String) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 支付完成，订单ID：$orderId"
        writeToLog(context, message)
    }
    
    fun logTaskCompleted(context: Context) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 任务十六完成：已成功搜索iPhone15，筛选价格范围和手机类别，将iPhone15 黑色 256GB加入购物车，选择微信支付和满3000减50优惠券完成结算"
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
        return logContent.contains("任务十六完成：已成功搜索iPhone15")
    }
}