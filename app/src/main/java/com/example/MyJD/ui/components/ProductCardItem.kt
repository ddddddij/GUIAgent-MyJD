package com.example.MyJD.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MyJD.model.Product
import java.text.DecimalFormat

@Composable
fun ProductCardItem(
    product: Product,
    onProductClick: (Product) -> Unit,
    onAddToCartClick: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    val priceFormatter = DecimalFormat("#,###")
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onProductClick(product) },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // 商品图片区域
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(
                        Color(0xFFF5F5F5),
                        RoundedCornerShape(6.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = product.imageUrl,
                    fontSize = 48.sp
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 商品标题
            Text(
                text = product.name,
                fontSize = 14.sp,
                color = Color.Black,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 价格信息
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "¥${priceFormatter.format(product.price.toInt())}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE53935)
                )
                
                product.originalPrice?.let { originalPrice ->
                    if (originalPrice > product.price) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "¥${priceFormatter.format(originalPrice.toInt())}",
                            fontSize = 12.sp,
                            color = Color(0xFF999999),
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 评价信息和加购物车按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 评价信息
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⭐",
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${product.rating}",
                        fontSize = 12.sp,
                        color = Color(0xFF777777)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "(${product.reviewCount})",
                        fontSize = 12.sp,
                        color = Color(0xFF777777)
                    )
                }
                
                // 加入购物车按钮
                IconButton(
                    onClick = { onAddToCartClick(product) },
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            Color(0xFFE53935),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "加入购物车",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            
            // 库存状态
            if (!product.isInStock) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "暂时缺货",
                    fontSize = 12.sp,
                    color = Color(0xFFFF9800),
                    fontWeight = FontWeight.Medium
                )
            } else if (product.stock < 10) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "仅剩${product.stock}件",
                    fontSize = 12.sp,
                    color = Color(0xFFFF5722),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}