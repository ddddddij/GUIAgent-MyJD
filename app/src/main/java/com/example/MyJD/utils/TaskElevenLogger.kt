package com.example.MyJD.utils

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

object TaskElevenLogger {
    private const val TAG = "TaskElevenLogger"
    private const val LOG_FILE_NAME = "task_eleven_log.txt"
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
        val message = "[$timestamp] 任务十一开始：计算我一共收到多少条消息"
        writeToLog(context, message)
    }
    
    fun logMessagePageEntered(context: Context) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 进入消息页面"
        writeToLog(context, message)
    }
    
    fun logMessagesLoaded(context: Context, messageCount: Int) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 消息列表加载完成，共计：$messageCount 条消息"
        writeToLog(context, message)
    }
    
    fun logTaskCompleted(context: Context, totalMessages: Int) {
        val timestamp = dateFormat.format(Date())
        val message = "[$timestamp] 任务十一完成：我一共收到了 $totalMessages 条消息"
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
        return logContent.contains("任务十一完成：我一共收到了")
    }
    
    fun getMessageCount(context: Context): Int {
        val logContent = readLog(context)
        val regex = "任务十一完成：我一共收到了 (\\d+) 条消息".toRegex()
        val matchResult = regex.find(logContent)
        return matchResult?.groupValues?.get(1)?.toIntOrNull() ?: 0
    }
}