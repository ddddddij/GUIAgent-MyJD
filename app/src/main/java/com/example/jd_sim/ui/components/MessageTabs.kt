package com.example.jd_sim.ui.components

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
import com.example.jd_sim.domain.model.MessageType
import com.example.jd_sim.ui.theme.JDRed
import com.example.jd_sim.ui.theme.JDTextPrimary
import com.example.jd_sim.ui.theme.JDTextSecondary

@Composable
fun MessageTabs(
    tabs: List<MessageType>,
    selectedTab: MessageType,
    onTabSelected: (MessageType) -> Unit,
    getTabDisplayName: (MessageType) -> String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            tabs.forEach { tab ->
                val isSelected = tab == selectedTab

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onTabSelected(tab) }
                        .padding(vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                color = if (isSelected) JDRed.copy(alpha = 0.08f) else Color(0xFFF8F8F8),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = getTabIcon(tab),
                            fontSize = 22.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = getTabDisplayName(tab),
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                        color = if (isSelected) JDTextPrimary else JDTextSecondary
                    )
                }
            }
        }

        // 分割线
        HorizontalDivider(
            color = Color(0xFFF0F0F0),
            thickness = 1.dp
        )
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