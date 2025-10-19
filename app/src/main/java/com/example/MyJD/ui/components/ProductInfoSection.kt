package com.example.MyJD.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MyJD.model.ProductSpecifications

@Composable
fun ProductInfoSection(
    title: String,
    tags: List<String>,
    specifications: ProductSpecifications,
    onGiftClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // AppleCare+ 优惠信息
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF3E0)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "👤",
                        fontSize = 16.sp
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "AppleCare+、iCloud+、充值卡特惠中，立即抢购！",
                        fontSize = 14.sp,
                        color = Color(0xFFE2231A),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color(0xFFE2231A),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            // 商品标题
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE2231A)
                        ),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "自营",
                            fontSize = 10.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        color = Color(0xFF333333),
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp
                    )
                }
            }
            
            // 标签行
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tags) { tag ->
                    TagChip(tag = tag)
                }
            }
            
            // 支持送礼物按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🎁",
                        fontSize = 16.sp
                    )
                    Text(
                        text = "支持送礼物",
                        fontSize = 14.sp,
                        color = Color(0xFF666666)
                    )
                }
                
                Button(
                    onClick = onGiftClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE2231A)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "链送礼",
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // 产品规格
            SpecificationGrid(specifications = specifications)
        }
    }
}

@Composable
private fun TagChip(
    tag: String,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when {
        tag.contains("榜单") -> Color(0xFFFFF3E0) to Color(0xFFFF6F00)
        tag.contains("免举证") -> Color(0xFFE8F5E8) to Color(0xFF00C853)
        tag.contains("免费上门") -> Color(0xFFE8F5E8) to Color(0xFF00C853)
        tag.contains("免息") -> Color(0xFFE2231A) to Color.White
        tag.contains("国家贴息") -> Color(0xFFFFF3E0) to Color(0xFFFF6F00)
        else -> Color(0xFFF5F5F5) to Color(0xFF666666)
    }
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = tag,
            fontSize = 12.sp,
            color = textColor,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun SpecificationGrid(
    specifications: ProductSpecifications,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8F8F8)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SpecificationItem(
                    icon = "📅",
                    label = "上市日期",
                    value = specifications.releaseDate,
                    modifier = Modifier.weight(1f)
                )
                
                SpecificationItem(
                    icon = "🔧",
                    label = "处理器",
                    value = specifications.processor,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SpecificationItem(
                    icon = "📱",
                    label = "屏幕",
                    value = specifications.screenSize,
                    modifier = Modifier.weight(1f)
                )
                
                SpecificationItem(
                    icon = "🖥️",
                    label = "屏幕材质",
                    value = specifications.displayTech,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SpecificationItem(
    icon: String,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = icon,
            fontSize = 16.sp
        )
        
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color(0xFF999999)
        )
        
        Text(
            text = value,
            fontSize = 12.sp,
            color = Color(0xFF333333),
            fontWeight = FontWeight.Medium
        )
    }
}