package com.example.jd_sim.ui.screen.messagedetail.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jd_sim.domain.model.ChatMessage

/**
 * 系统消息项
 */
@Composable
fun MessageSystemItem(message: ChatMessage) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                fontSize = 12.sp,
                color = Color(0xFF6C757D),
                textAlign = TextAlign.Center
            )
        }
    }
}
