package com.example.MyJD.presenter

import com.example.MyJD.repository.DataRepository

class MessageSettingPresenter(
    private val repository: DataRepository
) : MessageSettingContract.Presenter {
    
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
        
        // 从持久化存储中加载免打扰状态
        isNotificationEnabled = !repository.getMuteSetting(shopName) // 注意：免打扰开启=通知关闭
        view?.updateNotificationSwitch(isNotificationEnabled)
        
        android.util.Log.d("MessageSettingPresenter", "Loaded shop info: $shopName, notification enabled: $isNotificationEnabled")
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
        
        // 保存免打扰设置到持久化存储（免打扰=!通知开启）
        repository.setMuteSetting(currentShopName, !enabled)
        
        val status = if (enabled) "开启" else "关闭"
        val muteStatus = if (!enabled) "免打扰已开启" else "免打扰已关闭"
        android.util.Log.d("MessageSettingPresenter", "Notification switch: $status, $muteStatus")
        
        view?.showToast(muteStatus)
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