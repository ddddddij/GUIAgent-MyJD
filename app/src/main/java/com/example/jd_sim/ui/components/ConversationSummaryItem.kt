package com.example.jd_sim.ui.components

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
import com.example.jd_sim.domain.model.ConversationSummary
import com.example.jd_sim.ui.theme.JDTextPrimary
import com.example.jd_sim.ui.theme.JDTextSecondary
import com.example.jd_sim.ui.theme.JDTextHint
import com.example.jd_sim.ui.theme.JDRed
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ConversationSummaryItem(
    conversationSummary: ConversationSummary,
    onClick: (ConversationSummary) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clickable { onClick(conversationSummary) }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // 头像
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = Color(0xFFF0F0F0),
                        shape = RoundedCornerShape(8.dp)
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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 聊天名称
                    Text(
                        text = conversationSummary.chatName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = JDTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // 时间
                    Text(
                        text = formatTimestamp(conversationSummary.lastMessageTime),
                        fontSize = 11.sp,
                        color = JDTextHint
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 最新消息内容
                val lastMessage = if (conversationSummary.chatName == "得力装订文具旗舰店" || conversationSummary.chatName == "Apple官方旗舰店") {
                    "<好物分享>"
                } else {
                    conversationSummary.lastMessage
                }
                Text(
                    text = lastMessage,
                    fontSize = 13.sp,
                    color = JDTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

    // 分割线（左侧留出头像位置）
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 76.dp),
            color = Color(0xFFF0F0F0),
            thickness = 0.5.dp
        )
    }
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