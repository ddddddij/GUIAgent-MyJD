package com.example.jd_sim.ui.screen.settle.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 结算页支付方式部分
 */
@Composable
fun SettlePaymentSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "微信支付",
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "微信支付",
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )

            RadioButton(
                selected = true,
                onClick = { /* 已选中，无需操作 */ }
            )
        }
    }
}

/**
 * 结算页底部支付栏
 */
@Composable
fun SettleBottomPaymentBar(
    totalAmount: Double,
    onPaymentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "¥${totalAmount.toInt()}.00",
                fontSize = 20.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = Color(0xFFE93B3D),
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = onPaymentClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE93B3D)
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.width(120.dp)
            ) {
                Text(
                    text = "立即支付",
                    fontSize = 16.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
