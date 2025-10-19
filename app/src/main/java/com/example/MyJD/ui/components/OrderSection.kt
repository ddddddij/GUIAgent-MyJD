package com.example.MyJD.ui.components

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
import com.example.MyJD.model.MeTabOrderStatus

@Composable
fun OrderSection(
    orderStatuses: List<MeTabOrderStatus>,
    onOrderStatusClick: (MeTabOrderStatus) -> Unit,
    onViewAllClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Section header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "我的订单",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Text(
                    text = "查看全部 >",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.clickable { onViewAllClick() }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Order status icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                orderStatuses.forEach { orderStatus ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onOrderStatusClick(orderStatus) }
                            .padding(vertical = 8.dp)
                    ) {
                        Box {
                            Text(
                                text = orderStatus.iconEmoji,
                                fontSize = 24.sp
                            )
                            
                            // Show badge if count > 0
                            if (orderStatus.count > 0) {
                                Box(
                                    modifier = Modifier
                                        .offset(x = 16.dp, y = (-4).dp)
                                        .size(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    androidx.compose.foundation.Canvas(
                                        modifier = Modifier.size(16.dp)
                                    ) { 
                                        drawCircle(
                                            color = androidx.compose.ui.graphics.Color.Red
                                        )
                                    }
                                    
                                    Text(
                                        text = orderStatus.count.toString(),
                                        fontSize = 8.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = orderStatus.name,
                            fontSize = 12.sp,
                            color = Color.Black
                        )
                    }
                }
                
                // "全部" option
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onViewAllClick() }
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "📋",
                        fontSize = 24.sp
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "全部",
                        fontSize = 12.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}