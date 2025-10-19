package com.example.MyJD.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MyJD.model.ProductColor

@Composable
fun ProductVariantSection(
    purchaseTypes: List<String>,
    selectedPurchaseType: Int,
    onPurchaseTypeSelected: (Int) -> Unit,
    colors: List<ProductColor>,
    selectedColorIndex: Int,
    onColorSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 购买方式选择
            PurchaseTypeSelector(
                purchaseTypes = purchaseTypes,
                selectedType = selectedPurchaseType,
                onTypeSelected = onPurchaseTypeSelected
            )
            
            // 颜色选择
            ColorSelector(
                colors = colors,
                selectedIndex = selectedColorIndex,
                onColorSelected = onColorSelected
            )
        }
    }
}

@Composable
private fun PurchaseTypeSelector(
    purchaseTypes: List<String>,
    selectedType: Int,
    onTypeSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        purchaseTypes.forEachIndexed { index, type ->
            val isSelected = index == selectedType
            
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTypeSelected(index) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFFFFF3E0) else Color(0xFFF5F5F5)
                ),
                border = if (isSelected) {
                    androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2231A))
                } else null,
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = type,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color(0xFFE2231A) else Color(0xFF333333)
                    )
                    
                    if (index == 1) { // 以旧换新
                        Text(
                            text = "预估仅需¥399",
                            fontSize = 12.sp,
                            color = Color(0xFFE2231A),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorSelector(
    colors: List<ProductColor>,
    selectedIndex: Int,
    onColorSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 颜色标题
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "颜色",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )
            
            if (selectedIndex < colors.size) {
                Text(
                    text = colors[selectedIndex].name,
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                text = "共${colors.size}款",
                fontSize = 12.sp,
                color = Color(0xFF999999)
            )
        }
        
        // 颜色选择器
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(colors) { index, color ->
                ColorOptionItem(
                    color = color,
                    isSelected = index == selectedIndex,
                    onClick = { onColorSelected(index) }
                )
            }
        }
        
        // 补贴标签
        if (selectedIndex < colors.size && colors[selectedIndex].subsidyTags.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(colors[selectedIndex].subsidyTags.size) { index ->
                    val tag = colors[selectedIndex].subsidyTags[index]
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = when (tag) {
                                "政府补贴" -> Color(0xFFE8F5E8)
                                "已减100" -> Color(0xFFE8F5E8)
                                "国家贴息" -> Color(0xFFFFF3E0)
                                "12期免息" -> Color(0xFFE2231A)
                                else -> Color(0xFFF5F5F5)
                            }
                        ),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = tag,
                            fontSize = 12.sp,
                            color = when (tag) {
                                "政府补贴" -> Color(0xFF00C853)
                                "已减100" -> Color(0xFF00C853)
                                "国家贴息" -> Color(0xFFFF6F00)
                                "12期免息" -> Color.White
                                else -> Color(0xFF666666)
                            },
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorOptionItem(
    color: ProductColor,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 颜色图片/emoji
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    color = Color(android.graphics.Color.parseColor(color.colorCode)),
                    shape = RoundedCornerShape(8.dp)
                )
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) Color(0xFFE2231A) else Color(0xFFDDDDDD),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = color.image,
                fontSize = 24.sp
            )
        }
        
        // 颜色名称
        Text(
            text = color.name,
            fontSize = 12.sp,
            color = if (isSelected) Color(0xFFE2231A) else Color(0xFF666666),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}