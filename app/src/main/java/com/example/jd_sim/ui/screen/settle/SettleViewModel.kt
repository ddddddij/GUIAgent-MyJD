package com.example.jd_sim.ui.screen.settle

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jd_sim.domain.model.SettleData
import com.example.jd_sim.domain.model.SettlePricing
import com.example.jd_sim.domain.model.Coupon
import com.example.jd_sim.domain.model.OrderStatus
import com.example.jd_sim.domain.repository.DataRepository
import com.example.jd_sim.common.utils.TaskSixteenLogger
import com.example.jd_sim.common.utils.TaskSeventeenLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettleUiState(
    val settleData: SettleData? = null,
    val pricing: SettlePricing? = null,
    val quantity: Int = 1,
    val toastMessage: String? = null,
    val isLoading: Boolean = false,
    val shouldNavigateToPaymentSuccess: String? = null,
    val shouldNavigateToAddressList: Boolean = false,
    val showCouponDialog: Boolean = false,
    val availableCoupons: List<Coupon> = emptyList(),
    val currentOrderAmount: Double = 0.0
)

@HiltViewModel
class SettleViewModel @Inject constructor(
    private val repository: DataRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettleUiState())
    val uiState: StateFlow<SettleUiState> = _uiState.asStateFlow()

    private var isCartMode = false
    private var cartOrderIds: List<String> = emptyList()

    // Public methods for UI interaction
    fun loadSettleData(
        productId: String? = null,
        productName: String? = null,
        spec: String? = null,
        price: Double? = null,
        imageUrl: String? = null
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // 加载默认地址
                val defaultAddress = repository.getDefaultAddress()

                // 如果传入了参数，使用传入的数据创建SettleData
                val newSettleData = if (productId != null && productName != null && spec != null && price != null) {
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

                _uiState.value = _uiState.value.copy(
                    settleData = newSettleData,
                    pricing = newSettleData.pricing,
                    quantity = newSettleData.product.quantity,
                    isLoading = false
                )

                // 任务十七日志记录：立即购买操作
                productName?.let { name ->
                    if (name.contains("iPhone 15") && name.contains("粉色") && name.contains("256GB")) {
                        TaskSeventeenLogger.logImmediatePurchaseInitiated(context, name, spec ?: "", price ?: 0.0)
                    }
                }

            } catch (e: Exception) {
                android.util.Log.e("SettleViewModel", "Error loading settle data", e)
                _uiState.value = _uiState.value.copy(
                    toastMessage = "加载结算数据失败",
                    isLoading = false
                )
            }
        }
    }
    
    fun loadCartSettleData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                isCartMode = true
                val selectedItems = repository.getSelectedCartItems()

                // 加载默认地址
                val defaultAddress = repository.getDefaultAddress()

                if (selectedItems.isNotEmpty()) {
                    // 不立即创建订单，等到支付时再创建

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

                    val newSettleData = defaultData.copy(
                        product = defaultData.product.copy(quantity = totalQuantity),
                        pricing = SettlePricing(
                            productAmount = totalPrice,
                            shippingFee = 0.0,
                            totalAmount = totalPrice
                        )
                    )
                    _uiState.value = _uiState.value.copy(
                        settleData = newSettleData,
                        pricing = newSettleData.pricing,
                        quantity = newSettleData.product.quantity,
                        isLoading = false
                    )
                } else {
                    val newSettleData = SettleData.createDefault(address = defaultAddress)
                    _uiState.value = _uiState.value.copy(
                        settleData = newSettleData,
                        pricing = newSettleData.pricing,
                        quantity = newSettleData.product.quantity,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("SettleViewModel", "Error loading cart settle data", e)
                _uiState.value = _uiState.value.copy(
                    toastMessage = "加载购物车结算数据失败",
                    isLoading = false
                )
            }
        }
    }
    
    fun loadOrderSettleData(orderId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                isCartMode = false
                val order = repository.getOrderById(orderId)

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
                            imageUrl = firstItem.product.imageUrl,
                            address = order.shippingAddress // 使用订单自带的地址
                        )

                        val newSettleData = defaultData.copy(
                            product = defaultData.product.copy(quantity = totalQuantity),
                            pricing = SettlePricing(
                                productAmount = totalPrice,
                                shippingFee = 0.0,
                                totalAmount = totalPrice
                            )
                        )

                        _uiState.value = _uiState.value.copy(
                            settleData = newSettleData,
                            pricing = newSettleData.pricing,
                            quantity = newSettleData.product.quantity,
                            isLoading = false
                        )
                        // 存储当前订单ID用于支付
                        cartOrderIds = listOf(orderId)
                    } else {
                        _uiState.value = _uiState.value.copy(
                            toastMessage = "该订单状态不支持支付（当前状态：${getStatusDisplayText(order.status)}）",
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        toastMessage = "订单不存在",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("SettleViewModel", "Error loading order settle data", e)
                _uiState.value = _uiState.value.copy(
                    toastMessage = "加载订单结算数据失败",
                    isLoading = false
                )
            }
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
    
    fun onQuantityIncrease() {
        _uiState.value.settleData?.let { data ->
            val product = data.product
            if (product.quantity < 99) { // 限制最大数量
                product.quantity++
                updatePricingAndUi(data.copy(product = product))
            } else {
                _uiState.value = _uiState.value.copy(toastMessage = "商品数量不能超过99件")
            }
        }
    }

    fun onQuantityDecrease() {
        _uiState.value.settleData?.let { data ->
            val product = data.product
            if (product.quantity > 1) { // 最小数量为1
                product.quantity--
                updatePricingAndUi(data.copy(product = product))
            } else {
                _uiState.value = _uiState.value.copy(toastMessage = "商品数量不能少于1件")
            }
        }
    }
    
    fun onAddressClick() {
        _uiState.value = _uiState.value.copy(shouldNavigateToAddressList = true)
    }
    
    fun onAddressSelected(address: com.example.jd_sim.domain.model.Address) {
        _uiState.value.settleData?.let { currentData ->
            val updatedData = currentData.copy(address = address)
            _uiState.value = _uiState.value.copy(
                settleData = updatedData,
                toastMessage = "已选择地址：${address.recipientName}"
            )
        }
    }

    fun loadAddressById(addressId: String) {
        viewModelScope.launch {
            try {
                val address = repository.getAddressById(addressId)
                address?.let { onAddressSelected(it) }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    toastMessage = "加载地址失败：${e.message}"
                )
            }
        }
    }
    
    fun clearAddressListNavigation() {
        _uiState.value = _uiState.value.copy(shouldNavigateToAddressList = false)
    }
    
    fun onServiceClick() {
        _uiState.value = _uiState.value.copy(toastMessage = "功能开发中")
    }
    
    fun onDeliveryClick() {
        _uiState.value = _uiState.value.copy(toastMessage = "功能开发中")
    }
    
    fun onCouponClick() {
        _uiState.value.settleData?.let { data ->
            val orderAmount = data.pricing.productAmount
            val availableCoupons = repository.getAvailableCoupons(orderAmount)
            _uiState.value = _uiState.value.copy(
                showCouponDialog = true,
                availableCoupons = availableCoupons,
                currentOrderAmount = orderAmount
            )
        }
    }
    
    fun onCouponSelected(coupon: Coupon?) {
        _uiState.value.settleData?.let { data ->
            val couponDiscount = coupon?.discountAmount ?: 0.0
            val updatedPricing = SettlePricing.from(data.product, couponDiscount)
            val updatedData = data.copy(
                selectedCoupon = coupon,
                pricing = updatedPricing
            )
            _uiState.value = _uiState.value.copy(
                settleData = updatedData,
                pricing = updatedPricing,
                toastMessage = if (coupon != null) "已选择优惠券：${coupon.getDisplayText()}" else "已取消选择优惠券"
            )
            
            // 任务十六日志记录：优惠券选择
            if (data.product.productName.contains("iPhone15") || data.product.productName.contains("iPhone 15")) {
                coupon?.let { selectedCoupon ->
                    if (selectedCoupon.description.contains("满3000减50")) {
                        TaskSixteenLogger.logCouponSelected(context, selectedCoupon.description, selectedCoupon.discountAmount)
                    }
                }
            }
        }
    }
    
    fun dismissCouponDialog() {
        _uiState.value = _uiState.value.copy(showCouponDialog = false)
    }

    fun clearToast() {
        _uiState.value = _uiState.value.copy(toastMessage = null)
    }

    fun clearNavigation() {
        _uiState.value = _uiState.value.copy(shouldNavigateToPaymentSuccess = null)
    }
    
    fun onPaymentClick() {
        viewModelScope.launch {
            _uiState.value.settleData?.let { data ->
                try {
                    when {
                        // 购物车支付模式
                        isCartMode -> {
                            // 如果选择了优惠券，先通过 onCouponSelected 更新价格状态
                            data.selectedCoupon?.let { coupon ->
                                onCouponSelected(coupon)
                            }
                            // 创建带优惠券的订单，使用当前选择的地址
                            cartOrderIds = repository.createOrdersFromCartWithCoupon(data.selectedCoupon?.id, data.address)
                            
                            if (cartOrderIds.isNotEmpty()) {
                                // 支付订单
                                val paymentSuccess = repository.payOrders(cartOrderIds)
                                if (paymentSuccess) {
                                    // 跳转到支付成功页面
                                    val totalAmount = _uiState.value.settleData?.pricing?.totalAmount ?: data.pricing.totalAmount
                                    _uiState.value = _uiState.value.copy(shouldNavigateToPaymentSuccess = "¥${totalAmount.toInt()}.00")
                                } else {
                                    _uiState.value = _uiState.value.copy(toastMessage = "支付失败，请重试")
                                }
                            } else {
                                _uiState.value = _uiState.value.copy(toastMessage = "创建订单失败")
                            }
                        }
                        // 从订单页面进入的支付
                        cartOrderIds.isNotEmpty() -> {
                            // 更新所有订单的配送地址（如果用户选择了其他地址）
                            data.address?.let { address ->
                                cartOrderIds.forEach { orderId ->
                                    repository.updateOrderShippingAddress(orderId, address)
                                }
                            }
                            
                            val paymentSuccess = repository.payOrders(cartOrderIds)
                            if (paymentSuccess) {
                                // 支付成功，使用优惠券
                                data.selectedCoupon?.let { coupon ->
                                    repository.useCoupon(coupon.id)
                                }
                                // 跳转到支付成功页面
                                val totalAmount = data.pricing.totalAmount
                                _uiState.value = _uiState.value.copy(shouldNavigateToPaymentSuccess = "¥${totalAmount.toInt()}.00")
                            } else {
                                _uiState.value = _uiState.value.copy(toastMessage = "支付失败，请重试")
                            }
                        }
                        // 立即购买支付
                        else -> {
                            // 获取最新的待付款订单ID并支付
                            val orderId = repository.getLatestPendingOrderId()
                            if (orderId != null) {
                                // 更新订单的配送地址（如果用户选择了其他地址）
                                data.address?.let { address ->
                                    repository.updateOrderShippingAddress(orderId, address)
                                }
                                
                                val paymentSuccess = repository.payOrder(orderId)
                                if (paymentSuccess) {
                                    // 支付成功，使用优惠券
                                    data.selectedCoupon?.let { coupon ->
                                        repository.useCoupon(coupon.id)
                                    }
                                    // 跳转到支付成功页面
                                    val totalAmount = data.pricing.totalAmount
                                    _uiState.value = _uiState.value.copy(shouldNavigateToPaymentSuccess = "¥${totalAmount.toInt()}.00")
                                } else {
                                    _uiState.value = _uiState.value.copy(toastMessage = "支付失败，请重试")
                                }
                            } else {
                                _uiState.value = _uiState.value.copy(toastMessage = "未找到对应订单")
                            }
                        }
                    }
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(toastMessage = "支付失败：${e.message}")
                }
            } ?: run {
                _uiState.value = _uiState.value.copy(toastMessage = "支付数据错误")
            }
        }
    }

    fun onCartPaymentClick() {
        viewModelScope.launch {
            _uiState.value.settleData?.let { data ->
                try {
                    if (cartOrderIds.isNotEmpty()) {
                        val paymentSuccess = repository.payOrders(cartOrderIds)
                        if (paymentSuccess) {
                            // 支付成功，跳转到支付成功页面
                            val totalAmount = data.pricing.totalAmount
                            _uiState.value = _uiState.value.copy(shouldNavigateToPaymentSuccess = "¥${totalAmount.toInt()}.00")
                        } else {
                            _uiState.value = _uiState.value.copy(toastMessage = "支付失败，请重试")
                        }
                    } else {
                        _uiState.value = _uiState.value.copy(toastMessage = "未找到对应订单")
                    }
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(toastMessage = "支付过程中发生错误：${e.message}")
                }
            }
        }
    }

    // Helper function to update pricing and UI state
    private fun updatePricingAndUi(updatedData: SettleData) {
        val newPricing = SettlePricing.from(updatedData.product, updatedData.selectedCoupon?.discountAmount ?: 0.0)
        _uiState.value = _uiState.value.copy(
            settleData = updatedData.copy(pricing = newPricing),
            pricing = newPricing,
            quantity = updatedData.product.quantity
        )
    }
}
