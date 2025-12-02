package com.example.MyJD.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.MyJD.model.ColorOption

@Composable
fun ColorSelector(
    colorOptions: List<ColorOption>,
    selectedColor: String,
    onColorSelected: (String) -> Unit,
    onZoomClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 标题
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "外观 (${colorOptions.size})",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ZoomIn,
                    contentDescription = "列表",
                    tint = Color(0xFF666666),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "列表",
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
            }
        }
        
        // 颜色选项横向滚动
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 0.dp)
        ) {
            items(colorOptions) { color ->
                ColorOptionCard(
                    color = color,
                    isSelected = color.name == selectedColor,
                    onClick = { 
                        if (color.available) {
                            onColorSelected(color.name)
                        }
                    },
                    onZoomClick = { onZoomClick(color.image) }
                )
            }
        }
    }
}

@Composable
private fun ColorOptionCard(
    color: ColorOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    onZoomClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    Card(
        modifier = modifier
            .width(100.dp)
            .height(140.dp)
            .clickable(enabled = color.available) { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = if (isSelected && color.available) {
            androidx.compose.foundation.BorderStroke(3.dp, Color(0xFFE2231A))
        } else {
            androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // 商品图片
                Box(
                    modifier = Modifier
                        .size(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (color.image.startsWith("image/")) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data("file:///android_asset/${color.image}")
                                .crossfade(true)
                                .build(),
                            contentDescription = color.name,
                            modifier = Modifier
                                .fillMaxSize()
                                .let { mod ->
                                    if (!color.available) {
                                        mod.background(Color.Black.copy(alpha = 0.4f))
                                    } else mod
                                },
                            contentScale = ContentScale.Fit,
                            alpha = if (color.available) 1f else 0.4f
                        )
                    } else {
                        Text(
                            text = "📱",
                            fontSize = 32.sp,
                            color = if (color.available) Color.Black else Color.Gray
                        )
                    }
                }
                
                // 颜色名称
                Text(
                    text = color.name,
                    fontSize = 12.sp,
                    color = if (color.available) Color(0xFF333333) else Color(0xFF999999)
                )
                
                // 价格
                Text(
                    text = "¥${color.price.toInt()}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (color.available) Color(0xFFE2231A) else Color(0xFF999999)
                )
                
                // 库存标签
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (color.available) Color(0xFFE2231A) else Color(0xFF999999)
                    ),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = color.stockTag,
                        fontSize = 10.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            
            // 放大图标
            IconButton(
                onClick = onZoomClick,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ZoomIn,
                    contentDescription = "查看大图",
                    tint = Color(0xFF666666),
                    modifier = Modifier.size(16.dp)
                )
            }
            
            // 选中角标
            if (isSelected && color.available) {
                Card(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE2231A)
                    ),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "万人购买",
                        fontSize = 8.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}