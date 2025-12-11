package com.example.jd_sim.ui.screen.settle.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jd_sim.domain.model.SettleService
import com.example.jd_sim.domain.model.SettleDelivery

/**
 * 结算页服务和配送部分
 */
@Composable
fun SettleServiceDeliverySection(
    service: SettleService,
    delivery: SettleDelivery,
    onServiceClick: () -> Unit,
    onDeliveryClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Service
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onServiceClick() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "服务",
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Text(
                    text = service.title,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Delivery
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDeliveryClick() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "配送",
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = delivery.method,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = delivery.time,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Receive Method
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDeliveryClick() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "收货方式",
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Text(
                    text = delivery.receiveType,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
