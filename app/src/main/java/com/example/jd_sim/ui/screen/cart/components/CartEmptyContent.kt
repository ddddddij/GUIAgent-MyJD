package com.example.jd_sim.ui.screen.cart.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jd_sim.ui.theme.JDRed
import com.example.jd_sim.ui.theme.JDTextSecondary

/**
 * 空购物车内容
 */
@Composable
fun CartEmptyContent(
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ShoppingCart,
                contentDescription = "空购物车",
                modifier = Modifier.size(64.dp),
                tint = Color.Gray
            )
            Text(
                text = "购物车还是空的",
                fontSize = 18.sp,
                color = JDTextSecondary
            )
            Text(
                text = "快去挑选心仪的商品吧",
                fontSize = 14.sp,
                color = JDTextSecondary
            )
            Button(
                onClick = onNavigateToHome,
                colors = ButtonDefaults.buttonColors(containerColor = JDRed)
            ) {
                Text("去逛逛", color = Color.White)
            }
        }
    }
}
