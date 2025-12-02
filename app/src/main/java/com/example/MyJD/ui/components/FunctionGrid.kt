package com.example.MyJD.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MyJD.ui.theme.JDRed
import com.example.MyJD.ui.theme.JDTextPrimary

data class FunctionItem(
    val name: String,
    val icon: ImageVector,
    val emoji: String? = null
)

@Composable
fun FunctionGrid(
    onFunctionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val functions = listOf(
        FunctionItem("秒杀", Icons.Filled.FlashOn),
        FunctionItem("京东超市", Icons.Filled.Store),
        FunctionItem("试用领取", Icons.Filled.CardGiftcard),
        FunctionItem("领券", Icons.Filled.LocalOffer),
        FunctionItem("酒店", Icons.Filled.Hotel),
        FunctionItem("服饰鞋包", Icons.Filled.Checkroom),
        FunctionItem("手机", Icons.Filled.PhoneAndroid),
        FunctionItem("数码", Icons.Filled.Devices),
        FunctionItem("家电", Icons.Filled.Kitchen),
        FunctionItem("更多", Icons.Filled.MoreHoriz)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Split functions into rows of 5
            val rows = functions.chunked(5)
            rows.forEach { rowFunctions ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    rowFunctions.forEach { function ->
                        FunctionGridItem(
                            function = function,
                            onClick = { onFunctionClick(function.name) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Fill remaining slots if row is not complete
                    repeat(5 - rowFunctions.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
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
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    when (function.name) {
                        "秒杀" -> JDRed
                        "京东超市" -> Color(0xFF4CAF50)
                        "试用领取" -> Color(0xFF2196F3)
                        "领券" -> Color(0xFFFF9800)
                        else -> Color(0xFFF5F5F5)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (function.emoji != null) {
                Text(
                    text = function.emoji,
                    fontSize = 24.sp
                )
            } else {
                Icon(
                    imageVector = function.icon,
                    contentDescription = function.name,
                    tint = if (function.name in listOf("秒杀", "京东超市", "试用领取", "领券")) {
                        Color.White
                    } else {
                        JDTextPrimary
                    },
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        Text(
            text = function.name,
            fontSize = 12.sp,
            color = JDTextPrimary,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}