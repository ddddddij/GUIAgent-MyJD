package com.example.jd_sim.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.jd_sim.domain.model.CartItemSpec
import com.google.accompanist.flowlayout.FlowRow

@Composable
fun CartProductCard(
    cartItem: CartItemSpec,
    onSelectionToggle: () -> Unit,
    onQuantityChange: (Int) -> Unit,
    onSpecChange: () -> Unit = {},
    onRemove: (CartItemSpec) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 选择框
                Checkbox(
                    checked = cartItem.selected,
                    onCheckedChange = { onSelectionToggle() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFFE2231A),
                        uncheckedColor = Color(0xFFCCCCCC)
                    )
                )
                
                // 商品图片
                ProductImage(
                    imageUrl = cartItem.image,
                    modifier = Modifier.size(80.dp)
                )
                
                // 商品信息
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        // 商品标题
                        Text(
                            text = cartItem.productName,
                            fontSize = 14.sp,
                            color = Color(0xFF333333),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(end = 32.dp) // 避免与按钮重叠
                        )
                        
                        // 删除按钮
                        IconButton(
                            onClick = { onRemove(cartItem) },
                            modifier = Modifier.align(Alignment.TopEnd).size(24.dp)
                        ) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "删除",
                                tint = Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    
                    // 规格信息
                    ProductSpecInfo(
                        specText = cartItem.getSpecText(),
                        onSpecChange = onSpecChange
                    )
                    
                    // 促销标签
                    if (cartItem.promotionTags.isNotEmpty()) {
                        PromotionTags(
                            tags = cartItem.promotionTags
                        )
                    }
                    
                    // 价格变动提示
                    if (cartItem.subsidyInfo.isNotEmpty()) {
                        Text(
                            text = cartItem.subsidyInfo,
                            fontSize = 12.sp,
                            color = Color(0xFFE2231A)
                        )
                    }
                    
                    // 价格和数量选择
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // 价格信息
                        Column {
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "¥${cartItem.price.toInt()}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE2231A)
                                )
                                
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFF4CAF50)
                                    ),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "政府补贴价",
                                        fontSize = 10.sp,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            
                            if (cartItem.originalPrice > cartItem.price) {
                                Text(
                                    text = "¥${cartItem.originalPrice.toInt()}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF999999),
                                    textDecoration = TextDecoration.LineThrough
                                )
                            }
                        }
                        
                        // 数量选择器
                        QuantitySelector(
                            quantity = cartItem.quantity,
                            onQuantityChange = onQuantityChange
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductImage(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        )
    ) {
        if (imageUrl.startsWith("image/")) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data("file:///android_asset/$imageUrl")
                    .crossfade(true)
                    .build(),
                contentDescription = "商品图片",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📱",
                    fontSize = 32.sp
                )
            }
        }
    }
}

@Composable
private fun ProductSpecInfo(
    specText: String,
    onSpecChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.clickable { onSpecChange() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = specText,
            fontSize = 12.sp,
            color = Color(0xFF666666)
        )
        
        Icon(
            imageVector = Icons.Filled.KeyboardArrowDown,
            contentDescription = "更改规格",
            tint = Color(0xFF999999),
            modifier = Modifier.size(12.dp)
        )
    }
}

@Composable
private fun PromotionTags(
    tags: List<String>,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier,
        mainAxisSpacing = 4.dp,
        crossAxisSpacing = 4.dp
    ) {
        tags.forEach { tag ->
            PromotionTag(text = tag)
        }
    }
}

@Composable
private fun PromotionTag(
    text: String,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        text.contains("政府补贴") -> Color(0xFFE8F5E9)
        text.contains("免息") -> Color(0xFFFFF3E0)
        text.contains("价保") -> Color(0xFFE3F2FD)
        else -> Color(0xFFF5F5F5)
    }
    
    val textColor = when {
        text.contains("政府补贴") -> Color(0xFF4CAF50)
        text.contains("免息") -> Color(0xFFFF9800)
        text.contains("价保") -> Color(0xFF2196F3)
        else -> Color(0xFF666666)
    }
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            color = textColor,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}