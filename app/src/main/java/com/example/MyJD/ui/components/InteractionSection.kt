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
import com.example.MyJD.model.InteractionItem

@Composable
fun InteractionSection(
    interactionItems: List<InteractionItem>,
    onInteractionClick: (InteractionItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "互动游戏",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            interactionItems.forEach { interaction ->
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onInteractionClick(interaction) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box {
                            Text(
                                text = interaction.iconEmoji,
                                fontSize = 32.sp
                            )
                            
                            interaction.badgeText?.let { badge ->
                                Box(
                                    modifier = Modifier
                                        .offset(x = 20.dp, y = (-4).dp)
                                        .padding(2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = badge,
                                        fontSize = 8.sp,
                                        color = Color.Red,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = interaction.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                        
                        if (interaction.description.isNotEmpty()) {
                            Text(
                                text = interaction.description,
                                fontSize = 10.sp,
                                color = Color(0xFFFF8C00)
                            )
                        }
                    }
                }
            }
        }
    }
}