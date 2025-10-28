package com.example.MyJD.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.MyJD.model.Message
import com.example.MyJD.model.MessageType
import com.example.MyJD.model.MessageSubType
import com.example.MyJD.model.ConversationData
import com.example.MyJD.model.ConversationSummary
import com.example.MyJD.model.ChatMessage
import com.example.MyJD.model.ChatSender
import com.example.MyJD.model.ChatMessageType
import com.example.MyJD.repository.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(private val repository: DataRepository) : ViewModel() {
    
    private val _allMessages = MutableStateFlow<List<Message>>(emptyList())
    
    private val _filteredMessages = MutableStateFlow<List<Message>>(emptyList())
    val filteredMessages: StateFlow<List<Message>> = _filteredMessages.asStateFlow()
    
    private val _conversationSummaries = MutableStateFlow<List<ConversationSummary>>(emptyList())
    val conversationSummaries: StateFlow<List<ConversationSummary>> = _conversationSummaries.asStateFlow()
    
    private val _selectedMessageType = MutableStateFlow(MessageType.CUSTOMER_SERVICE)
    val selectedMessageType: StateFlow<MessageType> = _selectedMessageType.asStateFlow()
    
    private val _selectedSubType = MutableStateFlow(MessageSubType.ALL)
    val selectedSubType: StateFlow<MessageSubType> = _selectedSubType.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadMessages()
        loadConversationSummaries()
    }

    private fun loadMessages() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 加载原始消息
                val originalMessages = repository.loadMessages()
                
                // 加载新发送的消息并转换为Message格式
                val newChatMessages = repository.getNewMessages()
                val convertedNewMessages = newChatMessages.map { chatMessage ->
                    Message(
                        id = chatMessage.id,
                        type = MessageType.CUSTOMER_SERVICE, // 新消息默认归类为客服类型
                        subType = MessageSubType.ALL,
                        senderName = "用户发送", // 标识为用户发送的消息
                        senderAvatar = null,
                        content = chatMessage.content,
                        timestamp = chatMessage.timestamp,
                        isRead = true, // 用户发送的消息标记为已读
                        isOfficial = false,
                        hasUnreadDot = false
                    )
                }
                
                // 合并原始消息和新消息，按时间戳排序
                val allMessages = (originalMessages + convertedNewMessages).sortedByDescending { it.timestamp }
                _allMessages.value = allMessages
                filterMessages()
            } catch (e: Exception) {
                _allMessages.value = emptyList()
                _filteredMessages.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun loadConversationSummaries() {
        viewModelScope.launch {
            try {
                val summaries = repository.getConversationSummaries()
                _conversationSummaries.value = summaries
            } catch (e: Exception) {
                _conversationSummaries.value = emptyList()
            }
        }
    }

    fun selectMessageType(type: MessageType) {
        _selectedMessageType.value = type
        filterMessages()
    }

    fun selectSubType(subType: MessageSubType) {
        _selectedSubType.value = subType
        filterMessages()
    }

    private fun filterMessages() {
        val currentType = _selectedMessageType.value
        val currentSubType = _selectedSubType.value
        
        val filtered = _allMessages.value.filter { message ->
            val typeMatches = when (currentType) {
                MessageType.CUSTOMER_SERVICE -> message.type == MessageType.CUSTOMER_SERVICE || message.senderName == "京东客服"
                else -> message.type == currentType
            }
            
            val subTypeMatches = when (currentSubType) {
                MessageSubType.ALL -> true
                else -> message.subType == currentSubType
            }
            
            typeMatches && subTypeMatches
        }
        
        _filteredMessages.value = filtered
    }

    fun getMessageTabs(): List<MessageType> {
        return listOf(
            MessageType.CUSTOMER_SERVICE,
            MessageType.LOGISTICS,
            MessageType.REMINDER,
            MessageType.PROMOTION,
            MessageType.REVIEW
        )
    }

    fun getSubTabs(): List<MessageSubType> {
        return listOf(
            MessageSubType.ALL,
            MessageSubType.SHOPPING,
            MessageSubType.INSTANT_DELIVERY,
            MessageSubType.TAKEAWAY
        )
    }

    fun getMessageTypeDisplayName(type: MessageType): String {
        return when (type) {
            MessageType.CUSTOMER_SERVICE -> "客服"
            MessageType.LOGISTICS -> "物流"
            MessageType.REMINDER -> "提醒"
            MessageType.PROMOTION -> "优惠"
            MessageType.REVIEW -> "点评"
        }
    }

    fun getSubTypeDisplayName(subType: MessageSubType): String {
        return when (subType) {
            MessageSubType.ALL -> "全部"
            MessageSubType.SHOPPING -> "购物"
            MessageSubType.INSTANT_DELIVERY -> "秒送"
            MessageSubType.TAKEAWAY -> "外卖"
        }
    }
    
    fun getConversationIdForMessage(message: Message): String {
        // Create a mapping from legacy message sender names to conversation IDs
        return when (message.senderName) {
            "京东客服" -> "C20251021001"
            "得力装订文具旗舰店" -> "C20251021002"
            "文轩网旗舰店" -> "C20251021003"
            "Apple产品京东自营旗舰店" -> "C20251021004"
            "京东秒送" -> "C20251021005"
            else -> {
                // For other senders, create a fallback conversation ID based on the message type
                when (message.type) {
                    MessageType.CUSTOMER_SERVICE -> "C20251021001" // Default to JD customer service
                    MessageType.LOGISTICS -> "C20251021005" // Default to JD delivery
                    else -> "C20251021001" // Default fallback
                }
            }
        }
    }
    
    // 发送新消息的方法
    fun sendMessage(content: String, targetSender: String = "京东客服") {
        viewModelScope.launch {
            try {
                val newMessage = ChatMessage(
                    id = "user_msg_${System.currentTimeMillis()}",
                    sender = ChatSender.USER,
                    type = ChatMessageType.TEXT,
                    content = content,
                    timestamp = System.currentTimeMillis()
                )
                
                // 保存到持久化存储
                repository.addNewMessage(newMessage)
                
                // 重新加载消息以显示新消息
                loadMessages()
                loadConversationSummaries()
            } catch (e: Exception) {
                // 处理发送失败的情况
                android.util.Log.e("ChatViewModel", "Failed to send message", e)
            }
        }
    }
    
    // 获取免打扰设置
    fun getMuteSetting(senderName: String): Boolean {
        return repository.getMuteSetting(senderName)
    }
    
    // 设置免打扰
    fun setMuteSetting(senderName: String, isMuted: Boolean) {
        repository.setMuteSetting(senderName, isMuted)
    }
}