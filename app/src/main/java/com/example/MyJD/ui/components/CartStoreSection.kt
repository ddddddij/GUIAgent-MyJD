package com.example.MyJD.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CartStoreSection(
    storeName: String,
    subsidyInfo: String,
    isSelected: Boolean,
    onSelectionToggle: () -> Unit,
    onStoreClick: () -> Unit = {},
    onCouponClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onStoreClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 店铺信息行
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 选择框
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onSelectionToggle() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFFE2231A),
                        uncheckedColor = Color(0xFFCCCCCC)
                    )
                )
                
                // 自营标签
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE2231A)
                    ),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "自营",
                        fontSize = 12.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                
                // 店铺名称
                Text(
                    text = storeName,
                    fontSize = 14.sp,
                    color = Color(0xFF333333),
                    modifier = Modifier.weight(1f)
                )
                
                // 箭头
                Icon(
                    imageVector = Icons.Filled.ArrowForwardIos,
                    contentDescription = "进入店铺",
                    tint = Color(0xFF999999),
                    modifier = Modifier.size(12.dp)
                )
            }
            
            // 补贴信息
            if (subsidyInfo.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = subsidyInfo,
                        fontSize = 12.sp,
                        color = Color(0xFFE2231A)
                    )
                    
                    TextButton(
                        onClick = onCouponClick,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "领券凑单",
                            fontSize = 12.sp,
                            color = Color(0xFFE2231A)
                        )
                        
                        Icon(
                            imageVector = Icons.Filled.ArrowForwardIos,
                            contentDescription = "领券",
                            tint = Color(0xFFE2231A),
                            modifier = Modifier.size(8.dp)
                        )
                    }
                }
            }
        }
    }
}