package com.example.MyJD.model

data class SettleProduct(
    val productId: String,
    val productName: String,
    val storeName: String,
    val spec: String,
    val price: Double,
    var quantity: Int = 1,
    val imageUrl: String
) {
    val totalPrice: Double
        get() = price * quantity
}

data class SettleAddress(
    val name: String,
    val phone: String,
    val location: String,
    val isDefault: Boolean = true
) {
    val displayPhone: String
        get() = "${phone.take(3)}****${phone.takeLast(4)}"
}

data class SettleDelivery(
    val method: String = "京东快递",
    val time: String = "10月23日 [周四] 09:00–21:00",
    val receiveType: String = "送货上门"
)

data class SettleService(
    val title: String = "可选1年 AppleCare+ 共5项",
    val isAvailable: Boolean = true
)

data class SettlePricing(
    val productAmount: Double,
    val shippingFee: Double = 0.0,
    val couponCount: Int = 1,
    val totalAmount: Double
) {
    companion object {
        fun from(product: SettleProduct): SettlePricing {
            val productAmount = product.totalPrice
            return SettlePricing(
                productAmount = productAmount,
                totalAmount = productAmount
            )
        }
    }
}

enum class SettlePaymentMethod {
    WECHAT_PAY
}

data class SettleData(
    val product: SettleProduct,
    val address: SettleAddress,
    val delivery: SettleDelivery = SettleDelivery(),
    val service: SettleService = SettleService(),
    val pricing: SettlePricing,
    val paymentMethod: SettlePaymentMethod = SettlePaymentMethod.WECHAT_PAY
) {
    companion object {
        fun createDefault(
            productId: String = "iphone15_001",
            productName: String = "Apple/苹果 iPhone 15 (A3092) 128GB 蓝色",
            storeName: String = "京东自营",
            spec: String = "蓝色 | 128GB",
            price: Double = 3899.0,
            imageUrl: String = "image/iPhone15封面.JPG"
        ): SettleData {
            val product = SettleProduct(
                productId = productId,
                productName = productName,
                storeName = storeName,
                spec = spec,
                price = price,
                imageUrl = imageUrl
            )
            
            val address = SettleAddress(
                name = "代嘉仪",
                phone = "15312341773",
                location = "湖北武汉市江夏区 武汉纺织大学（阳光校区）-北门"
            )
            
            val pricing = SettlePricing.from(product)
            
            return SettleData(
                product = product,
                address = address,
                pricing = pricing
            )
        }
    }
}