package com.example.MyJD.presenter

import com.example.MyJD.model.SettleData
import com.example.MyJD.model.SettlePricing
import com.example.MyJD.repository.DataRepository

class SettlePresenter(
    private val repository: DataRepository
) : SettleContract.Presenter {
    
    private var view: SettleContract.View? = null
    private var settleData: SettleData? = null
    
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
        // 如果传入了参数，使用传入的数据创建SettleData
        settleData = if (productId != null && productName != null && spec != null && price != null) {
            SettleData.createDefault(
                productId = productId,
                productName = productName,
                spec = spec,
                price = price,
                imageUrl = imageUrl ?: "image/iPhone15封面.JPG"
            )
        } else {
            // 否则使用默认数据
            SettleData.createDefault()
        }
        
        settleData?.let { data ->
            view?.showSettleData(data)
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
        view?.showToast("切换地址")
    }
    
    override fun onServiceClick() {
        view?.showToast("功能开发中")
    }
    
    override fun onDeliveryClick() {
        view?.showToast("功能开发中")
    }
    
    override fun onCouponClick() {
        view?.showToast("暂不支持使用优惠券")
    }
    
    override fun onPaymentClick() {
        settleData?.let { data ->
            try {
                // 获取最新的待付款订单ID并支付
                val orderId = repository.getLatestPendingOrderId()
                if (orderId != null) {
                    val paymentSuccess = repository.payOrder(orderId)
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
                view?.showToast("支付失败：${e.message}")
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