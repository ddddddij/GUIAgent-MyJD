package com.example.MyJD.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.MyJD.model.Product
import com.example.MyJD.repository.DataRepository
import com.example.MyJD.ui.theme.JDRed
import com.example.MyJD.ui.theme.JDTextPrimary
import com.example.MyJD.ui.theme.JDTextSecondary
import com.example.MyJD.ui.theme.JDTextHint

@Composable
fun RecommendSection(
    products: List<Product>,
    onProductClick: (String) -> Unit,
    onAddToCart: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Section title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "为你推荐",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = JDTextPrimary
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "查看更多",
                fontSize = 14.sp,
                color = JDTextSecondary,
                modifier = Modifier.clickable { /* TODO: Navigate to more products */ }
            )
        }

        if (products.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Split products into rows of 2
                val rows = products.chunked(2)
                rows.forEach { rowProducts ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowProducts.forEach { product ->
                            ProductCard(
                                product = product,
                                onProductClick = { onProductClick(product.id) },
                                onAddToCart = { onAddToCart(product) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // Fill remaining slot if row is not complete
                        if (rowProducts.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ProductCard(
    product: Product,
    onProductClick: () -> Unit,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onProductClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Product image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                ProductImage(
                    imageUrl = product.imageUrl,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Product name
            Text(
                text = product.name,
                fontSize = 14.sp,
                color = JDTextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Rating and reviews
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "评分",
                    tint = Color(0xFFFFB000),
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "${product.rating}",
                    fontSize = 12.sp,
                    color = JDTextSecondary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "(${product.reviewCount})",
                    fontSize = 12.sp,
                    color = JDTextHint
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Price section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "¥",
                            fontSize = 12.sp,
                            color = JDRed,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${product.price.toInt()}",
                            fontSize = 16.sp,
                            color = JDRed,
                            fontWeight = FontWeight.Bold
                        )
                        val decimal = ((product.price - product.price.toInt()) * 100).toInt()
                        if (decimal > 0) {
                            Text(
                                text = ".${decimal}",
                                fontSize = 12.sp,
                                color = JDRed,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    product.originalPrice?.let { originalPrice ->
                        if (originalPrice > product.price) {
                            Text(
                                text = "¥${originalPrice.toInt()}",
                                fontSize = 12.sp,
                                color = JDTextHint,
                                textDecoration = TextDecoration.LineThrough
                            )
                        }
                    }
                }

                // Add to cart button
                FloatingActionButton(
                    onClick = onAddToCart,
                    modifier = Modifier.size(32.dp),
                    containerColor = JDRed,
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "加入购物车",
                        modifier = Modifier.size(16.dp)
                    )
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
    
    // 检查是否为本地assets图片
    if (imageUrl.startsWith("image/")) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data("file:///android_asset/$imageUrl")
                .crossfade(true)
                .build(),
            contentDescription = "商品图片",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else if (imageUrl.startsWith("http")) {
        // 网络图片
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = "商品图片",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        // emoji或其他占位符
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "📱",
                fontSize = 32.sp,
                color = Color(0xFFCCCCCC)
            )
        }
    }
}