package com.example.jd_sim.ui.screen.settle.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.jd_sim.domain.model.SettleProduct

/**
 * 结算页商品部分
 */
@Composable
fun SettleProductSection(
    product: SettleProduct,
    quantity: Int,
    onQuantityIncrease: () -> Unit,
    onQuantityDecrease: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Store Header
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFE93B3D),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        text = "自营",
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = product.storeName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Product Info
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Product Image
                AsyncImage(
                    model = "file:///android_asset/${product.imageUrl}",
                    contentDescription = product.productName,
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Product Details
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = product.productName,
                        fontSize = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = product.spec,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "¥${product.price.toInt()}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE93B3D)
                    )
                }

                // Quantity Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onQuantityDecrease,
                        modifier = Modifier.size(32.dp),
                        contentPadding = PaddingValues(0.dp),
                        shape = CircleShape
                    ) {
                        Text("-", fontSize = 14.sp)
                    }

                    Text(
                        text = quantity.toString(),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontSize = 14.sp
                    )

                    OutlinedButton(
                        onClick = onQuantityIncrease,
                        modifier = Modifier.size(32.dp),
                        contentPadding = PaddingValues(0.dp),
                        shape = CircleShape
                    ) {
                        Text("+", fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Product Features
            Column {
                Text(
                    text = "支持7天无理由退货（防伪盘、密封条损毁不支持）",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = "7天价保",
                    fontSize = 12.sp,
                    color = Color(0xFFFF9800)
                )
            }
        }
    }
}
