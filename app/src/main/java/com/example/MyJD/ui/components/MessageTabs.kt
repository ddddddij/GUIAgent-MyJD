package com.example.MyJD.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MyJD.model.MessageType
import com.example.MyJD.ui.theme.JDRed
import com.example.MyJD.ui.theme.JDTextPrimary
import com.example.MyJD.ui.theme.JDTextSecondary

@Composable
fun MessageTabs(
    tabs: List<MessageType>,
    selectedTab: MessageType,
    onTabSelected: (MessageType) -> Unit,
    getTabDisplayName: (MessageType) -> String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        tabs.forEach { tab ->
            val isSelected = tab == selectedTab
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabSelected(tab) }
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = if (isSelected) JDRed.copy(alpha = 0.1f) else Color.Transparent,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getTabIcon(tab),
                        fontSize = 24.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = getTabDisplayName(tab),
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) JDRed else JDTextSecondary
                )
                
                if (isSelected) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .width(20.dp)
                            .height(2.dp)
                            .background(JDRed)
                    )
                }
            }
        }
    }
}

private fun getTabIcon(type: MessageType): String {
    return when (type) {
        MessageType.CUSTOMER_SERVICE -> "👨‍💼"
        MessageType.LOGISTICS -> "🚚"
        MessageType.REMINDER -> "🔔"
        MessageType.PROMOTION -> "🎁"
        MessageType.REVIEW -> "💚"
    }
}