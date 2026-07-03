package com.example.jd_sim.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jd_sim.domain.model.InteractionItem

@Composable
fun InteractionSection(
    interactionItems: List<InteractionItem>,
    onInteractionClick: (InteractionItem) -> Unit
) {
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
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "互动游戏",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF191919)
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "此入口京豆翻倍",
                    fontSize = 12.sp,
                    color = Color(0xFF8F8F97)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            interactionItems.forEachIndexed { index, interaction ->
                val gradient = when (index) {
                    0 -> Brush.horizontalGradient(listOf(Color(0xFFFFF3C5), Color(0xFFFFF9E3)))
                    1 -> Brush.horizontalGradient(listOf(Color(0xFFFFF8F0), Color(0xFFFFFFFF)))
                    else -> Brush.horizontalGradient(listOf(Color(0xFFF5FFF0), Color(0xFFFFFFFF)))
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onInteractionClick(interaction) },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(gradient)
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = interaction.iconEmoji,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = interaction.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF191919),
                                maxLines = 1
                            )
                            if (interaction.description.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = interaction.description,
                                    fontSize = 11.sp,
                                    color = Color(0xFFAD7B11),
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                            }
                        }
                        interaction.badgeText?.let { badge ->
                            Text(
                                text = badge,
                                fontSize = 10.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .background(Color(0xFFFF4A52), RoundedCornerShape(999.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        }
                        Icon(
                            imageVector = Icons.Filled.ChevronRight,
                            contentDescription = null,
                            tint = Color(0xFF9A9A9A)
                        )
                    }
                }

                if (index != interactionItems.lastIndex) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
