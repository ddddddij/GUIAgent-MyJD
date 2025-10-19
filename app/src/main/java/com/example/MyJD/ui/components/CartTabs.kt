package com.example.MyJD.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CartTabs(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf("全部", "购物", "秒送", "外卖")
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 主标签
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { tab ->
                TabItem(
                    text = tab,
                    isSelected = tab == selectedTab,
                    onClick = { onTabSelected(tab) },
                    isPrimary = true
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            IconButton(
                onClick = { /* TODO: 搜索功能 */ },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "搜索",
                    tint = Color(0xFF666666),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        // 次级标签
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SecondaryTabItem(
                text = "清单",
                isPromotional = false,
                onClick = { /* TODO */ }
            )
            
            SecondaryTabItem(
                text = "跨店满减",
                isPromotional = true,
                onClick = { /* TODO */ }
            )
            
            SecondaryTabItem(
                text = "降价",
                isPromotional = false,
                onClick = { /* TODO */ }
            )
            
            SecondaryTabItem(
                text = "筛选",
                isPromotional = false,
                onClick = { /* TODO */ },
                showIcon = true
            )
        }
    }
}

@Composable
private fun TabItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    isPrimary: Boolean = true,
    modifier: Modifier = Modifier
) {
    val textColor = if (isSelected) Color(0xFFE2231A) else Color(0xFF333333)
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            fontSize = if (isPrimary) 16.sp else 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = textColor
        )
        
        if (isSelected && isPrimary) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(20.dp)
                    .height(2.dp)
                    .background(Color(0xFFE2231A), RoundedCornerShape(1.dp))
            )
        }
    }
}

@Composable
private fun SecondaryTabItem(
    text: String,
    isPromotional: Boolean,
    onClick: () -> Unit,
    showIcon: Boolean = false,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isPromotional) Color(0xFFE2231A) else Color.Transparent
    val textColor = if (isPromotional) Color.White else Color(0xFF666666)
    val borderColor = if (isPromotional) Color.Transparent else Color(0xFFE0E0E0)
    
    Card(
        modifier = modifier
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = if (!isPromotional) androidx.compose.foundation.BorderStroke(1.dp, borderColor) else null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = text,
                fontSize = 12.sp,
                color = textColor
            )
            
            if (showIcon) {
                Icon(
                    imageVector = Icons.Filled.FilterList,
                    contentDescription = "筛选",
                    tint = textColor,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}