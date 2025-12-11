package com.example.jd_sim.ui.screen.messagedetail

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jd_sim.domain.model.ChatMessage
import com.example.jd_sim.domain.model.ChatSender
import com.example.jd_sim.domain.model.ChatMessageType
import com.example.jd_sim.domain.model.Conversation
import com.example.jd_sim.domain.repository.DataRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MessageDetailUiState(
    val conversation: Conversation? = null,
    val messages: List<ChatMessage> = emptyList(),
    val toastMessage: String? = null,
    val isLoading: Boolean = false,
    val title: String = "",
    val avatar: String = "",
    val inputText: String = "",
    val scrollToBottomEvent: Boolean = false,
    val navigationEvent: NavigationEvent? = null
)

sealed class NavigationEvent {
    data class ToProductDetail(val productId: String) : NavigationEvent()
    object NavigateBack : NavigationEvent()
    object None : NavigationEvent()
}

@HiltViewModel
class MessageDetailViewModel @Inject constructor(
    private val repository: DataRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(MessageDetailUiState())
    val uiState: StateFlow<MessageDetailUiState> = _uiState.asStateFlow()

    private val gson = Gson() // For potential data parsing, keep if necessary

    fun loadConversation(conversationId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val conversationData = repository.loadConversationData()
                val conversation = conversationData.conversations.find { it.id == conversationId }

                if (conversation != null) {
                    // Get new messages belonging to the current conversation
                    val newMessages = repository.getNewMessages().filter {
                        it.conversationId == conversationId
                    }
                    val allMessages = (conversation.messages + newMessages).sortedBy { it.timestamp }

                    // Update conversation's message list (in memory for current session)
                    conversation.messages.clear()
                    conversation.messages.addAll(allMessages)

                    _uiState.value = _uiState.value.copy(
                        conversation = conversation,
                        messages = conversation.messages,
                        title = conversation.chatName,
                        avatar = conversation.chatAvatar,
                        isLoading = false,
                        scrollToBottomEvent = true // Trigger scroll after loading
                    )
                    android.util.Log.d("MessageDetailViewModel", "Loaded conversation: ${conversation.chatName} with ${conversation.messages.size} messages (including ${newMessages.size} new messages)")
                } else {
                    _uiState.value = _uiState.value.copy(
                        toastMessage = "找不到该对话",
                        isLoading = false
                    )
                    android.util.Log.w("MessageDetailViewModel", "Conversation not found: $conversationId")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    toastMessage = "加载对话失败: ${e.message}",
                    isLoading = false
                )
                android.util.Log.e("MessageDetailViewModel", "Failed to load conversation", e)
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun sendMessage(content: String) {
        if (content.trim().isEmpty()) {
            _uiState.value = _uiState.value.copy(toastMessage = "请输入消息内容")
            return
        }

        val conversation = _uiState.value.conversation
        if (conversation == null) {
            _uiState.value = _uiState.value.copy(toastMessage = "对话数据错误")
            return
        }

        viewModelScope.launch {
            try {
                // Create new user message
                val newMessage = ChatMessage(
                    id = "msg_user_${System.currentTimeMillis()}",
                    sender = ChatSender.USER,
                    type = ChatMessageType.TEXT,
                    content = content.trim(),
                    timestamp = System.currentTimeMillis(),
                    conversationId = conversation.id
                )

                // Add to conversation and update repository
                val updatedMessages = _uiState.value.messages.toMutableList().apply { add(newMessage) }
                repository.addNewMessage(newMessage)

                _uiState.value = _uiState.value.copy(
                    messages = updatedMessages,
                    inputText = "", // Clear input
                    scrollToBottomEvent = true // Trigger scroll
                )
                android.util.Log.d("MessageDetailViewModel", "Message sent: $content")

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(toastMessage = "发送消息失败: ${e.message}")
                android.util.Log.e("MessageDetailViewModel", "Failed to send message", e)
            }
        }
    }

    fun onQuickActionClick(action: String) {
        _uiState.value = _uiState.value.copy(toastMessage = "$action - 功能开发中")
        android.util.Log.d("MessageDetailViewModel", "Quick action clicked: $action")
    }

    fun onProductCardClick(productId: String) {
        _uiState.value = _uiState.value.copy(navigationEvent = NavigationEvent.ToProductDetail(productId))
        android.util.Log.d("MessageDetailViewModel", "Product card clicked: $productId")
    }

    fun onBackClick() {
        _uiState.value = _uiState.value.copy(navigationEvent = NavigationEvent.NavigateBack)
        android.util.Log.d("MessageDetailViewModel", "Back button clicked")
    }

    fun clearToast() {
        _uiState.value = _uiState.value.copy(toastMessage = null)
    }

    fun clearNavigationEvent() {
        _uiState.value = _uiState.value.copy(navigationEvent = NavigationEvent.None)
    }

    fun clearScrollToBottomEvent() {
        _uiState.value = _uiState.value.copy(scrollToBottomEvent = false)
    }

    fun onInputTextChange(newText: String) {
        _uiState.value = _uiState.value.copy(inputText = newText)
    }
}
