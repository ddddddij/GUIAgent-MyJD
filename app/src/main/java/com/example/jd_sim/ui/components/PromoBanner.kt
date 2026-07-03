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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jd_sim.domain.model.PromoBannerItem

@Composable
fun PromoBanner(
    promoBanners: List<PromoBannerItem>,
    onBannerClick: (PromoBannerItem) -> Unit
) {
    if (promoBanners.isEmpty()) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        promoBanners.take(2).forEachIndexed { index, banner ->
            val isPrimary = index == 0
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onBannerClick(banner) },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isPrimary) {
                                Brush.horizontalGradient(listOf(Color(0xFFF4F4F6), Color(0xFFFFFFFF)))
                            } else {
                                Brush.horizontalGradient(listOf(Color(0xFFFF3A50), Color(0xFFFF5A60)))
                            }
                        )
                        .padding(horizontal = 18.dp, vertical = 18.dp)
                ) {
                    Column {
                        Text(
                            text = if (isPrimary) "¥80" else "11.11抢先购",
                            fontSize = if (isPrimary) 20.sp else 12.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isPrimary) Color(0xFFFF4A52) else Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isPrimary) banner.title else "直降1折起",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPrimary) Color(0xFFFF4A52) else Color.White,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isPrimary) (banner.subtitle ?: "") else "主会场",
                            fontSize = 9.sp,
                            color = if (isPrimary) Color(0xFF5E5E66) else Color.White.copy(alpha = 0.92f),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
