package com.example.MyJD.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MyJD.data.model.DeliveryInfo

@Composable
fun ProductDeliverySection(
    deliveryInfo: DeliveryInfo,
    onAddressClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 配送时间
            DeliveryInfoRow(
                icon = "📦",
                title = "配送",
                content = deliveryInfo.deliveryTime,
                contentColor = Color(0xFF333333)
            )
            
            // 配送地址
            DeliveryInfoRow(
                icon = "📍",
                title = "送至",
                content = "${deliveryInfo.address} ${deliveryInfo.shippingFee}",
                contentColor = Color(0xFF333333),
                onClick = onAddressClick
            )
            
            // 物流服务
            DeliveryInfoRow(
                icon = "🚚",
                title = "由",
                content = deliveryInfo.logistics,
                contentColor = Color(0xFFE2231A)
            )
            
            // 退货政策
            DeliveryInfoRow(
                icon = "✅",
                title = "服务",
                content = deliveryInfo.returnPolicy,
                contentColor = Color(0xFF666666)
            )
            
            // 保修信息
            if (deliveryInfo.warranty.isNotEmpty()) {
                DeliveryInfoRow(
                    icon = "⚡",
                    title = "活动",
                    content = deliveryInfo.warranty,
                    contentColor = Color(0xFFE2231A)
                )
            }
        }
    }
}

@Composable
private fun DeliveryInfoRow(
    icon: String,
    title: String,
    content: String,
    contentColor: Color,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 图标
        Text(
            text = icon,
            fontSize = 16.sp
        )
        
        // 标题
        Text(
            text = title,
            fontSize = 14.sp,
            color = Color(0xFF666666),
            modifier = Modifier.width(40.dp)
        )
        
        // 内容
        if (onClick != null) {
            TextButton(
                onClick = onClick,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = content,
                    fontSize = 14.sp,
                    color = contentColor,
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            Text(
                text = content,
                fontSize = 14.sp,
                color = contentColor,
                modifier = Modifier.weight(1f)
            )
        }
    }
}