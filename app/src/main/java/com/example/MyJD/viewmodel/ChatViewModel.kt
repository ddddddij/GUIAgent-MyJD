package com.example.MyJD.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.MyJD.model.Message
import com.example.MyJD.model.MessageType
import com.example.MyJD.model.MessageSubType
import com.example.MyJD.repository.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(private val repository: DataRepository) : ViewModel() {
    
    private val _allMessages = MutableStateFlow<List<Message>>(emptyList())
    
    private val _filteredMessages = MutableStateFlow<List<Message>>(emptyList())
    val filteredMessages: StateFlow<List<Message>> = _filteredMessages.asStateFlow()
    
    private val _selectedMessageType = MutableStateFlow(MessageType.CUSTOMER_SERVICE)
    val selectedMessageType: StateFlow<MessageType> = _selectedMessageType.asStateFlow()
    
    private val _selectedSubType = MutableStateFlow(MessageSubType.ALL)
    val selectedSubType: StateFlow<MessageSubType> = _selectedSubType.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadMessages()
    }

    private fun loadMessages() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val messages = repository.loadMessages()
                _allMessages.value = messages
                filterMessages()
            } catch (e: Exception) {
                _allMessages.value = emptyList()
                _filteredMessages.value = emptyList()
            } finally {
                _isLoading.value = false
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
}