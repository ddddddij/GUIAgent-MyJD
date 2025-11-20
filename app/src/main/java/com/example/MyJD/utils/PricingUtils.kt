package com.example.MyJD.utils

object PricingUtils {
    
    // 基础定价映射
    private val basePrices = mapOf(
        "iPhone 13" to 3499.0,
        "iPhone 14 plus" to 4699.0,
        "iPhone 14 Plus" to 4699.0, // 处理大小写变化
        "iPhone 15" to 3699.0,
        "iPhone 15 pro" to 7499.0,
        "iPhone 15 Pro" to 7499.0, // 处理大小写变化
        "iPhone 15 plus" to 4999.0,
        "iPhone 15 Plus" to 4999.0, // 处理大小写变化
        "15 Pro Max" to 7999.0,
        "iPhone 15 pro max" to 7999.0,
        "iPhone 15 Pro Max" to 7999.0 // 处理大小写变化
    )
    
    // 内存容量价格增加
    private val storageUpgrades = mapOf(
        "128GB" to 0.0,      // 基础价格
        "256GB" to 1000.0,   // +1000元
        "512GB" to 3000.0    // +3000元
    )
    
    /**
     * 根据系列和存储容量计算价格
     * @param series iPhone型号
     * @param storage 存储容量
     * @return 计算后的价格，如果找不到对应价格返回0.0
     */
    fun calculatePrice(series: String, storage: String): Double {
        val basePrice = basePrices[series] ?: return 0.0
        val storageUpgrade = storageUpgrades[storage] ?: 0.0
        return basePrice + storageUpgrade
    }
    
    /**
     * 获取基础价格（128GB版本的价格）
     * @param series iPhone型号
     * @return 基础价格
     */
    fun getBasePrice(series: String): Double {
        return basePrices[series] ?: 0.0
    }
    
    /**
     * 获取存储容量价格增量
     * @param storage 存储容量
     * @return 价格增量
     */
    fun getStorageUpgrade(storage: String): Double {
        return storageUpgrades[storage] ?: 0.0
    }
    
    /**
     * 检查是否支持的iPhone系列
     * @param series iPhone型号
     * @return 是否支持
     */
    fun isSupportedSeries(series: String): Boolean {
        return basePrices.containsKey(series)
    }
    
    /**
     * 检查是否支持的存储容量
     * @param storage 存储容量
     * @return 是否支持
     */
    fun isSupportedStorage(storage: String): Boolean {
        return storageUpgrades.containsKey(storage)
    }
    
    /**
     * 获取所有支持的iPhone系列
     * @return 支持的系列列表
     */
    fun getSupportedSeries(): List<String> {
        return basePrices.keys.toList()
    }
    
    /**
     * 获取所有支持的存储容量
     * @return 支持的存储容量列表
     */
    fun getSupportedStorageOptions(): List<String> {
        return storageUpgrades.keys.toList()
    }
}