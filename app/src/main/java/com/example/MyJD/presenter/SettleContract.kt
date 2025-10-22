package com.example.MyJD.presenter

import com.example.MyJD.model.SettleData
import com.example.MyJD.model.SettlePricing
import com.example.MyJD.model.Address
import com.example.MyJD.model.Coupon

interface SettleContract {
    
    interface View {
        fun showSettleData(data: SettleData)
        fun updatePricing(pricing: SettlePricing)
        fun showToast(message: String)
        fun updateQuantity(quantity: Int)
        fun navigateBack()
        fun navigateToPaymentSuccess(orderAmount: String)
        fun navigateToAddressList()
        fun showCouponDialog(availableCoupons: List<Coupon>, orderAmount: Double)
    }
    
    interface Presenter {
        fun attach(view: View)
        fun detach()
        fun loadSettleData(
            productId: String? = null,
            productName: String? = null,
            spec: String? = null,
            price: Double? = null,
            imageUrl: String? = null
        )
        fun loadCartSettleData()
        fun loadOrderSettleData(orderId: String)
        fun onQuantityIncrease()
        fun onQuantityDecrease()
        fun onAddressClick()
        fun onAddressSelected(address: Address)
        fun onServiceClick()
        fun onDeliveryClick()
        fun onCouponClick()
        fun onCouponSelected(coupon: Coupon?)
        fun onPaymentClick()
        fun onCartPaymentClick()
    }
}