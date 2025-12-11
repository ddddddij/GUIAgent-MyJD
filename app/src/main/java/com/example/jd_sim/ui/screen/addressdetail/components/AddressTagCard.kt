package com.example.jd_sim.ui.screen.addressdetail.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 地址标签选择卡片
 */
@Composable
fun AddressTagCard(
    selectedTag: String,
    onTagSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "标签",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 标签选项
            val tags = listOf("学校", "家", "公司", "购物", "秒送/外卖", "自定义")

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (i in tags.indices step 3) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        for (j in 0 until 3) {
                            val index = i + j
                            if (index < tags.size) {
                                val tag = tags[index]
                                val isSelected = selectedTag == tag

                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            onTagSelect(tag)
                                        },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) Color(0xFFFFF2F0) else Color.Transparent,
                                    border = if (isSelected)
                                        androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE53935))
                                    else
                                        androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDDDDDD))
                                ) {
                                    Text(
                                        text = tag,
                                        modifier = Modifier.padding(12.dp),
                                        fontSize = 14.sp,
                                        color = if (isSelected) Color(0xFFE53935) else Color(0xFF333333)
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}
