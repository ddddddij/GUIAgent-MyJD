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
import com.example.jd_sim.domain.model.SettlePricing
import com.example.jd_sim.domain.model.Coupon

/**
 * 结算页价格部分
 */
@Composable
fun SettlePricingSection(
    pricing: SettlePricing,
    selectedCoupon: Coupon?,
    onCouponClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Product Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "商品金额",
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Text(
                    text = "¥${pricing.productAmount.toInt()}.00",
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Shipping Fee
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "运费",
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Text(
                    text = "¥${pricing.shippingFee.toInt()}.00",
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Coupon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCouponClick() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "优惠券",
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Text(
                    text = if (selectedCoupon != null) {
                        "已选择: ${selectedCoupon.getDisplayText()}"
                    } else {
                        "${pricing.couponCount}张可用"
                    },
                    fontSize = 14.sp,
                    color = if (selectedCoupon != null) Color(0xFFE93B3D) else Color.Gray
                )
            }

            // 显示优惠券优惠金额（如果有选中的优惠券）
            if (pricing.couponDiscount > 0) {
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "优惠券优惠",
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "-¥${pricing.couponDiscount.toInt()}.00",
                        fontSize = 14.sp,
                        color = Color(0xFFE93B3D)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Divider(color = Color.Gray.copy(alpha = 0.3f))

            Spacer(modifier = Modifier.height(12.dp))

            // Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "合计：",
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    text = "¥${pricing.totalAmount.toInt()}.00",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE93B3D)
                )
            }
        }
    }
}
