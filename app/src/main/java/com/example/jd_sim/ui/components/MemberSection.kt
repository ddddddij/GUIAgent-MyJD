package com.example.jd_sim.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jd_sim.domain.model.MemberBenefit
import com.example.jd_sim.domain.model.UserStats

@Composable
fun MemberSection(
    memberBenefits: List<MemberBenefit>,
    userStats: UserStats,
    onBenefitClick: (MemberBenefit) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(
                    "足迹" to userStats.footprint.toString(),
                    "收藏" to userStats.favorites.toString(),
                    "关注" to userStats.following.toString(),
                    "种草" to userStats.grass.toString()
                ).forEach { (label, value) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF191919)
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFF1F2F5), CircleShape)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = value,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF555555)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                memberBenefits.take(6).forEach { benefit ->
                    val label = when (benefit.name) {
                        "红包" -> "抽¥${benefit.value}"
                        "秒送" -> "退换/售后"
                        "更多" -> "更多"
                        else -> benefit.name
                    }
                    val icon = when (benefit.name) {
                        "优惠券" -> "🧾"
                        "京豆" -> "📦"
                        "红包" -> "🔖"
                        "白条取现" -> "💬"
                        "秒送" -> "¥"
                        else -> "全部"
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onBenefitClick(benefit) }
                    ) {
                        Box(contentAlignment = Alignment.TopEnd) {
                            Text(
                                text = icon,
                                fontSize = if (benefit.name == "秒送" || benefit.name == "更多") 13.sp else 18.sp,
                                fontWeight = if (benefit.name == "秒送" || benefit.name == "更多") FontWeight.Bold else FontWeight.Normal,
                                color = if (benefit.name == "秒送" || benefit.name == "更多") Color(0xFF1F1F1F) else Color.Unspecified
                            )
                            if (benefit.name == "白条取现") {
                                Box(
                                    modifier = Modifier
                                        .offset(x = 10.dp, y = (-6).dp)
                                        .background(Color(0xFFFF4A52), CircleShape)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "7",
                                        fontSize = 10.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = label,
                            fontSize = 9.sp,
                            color = Color(0xFF2B2B2B),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
