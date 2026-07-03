package com.example.jd_sim.ui.components

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
import com.example.jd_sim.domain.model.ProductColor

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
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
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
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        purchaseTypes.forEachIndexed { index, type ->
            val isSelected = index == selectedType
            val subtitle = if (type == "以旧换新") "预估仅需¥399" else " "
            
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(74.dp)
                    .clickable { onTypeSelected(index) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFFFFFBF8) else Color(0xFFF7F7F7)
                ),
                border = if (isSelected) {
                    androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2231A))
                } else {
                    androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE9E9E9))
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = type,
                        fontSize = 15.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color(0xFFE2231A) else Color(0xFF333333)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = if (type == "以旧换新") Color(0xFFE2231A) else Color.Transparent,
                        maxLines = 1
                    )
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
        verticalArrangement = Arrangement.spacedBy(10.dp)
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
            horizontalArrangement = Arrangement.spacedBy(10.dp)
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
                    SubsidyTagChip(tag = tag)
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
                .size(70.dp)
                .background(
                    color = Color(android.graphics.Color.parseColor(color.colorCode)),
                    shape = RoundedCornerShape(10.dp)
                )
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) Color(0xFFE2231A) else Color(0xFFDDDDDD),
                    shape = RoundedCornerShape(10.dp)
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
            fontSize = 13.sp,
            color = if (isSelected) Color(0xFFE2231A) else Color(0xFF666666),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun SubsidyTagChip(tag: String) {
    val (backgroundColor, textColor) = when (tag) {
        "政府补贴", "已减100" -> Color(0xFFE8F4E8) to Color(0xFF6E9F67)
        "国家贴息", "12期免息" -> Color(0xFFEFF7E8) to Color(0xFF6E9F67)
        else -> Color(0xFFFFF0F2) to Color(0xFFE24A57)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = tag,
            fontSize = 12.sp,
            color = textColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}
