package com.example.MyJD.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.MyJD.model.MessageSubType
import com.example.MyJD.ui.theme.JDRed
import com.example.MyJD.ui.theme.JDTextPrimary
import com.example.MyJD.ui.theme.JDTextSecondary

@Composable
fun SubTabs(
    tabs: List<MessageSubType>,
    selectedTab: MessageSubType,
    onTabSelected: (MessageSubType) -> Unit,
    getTabDisplayName: (MessageSubType) -> String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        tabs.forEach { tab ->
            val isSelected = tab == selectedTab
            
            Box(
                modifier = Modifier
                    .background(
                        color = if (isSelected) JDRed.copy(alpha = 0.1f) else Color.Transparent,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { onTabSelected(tab) }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getTabDisplayName(tab),
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) JDRed else JDTextSecondary
                )
            }
        }
    }
    
    // 分割线
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFEEEEEE),
        thickness = 1.dp
    )
}