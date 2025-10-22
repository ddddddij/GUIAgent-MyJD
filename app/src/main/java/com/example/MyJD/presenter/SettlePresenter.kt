package com.example.MyJD.presenter

import com.example.MyJD.model.SettleData
import com.example.MyJD.model.SettlePricing
import com.example.MyJD.model.OrderStatus
import com.example.MyJD.model.Coupon
import com.example.MyJD.repository.DataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettlePresenter(
    private val repository: DataRepository
) : SettleContract.Presenter {
    
    private var view: SettleContract.View? = null
    private var settleData: SettleData? = null
    private var isCartMode = false
    private var cartOrderIds: List<String> = emptyList()
    private val presenterScope = CoroutineScope(Dispatchers.Main)
    
    override fun attach(view: SettleContract.View) {
        this.view = view
    }
    
    override fun detach() {
        this.view = null
    }
    
    override fun loadSettleData(
        productId: String?,
        productName: String?,
        spec: String?,
        price: Double?,
        imageUrl: String?
    ) {
        presenterScope.launch {
            try {
                // 加载默认地址
                val defaultAddress = withContext(Dispatchers.IO) {
                    repository.getDefaultAddress()
                }
                
                // 如果传入了参数，使用传入的数据创建SettleData
                settleData = if (productId != null && productName != null && spec != null && price != null) {
                    SettleData.createDefault(
                        productId = productId,
                        productName = productName,
                        spec = spec,
                        price = price,
                        imageUrl = imageUrl ?: "image/iPhone15封面.JPG",
                        address = defaultAddress
                    )
                } else {
                    // 否则使用默认数据
                    SettleData.createDefault(address = defaultAddress)
                }
                
                settleData?.let { data ->
                    view?.showSettleData(data)
                }
            } catch (e: Exception) {
                android.util.Log.e("SettlePresenter", "Error loading settle data", e)
                // 如果加载失败，使用不带地址的默认数据
                settleData = SettleData.createDefault()
                settleData?.let { data ->
                    view?.showSettleData(data)
                }
            }
        }
    }
    
    override fun loadCartSettleData() {
        presenterScope.launch {
            try {
                isCartMode = true
                val selectedItems = repository.getSelectedCartItems()
                
                // 加载默认地址
                val defaultAddress = withContext(Dispatchers.IO) {
                    repository.getDefaultAddress()
                }
                
                if (selectedItems.isNotEmpty()) {
                    // 创建订单
                    cartOrderIds = repository.createOrdersFromCart()
                    
                    // 计算总价
                    val totalPrice = selectedItems.sumOf { it.totalPrice }
                    val totalQuantity = selectedItems.sumOf { it.quantity }
                    
                    // 使用第一个商品作为主要显示商品（可以改为显示商品列表）
                    val firstItem = selectedItems.first()
                    val defaultData = SettleData.createDefault(
                        productId = firstItem.productId,
                        productName = if (selectedItems.size > 1) 
                            "${firstItem.productName} 等${selectedItems.size}件商品" 
                        else firstItem.productName,
                        spec = "${firstItem.color} ${firstItem.storage}",
                        price = totalPrice / totalQuantity, // 平均单价
                        imageUrl = firstItem.image,
                        address = defaultAddress
                    )
                    
                    settleData = defaultData.copy(
                        product = defaultData.product.copy(quantity = totalQuantity),
                        pricing = SettlePricing(
                            productAmount = totalPrice,
                            shippingFee = 0.0,
                            totalAmount = totalPrice
                        )
                    )
                } else {
                    settleData = SettleData.createDefault(address = defaultAddress)
                }
                
                settleData?.let { data ->
                    view?.showSettleData(data)
                }
            } catch (e: Exception) {
                android.util.Log.e("SettlePresenter", "Error loading cart settle data", e)
                settleData = SettleData.createDefault()
                settleData?.let { data ->
                    view?.showSettleData(data)
                }
            }
        }
    }
    
    override fun loadOrderSettleData(orderId: String) {
        isCartMode = false
        val order = repository.getOrderById(orderId)
        
        android.util.Log.d("SettlePresenter", "Loading order settle data for orderId: $orderId")
        android.util.Log.d("SettlePresenter", "Found order: ${order?.id}, status: ${order?.status}")
        
        if (order != null) {
            if (order.status == OrderStatus.PENDING_PAYMENT) {
                // 根据订单数据创建SettleData
                val firstItem = order.items.first()
                val totalPrice = order.totalAmount
                val totalQuantity = order.items.sumOf { it.quantity }
                
                val defaultData = SettleData.createDefault(
                    productId = firstItem.product.id,
                    productName = if (order.items.size > 1) 
                        "${firstItem.product.name} 等${order.items.size}件商品" 
                    else firstItem.product.name,
                    spec = listOfNotNull(firstItem.selectedColor, firstItem.selectedVersion).joinToString(" "),
                    price = totalPrice / totalQuantity, // 平均单价
                    imageUrl = firstItem.product.imageUrl
                )
                
                settleData = defaultData.copy(
                    product = defaultData.product.copy(quantity = totalQuantity),
                    pricing = SettlePricing(
                        productAmount = totalPrice,
                        shippingFee = 0.0,
                        totalAmount = totalPrice
                    )
                )
                
                // 存储当前订单ID用于支付
                cartOrderIds = listOf(orderId)
                android.util.Log.d("SettlePresenter", "Order settle data loaded successfully. CartOrderIds: $cartOrderIds")
            } else {
                settleData = SettleData.createDefault()
                view?.showToast("该订单状态不支持支付（当前状态：${getStatusDisplayText(order.status)}）")
                android.util.Log.w("SettlePresenter", "Order $orderId has invalid status for payment: ${order.status}")
            }
        } else {
            settleData = SettleData.createDefault()
            view?.showToast("订单不存在")
            android.util.Log.e("SettlePresenter", "Order $orderId not found")
        }
        
        settleData?.let { data ->
            view?.showSettleData(data)
        }
    }
    
    private fun getStatusDisplayText(status: OrderStatus): String {
        return when (status) {
            OrderStatus.PENDING_PAYMENT -> "待付款"
            OrderStatus.PENDING_SHIPMENT -> "待使用"
            OrderStatus.PENDING_RECEIPT -> "待收货"
            OrderStatus.PENDING_REVIEW -> "待评价"
            OrderStatus.COMPLETED -> "已完成"
            OrderStatus.CANCELLED -> "已取消"
        }
    }
    
    override fun onQuantityIncrease() {
        settleData?.let { data ->
            val product = data.product
            if (product.quantity < 99) { // 限制最大数量
                product.quantity++
                updatePricingAndView()
            } else {
                view?.showToast("商品数量不能超过99件")
            }
        }
    }
    
    override fun onQuantityDecrease() {
        settleData?.let { data ->
            val product = data.product
            if (product.quantity > 1) { // 最小数量为1
                product.quantity--
                updatePricingAndView()
            } else {
                view?.showToast("商品数量不能少于1件")
            }
        }
    }
    
    override fun onAddressClick() {
        view?.navigateToAddressList()
    }
    
    override fun onAddressSelected(address: com.example.MyJD.model.Address) {
        settleData?.let { currentData ->
            settleData = currentData.copy(address = address)
            view?.showSettleData(settleData!!)
        }
    }
    
    override fun onServiceClick() {
        view?.showToast("功能开发中")
    }
    
    override fun onDeliveryClick() {
        view?.showToast("功能开发中")
    }
    
    override fun onCouponClick() {
        settleData?.let { data ->
            val orderAmount = data.pricing.productAmount
            val availableCoupons = repository.getAvailableCoupons(orderAmount)
            view?.showCouponDialog(availableCoupons, orderAmount)
        }
    }
    
    override fun onCouponSelected(coupon: Coupon?) {
        settleData?.let { data ->
            val couponDiscount = coupon?.discountAmount ?: 0.0
            val updatedPricing = SettlePricing.from(data.product, couponDiscount)
            val updatedData = data.copy(
                selectedCoupon = coupon,
                pricing = updatedPricing
            )
            settleData = updatedData
            view?.showSettleData(updatedData)
            
            if (coupon != null) {
                view?.showToast("已选择优惠券：${coupon.getDisplayText()}")
            } else {
                view?.showToast("已取消选择优惠券")
            }
        }
    }
    
    override fun onPaymentClick() {
        settleData?.let { data ->
            try {
                android.util.Log.d("SettlePresenter", "Payment clicked. CartOrderIds: $cartOrderIds, isCartMode: $isCartMode")
                
                when {
                    // 购物车支付或从订单页面进入的支付
                    cartOrderIds.isNotEmpty() -> {
                        android.util.Log.d("SettlePresenter", "Processing payment for orders: $cartOrderIds")
                        val paymentSuccess = repository.payOrders(cartOrderIds)
                        if (paymentSuccess) {
                            // 支付成功，使用优惠券
                            data.selectedCoupon?.let { coupon ->
                                repository.useCoupon(coupon.id)
                            }
                            // 跳转到支付成功页面
                            val totalAmount = data.pricing.totalAmount
                            android.util.Log.d("SettlePresenter", "Payment successful for orders: $cartOrderIds")
                            view?.navigateToPaymentSuccess("¥${totalAmount.toInt()}.00")
                        } else {
                            android.util.Log.w("SettlePresenter", "Payment failed for orders: $cartOrderIds")
                            view?.showToast("支付失败，请重试")
                        }
                    }
                    // 立即购买支付
                    else -> {
                        // 获取最新的待付款订单ID并支付
                        val orderId = repository.getLatestPendingOrderId()
                        android.util.Log.d("SettlePresenter", "Immediate purchase payment. Latest pending order: $orderId")
                        if (orderId != null) {
                            val paymentSuccess = repository.payOrder(orderId)
                            if (paymentSuccess) {
                                // 支付成功，使用优惠券
                                data.selectedCoupon?.let { coupon ->
                                    repository.useCoupon(coupon.id)
                                }
                                // 跳转到支付成功页面
                                val totalAmount = data.pricing.totalAmount
                                android.util.Log.d("SettlePresenter", "Immediate purchase payment successful for order: $orderId")
                                view?.navigateToPaymentSuccess("¥${totalAmount.toInt()}.00")
                            } else {
                                android.util.Log.w("SettlePresenter", "Immediate purchase payment failed for order: $orderId")
                                view?.showToast("支付失败，请重试")
                            }
                        } else {
                            android.util.Log.w("SettlePresenter", "No pending order found for immediate purchase")
                            view?.showToast("未找到对应订单")
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("SettlePresenter", "Payment error", e)
                view?.showToast("支付失败：${e.message}")
            }
        } ?: run {
            android.util.Log.e("SettlePresenter", "Settlement data is null")
            view?.showToast("支付数据错误")
        }
    }
    
    override fun onCartPaymentClick() {
        settleData?.let { data ->
            try {
                if (cartOrderIds.isNotEmpty()) {
                    val paymentSuccess = repository.payOrders(cartOrderIds)
                    if (paymentSuccess) {
                        // 支付成功，跳转到支付成功页面
                        val totalAmount = data.pricing.totalAmount
                        view?.navigateToPaymentSuccess("¥${totalAmount.toInt()}.00")
                    } else {
                        view?.showToast("支付失败，请重试")
                    }
                } else {
                    view?.showToast("未找到对应订单")
                }
            } catch (e: Exception) {
                view?.showToast("支付过程中发生错误：${e.message}")
                android.util.Log.e("SettlePresenter", "Cart payment error", e)
            }
        }
    }
    
    private fun updatePricingAndView() {
        settleData?.let { data ->
            val newPricing = SettlePricing.from(data.product)
            val updatedData = data.copy(pricing = newPricing)
            settleData = updatedData
            
            view?.updateQuantity(data.product.quantity)
            view?.updatePricing(newPricing)
        }
    }
}