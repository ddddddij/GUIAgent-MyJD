package com.example.MyJD.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuantitySelector(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    minQuantity: Int = 1,
    maxQuantity: Int = 99
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // 减少按钮
        OutlinedIconButton(
            onClick = { 
                if (quantity > minQuantity) {
                    onQuantityChange(quantity - 1)
                }
            },
            enabled = quantity > minQuantity,
            modifier = Modifier.size(32.dp),
            shape = RoundedCornerShape(4.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                width = 1.dp,
                brush = androidx.compose.ui.graphics.SolidColor(
                    if (quantity > minQuantity) Color(0xFFDDDDDD) else Color(0xFFEEEEEE)
                )
            ),
            colors = IconButtonDefaults.outlinedIconButtonColors(
                contentColor = if (quantity > minQuantity) Color(0xFF333333) else Color(0xFFCCCCCC)
            )
        ) {
            Icon(
                imageVector = Icons.Filled.Remove,
                contentDescription = "减少",
                modifier = Modifier.size(16.dp)
            )
        }
        
        // 数量显示
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(32.dp)
                .border(
                    width = 1.dp,
                    color = Color(0xFFDDDDDD),
                    shape = RoundedCornerShape(0.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = quantity.toString(),
                fontSize = 16.sp,
                color = Color(0xFF333333),
                textAlign = TextAlign.Center
            )
        }
        
        // 增加按钮
        OutlinedIconButton(
            onClick = { 
                if (quantity < maxQuantity) {
                    onQuantityChange(quantity + 1)
                }
            },
            enabled = quantity < maxQuantity,
            modifier = Modifier.size(32.dp),
            shape = RoundedCornerShape(4.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                width = 1.dp,
                brush = androidx.compose.ui.graphics.SolidColor(
                    if (quantity < maxQuantity) Color(0xFFDDDDDD) else Color(0xFFEEEEEE)
                )
            ),
            colors = IconButtonDefaults.outlinedIconButtonColors(
                contentColor = if (quantity < maxQuantity) Color(0xFF333333) else Color(0xFFCCCCCC)
            )
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "增加",
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun QuantitySelectorWithLabel(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    minQuantity: Int = 1,
    maxQuantity: Int = 99
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "数量",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333)
        )
        
        QuantitySelector(
            quantity = quantity,
            onQuantityChange = onQuantityChange,
            minQuantity = minQuantity,
            maxQuantity = maxQuantity
        )
    }
}