package com.example.MyJD.presenter

import com.example.MyJD.model.ChatMessage
import com.example.MyJD.model.Conversation

interface MessageDetailContract {
    
    interface View {
        fun showConversation(conversation: Conversation)
        fun showMessages(messages: List<ChatMessage>)
        fun addMessage(message: ChatMessage)
        fun showToast(message: String)
        fun clearInputText()
        fun scrollToBottom()
        fun showLoading(show: Boolean)
        fun navigateToProduct(productId: String)
        fun setTitle(title: String)
        fun setAvatar(avatar: String)
    }
    
    interface Presenter {
        fun attach(view: View)
        fun detach()
        fun loadConversation(conversationId: String)
        fun sendMessage(content: String)
        fun onQuickActionClick(action: String)
        fun onProductCardClick(productId: String)
        fun onBackClick()
    }
}