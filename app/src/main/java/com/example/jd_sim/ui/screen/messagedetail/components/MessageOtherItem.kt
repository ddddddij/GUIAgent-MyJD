package com.example.jd_sim.ui.screen.messagedetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jd_sim.domain.model.ChatMessage
import java.text.SimpleDateFormat
import java.util.*

/**
 * 其他用户消息项
 */
@Composable
fun MessageOtherItem(message: ChatMessage, avatar: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color(0xFFECF0F1), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = avatar,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Card(
                modifier = Modifier.padding(end = 64.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Text(
                    text = message.content,
                    modifier = Modifier.padding(12.dp),
                    color = Color.Black,
                    fontSize = 14.sp
                )
            }

            Text(
                text = formatTime(message.timestamp),
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }

        Spacer(modifier = Modifier.width(64.dp))
    }
}

private fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
