package com.example.myjd.common.utils

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

    // Base prices for Huawei P60 series (参考iPhone15定价规则，按系列阶梯定价)
    // 首页展示价格：P60 Pro 256GB = 4488元
    // 基础价格为128GB价格，参考iPhone15为不同系列设置不同基础价
    private val huaweiP60BasePrices = mapOf(
        "Huawei P60" to 3688.0,        // 标准版基础价（128GB）
        "Huawei P60 Pro" to 3988.0     // Pro版基础价（128GB）+300元
    )

    // Storage upgrade prices for Huawei P60
    // 参考iPhone15的存储升级策略
    private val huaweiP60StorageUpgrades = mapOf(
        "128GB" to 0.0,         // 基础配置
        "256GB" to 500.0,       // +500元 (标准版4188，Pro版4488=首页价)
        "512GB" to 1000.0       // +1000元 (标准版4688，Pro版4988)
    )

    /**
     * Calculates the price for a Huawei P60 based on its series and storage.
     * 按系列和存储容量阶梯定价，Pro版比标准版贵300元
     * @param series The Huawei P60 series (e.g., "Huawei P60", "Huawei P60 Pro").
     * @param storage The storage capacity (e.g., "128GB", "256GB", "512GB").
     * @return The calculated price, or 0.0 if the series or storage is not found.
     */
    fun calculateHuaweiP60Price(series: String, storage: String): Double {
        val basePrice = huaweiP60BasePrices[series] ?: return 0.0
        val storageUpgrade = huaweiP60StorageUpgrades[storage] ?: 0.0
        return basePrice + storageUpgrade
    }

    // Base prices for Huawei Mate 60 series (参考iPhone15定价规则，按系列阶梯定价)
    // 首页展示价格：Mate60 512GB = 4999元
    // 基础价格为256GB价格，参考iPhone15为不同系列设置不同基础价
    private val huaweiMate60BasePrices = mapOf(
        "Huawei Mate 60" to 4499.0,        // 标准版基础价（256GB）
        "Huawei Mate 60 Pro" to 4799.0     // Pro版基础价（256GB）+300元
    )

    // Storage upgrade prices for Huawei Mate 60
    // 参考iPhone15的存储升级策略
    private val huaweiMate60StorageUpgrades = mapOf(
        "256GB" to 0.0,         // 基础配置
        "512GB" to 500.0,       // +500元 (标准版4999=首页价，Pro版5299)
        "1TB" to 1000.0         // +1000元 (标准版5499，Pro版5799)
    )

    /**
     * Calculates the price for a Huawei Mate 60 based on its series and storage.
     * 按系列和存储容量阶梯定价，Pro版比标准版贵300元
     * @param series The Huawei Mate 60 series (e.g., "Huawei Mate 60", "Huawei Mate 60 Pro").
     * @param storage The storage capacity (e.g., "256GB", "512GB", "1TB").
     * @return The calculated price, or 0.0 if the series or storage is not found.
     */
    fun calculateHuaweiMate60Price(series: String, storage: String): Double {
        val basePrice = huaweiMate60BasePrices[series] ?: return 0.0
        val storageUpgrade = huaweiMate60StorageUpgrades[storage] ?: 0.0
        return basePrice + storageUpgrade
    }

    // Base prices for Huawei Nova 11 series (参考iPhone15定价规则，按系列阶梯定价)
    // 首页展示价格：Nova11 256GB = 3499元
    // 基础价格为128GB价格，参考iPhone15为不同系列设置不同基础价
    private val huaweiNova11BasePrices = mapOf(
        "Huawei Nova 11" to 2999.0,        // 标准版基础价（128GB）
        "Huawei Nova 11 SE" to 2799.0      // SE版基础价（128GB）-200元
    )

    // Storage upgrade prices for Huawei Nova 11
    // 参考iPhone15的存储升级策略
    private val huaweiNova11StorageUpgrades = mapOf(
        "128GB" to 0.0,         // 基础配置
        "256GB" to 500.0,       // +500元 (标准版3499=首页价，SE版3299)
        "512GB" to 1000.0       // +1000元 (标准版3999，SE版3799)
    )

    /**
     * Calculates the price for a Huawei Nova 11 based on its series and storage.
     * 按系列和存储容量阶梯定价，SE版比标准版便宜200元
     * @param series The Huawei Nova 11 series (e.g., "Huawei Nova 11", "Huawei Nova 11 SE").
     * @param storage The storage capacity (e.g., "128GB", "256GB", "512GB").
     * @return The calculated price, or 0.0 if the series or storage is not found.
     */
    fun calculateHuaweiNova11Price(series: String, storage: String): Double {
        val basePrice = huaweiNova11BasePrices[series] ?: return 0.0
        val storageUpgrade = huaweiNova11StorageUpgrades[storage] ?: 0.0
        return basePrice + storageUpgrade
    }

    // Base prices for ThinkPad series (基础价格: 4999元)
    // 参考iPhone15定价规则，按系列和配置阶梯定价
    private val thinkPadBasePrices = mapOf(
        "【2025】E14 超能版" to 4999.0,        // 基础系列
        "【2025】E16 超能版" to 5299.0,        // 大屏版 +300
        "【AIPC】E14 锐龙版" to 5499.0,        // AI版 +500
        "【AIPC】E16Ultra版" to 5799.0,       // AI Ultra版 +800
        "【经典版】E16 大屏商务本" to 5999.0    // 经典版 +1000
    )

    // Configuration upgrade prices for ThinkPad
    // 存储和处理器配置价格增量
    private val thinkPadStorageUpgrades = mapOf(
        "酷睿5 220H 16G 1T 2.8K" to 0.0,      // 基础配置
        "酷睿5 220H 32G 1T 2.8K" to 500.0,    // +500元
        "酷睿7 250H 32G 1T 2.8K" to 1000.0,   // +1000元
        "Ultra5 228V 32G 1TB 2.8K" to 1200.0, // +1200元
        "Ultra7 258V 32G 1TB 2.8K" to 1500.0  // +1500元
    )

    /**
     * Calculates the price for a ThinkPad based on its series and configuration.
     * 按系列和配置阶梯定价，基础价格4999元起
     * @param series The ThinkPad series (e.g., "【2025】E14 超能版").
     * @param storage The configuration (processor/memory/storage).
     * @return The calculated price, or 0.0 if the series or storage is not found.
     */
    fun calculateThinkPadPrice(series: String, storage: String): Double {
        val basePrice = thinkPadBasePrices[series] ?: return 0.0
        val storageUpgrade = thinkPadStorageUpgrades[storage] ?: 0.0
        return basePrice + storageUpgrade
    }
}