package com.example.MyJD.presenter

class MessageSettingPresenter : MessageSettingContract.Presenter {
    
    private var view: MessageSettingContract.View? = null
    private var currentShopName: String = ""
    private var currentShopAvatar: String = ""
    private var isNotificationEnabled: Boolean = false
    
    override fun attach(view: MessageSettingContract.View) {
        this.view = view
    }
    
    override fun detach() {
        this.view = null
    }
    
    override fun loadShopInfo(shopName: String, shopAvatar: String) {
        currentShopName = shopName
        currentShopAvatar = shopAvatar
        view?.setShopInfo(shopName, shopAvatar)
        
        android.util.Log.d("MessageSettingPresenter", "Loaded shop info: $shopName")
    }
    
    override fun onEnterShopClick() {
        android.util.Log.d("MessageSettingPresenter", "Enter shop clicked for: $currentShopName")
        view?.navigateToShop()
    }
    
    override fun onSearchChatHistoryClick() {
        android.util.Log.d("MessageSettingPresenter", "Search chat history clicked")
        // 按要求：无需实现功能，仅展示可点击样式
    }
    
    override fun onNotificationSwitchChanged(enabled: Boolean) {
        isNotificationEnabled = enabled
        view?.updateNotificationSwitch(enabled)
        
        val status = if (enabled) "开启" else "关闭"
        android.util.Log.d("MessageSettingPresenter", "Notification switch: $status")
        
        // 按要求：切换无实际功能，仅更新UI状态
    }
    
    override fun onMessageSettingsClick() {
        android.util.Log.d("MessageSettingPresenter", "Message settings clicked")
        // 按要求：无需功能，仅展示交互样式
    }
    
    override fun onClearLocalRecordsClick() {
        android.util.Log.d("MessageSettingPresenter", "Clear local records clicked")
        view?.showToast("本地记录已清除")
    }
    
    override fun onBackClick() {
        android.util.Log.d("MessageSettingPresenter", "Back button clicked")
        view?.navigateBack()
    }
    
    fun getNotificationStatus(): Boolean {
        return isNotificationEnabled
    }
    
    fun getShopInfo(): Pair<String, String> {
        return Pair(currentShopName, currentShopAvatar)
    }
}