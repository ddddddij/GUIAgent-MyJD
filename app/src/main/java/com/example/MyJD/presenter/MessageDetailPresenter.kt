package com.example.MyJD.presenter

import com.example.MyJD.model.*
import com.example.MyJD.repository.DataRepository
import kotlinx.coroutines.*
import com.google.gson.Gson
import com.google.gson.JsonParser

class MessageDetailPresenter(
    private val repository: DataRepository
) : MessageDetailContract.Presenter {
    
    private var view: MessageDetailContract.View? = null
    private var currentConversation: Conversation? = null
    private val gson = Gson()
    private val presenterScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    override fun attach(view: MessageDetailContract.View) {
        this.view = view
    }
    
    override fun detach() {
        this.view = null
        presenterScope.cancel()
    }
    
    override fun loadConversation(conversationId: String) {
        presenterScope.launch {
            view?.showLoading(true)
            try {
                val conversationData = loadConversationData()
                val conversation = conversationData.conversations.find { it.id == conversationId }
                
                if (conversation != null) {
                    currentConversation = conversation
                    view?.setTitle(conversation.chatName)
                    view?.setAvatar(conversation.chatAvatar)
                    view?.showConversation(conversation)
                    view?.showMessages(conversation.messages)
                    
                    // Auto scroll to bottom after loading
                    delay(100)
                    view?.scrollToBottom()
                    
                    android.util.Log.d("MessageDetailPresenter", "Loaded conversation: ${conversation.chatName} with ${conversation.messages.size} messages")
                } else {
                    view?.showToast("找不到该对话")
                    android.util.Log.w("MessageDetailPresenter", "Conversation not found: $conversationId")
                }
            } catch (e: Exception) {
                view?.showToast("加载对话失败: ${e.message}")
                android.util.Log.e("MessageDetailPresenter", "Failed to load conversation", e)
            } finally {
                view?.showLoading(false)
            }
        }
    }
    
    override fun sendMessage(content: String) {
        if (content.trim().isEmpty()) {
            view?.showToast("请输入消息内容")
            return
        }
        
        val conversation = currentConversation
        if (conversation == null) {
            view?.showToast("对话数据错误")
            return
        }
        
        try {
            // Create new user message
            val newMessage = ChatMessage(
                id = "msg_user_${System.currentTimeMillis()}",
                sender = ChatSender.USER,
                type = ChatMessageType.TEXT,
                content = content.trim(),
                timestamp = System.currentTimeMillis()
            )
            
            // Add to conversation
            conversation.messages.add(newMessage)
            
            // Update view
            view?.addMessage(newMessage)
            view?.clearInputText()
            
            // Auto scroll to bottom after sending
            presenterScope.launch {
                delay(500) // Small delay for animation
                view?.scrollToBottom()
            }
            
            android.util.Log.d("MessageDetailPresenter", "Message sent: $content")
            
        } catch (e: Exception) {
            view?.showToast("发送消息失败: ${e.message}")
            android.util.Log.e("MessageDetailPresenter", "Failed to send message", e)
        }
    }
    
    override fun onQuickActionClick(action: String) {
        view?.showToast("$action - 功能开发中")
        android.util.Log.d("MessageDetailPresenter", "Quick action clicked: $action")
    }
    
    override fun onProductCardClick(productId: String) {
        view?.navigateToProduct(productId)
        android.util.Log.d("MessageDetailPresenter", "Product card clicked: $productId")
    }
    
    override fun onBackClick() {
        // Handle back navigation - view should implement this
        android.util.Log.d("MessageDetailPresenter", "Back button clicked")
    }
    
    private suspend fun loadConversationData(): ConversationData {
        return repository.loadConversationData()
    }
}