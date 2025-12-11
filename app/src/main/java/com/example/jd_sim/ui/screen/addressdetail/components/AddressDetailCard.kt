package com.example.jd_sim.ui.screen.addressdetail.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 详细地址卡片
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressDetailCard(
    detailAddress: String,
    onDetailAddressChange: (String) -> Unit,
    detailAddressError: String?,
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
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 地址
            Text(
                text = "地址",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 详细地址
            OutlinedTextField(
                value = detailAddress,
                onValueChange = onDetailAddressChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "详细地址，如道路、门牌号、小区、楼栋号、单元室等",
                        color = Color(0xFF999999)
                    )
                },
                isError = detailAddressError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFE2231A),
                    errorBorderColor = Color(0xFFE53935)
                ),
                minLines = 2,
                maxLines = 3
            )

            detailAddressError?.let { error ->
                Text(
                    text = error,
                    color = Color(0xFFE53935),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
