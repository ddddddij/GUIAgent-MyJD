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
import com.example.MyJD.model.PromoBannerItem

@Composable
fun PromoBanner(
    promoBanners: List<PromoBannerItem>,
    onBannerClick: (PromoBannerItem) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            promoBanners.forEach { banner ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = Color(android.graphics.Color.parseColor(banner.backgroundColor)),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { onBannerClick(banner) }
                        .padding(12.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        banner.iconEmoji?.let { emoji ->
                            Text(
                                text = emoji,
                                fontSize = 20.sp,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = banner.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(android.graphics.Color.parseColor(banner.textColor))
                        )
                        
                        banner.subtitle?.let { subtitle ->
                            Text(
                                text = subtitle,
                                fontSize = 10.sp,
                                color = Color(android.graphics.Color.parseColor(banner.textColor))
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Coupon reminder section
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "优惠券",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "满59减10元券将过期",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.weight(1f)
            )
            
            Text(
                text = "06:29:00",
                fontSize = 12.sp,
                color = Color.Red,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "去使用 >",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}