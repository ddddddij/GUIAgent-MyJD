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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class FunctionItem(
    val name: String,
    val icon: ImageVector,
    val gradient: List<Color>,
    val badge: String? = null
)

@Composable
fun FunctionGrid(
    onFunctionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val functions = listOf(
        FunctionItem("秒杀", Icons.Filled.Bolt, listOf(Color(0xFFF74E49), Color(0xFFF1312D)), "领京豆"),
        FunctionItem("京东超市", Icons.Filled.Store, listOf(Color(0xFFF7B441), Color(0xFF55C88B))),
        FunctionItem("试用领取", Icons.Filled.CardGiftcard, listOf(Color(0xFFF86A55), Color(0xFFE93B31)), "包邮"),
        FunctionItem("领券", Icons.Filled.LocalOffer, listOf(Color(0xFFFFB33D), Color(0xFFF56B23))),
        FunctionItem("酒店", Icons.Filled.Hotel, listOf(Color(0xFF8EC7FF), Color(0xFF376FF1))),
        FunctionItem("服饰鞋包", Icons.Filled.Checkroom, listOf(Color(0xFFAA78FF), Color(0xFF6D42F3))),
        FunctionItem("手机", Icons.Filled.PhoneAndroid, listOf(Color(0xFFFF8F7A), Color(0xFFF24735))),
        FunctionItem("数码", Icons.Filled.Devices, listOf(Color(0xFF6BA7FF), Color(0xFF2D64F4))),
        FunctionItem("家电", Icons.Filled.Kitchen, listOf(Color(0xFF9BE078), Color(0xFF39B85A))),
        FunctionItem("更多", Icons.Filled.MoreHoriz, listOf(Color(0xFFFFB877), Color(0xFFF67D3D)))
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(start = 10.dp, end = 10.dp, top = 6.dp, bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            functions.forEach { function ->
                FunctionGridItem(
                    function = function,
                    onClick = { onFunctionClick(function.name) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .width(20.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color(0xFFE34B49))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .width(28.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color(0xFFD8D8D8))
            )
        }
    }
}

@Composable
private fun FunctionGridItem(
    function: FunctionItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(function.gradient)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = function.icon,
                    contentDescription = function.name,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            function.badge?.let { badge ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color(0xFFF44336))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badge,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = function.name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF4F4F55),
            textAlign = TextAlign.Center,
            lineHeight = 14.sp,
            maxLines = 2
        )
    }
}
