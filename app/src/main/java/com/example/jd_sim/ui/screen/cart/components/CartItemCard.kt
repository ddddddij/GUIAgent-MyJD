package com.example.jd_sim.ui.screen.cart.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jd_sim.domain.model.CartItem
import com.example.jd_sim.ui.theme.JDRed
import com.example.jd_sim.ui.theme.JDTextPrimary

/**
 * 购物车商品卡片（旧版本，保留用于兼容）
 */
@Composable
fun CartItemCard(
    cartItem: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit,
    onSelectionToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = cartItem.isSelected,
                onCheckedChange = { onSelectionToggle() },
                colors = CheckboxDefaults.colors(checkedColor = JDRed)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Product image placeholder
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📱", fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = cartItem.product.name,
                    fontSize = 14.sp,
                    color = JDTextPrimary,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "¥${cartItem.product.price}",
                    fontSize = 16.sp,
                    color = JDRed,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Quantity controls
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.background(
                            Color(0xFFF5F5F5),
                            RoundedCornerShape(4.dp)
                        )
                    ) {
                        IconButton(
                            onClick = {
                                if (cartItem.quantity > 1) {
                                    onQuantityChange(cartItem.quantity - 1)
                                }
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Filled.Remove,
                                contentDescription = "减少",
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Text(
                            text = "${cartItem.quantity}",
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        IconButton(
                            onClick = { onQuantityChange(cartItem.quantity + 1) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "增加",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(onClick = onRemove) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "删除",
                            tint = Color.Gray
                        )
                    }
                }
            }
        }
    }
}
