package com.example.jd_sim.ui.screen.settle.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jd_sim.domain.model.Coupon

/**
 * 优惠券选择对话框
 */
@Composable
fun SettleCouponSelectionDialog(
    availableCoupons: List<Coupon>,
    orderAmount: Double,
    selectedCoupon: Coupon?,
    onCouponSelected: (Coupon?) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "选择优惠券",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn {
                // 不使用优惠券选项
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCouponSelected(null) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedCoupon == null,
                            onClick = { onCouponSelected(null) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "不使用优惠券",
                            fontSize = 16.sp
                        )
                    }
                }

                // 优惠券列表
                items(availableCoupons.size) { index ->
                    val coupon = availableCoupons[index]
                    val isUsable = coupon.isAvailable(orderAmount)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = isUsable) {
                                if (isUsable) onCouponSelected(coupon)
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedCoupon?.id == coupon.id,
                            onClick = { if (isUsable) onCouponSelected(coupon) },
                            enabled = isUsable
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = coupon.getDisplayText(),
                                fontSize = 16.sp,
                                color = if (isUsable) Color.Black else Color.Gray
                            )
                            Text(
                                text = if (isUsable) {
                                    "有效期至 ${coupon.validUntil}"
                                } else {
                                    "不满足使用条件（订单金额需≥¥${coupon.minAmount.toInt()}）"
                                },
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("确定")
            }
        }
    )
}
