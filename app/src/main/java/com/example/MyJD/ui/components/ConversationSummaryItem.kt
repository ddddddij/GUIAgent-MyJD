package com.example.MyJD.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MyJD.model.ConversationSummary
import com.example.MyJD.ui.theme.JDTextPrimary
import com.example.MyJD.ui.theme.JDTextSecondary
import com.example.MyJD.ui.theme.JDTextHint
import com.example.MyJD.ui.theme.JDRed
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ConversationSummaryItem(
    conversationSummary: ConversationSummary,
    onClick: (ConversationSummary) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(conversationSummary) },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // 头像
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = Color(0xFFF5F5F5),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = conversationSummary.chatAvatar,
                    fontSize = 24.sp
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // 消息内容
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // 聊天名称
                    Text(
                        text = conversationSummary.chatName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = JDTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // 时间
                    Text(
                        text = formatTimestamp(conversationSummary.lastMessageTime),
                        fontSize = 12.sp,
                        color = JDTextHint
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // 最新消息内容
                Text(
                    text = conversationSummary.lastMessage,
                    fontSize = 12.sp,
                    color = JDTextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // 未读红点
            if (conversationSummary.hasUnread) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = JDRed,
                            shape = CircleShape
                        )
                )
            }
        }
    }
    
    // 分割线
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFEEEEEE),
        thickness = 0.5.dp
    )
}

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    
    return when {
        diff < 60 * 1000 -> "刚刚"
        diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)}分钟前"
        diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)}小时前"
        else -> {
            val sdf = SimpleDateFormat("MM-dd", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}