package com.example.MyJD.presenter

interface MessageSettingContract {
    
    interface View {
        fun setShopInfo(shopName: String, shopAvatar: String)
        fun showToast(message: String)
        fun updateNotificationSwitch(enabled: Boolean)
        fun navigateToShop()
        fun navigateBack()
    }
    
    interface Presenter {
        fun attach(view: View)
        fun detach()
        fun loadShopInfo(shopName: String, shopAvatar: String)
        fun onEnterShopClick()
        fun onSearchChatHistoryClick()
        fun onNotificationSwitchChanged(enabled: Boolean)
        fun onMessageSettingsClick()
        fun onClearLocalRecordsClick()
        fun onBackClick()
    }
}