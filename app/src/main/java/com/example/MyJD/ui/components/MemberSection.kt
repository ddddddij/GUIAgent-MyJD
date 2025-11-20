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
import com.example.MyJD.model.MemberBenefit
import com.example.MyJD.model.UserStats
import com.example.MyJD.ui.theme.JDRed

@Composable
fun MemberSection(
    memberBenefits: List<MemberBenefit>,
    userStats: UserStats,
    onBenefitClick: (MemberBenefit) -> Unit
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
            // Member benefits grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                memberBenefits.forEach { benefit ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onBenefitClick(benefit) }
                            .padding(vertical = 8.dp)
                    ) {
                        if (benefit.value.isNotEmpty()) {
                            Text(
                                text = benefit.value,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = JDRed
                            )
                            
                            Text(
                                text = benefit.description,
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = benefit.name,
                            fontSize = 12.sp,
                            color = Color.Black
                        )
                        
                        if (benefit.name == "更多") {
                            Text(
                                text = benefit.iconEmoji,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // User stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "足迹",
                    value = userStats.footprint.toString()
                )
                
                StatItem(
                    label = "收藏",
                    value = userStats.favorites.toString()
                )
                
                StatItem(
                    label = "关注",
                    value = userStats.following.toString()
                )
                
                StatItem(
                    label = "种草",
                    value = userStats.grass.toString()
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}