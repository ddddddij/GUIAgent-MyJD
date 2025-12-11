package com.example.jd_sim.ui.screen.messagesetting

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jd_sim.domain.repository.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MessageSettingUiState(
    val shopName: String = "",
    val shopAvatar: String = "",
    val isNotificationEnabled: Boolean = false,
    val toastMessage: String? = null,
    val navigationEvent: MessageSettingNavigationEvent? = null
)

sealed class MessageSettingNavigationEvent {
    object ToShop : MessageSettingNavigationEvent()
    object NavigateBack : MessageSettingNavigationEvent()
    object None : MessageSettingNavigationEvent()
}

@HiltViewModel
class MessageSettingViewModel @Inject constructor(
    private val repository: DataRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(MessageSettingUiState())
    val uiState: StateFlow<MessageSettingUiState> = _uiState.asStateFlow()

    fun loadShopInfo(shopName: String, shopAvatar: String) {
        viewModelScope.launch {
            val isNotificationEnabled = !repository.getMuteSetting(shopName) // Note: Mute enabled = notifications off
            _uiState.value = _uiState.value.copy(
                shopName = shopName,
                shopAvatar = shopAvatar,
                isNotificationEnabled = isNotificationEnabled
            )
            android.util.Log.d("MessageSettingViewModel", "Loaded shop info: $shopName, notification enabled: $isNotificationEnabled")
        }
    }

    fun onEnterShopClick() {
        _uiState.value = _uiState.value.copy(navigationEvent = MessageSettingNavigationEvent.ToShop)
        android.util.Log.d("MessageSettingViewModel", "Enter shop clicked for: ${_uiState.value.shopName}")
    }

    fun onSearchChatHistoryClick() {
        _uiState.value = _uiState.value.copy(toastMessage = "搜索聊天记录 - 功能开发中")
        android.util.Log.d("MessageSettingViewModel", "Search chat history clicked")
    }

    fun onNotificationSwitchChanged(enabled: Boolean) {
        viewModelScope.launch {
            repository.setMuteSetting(_uiState.value.shopName, !enabled) // Save mute setting
            val muteStatus = if (!enabled) "免打扰已开启" else "免打扰已关闭"
            _uiState.value = _uiState.value.copy(
                isNotificationEnabled = enabled,
                toastMessage = muteStatus
            )
            android.util.Log.d("MessageSettingViewModel", "Notification switch: ${if (enabled) "开启" else "关闭"}, $muteStatus")
        }
    }

    fun onMessageSettingsClick() {
        _uiState.value = _uiState.value.copy(toastMessage = "消息设置 - 功能开发中")
        android.util.Log.d("MessageSettingViewModel", "Message settings clicked")
    }

    fun onClearLocalRecordsClick() {
        _uiState.value = _uiState.value.copy(toastMessage = "本地记录已清除")
        android.util.Log.d("MessageSettingViewModel", "Clear local records clicked")
    }

    fun onBackClick() {
        _uiState.value = _uiState.value.copy(navigationEvent = MessageSettingNavigationEvent.NavigateBack)
        android.util.Log.d("MessageSettingViewModel", "Back button clicked")
    }

    fun clearToast() {
        _uiState.value = _uiState.value.copy(toastMessage = null)
    }

    fun clearNavigationEvent() {
        _uiState.value = _uiState.value.copy(navigationEvent = MessageSettingNavigationEvent.None)
    }
}
