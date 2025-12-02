package com.example.MyJD.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MyJD.model.Address

@Composable
fun AddressItemCard(
    address: Address,
    onAddressClick: (Address) -> Unit = {},
    onEditClick: (Address) -> Unit = {},
    onDeleteClick: (Address) -> Unit = {},
    onCopyClick: (Address) -> Unit = {},
    onSetDefaultClick: (Address) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onAddressClick(address) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 收货人信息行
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 收货人姓名
                Text(
                    text = address.recipientName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // 手机号（隐藏中间4位）
                val maskedPhone = if (address.phoneNumber.length >= 11) {
                    "${address.phoneNumber.take(3)}****${address.phoneNumber.takeLast(4)}"
                } else {
                    address.phoneNumber
                }
                Text(
                    text = maskedPhone,
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // 默认地址标签
                if (address.isDefault) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFE53935)
                    ) {
                        Text(
                            text = "默认",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 地址信息
            Text(
                text = address.fullAddress,
                fontSize = 14.sp,
                color = Color(0xFF666666),
                lineHeight = 20.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 标签显示
            if (address.tag.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFF5F5F5)
                ) {
                    Text(
                        text = address.tag,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        color = Color(0xFF999999)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // 操作按钮行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 左侧：设为默认地址复选框
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { 
                        if (!address.isDefault) {
                            onSetDefaultClick(address)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "设为默认地址",
                        tint = if (address.isDefault) Color(0xFFE53935) else Color(0xFFDDDDDD),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "设为购物默认",
                        fontSize = 12.sp,
                        color = if (address.isDefault) Color(0xFFE53935) else Color(0xFF999999)
                    )
                }
                
                // 右侧：操作按钮
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 删除按钮
                    TextButton(
                        onClick = { onDeleteClick(address) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "删除",
                            fontSize = 14.sp,
                            color = Color(0xFF999999)
                        )
                    }
                    
                    // 复制按钮
                    TextButton(
                        onClick = { onCopyClick(address) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "复制",
                            fontSize = 14.sp,
                            color = Color(0xFF999999)
                        )
                    }
                    
                    // 修改按钮
                    TextButton(
                        onClick = { onEditClick(address) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "修改",
                            fontSize = 14.sp,
                            color = Color(0xFF999999)
                        )
                    }
                }
            }
        }
    }
}