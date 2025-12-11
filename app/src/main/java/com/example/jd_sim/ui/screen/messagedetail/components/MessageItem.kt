package com.example.jd_sim.ui.screen.messagedetail.components

import androidx.compose.runtime.Composable
import com.example.jd_sim.domain.model.ChatMessage
import com.example.jd_sim.domain.model.ChatMessageType
import com.example.jd_sim.domain.model.ChatSender

/**
 * 消息项包装组件
 */
@Composable
fun MessageItem(
    message: ChatMessage,
    chatAvatar: String,
    onProductClick: (String) -> Unit
) {
    when (message.type) {
        ChatMessageType.SYSTEM -> {
            MessageSystemItem(message)
        }
        ChatMessageType.PRODUCT -> {
            MessageProductItem(message, onProductClick)
        }
        else -> {
            if (message.sender == ChatSender.USER) {
                MessageUserItem(message)
            } else {
                MessageOtherItem(message, chatAvatar)
            }
        }
    }
}
