package com.example.jd_sim.ui.screen.settle.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jd_sim.domain.model.Address

/**
 * 结算页地址部分
 */
@Composable
fun SettleAddressSection(
    address: Address?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        if (address != null) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (address.isDefault) {
                        Surface(
                            color = Color(0xFFE93B3D),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = "默认",
                                color = Color.White,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    if (address.tag.isNotEmpty()) {
                        Surface(
                            color = Color(0xFFE93B3D),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = address.tag,
                                color = Color.White,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = "${address.province}${address.city}${address.district}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = address.detailAddress,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                val maskedPhone = if (address.phoneNumber.length >= 11) {
                    "${address.phoneNumber.take(3)}****${address.phoneNumber.takeLast(4)}"
                } else {
                    address.phoneNumber
                }

                Text(
                    text = "${address.recipientName} $maskedPhone",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        } else {
            // 没有地址时的处理
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "📍",
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "请选择收货地址",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
