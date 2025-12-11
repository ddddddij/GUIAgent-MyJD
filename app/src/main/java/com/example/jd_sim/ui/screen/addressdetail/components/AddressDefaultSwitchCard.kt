package com.example.jd_sim.ui.screen.addressdetail.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 设为默认地址开关卡片
 */
@Composable
fun AddressDefaultSwitchCard(
    isDefault: Boolean,
    onDefaultChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "设为默认地址",
                    fontSize = 16.sp,
                    color = Color(0xFF333333)
                )
                Text(
                    text = "提醒：下单时会优先使用该地址",
                    fontSize = 12.sp,
                    color = Color(0xFF999999)
                )
            }

            Switch(
                checked = isDefault,
                onCheckedChange = onDefaultChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFFE2231A),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFDDDDDD)
                )
            )
        }
    }
}
