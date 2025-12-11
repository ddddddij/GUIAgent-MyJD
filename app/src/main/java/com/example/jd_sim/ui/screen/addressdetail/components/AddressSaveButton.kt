package com.example.jd_sim.ui.screen.addressdetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 地址保存按钮
 */
@Composable
fun AddressSaveButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(48.dp),
        shape = RoundedCornerShape(25.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFFF4D4F),
                            Color(0xFFFF7A45)
                        )
                    ),
                    shape = RoundedCornerShape(25.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "确认",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}
