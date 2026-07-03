package com.example.jd_sim.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jd_sim.common.utils.TaskSixLogger
import com.example.jd_sim.domain.model.MeTabOrderStatus

@Composable
fun OrderSection(
    orderStatuses: List<MeTabOrderStatus>,
    onOrderStatusClick: (MeTabOrderStatus) -> Unit,
    onViewAllClick: () -> Unit,
    onOrdersSectionFound: () -> Unit = {},
    onAllOrdersClick: () -> Unit = {}
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        onOrdersSectionFound()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 15.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "我的订单",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF191919)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        onAllOrdersClick()
                        onViewAllClick()
                    }
                ) {
                    Text(
                        text = "查看全部",
                        fontSize = 13.sp,
                        color = Color(0xFF8D8D8D)
                    )
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = Color(0xFF9A9A9A)
                    )
                }
            }

            Spacer(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                orderStatuses.forEach { orderStatus ->
                    OrderItem(
                        orderStatus = orderStatus,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (orderStatus.name == "待付款" && orderStatus.count > 0) {
                                TaskSixLogger.logTaskStart(context)
                            }
                            onOrderStatusClick(orderStatus)
                        }
                    )
                }

                OrderAllItem(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onAllOrdersClick()
                        onViewAllClick()
                    }
                )
            }
        }
    }
}

@Composable
private fun OrderItem(
    orderStatus: MeTabOrderStatus,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayName = if (orderStatus.name == "退换/售后") "售后" else orderStatus.name

    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Text(
                text = orderStatus.iconEmoji,
                fontSize = 26.sp
            )
            if (orderStatus.count > 0) {
                Box(
                    modifier = Modifier
                        .offset(x = 12.dp, y = (-6).dp)
                        .background(Color(0xFFFF404A), CircleShape)
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = orderStatus.count.toString(),
                        fontSize = 10.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Spacer(modifier = Modifier.padding(vertical = 4.dp))
        Text(
            text = displayName,
            fontSize = 13.sp,
            color = Color(0xFF2A2A2A)
        )
    }
}

@Composable
private fun OrderAllItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "全部",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF1C1C1C)
        )
        Spacer(modifier = Modifier.padding(vertical = 4.dp))
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = Color(0xFF7A7A7A)
        )
    }
}
