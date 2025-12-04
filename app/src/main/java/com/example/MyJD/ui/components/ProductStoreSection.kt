package com.example.myjd.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myjd.domain.model.StoreInfo

@Composable
fun ProductStoreSection(
    stores: List<StoreInfo>,
    onAppointmentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (stores.isEmpty()) return
    
    val store = stores.first()
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 标题
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "线下门店",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "共 ${store.totalStores} 家",
                        fontSize = 14.sp,
                        color = Color(0xFF666666)
                    )
                    
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color(0xFF999999),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            // 门店信息卡片
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF8F8F8)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // 左侧门店信息
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // 门店名称
                        Text(
                            text = store.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF333333)
                        )
                        
                        // 距离和地址
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "📍",
                                fontSize = 12.sp
                            )
                            
                            Text(
                                text = "距您 ${store.distance} ${store.address}",
                                fontSize = 12.sp,
                                color = Color(0xFF666666),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    
                    // 右侧预约按钮
                    OutlinedButton(
                        onClick = onAppointmentClick,
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFFE2231A)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp, 
                            Color(0xFFE2231A)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "去预约",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}