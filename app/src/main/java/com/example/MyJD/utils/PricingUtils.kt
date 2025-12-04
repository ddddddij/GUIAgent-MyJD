package com.example.myjd.utils

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

    // Base prices for Huawei P60 series (基础价格按products.json中256GB价格反推)
    // 华为P60 Pro 256GB在products.json中是4488，减去256GB升级费用500得到基础价
    private val huaweiP60BasePrices = mapOf(
        "Huawei P60" to 3988.0,      // 基础价（128GB）
        "Huawei P60 Pro" to 3988.0   // 基础价（128GB）
    )

    // Storage upgrade prices for Huawei P60
    private val huaweiP60StorageUpgrades = mapOf(
        "128GB" to 0.0,
        "256GB" to 500.0,    // +500元 -> 4488
        "512GB" to 1000.0    // +1000元 -> 4988
    )

    /**
     * Calculates the price for a Huawei P60 based on its series and storage.
     * @param series The Huawei P60 series (e.g., "Huawei P60", "Huawei P60 Pro").
     * @param storage The storage capacity (e.g., "128GB", "256GB", "512GB").
     * @return The calculated price, or 0.0 if the series or storage is not found.
     */
    fun calculateHuaweiP60Price(series: String, storage: String): Double {
        val basePrice = huaweiP60BasePrices[series] ?: return 0.0
        val storageUpgrade = huaweiP60StorageUpgrades[storage] ?: 0.0
        return basePrice + storageUpgrade
    }

    // Base prices for Huawei Mate 60 series (基础价格按products.json中512GB价格反推)
    // 华为Mate60 512GB在products.json中是4999，以256GB为基础版本
    private val huaweiMate60BasePrices = mapOf(
        "Huawei Mate 60" to 4499.0,      // 基础价（256GB）
        "Huawei Mate 60 Pro" to 4499.0   // 基础价（256GB）
    )

    // Storage upgrade prices for Huawei Mate 60
    private val huaweiMate60StorageUpgrades = mapOf(
        "256GB" to 0.0,       // 基础价 -> 4499
        "512GB" to 500.0,     // +500元 -> 4999
        "1TB" to 1000.0       // +1000元 -> 5499
    )

    /**
     * Calculates the price for a Huawei Mate 60 based on its series and storage.
     * @param series The Huawei Mate 60 series (e.g., "Huawei Mate 60", "Huawei Mate 60 Pro").
     * @param storage The storage capacity (e.g., "256GB", "512GB", "1TB").
     * @return The calculated price, or 0.0 if the series or storage is not found.
     */
    fun calculateHuaweiMate60Price(series: String, storage: String): Double {
        val basePrice = huaweiMate60BasePrices[series] ?: return 0.0
        val storageUpgrade = huaweiMate60StorageUpgrades[storage] ?: 0.0
        return basePrice + storageUpgrade
    }

    // Base prices for Huawei Nova 11 series (基础价格按products.json中256GB价格反推)
    // 华为Nova11 SE 256GB在products.json中是3499，以128GB为基础版本
    private val huaweiNova11BasePrices = mapOf(
        "Huawei Nova 11" to 2999.0,      // 基础价（128GB）
        "Huawei Nova 11 SE" to 2999.0    // 基础价（128GB）
    )

    // Storage upgrade prices for Huawei Nova 11
    private val huaweiNova11StorageUpgrades = mapOf(
        "128GB" to 0.0,       // 基础价 -> 2999
        "256GB" to 500.0,     // +500元 -> 3499
        "512GB" to 1000.0     // +1000元 -> 3999
    )

    /**
     * Calculates the price for a Huawei Nova 11 based on its series and storage.
     * @param series The Huawei Nova 11 series (e.g., "Huawei Nova 11", "Huawei Nova 11 SE").
     * @param storage The storage capacity (e.g., "128GB", "256GB", "512GB").
     * @return The calculated price, or 0.0 if the series or storage is not found.
     */
    fun calculateHuaweiNova11Price(series: String, storage: String): Double {
        val basePrice = huaweiNova11BasePrices[series] ?: return 0.0
        val storageUpgrade = huaweiNova11StorageUpgrades[storage] ?: 0.0
        return basePrice + storageUpgrade
    }
}