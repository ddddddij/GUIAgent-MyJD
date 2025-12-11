package com.example.jd_sim.ui.screen.messagedetail.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 消息输入栏
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInputBar(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onQuickActionClick: (String) -> Unit
) {
    val quickActions = listOf("发订单", "申请价保", "什么时间配送", "我要退换货")

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            // Quick actions
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                items(quickActions) { action ->
                    OutlinedButton(
                        onClick = { onQuickActionClick(action) },
                        modifier = Modifier.height(32.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE74C3C)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFE74C3C)
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Text(
                            text = action,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Input area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                // Voice button
                IconButton(onClick = { onQuickActionClick("语音消息") }) {
                    Icon(
                        Icons.Filled.Mic,
                        contentDescription = "语音",
                        tint = Color(0xFF6C757D)
                    )
                }

                // Text input
                OutlinedTextField(
                    value = inputText,
                    onValueChange = onInputChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("请输入您要咨询的内容…", fontSize = 14.sp) },
                    maxLines = 4,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (inputText.trim().isNotEmpty()) {
                                onSendClick()
                            }
                        }
                    ),
                    shape = RoundedCornerShape(20.dp)
                )

                // Emoji button
                IconButton(onClick = { onQuickActionClick("表情") }) {
                    Icon(
                        Icons.Filled.EmojiEmotions,
                        contentDescription = "表情",
                        tint = Color(0xFF6C757D)
                    )
                }

                // Send button
                Button(
                    onClick = onSendClick,
                    enabled = inputText.trim().isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE74C3C),
                        disabledContainerColor = Color(0xFFCCCCCC)
                    ),
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        Icons.Filled.Send,
                        contentDescription = "发送",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
