package com.example.jd_sim.ui.screen.cart.components

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
import com.example.jd_sim.ui.theme.JDRed
import com.example.jd_sim.ui.theme.JDTextPrimary
import com.example.jd_sim.ui.theme.JDTextSecondary

/**
 * 购物车底部栏（旧版本，保留用于兼容）
 */
@Composable
fun CartBottomBar(
    totalPrice: Double,
    selectedCount: Int,
    onCheckout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "已选商品($selectedCount)件",
                    fontSize = 12.sp,
                    color = JDTextSecondary
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "合计: ¥",
                        fontSize = 14.sp,
                        color = JDTextPrimary
                    )
                    Text(
                        text = "${totalPrice.toInt()}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = JDRed
                    )
                    val decimal = ((totalPrice - totalPrice.toInt()) * 100).toInt()
                    if (decimal > 0) {
                        Text(
                            text = ".${decimal}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = JDRed
                        )
                    }
                }
            }

            Button(
                onClick = onCheckout,
                enabled = selectedCount > 0,
                colors = ButtonDefaults.buttonColors(containerColor = JDRed),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "结算($selectedCount)",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * 购物车底部栏（新版本）
 */
@Composable
fun CartNewBottomBar(
    isAllSelected: Boolean,
    onAllSelectToggle: () -> Unit,
    selectedCount: Int,
    totalPrice: Double,
    onCheckout: () -> Unit,
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 全选
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Checkbox(
                    checked = isAllSelected,
                    onCheckedChange = { onAllSelectToggle() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFFE2231A),
                        uncheckedColor = Color(0xFFCCCCCC)
                    )
                )

                Text(
                    text = "全选",
                    fontSize = 14.sp,
                    color = Color(0xFF333333)
                )
            }

            // 合计和结算
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "合计：¥",
                            fontSize = 14.sp,
                            color = Color(0xFF333333)
                        )
                        Text(
                            text = "${totalPrice.toInt()}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE2231A)
                        )
                        val decimal = ((totalPrice - totalPrice.toInt()) * 100).toInt()
                        if (decimal > 0) {
                            Text(
                                text = ".${String.format("%02d", decimal)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE2231A)
                            )
                        }
                    }
                }

                Button(
                    onClick = onCheckout,
                    enabled = selectedCount > 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE2231A),
                        disabledContainerColor = Color(0xFFCCCCCC)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "去结算",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
